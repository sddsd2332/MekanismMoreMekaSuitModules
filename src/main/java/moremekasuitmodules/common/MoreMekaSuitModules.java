package moremekasuitmodules.common;

import mekanism.api.MekanismIMC;
import mekanism.common.Mekanism;
import mekanism.common.base.IModModule;
import mekanism.common.config.MekanismModConfig;
import mekanism.common.lib.Version;
import mekanism.common.registries.MekanismCreativeTabs;
import moremekasuitmodules.common.config.MoreModulesConfig;
import moremekasuitmodules.common.integration.MoreMekaSuitModulesHooks;
import moremekasuitmodules.common.integration.botania.botaniaImcQueue;
import moremekasuitmodules.common.integration.botania.botaniaModules;
import moremekasuitmodules.common.integration.botania.botaniaModulesItem;
import moremekasuitmodules.common.integration.iceandfire.iceAndFireModules;
import moremekasuitmodules.common.integration.iceandfire.iceAndFireModulesItem;
import moremekasuitmodules.common.integration.iceandfire.iceandfireImcQueue;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesCreativeTabs;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@SuppressWarnings("removal")
@Mod(MoreMekaSuitModules.MODID)
public class MoreMekaSuitModules implements IModModule {

    public static final String MODID = "moremekasuitmodules";

    public static MoreMekaSuitModules instance;

    public final Version versionNumber;

    public static  MoreMekaSuitModulesHooks hooks = new MoreMekaSuitModulesHooks();

    public MoreMekaSuitModules() {
        Mekanism.addModule(instance = this);
        MoreModulesConfig.registerConfigs(ModLoadingContext.get());
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        hooks.hookCommonSetup();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onConfigLoad);
        modEventBus.addListener(this::imcQueue);
        MekaSuitMoreModulesItem.ITEMS.register(modEventBus);
        MekaSuitMoreModules.MODULES.register(modEventBus);
        if (hooks.DraconicEvolutionLoaded) {

        }
        if (hooks.IceAndFireLoaded) {
            modEventBus.addListener(iceandfireImcQueue::imcQueue);
            iceAndFireModulesItem.ITEMS.register(modEventBus);
            iceAndFireModules.MODULES.register(modEventBus);
        }
        if (hooks.BotaniaLoaded) {
            modEventBus.addListener(botaniaImcQueue::imcQueue);
            botaniaModulesItem.ITEMS.register(modEventBus);
            botaniaModules.MODULES.register(modEventBus);
        }

        MekaSuitMoreModulesCreativeTabs.CREATIVE_TABS.register(modEventBus);
        versionNumber = new Version(ModLoadingContext.get().getActiveContainer());

    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MoreMekaSuitModules.MODID, path);
    }


    private void imcQueue(InterModEnqueueEvent event) {
        MekanismIMC.addModulesToAll(
                MekaSuitMoreModules.INFINITE_ENERGY_SUPPLY_UNIT
        );
        //meka套全部
        MekanismIMC.addMekaSuitModules(
                MekaSuitMoreModules.INSULATED_UNIT,
                MekaSuitMoreModules.ENERGY_SHIELD_UNIT,
                MekaSuitMoreModules.POWER_ENHANCEMENT_UNIT,
                MekaSuitMoreModules.HP_BOOTS_UNIT
        );
        //meka头盔
        MekanismIMC.addMekaSuitHelmetModules(
                MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT,
                MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT,
                MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT,
                MekaSuitMoreModules.AUTOMATIC_ATTACK_UNIT);


        //meka护甲
        MekanismIMC.addMekaSuitBodyarmorModules(
                MekaSuitMoreModules.HEALTH_REGENERATION_UNIT,
                MekaSuitMoreModules.INFINITE_CHEMICAL_AND_FLUID_SUPPLY_UNIT,
                MekaSuitMoreModules.HIGH_SPEED_COOLING_UNIT);
        //meka护腿

        //meka靴子
    }


    private void commonSetup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new CommonPlayerTickHandler());
        MinecraftForge.EVENT_BUS.register(new ShieldProviderHandler());
    }



    @Override
    public Version getVersion() {
        return versionNumber;
    }

    @Override
    public String getName() {
        return "MoreMekaSuitModules";
    }

    @Override
    public void resetClient() {

    }

    private void onConfigLoad(ModConfigEvent configEvent) {
        //Note: We listen to both the initial load and the reload, to make sure that we fix any accidentally
        // cached values from calls before the initial loading
        ModConfig config = configEvent.getConfig();
        //Make sure it is for the same modid as us
        if (config.getModId().equals(MODID) && config instanceof MekanismModConfig mekConfig) {
            mekConfig.clearCache(configEvent);
        }
    }
}
