package moremekasuitmodules.common;

import mekanism.api.gear.ModuleData;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.integration.MekanismHooks;
import moremekasuitmodules.common.content.gear.integration.astralsorcery.ModuleIchosicResonatorUnit;
import moremekasuitmodules.common.content.gear.integration.draconicevolution.ModuleChaosVortexStabilizationUnit;
import moremekasuitmodules.common.content.gear.integration.galacticraft.ModuleThermalProtectionUnit;
import moremekasuitmodules.common.content.gear.integration.thaumcraft.ModuleWarpClearAdvancedUnit;
import moremekasuitmodules.common.content.gear.integration.thaumcraft.ModuleWarpClearBaseUnit;
import moremekasuitmodules.common.content.gear.integration.thaumcraft.ModuleWarpClearUltimateUnit;
import moremekasuitmodules.common.content.gear.integration.toughasnails.ModuleAutomaticLiquidSupplyUnit;
import moremekasuitmodules.common.content.gear.integration.toughasnails.ModuleIntelligentTemperatureRegulationUnit;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.ModuleInfiniteEnergySupplyUnit;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.ModuleGravitationalModulatingAdditionalUnit;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.fml.common.Loader;

public class MekaSuitMoreModules {

    //紧急救援（消耗该单元来进行复活玩家）
    public static final ModuleData<?> EMERGENCY_RESCUE_UNIT = ModuleHelper.registerMarker("emergency_rescue_unit", builder -> builder.maxStackSize(10).rarity(EnumRarity.EPIC));
    //先进救援（不消耗该单元来进行复活玩家，且如果安装并启用了紧急救援，则不消耗紧急救援）
    public static final ModuleData<?> ADVANCED_INTERCEPTION_SYSTEM_UNIT = ModuleHelper.registerMarker("advanced_interception_system_unit", builder -> builder.rarity(EnumRarity.EPIC));
    //密封单元 //GC或者AR （允许在太空中呼吸）
    public static final ModuleData<?> SEAL_UNIT = ModuleHelper.registerMarker("seal_unit", builder -> builder.maxStackSize(1).canEnable(!Loader.isModLoaded(MekanismHooks.GC_MOD_ID) && !Loader.isModLoaded(MekanismHooks.AR_MOD_ID)).notEnabled("tooltip.install.space"));
    //热防护单元 //GC （允许在更热的星球不会导致过热）
    public static final ModuleData<ModuleThermalProtectionUnit> THERMAL_PROTECTION_UNIT = ModuleHelper.register("thermal_protection_unit", ModuleThermalProtectionUnit::new, builder -> builder.maxStackSize(1).canEnable(!Loader.isModLoaded(MekanismHooks.GC_MOD_ID)).notEnabled("tooltip.install.gc"));
    //绝缘单元 IE 和 GTCEU（将电流导入大地，防止导致受伤）
    public static final ModuleData<?> INSULATED_UNIT = ModuleHelper.registerMarker("insulated_unit", builder -> builder.canEnable(!Loader.isModLoaded(MekanismHooks.GTCEU_MOD_ID) && !Loader.isModLoaded("immersiveengineering")).notEnabled("tooltip.install.ie.or.gtceu"));
    //防蜂单元 林业（散发特殊的信息素，让蜜蜂对你视而不见）(需要mixin)
    public static final ModuleData<?> BEE_CONTROL_UNIT = ModuleHelper.registerMarker("bee_control_unit", builder -> builder.canEnable(!Loader.isModLoaded("forestry")).notEnabled("tooltip.install.fr"));
    //扭曲清除基础单元 神秘（通过特殊的方法移除身上的临时扭曲值）
    public static final ModuleData<ModuleWarpClearBaseUnit> WARP_CLEAR_BASE_UNIT = ModuleHelper.register("warp_clear_base_unit", ModuleWarpClearBaseUnit::new, builder -> builder.rarity(EnumRarity.UNCOMMON).canEnable(!Loader.isModLoaded("thaumcraft")).notEnabled("tooltip.install.tc6"));
    //扭曲清除高级单元 神秘（通过特殊的方法移除身上的普通扭曲值）
    public static final ModuleData<ModuleWarpClearAdvancedUnit> WARP_CLEAR_ADVANCED_UNIT = ModuleHelper.register("warp_clear_advanced_unit", ModuleWarpClearAdvancedUnit::new, builder -> builder.rarity(EnumRarity.RARE).canEnable(!Loader.isModLoaded("thaumcraft")).notEnabled("tooltip.install.tc6"));
    //扭曲清除终极单元 神秘（通过特殊的方法移除身上的永久扭曲值）【创造物品】
    public static final ModuleData<ModuleWarpClearUltimateUnit> WARP_CLEAR_ULTIMATE_UNIT = ModuleHelper.register("warp_clear_ultimate_unit", ModuleWarpClearUltimateUnit::new, builder -> builder.rarity(EnumRarity.EPIC).canEnable(!Loader.isModLoaded("thaumcraft")).notEnabled("tooltip.install.tc6"));
    //魔力优化单元 神秘（使用特殊的方法，减少魔力Vis的使用）[最大25个](需要mixin)
    public static final ModuleData<?> MAGIC_OPTIMIZATION_UNIT = ModuleHelper.registerMarker("magic_optimization_unit", builder -> builder.maxStackSize(25).canEnable(!Loader.isModLoaded("thaumcraft")).notEnabled("tooltip.install.tc6"));
    //揭示护目单元 神秘 （同揭示之护目镜）(需要mixin)
    public static final ModuleData<?> GOGGLES_OF_REVEALING_UNIT = ModuleHelper.registerMarker("goggles_of_revealing_unit", builder -> builder.rarity(EnumRarity.UNCOMMON).canEnable(!Loader.isModLoaded("thaumcraft")).notEnabled("tooltip.install.tc6"));
    //智能温度调节单元 意志坚定(稳定身体的温度，一直保持最佳温度)
    public static final ModuleData<ModuleIntelligentTemperatureRegulationUnit> INTELLIGENT_TEMPERATURE_REGULATION_UNIT = ModuleHelper.register("intelligent_temperature_regulation_unit", ModuleIntelligentTemperatureRegulationUnit::new, builder -> builder.rarity(EnumRarity.UNCOMMON).canEnable(!Loader.isModLoaded("toughasnails")).notEnabled("tooltip.install.tna"));
    //自动供液单元 意志坚定(如果口渴了，自动喝水)
    public static final ModuleData<ModuleAutomaticLiquidSupplyUnit> AUTOMATIC_LIQUID_SUPPLY_UNIT = ModuleHelper.register("automatic_liquid_supply_unit", ModuleAutomaticLiquidSupplyUnit::new, builder -> builder.rarity(EnumRarity.UNCOMMON).canEnable(!Loader.isModLoaded("toughasnails")).notEnabled("tooltip.install.tna"));
    //微重力调节单元[移植Gravitational Modulating Additional Unit]
    public static final ModuleData<ModuleGravitationalModulatingAdditionalUnit> GRAVITATIONAL_MODULATING_ADDITIONAL_UNIT = ModuleHelper.register("gravitational_modulating_additional_unit", ModuleGravitationalModulatingAdditionalUnit::new, builder -> builder.maxStackSize(1).rarity(EnumRarity.RARE));
    //汇星共振单元 星辉（通过于星能场的共振，可以显示世界上星能范围）
    public static final ModuleData<?> FOSIC_RESONATOR_UNIT = ModuleHelper.registerMarker("fosic_resonator_unit", builder -> builder.rarity(EnumRarity.RARE).canEnable(!Loader.isModLoaded("astralsorcery")).notEnabled("tooltip.install.as"));
    //液华共振器 星辉（通过于星能场的共振，可以显示世界上涌泉的流体本源精华）
    public static final ModuleData<ModuleIchosicResonatorUnit> ICHOSIC_RESONATOR_UNIT = ModuleHelper.register("ichosic_resonator_unit", ModuleIchosicResonatorUnit::new, builder -> builder.rarity(EnumRarity.RARE).canEnable(!Loader.isModLoaded("astralsorcery")).notEnabled("tooltip.install.as"));
    //AE智能合成无线单元模块 AE （可以访问AE内的物品并可以合成物品？） 【可能不会实现】

    //能量护盾单元 DR （给meka套提供能量护盾）[最大10个](需要mixin)
    public static final ModuleData<?> ENERGY_SHIELD_UNIT = ModuleHelper.registerMarker("energy_shield_unit", builder -> builder.maxStackSize(10).rarity(EnumRarity.RARE).canEnable(!Loader.isModLoaded(MekanismHooks.DraconicEvolution_MOD_ID)).notEnabled("tooltip.install.DR"));
    ///混沌抗性单元 （给meka套提供阻挡混沌伤害的抵抗效果）[最大25个]
    public static final ModuleData<?> CHAOS_RESISTANCE_UNIT = ModuleHelper.registerMarker("chaos_resistance_unit", builder -> builder.maxStackSize(25).rarity(EnumRarity.EPIC).canEnable(!Loader.isModLoaded(MekanismHooks.DraconicEvolution_MOD_ID)).notEnabled("tooltip.install.DR"));
    //混沌旋涡稳定器 DR（当挖掘混沌晶体时，如果玩家附近会产生混沌旋涡，则移除本单元来平息该爆炸）
    public static final ModuleData<ModuleChaosVortexStabilizationUnit> CHAOS_VORTEX_STABILIZATION_UNIT = ModuleHelper.register("chaos_vortex_stabilization_unit", ModuleChaosVortexStabilizationUnit::new, builder -> builder.maxStackSize(10).rarity(EnumRarity.RARE).canEnable(!Loader.isModLoaded(MekanismHooks.DraconicEvolution_MOD_ID)).notEnabled("tooltip.install.DR"));
    //智能屏蔽单元 冰与火（通过芯片分析，自动屏蔽对方的目光）【需要mixin】
    public static final ModuleData<?> SMART_SHIELDING_UNIT = ModuleHelper.registerMarker("smart_shielding_unit", builder -> builder.rarity(EnumRarity.UNCOMMON).canEnable(!Loader.isModLoaded("iceandfire")).notEnabled("tooltip.install.IAF"));
    //无限能量供能单元 （让MekaSuit始终充满能量）【创造物品】
    public static final ModuleData<ModuleInfiniteEnergySupplyUnit> INFINITE_ENERGY_SUPPLY_UNIT = ModuleHelper.register("infinite_energy_supply_unit", ModuleInfiniteEnergySupplyUnit::new, builder -> builder.maxStackSize(1).rarity(EnumRarity.EPIC));

}
