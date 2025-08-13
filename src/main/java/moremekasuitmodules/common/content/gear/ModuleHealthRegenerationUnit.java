package moremekasuitmodules.common.content.gear;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleContainer;
import moremekasuitmodules.common.config.MoreModulesConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@ParametersAreNotNullByDefault
public class ModuleHealthRegenerationUnit implements ICustomModule<ModuleHealthRegenerationUnit> {

    @Override
    public void tickServer(IModule<ModuleHealthRegenerationUnit> module, IModuleContainer moduleContainer, ItemStack stack, Player player) {
        if (player.getHealth() < player.getMaxHealth() && module.hasEnoughEnergy(stack,MoreModulesConfig.config.mekaEnergyUsageHealthRegeneration.get())) {
            float health = ((float) module.getInstalledCount() / module.getData().getMaxStackSize()) * player.getMaxHealth();
            player.heal(health);
            module.useEnergy(player,stack,MoreModulesConfig.config.mekaEnergyUsageHealthRegeneration.get());
        }
    }
}
