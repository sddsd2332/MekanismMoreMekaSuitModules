package moremekasuitmodules.common.content.gear;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.math.FloatingLong;
import mekanism.common.config.MekanismConfig;
import moremekasuitmodules.common.config.MoreModulesConfig;
import net.minecraft.world.entity.player.Player;

@ParametersAreNotNullByDefault
public class ModuleHealthRegenerationUnit implements ICustomModule<ModuleHealthRegenerationUnit> {

    @Override
    public void tickServer(IModule<ModuleHealthRegenerationUnit> module, Player player) {
        if (player.getHealth() < player.getMaxHealth() && module.hasEnoughEnergy(MoreModulesConfig.config.mekaEnergyUsageHealthRegeneration.get())) {
            float health = ((float) module.getInstalledCount() / module.getData().getMaxStackSize()) * player.getMaxHealth();
            player.heal(health);
            module.useEnergy(player,MoreModulesConfig.config.mekaEnergyUsageHealthRegeneration.get());
        }
    }
}
