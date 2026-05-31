package moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut;

import mekanism.api.text.ILangEntry;
import mekanism.common.Mekanism;
import mekanism.common.util.LangUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public enum MoreMekaSuitModulesLang implements ILangEntry {

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
    MODULE_DAMAGE_SOURCE("module","damage_true_source_exclude"),
    MODULE_DAMAGE_SOURCE_INDIRECT("module","damage_true_source_exclude_Indirect"),
    MODULE_CHUNK_REMOVE("module", "chunk_remove"),
    MODULE_ATTACK_PLAYER("module", "attack_player"),
    MODULE_ATTACK_HOSTILE("module","attack_hostile"),
    MODULE_ATTACK_FRIENDLY("module","attack_friendly"),
    MODULE_ATTACK_OTHER("module", "attack_other"),
    MODULE_PHASE_THROUGH_BLOCKS("module","phase_through_blocks"),
    MODULE_ATTACK_TICK("module", "attack_tick"),
    MODULE_OPTICAL_CAMOUFLAGE("module", "optical_camouflage"),
    MODULE_BOX_COLOR("module", "box_color"),
    MODULE_NAME_COLOR("module", "name_color"),
    MODULE_DISTANCE_COLOR("module", "distance_color"),
    MODULE_MAX_BOXES("module", "max_boxes"),
    MODULE_HEALTH_DISPLAY("module", "health_display"),
    MODULE_WALL_CLING("module", "wall_cling"),
    MODULE_CLIMB_SPEED("module", "climb_speed"),
    MODULE_SCAN_DELAY("module", "scan_delay"),
    MODULE_ORE_SCAN_NEXT("module", "ore_scan_next"),
    MODULE_ORE_SCAN_COUNT("module", "ore_scan_count"),
    MODULE_TEXT_COLOR("module", "text_color"),
    MODULE_MAX_BLOCKS("module", "max_blocks"),
    MODULE_CHAIN_MINING("module", "chain_mining"),
    MODULE_MINING_WAVE_COLOR("module", "mining_wave_color"),
    MODULE_COUNTER_LEVEL("module", "counter_level"),
    MODULE_LOOTING_LEVEL("module", "looting_level"),
    MODULE_KNOCKBACK_STRENGTH("module", "knockback_strength"),
    MODULE_EXECUTION_LEVEL("module", "execution_level"),
    MODULE_IMPACT_TRIGGER_HEIGHT("module", "impact_trigger_height"),
    MODULE_IMPACT_RADIUS("module", "impact_radius"),
    MODULE_IMPACT_DAMAGE("module", "impact_damage"),
    MODULE_IMPACT_WAVE_COLOR("module", "impact_wave_color"),
    // EOL
    ;

    private final String key;

    MoreMekaSuitModulesLang(String key) {
        this.key = key;
    }

    MoreMekaSuitModulesLang(String type, String path) {
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
