package moremekasuitmodules.common.content.gear;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import moremekasuitmodules.common.config.MoreModulesConfig;

@ParametersAreNotNullByDefault
public class ModuleEnergyShieldUnit implements ICustomModule<ModuleEnergyShieldUnit> {


    public double getRecoveryRate(IModule<ModuleEnergyShieldUnit> module) {
        int upgradeLevel = module.getInstalledCount();
        if (MoreModulesConfig.config.mekaSuitRecovery.get()) {
            return MoreModulesConfig.config.mekaSuitRecoveryRate.get() * (int) Math.pow(2, upgradeLevel);
        } else {
            return MoreModulesConfig.config.mekaSuitRecoveryRate.get() * (1.0F + upgradeLevel);
        }
    }

    public double getProtectionPoints(IModule<ModuleEnergyShieldUnit> module, float absorption, double Points) {
        int upgradeLevel = module.getInstalledCount();
        if (module.isEnabled()) {
            if (MoreModulesConfig.config.mekaSuitShield.get()) {
                return MoreModulesConfig.config.mekaSuitShieldCapacity.get() * absorption * (int) Math.pow(2, upgradeLevel);
            } else {
                return MoreModulesConfig.config.mekaSuitShieldCapacity.get() * absorption * upgradeLevel;
            }
        } else {
            return Points;
        }
    }

}
