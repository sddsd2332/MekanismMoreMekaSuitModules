package moremekasuitmodules.common.registries;

import mekanism.common.item.ItemModule;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.registration.impl.ItemRegistryObject;
import moremekasuitmodules.common.MoreMekaSuitModules;

public class MekaSuitMoreModulesItem {

    public MekaSuitMoreModulesItem() {
    }

    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(MoreMekaSuitModules.MODID);

    //生命恢复单元
    public static final ItemRegistryObject<ItemModule> MODULE_HEALTH_REGENERATION = ITEMS.registerModule(MekaSuitMoreModules.HEALTH_REGENERATION_UNIT);
    //紧急救援（消耗该单元来进行复活玩家）
    public static final ItemRegistryObject<ItemModule> MODULE_EMERGENCY_RESCUE = ITEMS.registerModule(MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT);
    //先进救援（不消耗该单元来进行复活玩家，且如果安装并启用了紧急救援，则不消耗紧急救援）
    public static final ItemRegistryObject<ItemModule> MODULE_ADVANCED_INTERCEPTION_SYSTEM = ITEMS.registerModule(MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT);
    //绝缘单元 IE 和 GTCEU（将电流导入大地，防止导致受伤）
    public static final ItemRegistryObject<ItemModule> MODULE_INSULATED = ITEMS.registerModule(MekaSuitMoreModules.INSULATED_UNIT);
    //能量护盾单元 龙研 （给meka套提供能量护盾）[最大10个](需要mixin)[因为DR3的盾无法实现，所以是按照DR2的盾来实现]
    public static final ItemRegistryObject<ItemModule> MODULE_ENERGY_SHIELD = ITEMS.registerModule(MekaSuitMoreModules.ENERGY_SHIELD_UNIT);
    //无限能量供能单元 （让MekaSuit始终充满能量）【创造物品】
    public static final ItemRegistryObject<ItemModule> MODULE_INFINITE_ENERGY_SUPPLY = ITEMS.registerModule(MekaSuitMoreModules.INFINITE_ENERGY_SUPPLY_UNIT);
    //无限拦截救援系统单元 不再受伤【创造物品】
    public static final ItemRegistryObject<ItemModule> MODULE_INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM = ITEMS.registerModule(MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
    //无限供能单元 （让MekaSuit始终充气体/流体）【创造物品】 //TODO
    public static final ItemRegistryObject<ItemModule> MODULE_INFINITE_CHEMICAL_AND_FLUID_SUPPLY = ITEMS.registerModule(MekaSuitMoreModules.INFINITE_CHEMICAL_AND_FLUID_SUPPLY_UNIT);
    //智能范围攻击单元 (WC 挂)
    public static final ItemRegistryObject<ItemModule> MODULE_AUTOMATIC_ATTACK = ITEMS.registerModule(MekaSuitMoreModules.AUTOMATIC_ATTACK_UNIT);
    //动力增强单元（增加伤害和攻击速度）
    public static final ItemRegistryObject<ItemModule> MODULE_POWER_ENHANCEMENT = ITEMS.registerModule(MekaSuitMoreModules.POWER_ENHANCEMENT_UNIT);
    //加速冷却单元(加速物品的冷却)
    public static final ItemRegistryObject<ItemModule> MODULE_HIGH_SPEED_COOLING = ITEMS.registerModule(MekaSuitMoreModules.HIGH_SPEED_COOLING_UNIT);
    //生命提升单元
    public static final ItemRegistryObject<ItemModule> MODULE_HP_BOOTS = ITEMS.registerModule(MekaSuitMoreModules.HP_BOOTS_UNIT);
}
