package moremekasuitmodules.common;

import mekanism.common.Mekanism;
import mekanism.common.item.ItemModule;
import moremekasuitmodules.common.config.MoreModulesConfig;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.registries.IForgeRegistry;

public class MekaSuitMoreModulesItem {

    public static final ItemModule MODULE_EMERGENCY_RESCUE = new ItemModule(MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT);
    public static final ItemModule MODULE_ADVANCED_INTERCEPTION_SYSTEM = new ItemModule(MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT);
    public static final ItemModule MODULE_SEAL = new ItemModule(MekaSuitMoreModules.SEAL_UNIT);
    public static final ItemModule MODULE_THERMAL_PROTECTION = new ItemModule(MekaSuitMoreModules.THERMAL_PROTECTION_UNIT);
    public static final ItemModule MODULE_INSULATED = new ItemModule(MekaSuitMoreModules.INSULATED_UNIT);
    public static final ItemModule MODULE_BEE_CONTROL = new ItemModule(MekaSuitMoreModules.BEE_CONTROL_UNIT);
    public static final ItemModule MODULE_WARP_CLEAR_BASE = new ItemModule(MekaSuitMoreModules.WARP_CLEAR_BASE_UNIT);
    public static final ItemModule MODULE_WARP_CLEAR_ADVANCED = new ItemModule(MekaSuitMoreModules.WARP_CLEAR_ADVANCED_UNIT);
    public static final ItemModule MODULE_WARP_CLEAR_ULTIMATE = new ItemModule(MekaSuitMoreModules.WARP_CLEAR_ULTIMATE_UNIT);
    public static final ItemModule MODULE_MAGIC_OPTIMIZATION = new ItemModule(MekaSuitMoreModules.MAGIC_OPTIMIZATION_UNIT);
    public static final ItemModule MODULE_GOGGLES_OF_REVEALING = new ItemModule(MekaSuitMoreModules.GOGGLES_OF_REVEALING_UNIT);
    public static final ItemModule MODULE_INTELLIGENT_TEMPERATURE_REGULATION = new ItemModule(MekaSuitMoreModules.INTELLIGENT_TEMPERATURE_REGULATION_UNIT);
    public static final ItemModule MODULE_AUTOMATIC_LIQUID_SUPPLY = new ItemModule(MekaSuitMoreModules.AUTOMATIC_LIQUID_SUPPLY_UNIT);
    public static final ItemModule MODULE_GRAVITATIONAL_MODULATING_ADDITIONAL = new ItemModule(MekaSuitMoreModules.GRAVITATIONAL_MODULATING_ADDITIONAL_UNIT);
    public static final ItemModule MODULE_ENERGY_SHIELD = new ItemModule(MekaSuitMoreModules.ENERGY_SHIELD_UNIT);
    public static final ItemModule MODULE_CHAOS_RESISTANCE = new ItemModule(MekaSuitMoreModules.CHAOS_RESISTANCE_UNIT);
    public static final ItemModule MODULE_CHAOS_VORTEX_STABILIZATION = new ItemModule(MekaSuitMoreModules.CHAOS_VORTEX_STABILIZATION_UNIT);
    public static final ItemModule MODULE_SMART_SHIELDING = new ItemModule(MekaSuitMoreModules.SMART_SHIELDING_UNIT);
    public static final ItemModule MODULE_INFINITE_ENERGY_SUPPLY = new ItemModule(MekaSuitMoreModules.INFINITE_ENERGY_SUPPLY_UNIT);
    public static final ItemModule MODULE_INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM = new ItemModule(MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
    public static final ItemModule MODULE_BAND_OF_AURA = new ItemModule(MekaSuitMoreModules.BAND_OF_AURA_UNIT);
    public static final ItemModule MODULE_BASIC_BAND_OF_AURA = new ItemModule(MekaSuitMoreModules.BASIC_BAND_OF_AURA_UNIT);
    public static final ItemModule MODULE_ADVANCED_BAND_OF_AURA = new ItemModule(MekaSuitMoreModules.ADVANCED_BAND_OF_AURA_UNIT);
    public static final ItemModule MODULE_ELITE_BAND_OF_AURA = new ItemModule(MekaSuitMoreModules.ELITE_BAND_OF_AURA_UNIT);
    public static final ItemModule MODULE_ULTIMATE_BAND_OF_AURA = new ItemModule(MekaSuitMoreModules.ULTIMATE_BAND_OF_AURA_UNIT);
    public static final ItemModule MODULE_CREATIVE_BAND_OF_AURA = new ItemModule(MekaSuitMoreModules.CREATIVE_BAND_OF_AURA_UNIT);
    public static final ItemModule MODULE_SMART_WIRELESS = new ItemModule(MekaSuitMoreModules.SMART_WIRELESS_UNIT);

    public static void registerItems(IForgeRegistry<Item> registry) {
        registry.register(initModule(MODULE_EMERGENCY_RESCUE));
        registry.register(initModule(MODULE_ADVANCED_INTERCEPTION_SYSTEM));
        if (Mekanism.hooks.GC || Mekanism.hooks.AR) {
            registry.register(initModule(MODULE_SEAL));
        }

        if (Mekanism.hooks.GC) {
            registry.register(initModule(MODULE_THERMAL_PROTECTION));
        }
        if (Loader.isModLoaded("immersiveengineering") || Mekanism.hooks.GTCEULoaded) {
            registry.register(initModule(MODULE_INSULATED));
        }
        if (Loader.isModLoaded("forestry")) {
            registry.register(initModule(MODULE_BEE_CONTROL));
        }
        if (Loader.isModLoaded("thaumcraft")) {
            registry.register(initModule(MODULE_WARP_CLEAR_BASE));
            registry.register(initModule(MODULE_WARP_CLEAR_ADVANCED));
            registry.register(initModule(MODULE_WARP_CLEAR_ULTIMATE));
            registry.register(initModule(MODULE_MAGIC_OPTIMIZATION));
            registry.register(initModule(MODULE_GOGGLES_OF_REVEALING));
        }
        if (Loader.isModLoaded("toughasnails")) {
            registry.register(initModule(MODULE_INTELLIGENT_TEMPERATURE_REGULATION));
            registry.register(initModule(MODULE_AUTOMATIC_LIQUID_SUPPLY));
        }
        registry.register(initModule(MODULE_GRAVITATIONAL_MODULATING_ADDITIONAL));


        if (Mekanism.hooks.DraconicEvolution) {
            registry.register(initModule(MODULE_ENERGY_SHIELD));
            registry.register(initModule(MODULE_CHAOS_RESISTANCE));
            registry.register(initModule(MODULE_CHAOS_VORTEX_STABILIZATION));
        }
        if (Loader.isModLoaded("iceandfire")) {
            registry.register(initModule(MODULE_SMART_SHIELDING));
        }
        registry.register(initModule(MODULE_INFINITE_ENERGY_SUPPLY));
        if (MoreModulesConfig.current().config.InfiniteInterception.val()) {
            registry.register(initModule(MODULE_INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM));
        }
        if (Loader.isModLoaded("botania")) {
            registry.register(initModule(MODULE_BAND_OF_AURA));
            registry.register(initModule(MODULE_BASIC_BAND_OF_AURA));
            registry.register(initModule(MODULE_ADVANCED_BAND_OF_AURA));
            registry.register(initModule(MODULE_ELITE_BAND_OF_AURA));
            registry.register(initModule(MODULE_ULTIMATE_BAND_OF_AURA));
            registry.register(initModule(MODULE_CREATIVE_BAND_OF_AURA));
        }

        if (Loader.isModLoaded("appliedenergistics2")) {
            registry.register(initModule(MODULE_SMART_WIRELESS));
        }
    }

    public static Item initModule(ItemModule item) {
        String name = "module_" + item.getModuleData().getName();
        return item.setTranslationKey(name).setRegistryName(new ResourceLocation(MoreMekaSuitModules.MODID, name)).setCreativeTab(Mekanism.tabMekanism);
    }
}
