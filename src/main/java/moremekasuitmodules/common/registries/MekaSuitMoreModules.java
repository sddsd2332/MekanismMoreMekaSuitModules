package moremekasuitmodules.common.registries;

import mekanism.common.registration.impl.ModuleDeferredRegister;
import mekanism.common.registration.impl.ModuleRegistryObject;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.content.gear.*;
import moremekasuitmodules.common.integration.botania.botaniaModules;
import moremekasuitmodules.common.integration.iceandfire.iceAndFireModules;
import net.minecraft.world.item.Rarity;

@SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
public class MekaSuitMoreModules {

    public MekaSuitMoreModules() {
    }

    //注册所属ID
    public static final ModuleDeferredRegister MODULES = new ModuleDeferredRegister(MoreMekaSuitModules.MODID);

    //开始注册模块
    //磁吸单元（在安装物品上能够确保该物品一直吸附在身上）（缺少接口，暂时不实现）
    //生命恢复单元
    public static final ModuleRegistryObject<ModuleHealthRegenerationUnit> HEALTH_REGENERATION_UNIT = MODULES.register("health_regeneration_unit", ModuleHealthRegenerationUnit::new, () -> MekaSuitMoreModulesItem.MODULE_HEALTH_REGENERATION.asItem(), builder -> builder.maxStackSize(10).rarity(Rarity.RARE));
    //紧急救援（消耗该单元来进行复活玩家）
    public static final ModuleRegistryObject<?> EMERGENCY_RESCUE_UNIT = MODULES.registerMarker("emergency_rescue_unit", () -> MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE.asItem(), builder -> builder.maxStackSize(10).rarity(Rarity.EPIC));
    //先进救援（不消耗该单元来进行复活玩家，且如果安装并启用了紧急救援，则不消耗紧急救援）
    public static final ModuleRegistryObject<?> ADVANCED_INTERCEPTION_SYSTEM_UNIT = MODULES.registerMarker("advanced_interception_system_unit", () -> MekaSuitMoreModulesItem.MODULE_ADVANCED_INTERCEPTION_SYSTEM.asItem(), builder -> builder.rarity(Rarity.EPIC));
    //密封单元 //GC或者AR （允许在太空中呼吸））//和mod重复，不添加 https://www.curseforge.com/minecraft/mc-mods/mekanism-x-create-northstar
    //热防护单元 //GC （允许在更热的星球不会导致过热）//和mod重复，不添加 https://www.curseforge.com/minecraft/mc-mods/mekanism-x-create-northstar
    //绝缘单元 IE 和 GTCEU（将电流导入大地，防止导致受伤）
    public static final ModuleRegistryObject<?> INSULATED_UNIT = MODULES.registerMarker("insulated_unit", () -> MekaSuitMoreModulesItem.MODULE_INSULATED.asItem(), builder -> builder);
    //防蜂单元 （散发特殊的信息素，让蜜蜂对你视而不见） //TODO

    //扭曲清除基础单元 神秘（通过特殊的方法移除身上的临时扭曲值）//神秘未到1.20.1
    //扭曲清除高级单元 神秘（通过特殊的方法移除身上的普通扭曲值）//神秘未到1.20.1
    //扭曲清除终极单元 神秘（通过特殊的方法移除身上的永久扭曲值）【创造物品】//神秘未到1.20.1
    //魔力优化单元 神秘（使用特殊的方法，减少魔力Vis的使用）[最大25个]//神秘未到1.20.1
    //揭示护目单元 神秘 （同揭示之护目镜）(需要mixin)//神秘未到1.20.1
    //智能温度调节单元 意志坚定(稳定身体的温度，一直保持最佳温度)//和mod重复，不添加 https://modrinth.com/mod/mekanismmoremodules
    //自动供液单元 意志坚定(如果口渴了，自动喝水)//和mod重复，不添加 https://modrinth.com/mod/mekanismmoremodules
    //微重力调节单元 //和mod重复，不添加 https://www.curseforge.com/minecraft/mc-mods/gravitational-modulating-additional-unit
    //能量护盾单元 龙研[改用MEK实现]（给meka套提供能量护盾）[最大10个](需要mixin)[因为DR3的盾无法实现，所以是按照DR2的盾来实现]
    public static final ModuleRegistryObject<ModuleEnergyShieldUnit> ENERGY_SHIELD_UNIT = MODULES.register("energy_shield_unit", ModuleEnergyShieldUnit::new, () -> MekaSuitMoreModulesItem.MODULE_ENERGY_SHIELD.asItem(), builder -> builder.maxStackSize(10).rarity(Rarity.RARE));
    ///混沌抗性单元 龙研（给meka套提供阻挡混沌伤害的抵抗效果）[最大25个] //TODO

    //混沌旋涡稳定器 龙研（当挖掘混沌晶体时，如果玩家附近会产生混沌旋涡，则移除本单元来平息该爆炸）//1.20.1无该实体，取消
    //智能屏蔽单元 冰与火（通过芯片分析，自动屏蔽对方的目光）【需要mixin】
    /**
     * {@link iceAndFireModules#SMART_SHIELDING_UNIT}
     */
    //无限能量供能单元 （让MekaSuit始终充满能量）【创造物品】
    public static final ModuleRegistryObject<ModuleInfiniteEnergySupplyUnit> INFINITE_ENERGY_SUPPLY_UNIT = MODULES.register("infinite_energy_supply_unit", ModuleInfiniteEnergySupplyUnit::new, () -> MekaSuitMoreModulesItem.MODULE_INFINITE_ENERGY_SUPPLY.asItem(), builder -> builder.rarity(Rarity.EPIC));
    //无限拦截救援系统单元 不再受伤【创造物品】
    public static final ModuleRegistryObject<ModuleInfiniteInterceptionAndRescueSystemUnit> INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT = MODULES.register("infinite_interception_and_rescue_system_unit", ModuleInfiniteInterceptionAndRescueSystemUnit::new, () -> MekaSuitMoreModulesItem.MODULE_INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM.asItem(), builder -> builder.rarity(Rarity.EPIC).noDisable());
    //光环单元 植物魔法
    /**
     * {@link botaniaModules#BAND_OF_AURA_UNIT}
     */
    //基础光环单元 植物魔法
    /**
     * {@link botaniaModules#BASIC_BAND_OF_AURA_UNIT}
     */
    //高级光环单元 植物魔法
    /**
     * {@link botaniaModules#ADVANCED_BAND_OF_AURA_UNIT}
     */
    //精英光环单元 植物魔法
    /**
     * {@link botaniaModules#ELITE_BAND_OF_AURA_UNIT}
     */
    //终极光环单元 植物魔法
    /**
     * {@link botaniaModules#ULTIMATE_BAND_OF_AURA_UNIT}
     */
    //创造光环单元【创造物品】 植物魔法
    /**
     * {@link botaniaModules#CREATIVE_BAND_OF_AURA_UNIT}
     */
    //AE智能无线单元 AE
    //无限供能单元 （让MekaSuit始终充气体/流体）【创造物品】
    public static final ModuleRegistryObject<ModuleInfiniteChemicalAndFluidSupplyUnit> INFINITE_CHEMICAL_AND_FLUID_SUPPLY_UNIT = MODULES.register("infinite_chemical_and_fluid_supply_unit", ModuleInfiniteChemicalAndFluidSupplyUnit::new, () -> MekaSuitMoreModulesItem.MODULE_INFINITE_CHEMICAL_AND_FLUID_SUPPLY.asItem(), builder -> builder.rarity(Rarity.EPIC));
    //智能范围攻击单元 (WC 挂)
    public static final ModuleRegistryObject<ModuleAutomaticAttackUnit> AUTOMATIC_ATTACK_UNIT = MODULES.register("automatic_attack_unit", ModuleAutomaticAttackUnit::new, () -> MekaSuitMoreModulesItem.MODULE_AUTOMATIC_ATTACK.asItem(), builder -> builder.maxStackSize(4).rarity(Rarity.EPIC).disabledByDefault());
    //动力增强单元（增加伤害和攻击速度）
    public static final ModuleRegistryObject<?> POWER_ENHANCEMENT_UNIT = MODULES.registerMarker("power_enhancement_unit", () -> MekaSuitMoreModulesItem.MODULE_POWER_ENHANCEMENT.asItem(), builder -> builder.maxStackSize(64).rarity(Rarity.EPIC));
    //加速冷却单元(加速物品的冷却)
    public static final ModuleRegistryObject<ModuleHighSpeedCoolingUnit> HIGH_SPEED_COOLING_UNIT = MODULES.register("high_speed_cooling_unit", ModuleHighSpeedCoolingUnit::new, () -> MekaSuitMoreModulesItem.MODULE_HIGH_SPEED_COOLING.asItem(), builder -> builder.maxStackSize(10).rarity(Rarity.EPIC));
    //量子重建单元 //和mod重复，不添加 https://www.curseforge.com/minecraft/mc-mods/mekanism-spectator-module
    //生命提升单元
    public static final ModuleRegistryObject<?> HP_BOOTS_UNIT = MODULES.registerMarker("hp_boots_unit",()->MekaSuitMoreModulesItem.MODULE_HP_BOOTS.asItem(),builder -> builder.maxStackSize(64).rarity(Rarity.EPIC).noDisable());
}
