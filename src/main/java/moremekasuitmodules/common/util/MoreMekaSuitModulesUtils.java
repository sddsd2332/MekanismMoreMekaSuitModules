package moremekasuitmodules.common.util;


import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.registration.impl.ModuleRegistryObject;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.MoreMekaSuitModulesLang;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MoreMekaSuitModulesUtils {

    public static ResourceLocation getResource(ResourceType type, String name) {
        return MoreMekaSuitModules.rl(type.getPrefix() + name);
    }

    public static Component ShieldModuleWarning() {
        return moduleWarning(MekaSuitMoreModules.ENERGY_SHIELD_UNIT, MoreMekaSuitModulesLang.MODULE_SHIELD_DEPLETED);
    }

    public static Component moduleWarning(ModuleRegistryObject<?> moduleData) {
        return moduleWarning(moduleData, MoreMekaSuitModulesLang.MODULE_EMERGENCY_RESCUE);
    }

    public static Component moduleWarning(ModuleRegistryObject<?> moduleData, Object message) {
        return MekanismLang.LOG_FORMAT.translateColored(EnumColor.RED, moduleData, EnumColor.YELLOW, message);
    }


    public enum ResourceType {
        GUI("gui"),
        GUI_BUTTON("gui/button"),
        GUI_BAR("gui/bar"),
        GUI_GAUGE("gui/gauge"),
        GUI_HUD("gui/hud"),
        GUI_ICONS("gui/icons"),
        GUI_PROGRESS("gui/progress"),
        GUI_RADIAL("gui/radial"),
        GUI_SLOT("gui/slot"),
        GUI_TAB("gui/tabs"),
        SOUND("sound"),
        RENDER("render"),
        TEXTURE_BLOCKS("textures/block"),
        TEXTURE_ITEMS("textures/item"),
        MODEL("models"),
        INFUSE("infuse"),
        PIGMENT("pigment"),
        SLURRY("slurry");

        private final String prefix;

        ResourceType(String s) {
            prefix = s;
        }

        public String getPrefix() {
            return prefix + "/";
        }
    }
}
