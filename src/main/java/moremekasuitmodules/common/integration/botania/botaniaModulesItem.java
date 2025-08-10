package moremekasuitmodules.common.integration.botania;

import mekanism.common.item.ItemModule;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.registration.impl.ItemRegistryObject;
import moremekasuitmodules.common.MoreMekaSuitModules;

public class botaniaModulesItem {

    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(MoreMekaSuitModules.MODID);
    public static final ItemRegistryObject<ItemModule> MODULE_BAND_OF_AURA = ITEMS.registerModule(botaniaModules.BAND_OF_AURA_UNIT);
    public static final ItemRegistryObject<ItemModule> MODULE_BASIC_BAND_OF_AURA = ITEMS.registerModule(botaniaModules.BASIC_BAND_OF_AURA_UNIT);
    public static final ItemRegistryObject<ItemModule> MODULE_ADVANCED_BAND_OF_AURA = ITEMS.registerModule(botaniaModules.ADVANCED_BAND_OF_AURA_UNIT);
    public static final ItemRegistryObject<ItemModule> MODULE_ELITE_BAND_OF_AURA = ITEMS.registerModule(botaniaModules.ELITE_BAND_OF_AURA_UNIT);
    public static final ItemRegistryObject<ItemModule> MODULE_ULTIMATE_BAND_OF_AURA = ITEMS.registerModule(botaniaModules.ULTIMATE_BAND_OF_AURA_UNIT);
    public static final ItemRegistryObject<ItemModule> MODULE_CREATIVE_BAND_OF_AURA = ITEMS.registerModule(botaniaModules.CREATIVE_BAND_OF_AURA_UNIT);
}
