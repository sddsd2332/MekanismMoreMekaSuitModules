package moremekasuitmodules.common.config;

import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.MekanismConfigTranslations;
import mekanism.common.config.value.CachedBooleanValue;
import mekanism.common.config.value.CachedDoubleValue;
import mekanism.common.config.value.CachedIntValue;
import mekanism.common.config.value.CachedLongValue;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec;

public class MekaSuitMoreModulesConfig extends BaseMekanismConfig {

    private final ModConfigSpec configSpec;

    private static final String MEKASUIT_CATEGORY = "mekasuit";
    //生命恢复单元
    public final CachedLongValue mekaEnergyUsageHealthRegeneration;

    //智能范围攻击单元
    public final CachedLongValue mekaSuitEnergyUsageItemAttack;

    //伤害拦截
    public final CachedBooleanValue mekaSuitOverloadProtection;

    //能量护盾
    public final CachedBooleanValue mekaSuitShield;
    public final CachedDoubleValue mekaSuitShieldCapacity;
    public final CachedBooleanValue mekaSuitRecovery;
    public final CachedDoubleValue mekaSuitRecoveryRate;
    public final CachedIntValue mekaSuitShieldRestoresEnergy;
    public final CachedIntValue lastStandEnergyRequirement;
    public final CachedBooleanValue addALLModueltoMekaSuit;

    public MekaSuitMoreModulesConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        MekanismConfigTranslations.GEAR_MEKA_SUIT.applyToBuilder(builder).push(MEKASUIT_CATEGORY);
        mekaEnergyUsageHealthRegeneration = CachedLongValue.definePositive(this, builder, MekaSuitMoreModulesConfigTranslations.GEAR_MEKA_SUIT_ENERGY_USAGE_HEALTH_REGENERATION, "mekaEnergyUsageHealthRegeneration", 100);
        mekaSuitEnergyUsageItemAttack = CachedLongValue.definePositive(this, builder, MekaSuitMoreModulesConfigTranslations.GEAR_MEKA_SUIT_ENERGY_USAGE_ATTACK, "energyUsageItemattack", 200);
        mekaSuitOverloadProtection = CachedBooleanValue.wrap(this, MekaSuitMoreModulesConfigTranslations.GEAR_MEKA_SUIT_OVERLOAD_PROTECTION.applyToBuilder(builder).define("mekaSuitOverloadProtection", true));
        mekaSuitShield = CachedBooleanValue.wrap(this, MekaSuitMoreModulesConfigTranslations.GEAR_MEKA_SUIT_SHIELD_DEFAULT.applyToBuilder(builder).define("mekaSuitShield", true));
        mekaSuitShieldCapacity = CachedDoubleValue.wrap(this, MekaSuitMoreModulesConfigTranslations.GEAR_MEKA_SUIT_SHIELD_CAPACITY.applyToBuilder(builder).defineInRange("mekaSuitShieldCapacity", 1000F, 1F, Float.MAX_VALUE));
        mekaSuitRecovery = CachedBooleanValue.wrap(this, MekaSuitMoreModulesConfigTranslations.GEAR_MEKA_SUIT_RECOVERY_DEFAULT.applyToBuilder(builder).define("mekaSuitRecovery", true));
        mekaSuitRecoveryRate = CachedDoubleValue.wrap(this, MekaSuitMoreModulesConfigTranslations.GEAR_MEKA_SUIT_SHIELD_RECOVERY_RATE.applyToBuilder(builder).defineInRange("mekaSuitRecoveryRate", 10.0F, 0.1F, Float.MAX_VALUE));
        mekaSuitShieldRestoresEnergy = CachedIntValue.wrap(this, MekaSuitMoreModulesConfigTranslations.GEAR_MEKA_SUIT_SHIELD_RESTORES.applyToBuilder(builder).defineInRange("mekaSuitShieldRestoresEnergy", 500, 0, Integer.MAX_VALUE));
        lastStandEnergyRequirement = CachedIntValue.wrap(this,MekaSuitMoreModulesConfigTranslations.GEAR_MEKA_SUIT_SHIELD_LAST_STAND.applyToBuilder(builder).define("lastStandEnergyRequirement", 10000000));
        addALLModueltoMekaSuit = CachedBooleanValue.wrap(this,MekaSuitMoreModulesConfigTranslations.GEAR_MEKA_SUIT_ADD_ALL_MODULE.applyToBuilder(builder).define("addAllModuleToMekaSuit",true));
        builder.pop();
        configSpec = builder.build();
    }


    @Override
    public String getFileName() {
        return "moremekasuitmodules";
    }

    @Override
    public String getTranslation() {
        return "More MekaSuit Modules Config";
    }

    @Override
    public ModConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public Type getConfigType() {
        return Type.SERVER;
    }
}
