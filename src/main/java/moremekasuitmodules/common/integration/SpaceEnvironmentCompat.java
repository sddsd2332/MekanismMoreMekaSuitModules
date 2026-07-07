package moremekasuitmodules.common.integration;

import mekanism.common.Mekanism;
import net.minecraftforge.fml.common.Loader;

public final class SpaceEnvironmentCompat {

    public static final String AD_ASTRA_REBORN_MOD_ID = "ad_astra";

    private SpaceEnvironmentCompat() {
    }

    public static boolean isAdAstraRebornLoaded() {
        return Loader.isModLoaded(AD_ASTRA_REBORN_MOD_ID);
    }

    public static boolean isSpaceEnvironmentLoaded() {
        return Mekanism.hooks.GC || Mekanism.hooks.AR || isAdAstraRebornLoaded();
    }
}
