package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import moremekasuitmodules.common.config.MoreModulesConfig;
import net.minecraft.entity.player.EntityPlayer;

@ParametersAreNotNullByDefault
public class ModuleAutomaticExtinguishUnit implements ICustomModule<ModuleAutomaticExtinguishUnit> {

    @Override
    public void tickServer(IModule<ModuleAutomaticExtinguishUnit> module, EntityPlayer player) {
        if (!player.isBurning()) {
            return;
        }
        double usage = MoreModulesConfig.current().config.mekaSuitEnergyUsageAutomaticExtinguish.val();
        if (usage <= 0 || player.isCreative()) {
            player.extinguish();
        } else if (module.useEnergy(player, usage) > 0) {
            player.extinguish();
        }
    }
}
