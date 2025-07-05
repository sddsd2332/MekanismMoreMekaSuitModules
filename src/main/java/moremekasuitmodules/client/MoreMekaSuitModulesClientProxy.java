package moremekasuitmodules.client;

import mekanism.client.render.MekanismRenderer;
import mekanism.common.Mekanism;
import moremekasuitmodules.client.key.GMUTKeyHandler;
import moremekasuitmodules.common.MekaSuitMoreModulesItem;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.MoreMekaSuitModulesCommonProxy;
import moremekasuitmodules.common.config.MoreModulesConfig;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MoreMekaSuitModulesClientProxy extends MoreMekaSuitModulesCommonProxy {

    public void init() {
        super.init();
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
        MinecraftForge.EVENT_BUS.register(new RenderTickHandler());
        new GMUTKeyHandler();
    }

    @Override
    public void registerItemRenders() {
        registerItemRender(MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_ADVANCED_INTERCEPTION_SYSTEM);
        if (Mekanism.hooks.GC || Mekanism.hooks.AR) {
            registerItemRender(MekaSuitMoreModulesItem.MODULE_SEAL);
        }

        if (Mekanism.hooks.GC) {
            registerItemRender(MekaSuitMoreModulesItem.MODULE_THERMAL_PROTECTION);
        }
        if (Loader.isModLoaded("immersiveengineering") || Mekanism.hooks.GTCEULoaded) {
            registerItemRender(MekaSuitMoreModulesItem.MODULE_INSULATED);
        }
        if (Loader.isModLoaded("forestry")) {
            registerItemRender(MekaSuitMoreModulesItem.MODULE_BEE_CONTROL);
        }
        if (Loader.isModLoaded("thaumcraft")) {
            registerItemRender(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_BASE);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_ADVANCED);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_ULTIMATE);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_MAGIC_OPTIMIZATION);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_GOGGLES_OF_REVEALING);
        }
        if (Loader.isModLoaded("toughasnails")) {
            registerItemRender(MekaSuitMoreModulesItem.MODULE_INTELLIGENT_TEMPERATURE_REGULATION);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_AUTOMATIC_LIQUID_SUPPLY);
        }
        registerItemRender(MekaSuitMoreModulesItem.MODULE_GRAVITATIONAL_MODULATING_ADDITIONAL);

        if (Mekanism.hooks.DraconicEvolution) {
            registerItemRender(MekaSuitMoreModulesItem.MODULE_ENERGY_SHIELD);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_CHAOS_RESISTANCE);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_CHAOS_VORTEX_STABILIZATION);
        }
        if (Loader.isModLoaded("iceandfire")) {
            registerItemRender(MekaSuitMoreModulesItem.MODULE_SMART_SHIELDING);
        }
        registerItemRender(MekaSuitMoreModulesItem.MODULE_INFINITE_ENERGY_SUPPLY);
        if (MoreModulesConfig.current().config.InfiniteInterception.val()){
            registerItemRender(MekaSuitMoreModulesItem.MODULE_INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM);
        }

        if (Loader.isModLoaded("botania")){
            registerItemRender(MekaSuitMoreModulesItem.MODULE_BAND_OF_AURA);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_BASIC_BAND_OF_AURA);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_ADVANCED_BAND_OF_AURA);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_ELITE_BAND_OF_AURA);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_ULTIMATE_BAND_OF_AURA);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_CREATIVE_BAND_OF_AURA);
        }

        if (Loader.isModLoaded("appliedenergistics2")){
            registerItemRender(MekaSuitMoreModulesItem.MODULE_SMART_WIRELESS);
        }
        registerItemRender(MekaSuitMoreModulesItem.MODULE_INFINITE_GAS_SUPPLY);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_AUTOMATIC_ATTACK);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_POWER_ENHANCEMENT);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_HIGH_SPEED_COOLING);
    }

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void registerItemRender(Item item) {
        MekanismRenderer.registerItemRender(MoreMekaSuitModules.MODID, item);
    }
}
