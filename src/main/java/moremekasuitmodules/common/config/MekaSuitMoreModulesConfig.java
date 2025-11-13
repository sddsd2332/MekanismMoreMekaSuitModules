package moremekasuitmodules.common.config;

import mekanism.common.config.BaseConfig;
import mekanism.common.config.options.BooleanOption;
import mekanism.common.config.options.DoubleOption;
import mekanism.common.config.options.FloatOption;
import mekanism.common.config.options.IntOption;

public class MekaSuitMoreModulesConfig extends BaseConfig {

    public BooleanOption mekaSuitShield = new BooleanOption(this, "mekaSuitShield", true, "Enables the default calculation of the full set of shields");
    public FloatOption mekaSuitShieldCapacity = new FloatOption(this,  "mekaSuitShieldCapacity", 1000.0F, "The default set of base shield values after installation", 1.0F, Float.MAX_VALUE);
    public BooleanOption mekaSuitRecovery = new BooleanOption(this,  "mekaSuitRecovery", true, "Enables the default calculation of the full set of Recovery");
    public FloatOption mekaSuitRecoveryRate = new FloatOption(this,  "mekaSuitRecoveryRate", 10.0F, "The recovery rate of the full set of base shields after the default installation", 0.1F, Float.MAX_VALUE);
    public IntOption mekaSuitShieldRestoresEnergy = new IntOption(this,  "mekaSuitShieldRestoresEnergy", 500, "The amount of energy required whenever the shield recovers a little", 0, Integer.MAX_VALUE);
    public BooleanOption MekAsuitOverloadProtection = new BooleanOption(this,  "MekAsuitOverloadProtection", true, "Allows MekAsuit to intercept direct setHealth with Emergency Rescue installed");
    public final BooleanOption DRrecipes = new BooleanOption(this,  "DRrecipes", true, "Enable the default Draconic Evolution recipe").setRequiresGameRestart();
    public final BooleanOption DRAdditionsrecipes = new BooleanOption(this,  "DRAdditionsrecipes", true, "Enable the default Draconic Additions recipe").setRequiresGameRestart();
    public final BooleanOption InfiniteInterception = new BooleanOption(this,  "InfiniteInterception", false, "Enable Infinite interception and rescue system unit").setRequiresGameRestart();
    public final BooleanOption TCRecipes = new BooleanOption(this,  "TCRecipes", true, "Enable the default Thaumcraft Recipe");
    public final BooleanOption TCAspectRecipes = new BooleanOption(this,  "TCAspectRecipes", true, "Let default Thaumcraft recipes add the elements synthesized from both types of elements into the recipe.");
    public DoubleOption mekaSuitEnergyUsageItemAttack = new DoubleOption(this, "energyUsageItemattack", 200, "Energy cost per tick attacking entity");

    @Override
    public String getCategory() {
        return "moremodules";
    }

}
