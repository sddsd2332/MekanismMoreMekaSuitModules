package moremekasuitmodules.common;

import mekanism.api.text.ILangEntry;
import net.minecraft.Util;

public enum MoreMekaSuitModulesLang implements ILangEntry {
    MEKANISM_MORE_MODULES("constants", "mod_name"),
    MODULE_AE("module", "wireless_terminal"),
    MODULE_AE_MATCHING("module", "wireless_matching"),
    MODULE_DAMAGE_SOURCE("module", "damage_1"),
    MODULE_DAMAGE_SOURCE_INDIRECT("module", "damage_2"),
    MODULE_CHUNK_REMOVE("module", "chunk_remove"),
    MODULE_SHIELD_ENABLE("module", "shield_enable"),
    MODULE_ATTACK_PLAYER("module", "attack_player"),
    MODULE_ATTACK_HOSTILE("module", "attack_hostile"),
    MODULE_ATTACK_NEUTRAL("module", "attack_neutral"),
    MODULE_ATTACK_OTHER("module", "attack_other"),
    MODULE_SUPPLY_ARMOR("module", "supply_armor"),
    MODULE_SUPPLY_INVENTORY("module", "supply_inventory"),
    MODULE_SUPPLY_OFFHAND("module", "supply_offhand"),
    MODULE_SUPPLY_CURIOS("module", "supply_curios"),
    MODULE_EMERGENCY_RESCUE("module","emergency_rescue"),
    MODULE_SHIELD_DEPLETED("module","shield_depleted"),
    MODULE_QIO_OPEN("module","open_qio"),
    // EOL
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
