package moremekasuitmodules.common;

import asmodeuscore.core.event.PressureEvent;
import asmodeuscore.core.event.RadiationEvent;
import blusunrize.immersiveengineering.common.util.IEDamageSources.ElectricDamageSource;
import com.brandon3055.draconicevolution.lib.DEDamageSources;
import com.google.common.collect.Sets;
import mekanism.api.gear.IModule;
import mekanism.api.gear.ModuleData;
import mekanism.api.text.TextComponentGroup;
import mekanism.common.MekanismModules;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.integration.MekanismHooks;
import moremekasuitmodules.common.network.to_client.PacketPlayerRescueSync;
import moremekasuitmodules.common.network.to_client.PacketOreVisualRemove;
import moremekasuitmodules.common.content.gear.mekanism.mekatool.AutomaticOreMiningDropRedirector;
import moremekasuitmodules.common.content.gear.mekanism.mekatool.AutomaticOreMiningTracker;
import micdoodle8.mods.galacticraft.api.event.oxygen.GCCoreOxygenSuffocationEvent;
import moremekasuitmodules.common.config.MoreModulesConfig;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.ModuleOxygenSupplyUnit;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.OreVisualScanServerCache;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.OreVisualScanTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zmaster587.advancedRocketry.api.event.AtmosphereEvent;

import java.util.Set;

public class CommonPlayerTickHandler {


    private static boolean ModuleInstallation(ItemStack stack, ModuleData<?> data) {
        if (stack.getItem() instanceof IModuleContainerItem item) {
            return item.isModuleEnabled(stack, data);
        }
        return false;
    }

    private static boolean sealArmor(ItemStack stack) {
        return ModuleInstallation(stack, MekaSuitMoreModules.SEAL_UNIT);
    }

    private static boolean sealHelmetArmor(ItemStack stack) {
        if (stack.getItem() instanceof IModuleContainerItem item) {
            return sealArmor(stack) && item.isModuleEnabled(stack, MekaSuitMoreModules.OXYGEN_SUPPLY_UNIT) && ModuleOxygenSupplyUnit.hasOxygen(stack);
        }
        return false;
    }

    public static boolean hasFullSealedMekaSuit(EntityLivingBase base) {
        boolean sealHelmet = sealHelmetArmor(base.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
        boolean sealChest = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
        boolean sealLegs = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
        boolean sealFeet = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.FEET));
        return sealHelmet && sealChest && sealLegs && sealFeet;
    }

    public static boolean hasFullSealUnit(EntityLivingBase base) {
        boolean sealHelmet = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
        boolean sealChest = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
        boolean sealLegs = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
        boolean sealFeet = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.FEET));
        return sealHelmet && sealChest && sealLegs && sealFeet;
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!event.getWorld().isRemote && !event.isCanceled() && event.getPlayer() instanceof EntityPlayerMP player && hasOreVisualEnhancement(player)) {
            OreVisualScanServerCache.remove(player, event.getPos());
            MoreMekaSuitModules.packetHandler.sendTo(new PacketOreVisualRemove.Message(event.getPos()), player);
        }
    }

    private boolean hasOreVisualEnhancement(EntityPlayer player) {
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        return helmet.getItem() instanceof IModuleContainerItem item && item.isModuleEnabled(helmet, MekaSuitMoreModules.ORE_VISUAL_ENHANCEMENT_UNIT);
    }

    @SubscribeEvent
    public void onOreVisualPlayerLogin(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP player) {
            OreVisualScanTracker.clear(player);
            OreVisualScanTracker.delayInitialScan(player);
            OreVisualScanServerCache.clear(player);
            AutomaticOreMiningTracker.clear(player);
        }
    }

    @SubscribeEvent
    public void onOreVisualPlayerLogout(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP player) {
            AutomaticOreMiningTracker.cancel(player);
            OreVisualScanTracker.clear(player);
            OreVisualScanTracker.delayInitialScan(player);
            OreVisualScanServerCache.clear(player);
            AutomaticOreMiningTracker.clear(player);
        }
    }

    @SubscribeEvent
    public void onOreVisualPlayerChangedDimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.player instanceof EntityPlayerMP player) {
            AutomaticOreMiningTracker.cancel(player);
            OreVisualScanTracker.clear(player);
            OreVisualScanServerCache.clear(player);
            AutomaticOreMiningTracker.clear(player);
        }
    }

    @SubscribeEvent
    public void onOreVisualWorldUnload(WorldEvent.Unload event) {
        if (!event.getWorld().isRemote) {
            OreVisualScanTracker.clearDimension(event.getWorld().provider.getDimension());
            OreVisualScanServerCache.clearDimension(event.getWorld().provider.getDimension());
            AutomaticOreMiningTracker.clearDimension(event.getWorld().provider.getDimension());
            AutomaticOreMiningDropRedirector.clearWorld(event.getWorld());
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = MekanismHooks.AR_MOD_ID)
    public void canARBreathe(AtmosphereEvent.AtmosphereTickEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityLivingBase base) {
            if (hasFullSealedMekaSuit(base)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = MekanismHooks.GC_MOD_ID)
    public void canGCBreathe(GCCoreOxygenSuffocationEvent.Pre event) {
        EntityLivingBase base = event.getEntityLiving();
        if (hasFullSealedMekaSuit(base)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = "asmodeuscore")
    public void canPressure(PressureEvent event) {
        if (event.getEntity() instanceof EntityLivingBase base) {
            boolean SealHelmet = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
            boolean SealChest = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
            boolean SealLegs = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
            boolean seaFeet = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.FEET));
            if (SealHelmet && SealChest && SealLegs && seaFeet) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = "asmodeuscore")
    public void canPressure(RadiationEvent event) {
        if (event.getEntity() instanceof EntityLivingBase base) {
            boolean SealHelmet = ModuleInstallation(base.getItemStackFromSlot(EntityEquipmentSlot.HEAD), MekanismModules.RADIATION_SHIELDING_UNIT);
            boolean SealChest = ModuleInstallation(base.getItemStackFromSlot(EntityEquipmentSlot.CHEST), MekanismModules.RADIATION_SHIELDING_UNIT);
            boolean SealLegs = ModuleInstallation(base.getItemStackFromSlot(EntityEquipmentSlot.LEGS), MekanismModules.RADIATION_SHIELDING_UNIT);
            boolean seaFeet = ModuleInstallation(base.getItemStackFromSlot(EntityEquipmentSlot.FEET), MekanismModules.RADIATION_SHIELDING_UNIT);
            if (SealHelmet && SealChest && SealLegs && seaFeet) {
                event.setCanceled(true);
            }
        }
    }


    //When the player dies
    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer player) {
            ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (head.getItem() instanceof IModuleContainerItem item) {
                boolean isInfiniteModule = item.hasModule(head, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
                if (item.isModuleEnabled(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT) || item.isModuleEnabled(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT) || isInfiniteModule) {
                    event.setCanceled(true);
                    if (!item.hasModule(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT)) {
                        item.removeModule(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT);
                    }
                    Death(player, isInfiniteModule);
                    if (player instanceof EntityPlayerMP serverPlayer) {
                        syncPlayerRescueState(serverPlayer);
                    }
                    sendMessage(player, isInfiniteModule, item, head);
                }
            }

        }
    }


    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        //If the player is affected by setHealth
        //What? Why do you want to go straight to setHealth?
        if (MoreModulesConfig.current().config.MekAsuitOverloadProtection.val()) {
            if (event.getEntityLiving() instanceof EntityPlayerMP player) {
                ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                if (head.getItem() instanceof IModuleContainerItem item) {
                    boolean isInfiniteModule = item.hasModule(head, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
                    if (player.getHealth() <= 0.0F) {
                        if (item.isModuleEnabled(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT) || item.isModuleEnabled(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT) || isInfiniteModule) {
                            if (!item.hasModule(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT)) {
                                item.removeModule(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT);
                            }
                            Death(player, isInfiniteModule);
                            syncPlayerRescueState(player);
                            sendMessage(player, isInfiniteModule, item, head);
                        }
                    } else if (player.isDead || player.deathTime > 0) {
                        resetPlayerDeathState(player);
                        syncPlayerRescueState(player);
                    }
                }
            }
        }
    }


    private void Death(EntityPlayer player, boolean isInfiniteModule) {
        resetPlayerDeathState(player);
        player.setHealth(isInfiniteModule ? player.getMaxHealth() : 5F);
        player.clearActivePotions();
        player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 800, 2));
        player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 900, 2));
        player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 2));
        player.setAir(300);
        player.getFoodStats().addStats(20, 20);
    }

    private void resetPlayerDeathState(EntityPlayer player) {
        player.isDead = false;
        player.deathTime = 0;
        player.hurtTime = 0;
        player.maxHurtTime = 0;
    }

    private void syncPlayerRescueState(EntityPlayerMP player) {
        PacketPlayerRescueSync.Message message = new PacketPlayerRescueSync.Message(player.getEntityId(), player.getHealth());
        MoreMekaSuitModules.packetHandler.sendTo(message, player);
        MoreMekaSuitModules.packetHandler.sendToAllTracking(message, player);
        player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
    }

    private void sendMessage(EntityPlayer player, boolean isInfiniteModule, IModuleContainerItem item, ItemStack head) {
        if (isInfiniteModule) {
            player.sendMessage(new TextComponentGroup(TextFormatting.GRAY).string("[", TextFormatting.RED).translation(MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT.getTranslationKey(), TextFormatting.RED).string("]", TextFormatting.RED).string(":").translation("module.emergency_rescue.use", TextFormatting.YELLOW));
        } else if (item.isModuleEnabled(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT)) {
            player.sendMessage(new TextComponentGroup(TextFormatting.GRAY).string("[", TextFormatting.RED).translation(MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT.getTranslationKey(), TextFormatting.RED).string("]", TextFormatting.RED).string(":").translation("module.emergency_rescue.use", TextFormatting.YELLOW));
        } else if (item.isModuleEnabled(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT)) {
            player.sendMessage(new TextComponentGroup(TextFormatting.GRAY).string("[", TextFormatting.RED).translation(MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT.getTranslationKey(), TextFormatting.RED).string("]", TextFormatting.RED).string(":").translation("module.emergency_rescue.use", TextFormatting.YELLOW));
        }
    }


    private boolean isInsulated(ItemStack stack) {
        return ModuleInstallation(stack, MekaSuitMoreModules.INSULATED_UNIT);
    }

    @SubscribeEvent
    @Optional.Method(modid = "immersiveengineering")
    public void onIEElectricDamage(LivingAttackEvent event) {
        EntityLivingBase base = event.getEntityLiving();
        boolean helmet = isInsulated(base.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
        boolean chest = isInsulated(base.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
        boolean legs = isInsulated(base.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
        boolean feet = isInsulated(base.getItemStackFromSlot(EntityEquipmentSlot.FEET));
        if (helmet && chest && legs && feet) {
            if (event.getSource() instanceof ElectricDamageSource damageSource) {
                damageSource.dmg = 0;
                event.setCanceled(true);
            }
        }
    }


    public static final Set<String> CHAOS_DAMAGE_NAMES = Sets.newHashSet(
            "de.GuardianFireball", "de.GuardianEnergyBall", "de.GuardianChaosBall",
            "chaosImplosion", "damage.de.fusionExplode", "de.islandImplode");

    @SubscribeEvent //这个事件用于计算是否可以完全取消混沌伤害
    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public void onDEDamage(LivingAttackEvent event) {
        if (event.getSource() instanceof DEDamageSources.DamageSourceChaos || CHAOS_DAMAGE_NAMES.contains(event.getSource().damageType) || event.getSource().damageType.equals("chaos")) {
            EntityLivingBase base = event.getEntityLiving();
            int totalLevel = 0;
            for (ItemStack stack : base.getArmorInventoryList()) {
                if (stack.getItem() instanceof IModuleContainerItem item) {
                    IModule<?> module = item.getModule(stack, MekaSuitMoreModules.CHAOS_RESISTANCE_UNIT);
                    if (module != null && module.isEnabled()) {
                        totalLevel += module.getInstalledCount();
                    }
                }
            }
            if (totalLevel > 0) {
                float newDamage = event.getAmount() * (1F - (totalLevel * 0.01F));
                if (newDamage <= 0F) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent //这个事件用于计算是否可以取消混沌伤害 ，如果不能，则重新设置消减后的值
    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public void onDEDamage(LivingHurtEvent event) {
        if (event.getSource() instanceof DEDamageSources.DamageSourceChaos || CHAOS_DAMAGE_NAMES.contains(event.getSource().damageType) || event.getSource().damageType.equals("chaos")) {
            EntityLivingBase base = event.getEntityLiving();
            int totalLevel = 0;
            for (ItemStack stack : base.getArmorInventoryList()) {
                if (stack.getItem() instanceof IModuleContainerItem item) {
                    IModule<?> module = item.getModule(stack, MekaSuitMoreModules.CHAOS_RESISTANCE_UNIT);
                    if (module != null && module.isEnabled()) {
                        totalLevel += module.getInstalledCount();
                    }
                }
            }
            if (totalLevel > 0) {
                float newDamage = event.getAmount() * (1F - (totalLevel * 0.01F));
                if (newDamage > 0F) {
                    event.setAmount(newDamage);
                } else {
                    event.setCanceled(true);
                }
            }
        }
    }


    /**
     * 无限拦截模块 开始
     */

    @SubscribeEvent //实体更新(玩家另外处理)
    public void isInfiniteModule(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase base = event.getEntityLiving();
        if (!(base instanceof EntityPlayer) && isInfiniteModule(base)) {
            if (base.getHealth() != base.getMaxHealth()) {
                base.setHealth(base.getMaxHealth());
            }
            if (base.isEntityAlive()) {
                base.isDead = false;
                base.deathTime = 0;
                base.clearActivePotions();
                base.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 800, 2));
                base.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 900, 2));
                base.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 2));
            }

        }
    }


    @SubscribeEvent  //取消所有伤害
    public void isInfiniteModule(LivingAttackEvent event) {
        EntityLivingBase base = event.getEntityLiving();
        if (isInfiniteModule(base)) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent  //取消所有击退
    public void isInfiniteModule(LivingKnockBackEvent event) {
        EntityLivingBase base = event.getEntityLiving();
        if (isInfiniteModule(base)) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent  //取消所有伤害2
    public void isInfiniteModule(LivingHurtEvent event) {
        EntityLivingBase base = event.getEntityLiving();
        if (isInfiniteModule(base)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent  //取消所有伤害3
    public void isInfiniteModule(LivingDamageEvent event) {
        EntityLivingBase base = event.getEntityLiving();
        if (isInfiniteModule(base)) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent  //取消所有死亡(玩家另外处理)
    public void isInfiniteModule(LivingDeathEvent event) {
        EntityLivingBase base = event.getEntityLiving();
        if (!(base instanceof EntityPlayer) && isInfiniteModule(base)) {
            event.setCanceled(true);
            base.setHealth(base.getMaxHealth());
        }
    }


    @SubscribeEvent  //如果玩家攻击目标带有无限模块，取消本次攻击
    public void isInfiniteModule(AttackEntityEvent event) {
        if (event.getTarget() instanceof EntityLivingBase base) {
            if (isInfiniteModule(base)) {
                event.setCanceled(true);
            }

        }
    }

    @SubscribeEvent //弹射物伤害取消？
    public void isInfiniteModule(CriticalHitEvent event) {
        if (event.getTarget() instanceof EntityLivingBase base) {
            if (isInfiniteModule(base)) {
                event.setDamageModifier(0);
            }
        }
    }


    public boolean isInfiniteModule(EntityLivingBase base) {
        ItemStack head = base.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (head.getItem() instanceof IModuleContainerItem item) {
            return item.hasModule(head, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
        }
        return false;
    }


    @SubscribeEvent
    public void isInfiniteModule(PlayerEvent.Visibility event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (isInfiniteModule(entity)) {
            event.modifyVisibility(0);
        }
    }

    @SubscribeEvent
    public void isInfiniteModuleStopTasks(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity instanceof EntityLiving mob) {
            setStop(mob);
            if (mob instanceof EntityPigZombie zombie) {
                zombie.angerTargetUUID = null;
                zombie.angerLevel = 0;
            }
        }
    }

    private void setStop(EntityLiving entity) {
        if (entity.getAttackTarget() != null && isInfiniteModule(entity.getAttackTarget())) {
            entity.setAttackTarget(null);
            if (entity.targetTasks != null) {
                entity.targetTasks.taskEntries.forEach(task -> task.action.resetTask());
                entity.targetTasks.executingTaskEntries.forEach(task -> task.action.resetTask());
            }
        }

        if (entity.getRevengeTarget() != null && isInfiniteModule(entity.getRevengeTarget())) {
            entity.setAttackTarget(null);
            entity.setRevengeTarget(null);
            entity.attackingPlayer = null;
        }
    }


}
