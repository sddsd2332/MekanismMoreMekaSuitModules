package moremekasuitmodules.common.integration.appliedenergistics2;

import appeng.api.AEApi;
import appeng.api.features.IWirelessTermHandler;
import mekanism.common.MekanismItems;

public final class LegacyWirelessRegistration {

    private LegacyWirelessRegistration() {
    }

    public static void registerMekaSuitHelmet() {
        AEApi.instance().registries().wireless().registerWirelessHandler((IWirelessTermHandler) MekanismItems.MEKASUIT_HELMET);
    }
}
