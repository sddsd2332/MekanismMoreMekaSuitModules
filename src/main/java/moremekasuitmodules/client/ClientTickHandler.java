package moremekasuitmodules.client;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.util.SkyCollectionHelper;
import mekanism.api.gear.IModule;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.ModuleHelper;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.ModuleGravitationalModulatingAdditionalUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class ClientTickHandler {

    public static Random rand = new Random();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().player != null) {
            if (Loader.isModLoaded("astralsorcery")) {
                AsStarlightFieldEffect(Minecraft.getMinecraft().player);
            }
        }
    }

    @Optional.Method(modid = "astralsorcery")
    private void AsStarlightFieldEffect(EntityPlayer player) {
        ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (head.getItem() instanceof IModuleContainerItem item) {
            if (item.isModuleEnabled(head, MekaSuitMoreModules.FOSIC_RESONATOR_UNIT)) {
                playStarlightFieldEffect();
            }
        }
    }

    //copy ItemSkyResonator playStarlightFieldEffect
    @Optional.Method(modid = "astralsorcery")
    private void playStarlightFieldEffect() {
        if (!ConstellationSkyHandler.getInstance().getSeedIfPresent(Minecraft.getMinecraft().world).isPresent()) return;
        float nightPerc = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(Minecraft.getMinecraft().world);
        if (nightPerc >= 0.05) {
            Color c = new Color(0, 6, 58);
            BlockPos center = Minecraft.getMinecraft().player.getPosition();
            int offsetX = center.getX();
            int offsetZ = center.getZ();
            BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain(center);

            for (int xx = -30; xx <= 30; xx++) {
                for (int zz = -30; zz <= 30; zz++) {

                    BlockPos top = Minecraft.getMinecraft().world.getTopSolidOrLiquidBlock(pos.setPos(offsetX + xx, 0, offsetZ + zz));
                    //Can be force unwrapped since statement 2nd Line prevents non-present values.
                    Float opF = SkyCollectionHelper.getSkyNoiseDistributionClient(Minecraft.getMinecraft().world, top).get();

                    float fPerc = (float) Math.pow((opF - 0.4F) * 1.65F, 2);
                    if (opF >= 0.4F && rand.nextFloat() <= fPerc) {
                        if (rand.nextFloat() <= fPerc && rand.nextInt(6) == 0) {
                            EffectHelper.genericFlareParticle(top.getX() + rand.nextFloat(), top.getY() + 0.15, top.getZ() + rand.nextFloat())
                                    .scale(4F)
                                    .setColor(c)
                                    .enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID)
                                    .gravity(0.004)
                                    .setAlphaMultiplier(nightPerc * fPerc);
                            if (opF >= 0.8F && rand.nextInt(3) == 0) {
                                EffectHelper.genericFlareParticle(top.getX() + rand.nextFloat(), top.getY() + 0.15, top.getZ() + rand.nextFloat())
                                        .scale(0.3F)
                                        .setColor(Color.WHITE)
                                        .gravity(0.01)
                                        .setAlphaMultiplier(nightPerc);
                            }
                        }
                    }
                }
            }
            pos.release();
        }
    }

    @SubscribeEvent
    public static void onFOVModifier(FOVUpdateEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        IModule<ModuleGravitationalModulatingAdditionalUnit> module = ModuleHelper.get().load(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST), MekaSuitMoreModules.GRAVITATIONAL_MODULATING_ADDITIONAL_UNIT);
        if (module != null && module.isEnabled()) {
            boolean fixFOV = module.getCustomInstance().getFixFOV().get();
            if (fixFOV) {
                e.setNewfov(1.0F);
            }
        }
    }

    @SubscribeEvent
    public void GuiScreenEvent(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiGameOver) {
            Minecraft minecraft = event.getGui().mc;
            if (minecraft.player instanceof EntityPlayerSP) {
                ItemStack head = minecraft.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                if (!minecraft.player.isEntityAlive()){
                    if (head.getItem() instanceof IModuleContainerItem item){
                        if (item.isModuleEnabled(head,MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT) || item.isModuleEnabled(head,MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT)){
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }
}
