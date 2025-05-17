package moremekasuitmodules.common;


import io.netty.buffer.ByteBuf;
import mekanism.api.gas.IGasItem;
import mekanism.common.Mekanism;
import mekanism.common.MekanismItems;
import mekanism.common.Version;
import mekanism.common.base.IModule;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.network.PacketSimpleGui;
import moremekasuitmodules.common.config.MoreModulesConfig;
import moremekasuitmodules.common.network.GMUTPacketHandler;
import moremekasuitmodules.moremekasuitmodules.Tags;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.File;

@Mod(modid = MoreMekaSuitModules.MODID, useMetadata = true, version = Tags.VERSION)
@Mod.EventBusSubscriber()
public class MoreMekaSuitModules implements IModule {

    public static final String MODID = Tags.MOD_ID;

    @SidedProxy(clientSide = "moremekasuitmodules.client.MoreMekaSuitModulesClientProxy", serverSide = "moremekasuitmodules.common.MoreMekaSuitModulesCommonProxy")
    public static MoreMekaSuitModulesCommonProxy proxy;

    @Mod.Instance(MODID)
    public static MoreMekaSuitModules instance;

    public static Version versionNumber = new Version(999, 999, 999);
    public static final int DATA_VERSION = 1;
    public static final GMUTPacketHandler packetHandler = new GMUTPacketHandler();

    public static Configuration config;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Mekanism.modulesLoaded.add(this);
        PacketSimpleGui.handlers.add(proxy);
        MinecraftForge.EVENT_BUS.register(new CommonPlayerTickHandler());
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new MoreMekaSuitModulesGuiHandler());
        MinecraftForge.EVENT_BUS.register(this);
        packetHandler.initialize();
        proxy.init();
        imcQueue();
        Mekanism.logger.info("Loaded Mekanism MoreMeka Suit Modules module.");
    }

    @Override
    public Version getVersion() {
        return versionNumber;
    }

    @Override
    public String getName() {
        return "MoreMekaSuitModules";
    }


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
        config = new Configuration(new File("config/mekanism/MoreMekaSuitModules.cfg"));
        loadConfiguration();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MODID) || event.getModID().equals(Mekanism.MODID)) {
            loadConfiguration();
        }
    }


    @Override
    public void writeConfig(ByteBuf byteBuf, MekanismConfig mekanismConfig) {

    }

    @Override
    public void readConfig(ByteBuf byteBuf, MekanismConfig mekanismConfig) {

    }

    @Override
    public void resetClient() {

    }

    public void loadConfiguration() {
        MoreModulesConfig.local().config.load(config);
        if (config.hasChanged()) {
            config.save();
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        MekaSuitMoreModulesItem.registerItems(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        drRecipes.addRecipes();
        tcRecipes.addRecipes();
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        proxy.registerItemRenders();
    }

    private void imcQueue() {
        for (Item allmodules : ForgeRegistries.ITEMS) {
            if (allmodules instanceof IModuleContainerItem) {
                ModuleHelper.get().setSupported(allmodules, MekaSuitMoreModules.INFINITE_ENERGY_SUPPLY_UNIT);
                if (allmodules instanceof IGasItem) {
                    ModuleHelper.get().setSupported(allmodules, MekaSuitMoreModules.INFINITE_GAS_SUPPLY_UNIT);
                }
            }
        }

        Item[] addMekaSuitModules = {MekanismItems.MEKASUIT_HELMET, MekanismItems.MEKASUIT_BODYARMOR, MekanismItems.MEKASUIT_PANTS, MekanismItems.MEKASUIT_BOOTS};
        for (Item stack : addMekaSuitModules) {

            if (Mekanism.hooks.DraconicEvolution) {
                ModuleHelper.get().setSupported(stack, MekaSuitMoreModules.ENERGY_SHIELD_UNIT, MekaSuitMoreModules.CHAOS_RESISTANCE_UNIT);
            }
            if (Mekanism.hooks.AR || Mekanism.hooks.GC) {
                ModuleHelper.get().setSupported(stack, MekaSuitMoreModules.SEAL_UNIT);
            }
            if (Loader.isModLoaded("immersiveengineering") || Mekanism.hooks.GTCEULoaded) {
                ModuleHelper.get().setSupported(stack, MekaSuitMoreModules.INSULATED_UNIT);
            }
            if (Loader.isModLoaded("forestry")) {
                ModuleHelper.get().setSupported(stack, MekaSuitMoreModules.BEE_CONTROL_UNIT);
            }
            if (Loader.isModLoaded("thaumcraft")) {
                ModuleHelper.get().setSupported(stack, MekaSuitMoreModules.MAGIC_OPTIMIZATION_UNIT);
            }
        }

        ModuleHelper.get().setSupported(MekanismItems.MEKASUIT_HELMET, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT);
        if (MoreModulesConfig.current().config.InfiniteInterception.val()) {
            ModuleHelper.get().setSupported(MekanismItems.MEKASUIT_HELMET, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
        }
        if (Loader.isModLoaded("thaumcraft")) {
            ModuleHelper.get().setSupported(MekanismItems.MEKASUIT_HELMET, MekaSuitMoreModules.WARP_CLEAR_BASE_UNIT, MekaSuitMoreModules.WARP_CLEAR_ADVANCED_UNIT, MekaSuitMoreModules.WARP_CLEAR_ULTIMATE_UNIT, MekaSuitMoreModules.GOGGLES_OF_REVEALING_UNIT);
        }

        if (Loader.isModLoaded("toughasnails")) {
            ModuleHelper.get().setSupported(MekanismItems.MEKASUIT_HELMET, MekaSuitMoreModules.AUTOMATIC_LIQUID_SUPPLY_UNIT);
            ModuleHelper.get().setSupported(MekanismItems.MEKASUIT_BODYARMOR, MekaSuitMoreModules.INTELLIGENT_TEMPERATURE_REGULATION_UNIT);
        }

        if (Mekanism.hooks.GC) {
            ModuleHelper.get().setSupported(MekanismItems.MEKASUIT_BODYARMOR, MekaSuitMoreModules.THERMAL_PROTECTION_UNIT);
        }
        ModuleHelper.get().setSupported(MekanismItems.MEKASUIT_BODYARMOR, MekaSuitMoreModules.GRAVITATIONAL_MODULATING_ADDITIONAL_UNIT);

        if (Mekanism.hooks.DraconicEvolution) {
            ModuleHelper.get().setSupported(MekanismItems.MEKASUIT_BODYARMOR, MekaSuitMoreModules.CHAOS_VORTEX_STABILIZATION_UNIT);
        }

        if (Loader.isModLoaded("iceandfire")) {
            ModuleHelper.get().setSupported(MekanismItems.MEKASUIT_HELMET, MekaSuitMoreModules.SMART_SHIELDING_UNIT);
        }

        if (Loader.isModLoaded("botania")) {
            ModuleHelper.get().setSupported(MekanismItems.MEKASUIT_BODYARMOR, MekaSuitMoreModules.BAND_OF_AURA_UNIT, MekaSuitMoreModules.BASIC_BAND_OF_AURA_UNIT, MekaSuitMoreModules.ADVANCED_BAND_OF_AURA_UNIT, MekaSuitMoreModules.ELITE_BAND_OF_AURA_UNIT, MekaSuitMoreModules.ULTIMATE_BAND_OF_AURA_UNIT, MekaSuitMoreModules.CREATIVE_BAND_OF_AURA_UNIT);
        }

        if (Loader.isModLoaded("appliedenergistics2")) {
            ModuleHelper.get().setSupported(MekanismItems.MEKASUIT_HELMET, MekaSuitMoreModules.SMART_WIRELESS_UNIT);
        }

    }


}
