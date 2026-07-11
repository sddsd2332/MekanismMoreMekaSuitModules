package moremekasuitmodules.common.integration;

import mekanism.common.Mekanism;
import net.minecraftforge.fml.common.Loader;

public final class AppliedEnergisticsCompat {

    public static final String LEGACY_AE2_MOD_ID = "appliedenergistics2";
    public static final String AE2_MOD_ID = "ae2";

    private AppliedEnergisticsCompat() {
    }

    public static boolean isLegacyAppliedEnergisticsLoaded() {
        return Loader.isModLoaded(LEGACY_AE2_MOD_ID);
    }

    public static boolean isNewAppliedEnergisticsLoaded() {
        return Loader.isModLoaded(AE2_MOD_ID);
    }

    public static boolean shouldLoadLegacyWireless() {
        return isLegacyAppliedEnergisticsLoaded() && !isNewAppliedEnergisticsLoaded();
    }

    public static void registerLegacyWireless() {
        try {
            Class.forName("moremekasuitmodules.common.integration.appliedenergistics2.LegacyWirelessRegistration")
                    .getMethod("registerMekaSuitHelmet")
                    .invoke(null);
        } catch (ReflectiveOperationException e) {
            Mekanism.logger.error(Mekanism.LOG_TAG + "Failed to register legacy AE2 wireless MekaSuit helmet support.", e);
        }
    }
}
