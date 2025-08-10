package moremekasuitmodules.common.integration.iceandfire;

import mekanism.common.item.ItemModule;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.registration.impl.ItemRegistryObject;
import moremekasuitmodules.common.MoreMekaSuitModules;

public class iceAndFireModulesItem {

    public iceAndFireModulesItem() {
    }

    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(MoreMekaSuitModules.MODID);

    public static final ItemRegistryObject<ItemModule> MODULE_SMART_SHIELDING = ITEMS.registerModule(iceAndFireModules.SMART_SHIELDING_UNIT);
}
