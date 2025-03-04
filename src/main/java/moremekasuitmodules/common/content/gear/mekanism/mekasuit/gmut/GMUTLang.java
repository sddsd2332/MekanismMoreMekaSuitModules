package moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut;

import mekanism.api.text.ILangEntry;
import mekanism.common.Mekanism;
import mekanism.common.util.LangUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public enum GMUTLang implements ILangEntry {

    // Constants
    KEY_CATAGORY("constants", "key_category"),

    // Key
    KEY_VERTICAL_SPEED("key", "vertical_speed"),

    // Modules
    MODULE_FLY_ALWAYS("module", "fly_always"),
    MODULE_STOP_IMMEDIATELY("module", "stop_immediately"),
    MODULE_FIX_FOV("module", "fix_fov"),
    MODULE_VERTICAL_SPEED("module", "vertical_speed"),
    MODULE_AE("module","wireless_terminal"),
    MODULE_AE_MATCHING("module","wireless_matching"),
    // EOL
    ;

    private final String key;

    GMUTLang(String key) {
        this.key = key;
    }

    GMUTLang(String type, String path) {
        this(makeDescriptionId(type, Mekanism.rl(path)));
    }

    public static String makeDescriptionId(String pType, @Nullable ResourceLocation pId) {
        return pId == null ? pType + ".unregistered_sadface" : pType + "." + pId.getNamespace() + "." + pId.getPath().replace('/', '.');
    }

    @Override
    public String getTranslationKey() {
        return LangUtils.localize(key);
    }

}