package moremekasuitmodules.common.integration.botania;

import mekanism.common.registration.impl.ModuleDeferredRegister;
import mekanism.common.registration.impl.ModuleRegistryObject;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.integration.botania.gear.*;
import net.minecraft.world.item.Rarity;

@SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
public class botaniaModules {

    //注册所属ID
    public static final ModuleDeferredRegister MODULES = new ModuleDeferredRegister(MoreMekaSuitModules.MODID);
    //光环单元
    public static final ModuleRegistryObject<ModuleBandofAuraUnit> BAND_OF_AURA_UNIT = MODULES.register("band_of_aura_unit", ModuleBandofAuraUnit::new, () -> botaniaModulesItem.MODULE_BAND_OF_AURA.asItem(), builder -> builder.disabledByDefault());
    //基础光环单元
    public static final ModuleRegistryObject<ModuleBasicBandofAuraUnit> BASIC_BAND_OF_AURA_UNIT = MODULES.register("basic_band_of_aura_unit", ModuleBasicBandofAuraUnit::new, () -> botaniaModulesItem.MODULE_BASIC_BAND_OF_AURA.asItem(), builder -> builder.rarity(Rarity.UNCOMMON).disabledByDefault());
    //高级光环单元
    public static final ModuleRegistryObject<ModuleAdvancedBandofAuraUnit> ADVANCED_BAND_OF_AURA_UNIT = MODULES.register("advanced_band_of_aura_unit", ModuleAdvancedBandofAuraUnit::new, () -> botaniaModulesItem.MODULE_ADVANCED_BAND_OF_AURA.asItem(), builder -> builder.rarity(Rarity.UNCOMMON).disabledByDefault());
    //精英光环单元
    public static final ModuleRegistryObject<ModuleEliteBandofAuraUnit> ELITE_BAND_OF_AURA_UNIT = MODULES.register("elite_band_of_aura_unit", ModuleEliteBandofAuraUnit::new, () -> botaniaModulesItem.MODULE_ELITE_BAND_OF_AURA.asItem(), builder -> builder.rarity(Rarity.RARE).disabledByDefault());
    //终极光环单元
    public static final ModuleRegistryObject<ModuleUltimateBandofAuraUnit> ULTIMATE_BAND_OF_AURA_UNIT = MODULES.register("ultimate_band_of_aura_unit", ModuleUltimateBandofAuraUnit::new, () -> botaniaModulesItem.MODULE_ULTIMATE_BAND_OF_AURA.asItem(), builder -> builder.rarity(Rarity.RARE).disabledByDefault());
    //创造光环单元【创造物品】
    public static final ModuleRegistryObject<ModuleCreativeBandofAuraUnit> CREATIVE_BAND_OF_AURA_UNIT = MODULES.register("creative_band_of_aura_unit", ModuleCreativeBandofAuraUnit::new, () -> botaniaModulesItem.MODULE_CREATIVE_BAND_OF_AURA.asItem(), builder -> builder.rarity(Rarity.EPIC).disabledByDefault());
}
