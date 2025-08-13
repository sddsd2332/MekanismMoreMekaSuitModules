package moremekasuitmodules.common;

import mekanism.api.text.ILangEntry;
import net.minecraft.Util;

public enum MoreMekaSuitModulesLang implements ILangEntry {

    MEKANISM_MORE_MODULES("constants", "mod_name"),
    MODULE_EMERGENCY_RESCUE("module","emergency_rescue"),
    MODULE_SHIELD_DEPLETED("module","shield_depleted"),
    MODULE_PHASE_THROUGH_BLOCKS("module","phase_through_blocks"),
    ;
    private final String key;

    MoreMekaSuitModulesLang(String type, String path) {
        this(Util.makeDescriptionId(type, MoreMekaSuitModules.rl(path)));
    }

    MoreMekaSuitModulesLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }

}
