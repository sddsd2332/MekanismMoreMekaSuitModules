package moremekasuitmodules.common.integration.iceandfire;

import mekanism.common.registration.impl.ModuleDeferredRegister;
import mekanism.common.registration.impl.ModuleRegistryObject;
import moremekasuitmodules.common.MoreMekaSuitModules;
import net.minecraft.world.item.Rarity;

@SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
public class iceAndFireModules {

    //注册所属ID
    public static final ModuleDeferredRegister MODULES = new ModuleDeferredRegister(MoreMekaSuitModules.MODID);
    public static final ModuleRegistryObject<?> SMART_SHIELDING_UNIT = MODULES.registerMarker("smart_shielding_unit", () -> iceAndFireModulesItem.MODULE_SMART_SHIELDING.asItem(), builder -> builder.rarity(Rarity.UNCOMMON));

}
