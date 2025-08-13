package moremekasuitmodules.common.config;

import mekanism.common.config.IConfigTranslation;
import mekanism.common.config.TranslationPreset;
import moremekasuitmodules.common.MoreMekaSuitModules;
import net.minecraft.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MekaSuitMoreModulesConfigTranslations implements IConfigTranslation {
    GEAR_MEKA_SUIT_ENERGY_USAGE_HEALTH_REGENERATION("gear.meka_suit.moremekasuitmodules.health_regeneration","Health Regeneration","Energy usage (Joules) of MekaSuit per tick of using Health Regeneration."),
    GEAR_MEKA_SUIT_ENERGY_USAGE_ATTACK("gear.meka_suit.moremekasuitmodules.attack","Automated Attack","Energy usage (Joules) of MekaSuit per tick of using Automated Attack."),
    GEAR_MEKA_SUIT_OVERLOAD_PROTECTION("gear.meka_suit.moremekasuitmodules.overload_protection","Overload Protection","Allows MekAsuit to intercept direct setHealth with Emergency Rescue installed"),
    GEAR_MEKA_SUIT_SHIELD_DEFAULT("gear.meka_suit.moremekasuitmodules.shield_default","Shield Default","Enables the default calculation of the full set of shields"),
    GEAR_MEKA_SUIT_SHIELD_CAPACITY("gear.meka_suit.moremekasuitmodules.shield_capacity","Shield Capacity","The default set of base shield values after installation"),
    GEAR_MEKA_SUIT_RECOVERY_DEFAULT("gear.meka_suit.moremekasuitmodules.recovery_default","Recovery Default","Enables the default calculation of the full set of Recovery"),
    GEAR_MEKA_SUIT_SHIELD_RECOVERY_RATE("gear.meka_suit.moremekasuitmodules.recovery_rate","Recovery Rate","The recovery rate of the full set of base shields after the default installation"),
    GEAR_MEKA_SUIT_SHIELD_RESTORES("gear.meka_suit.moremekasuitmodules.shield_restores","Shield Restores","The amount of energy required whenever the shield recovers a little"),
    GEAR_MEKA_SUIT_SHIELD_LAST_STAND("gear.meka_suit.moremekasuitmodules.last_stand","Last Stand","How much energy regeneration is needed for a health regeneration"),
    GEAR_MEKA_SUIT_ADD_ALL_MODULE("gear.meka_suit.moremekasuitmodules.all_modules","Add All Modules","Allows MekaSuit to display the module added in Tap");

    MekaSuitMoreModulesConfigTranslations(TranslationPreset preset, String type) {
        this(preset.path(type), preset.title(type), preset.tooltip(type));
    }

    MekaSuitMoreModulesConfigTranslations(String path, String title, String tooltip) {
        this(path, title, tooltip, false);
    }

    MekaSuitMoreModulesConfigTranslations(String path, String title, String tooltip, boolean isSection) {
        this(path, title, tooltip, IConfigTranslation.getSectionTitle(title, isSection));
    }

    MekaSuitMoreModulesConfigTranslations(String path, String title, String tooltip, @Nullable String button) {
        this.key = Util.makeDescriptionId("configuration", MoreMekaSuitModules.rl(path));
        this.title = title;
        this.tooltip = tooltip;
        this.button = button;
    }


    private final String key;
    private final String title;
    private final String tooltip;
    @Nullable
    private final String button;


    @NotNull
    @Override
    public String getTranslationKey() {
        return key;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String tooltip() {
        return tooltip;
    }

    @Nullable
    @Override
    public String button() {
        return button;
    }
}

