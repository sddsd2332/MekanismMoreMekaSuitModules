package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import mekanism.api.gear.IModule;
import mekanism.common.MekanismDamageSource;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.config.MoreModulesConfig;
import moremekasuitmodules.common.network.to_client.PacketImpactWave;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ImpactWaveHandler {

    public static final MekanismDamageSource IMPACT_WAVE_DAMAGE = new MekanismDamageSource("impact_wave");
    private static final int IMPACT_WAVE_DURATION_TICKS = 10;
    private static final Map<UUID, ActiveImpactWave> ACTIVE_WAVES = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP player) || player.world.isRemote || player.isSpectator()) {
            return;
        }
        if (player.isInWater() || player.isInLava() || player.isOnLadder() || player.isElytraFlying()) {
            return;
        }

        ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (!(boots.getItem() instanceof IModuleContainerItem container)) {
            return;
        }
        IModule<ModuleImpactWaveUnit> module = container.getModule(boots, MekaSuitMoreModules.IMPACT_WAVE_UNIT);
        if (module == null || !module.isEnabled()) {
            return;
        }

        ModuleImpactWaveUnit unit = module.getCustomInstance();
        float distance = event.getDistance();
        float triggerHeight = unit.getTriggerHeight();
        if (distance < triggerHeight) {
            return;
        }

        float radius = unit.getRadius();
        float damage = (distance - triggerHeight) * unit.getDamageMultiplier();
        if (damage <= 0.0F) {
            return;
        }

        double usage = MoreModulesConfig.current().config.mekaSuitEnergyUsageImpactWave.val() * Math.max(1.0F, radius);
        if (!module.canUseEnergy(player, usage, false)) {
            return;
        }

        module.useEnergy(player, usage);
        Vec3d pos = player.getPositionVector();
        ACTIVE_WAVES.put(player.getUniqueID(), new ActiveImpactWave(player.dimension, pos.x, player.posY, pos.z, radius, damage, player.world.getTotalWorldTime()));
        PacketImpactWave.Message message = new PacketImpactWave.Message(pos.x, player.posY + 0.05D, pos.z, radius, unit.getWaveColor(), IMPACT_WAVE_DURATION_TICKS, player.getEntityId(), distance);
        MoreMekaSuitModules.packetHandler.sendToAllTracking(message, player);
        MoreMekaSuitModules.packetHandler.sendTo(message, player);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote || ACTIVE_WAVES.isEmpty()) {
            return;
        }
        ActiveImpactWave wave = ACTIVE_WAVES.get(event.player.getUniqueID());
        if (wave == null) {
            return;
        }
        if (!(event.player instanceof EntityPlayerMP player) || player.dimension != wave.dimension) {
            ACTIVE_WAVES.remove(event.player.getUniqueID());
            return;
        }
        long elapsed = player.world.getTotalWorldTime() - wave.startTick;
        if (elapsed > IMPACT_WAVE_DURATION_TICKS) {
            ACTIVE_WAVES.remove(event.player.getUniqueID());
            return;
        }
        damageWaveFront(player, wave, elapsed);
    }

    private void damageWaveFront(EntityPlayerMP player, ActiveImpactWave wave, long elapsedTicks) {
        double progress = Math.max(0.0D, Math.min(1.0D, elapsedTicks / (double) IMPACT_WAVE_DURATION_TICKS));
        double currentRadius = wave.radius * progress * progress;
        double previousRadius = wave.lastRadius;
        wave.lastRadius = currentRadius;
        if (currentRadius <= 0.0D) {
            return;
        }

        AxisAlignedBB area = new AxisAlignedBB(wave.x - currentRadius - 1.0D, wave.y - 1.5D, wave.z - currentRadius - 1.0D,
                wave.x + currentRadius + 1.0D, wave.y + 2.0D, wave.z + currentRadius + 1.0D);
        List<EntityLivingBase> entities = player.world.getEntitiesWithinAABB(EntityLivingBase.class, area, entity ->
                entity != null && entity != player && entity.isEntityAlive() && !entity.isDead);
        for (EntityLivingBase target : entities) {
            if (!wave.damagedEntities.add(target.getEntityId())) {
                continue;
            }
            double dx = target.posX - wave.x;
            double dz = target.posZ - wave.z;
            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            if (horizontalDistance > currentRadius || horizontalDistance + 0.35D < previousRadius || horizontalDistance > wave.radius) {
                wave.damagedEntities.remove(target.getEntityId());
                continue;
            }
            float falloff = 1.0F - (float) (horizontalDistance / wave.radius);
            float scaledDamage = Math.max(1.0F, wave.damage * (0.35F + 0.65F * falloff));
            if (target.attackEntityFrom(IMPACT_WAVE_DAMAGE.setTrueSource(player), scaledDamage)) {
                double push = 0.35D + 0.65D * falloff;
                if (horizontalDistance > 0.0001D) {
                    target.motionX += dx / horizontalDistance * push;
                    target.motionZ += dz / horizontalDistance * push;
                }
                target.motionY += 0.18D + 0.2D * falloff;
            }
        }
    }

    public static void clearAll() {
        ACTIVE_WAVES.clear();
    }

    private static class ActiveImpactWave {

        private final int dimension;
        private final double x;
        private final double y;
        private final double z;
        private final float radius;
        private final float damage;
        private final long startTick;
        private final Set<Integer> damagedEntities = new HashSet<>();
        private double lastRadius;

        private ActiveImpactWave(int dimension, double x, double y, double z, float radius, float damage, long startTick) {
            this.dimension = dimension;
            this.x = x;
            this.y = y;
            this.z = z;
            this.radius = radius;
            this.damage = damage;
            this.startTick = startTick;
        }
    }
}
