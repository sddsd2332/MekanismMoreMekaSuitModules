package moremekasuitmodules.common.config;

import mekanism.api.math.FloatingLong;
import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedBooleanValue;
import mekanism.common.config.value.CachedDoubleValue;
import mekanism.common.config.value.CachedFloatingLongValue;
import mekanism.common.config.value.CachedIntValue;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig.Type;

public class MekaSuitMoreModulesConfig extends BaseMekanismConfig {

    private static final String MEKASUIT_CATEGORY = "mekasuit";
    private final ForgeConfigSpec configSpec;
    //生命恢复单元
    public final CachedFloatingLongValue mekaEnergyUsageHealthRegeneration;

    //智能范围攻击单元
    public final CachedFloatingLongValue mekaSuitEnergyUsageItemAttack;

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
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("MoreMekaSuitModules Config. This config is synced from server to client.").push("moremekasuitmodules");
        builder.comment("MekaSuit Settings").push(MEKASUIT_CATEGORY);
        mekaEnergyUsageHealthRegeneration = CachedFloatingLongValue.define(this, builder, "How much energy regeneration is needed for a health regeneration", "mekaEnergyUsageHealthRegeneration", FloatingLong.create(100));
        mekaSuitEnergyUsageItemAttack = CachedFloatingLongValue.define(this, builder, "Energy cost per tick attacking entity", "energyUsageItemattack", FloatingLong.create(200));
        mekaSuitOverloadProtection = CachedBooleanValue.wrap(this, builder.comment("Allows MekAsuit to intercept direct setHealth with Emergency Rescue installed").define("mekaSuitOverloadProtection", true));
        mekaSuitShield = CachedBooleanValue.wrap(this, builder.comment("Enables the default calculation of the full set of shields").define("mekaSuitShield", true));
        mekaSuitShieldCapacity = CachedDoubleValue.wrap(this, builder.comment("The default set of base shield values after installation").defineInRange("mekaSuitShieldCapacity", 1000.0F, 1.0F, Float.MAX_VALUE));
        mekaSuitRecovery = CachedBooleanValue.wrap(this, builder.comment("Enables the default calculation of the full set of Recovery").define("mekaSuitRecovery", true));
        mekaSuitRecoveryRate = CachedDoubleValue.wrap(this, builder.comment("The recovery rate of the full set of base shields after the default installation").defineInRange("mekaSuitRecoveryRate", 10.0F, 0.1F, Float.MAX_VALUE));
        mekaSuitShieldRestoresEnergy = CachedIntValue.wrap(this, builder.comment("The amount of energy required whenever the shield recovers a little").defineInRange("mekaSuitShieldRestoresEnergy", 500, 0, Integer.MAX_VALUE));
        lastStandEnergyRequirement = CachedIntValue.wrap(this, builder.comment("How much energy regeneration is needed for a health regeneration").define("lastStandEnergyRequirement", 10000000));
        addALLModueltoMekaSuit = CachedBooleanValue.wrap(this, builder.comment("Allows MekaSuit to display the module added in Tap").define("addAllModuleToMekaSuit",true));
        builder.pop();
        configSpec = builder.build();
    }


    @Override
    public String getFileName() {
        return "moremekasuitmodules";
    }

    @Override
    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public Type getConfigType() {
        return Type.SERVER;
    }
}
