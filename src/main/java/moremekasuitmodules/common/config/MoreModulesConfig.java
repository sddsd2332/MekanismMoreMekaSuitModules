package moremekasuitmodules.common.config;

import mekanism.common.config.MekanismConfigHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;

@SuppressWarnings("removal")
public class MoreModulesConfig {

    private MoreModulesConfig() {
    }


    public static final MekaSuitMoreModulesConfig config = new MekaSuitMoreModulesConfig();

    public static void registerConfigs(ModLoadingContext modLoadingContext) {
        ModContainer modContainer = modLoadingContext.getActiveContainer();
        MekanismConfigHelper.registerConfig(modContainer,config);
    }
}
