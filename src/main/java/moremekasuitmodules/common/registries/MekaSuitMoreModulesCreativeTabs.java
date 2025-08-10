package moremekasuitmodules.common.registries;

import mekanism.common.registration.impl.CreativeTabDeferredRegister;
import mekanism.common.registration.impl.CreativeTabRegistryObject;
import mekanism.common.registries.MekanismCreativeTabs;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.MoreMekaSuitModulesLang;
import moremekasuitmodules.common.integration.botania.botaniaModulesItem;
import moremekasuitmodules.common.integration.iceandfire.iceAndFireModulesItem;

public class MekaSuitMoreModulesCreativeTabs {

    public static final CreativeTabDeferredRegister CREATIVE_TABS = new CreativeTabDeferredRegister(MoreMekaSuitModules.MODID);

    public static final CreativeTabRegistryObject MoreModules = CREATIVE_TABS.registerMain(MoreMekaSuitModulesLang.MEKANISM_MORE_MODULES, MekaSuitMoreModulesItem.MODULE_INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM, builder ->
            builder.withTabsBefore(MekanismCreativeTabs.MEKANISM.key())
                    .displayItems((displayParameters, output) -> {
                        CreativeTabDeferredRegister.addToDisplay(MekaSuitMoreModulesItem.ITEMS, output);
                        if (MoreMekaSuitModules.hooks.DraconicEvolutionLoaded) {
                        }
                        if (MoreMekaSuitModules.hooks.IceAndFireLoaded) {
                            CreativeTabDeferredRegister.addToDisplay(iceAndFireModulesItem.ITEMS, output);
                        }
                        if (MoreMekaSuitModules.hooks.BotaniaLoaded) {
                            CreativeTabDeferredRegister.addToDisplay(botaniaModulesItem.ITEMS, output);
                        }
                    })
    );
}
