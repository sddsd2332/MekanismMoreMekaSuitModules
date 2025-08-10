package moremekasuitmodules.common.content.gear;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleBooleanData;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import moremekasuitmodules.common.MoreMekaSuitModulesLang;
import moremekasuitmodules.common.config.MoreModulesConfig;
import net.minecraft.world.item.ArmorItem;

@ParametersAreNotNullByDefault
public class ModuleEnergyShieldUnit implements ICustomModule<ModuleEnergyShieldUnit> {

    private IModuleConfigItem<Boolean> enableShield;

    @Override
    public void init(IModule<ModuleEnergyShieldUnit> module, ModuleConfigItemCreator configItemCreator) {
        //只在头部注册是否控制护盾启用
        if (module.getContainer().getItem() instanceof ArmorItem item && item.getType().equals(ArmorItem.Type.HELMET)){
            enableShield = configItemCreator.createConfigItem("enable_shield", MoreMekaSuitModulesLang.MODULE_SHIELD_ENABLE, new ModuleBooleanData());
        }
    }

    public boolean getEnableShield(){
            return enableShield.get();
    }

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
