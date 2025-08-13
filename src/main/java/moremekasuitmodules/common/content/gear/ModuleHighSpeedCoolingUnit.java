package moremekasuitmodules.common.content.gear;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNotNullByDefault
public class ModuleHighSpeedCoolingUnit implements ICustomModule<ModuleHighSpeedCoolingUnit> {

    @Override
    public void tickServer(IModule<ModuleHighSpeedCoolingUnit> module, IModuleContainer moduleContainer, ItemStack item, Player player) {
        List<ItemStack> itemStack = new ArrayList<>(player.getInventory().items);
        List<ItemStack> NeedToCoolDown = new ArrayList<>();
        for (ItemStack stack : itemStack) {
            if (!stack.isEmpty() && player.getCooldowns().isOnCooldown(stack.getItem())) {
                NeedToCoolDown.add(stack);
            }
        }
        if (!NeedToCoolDown.isEmpty()) {
            for (int i = 0; i < module.getInstalledCount(); i++) {
                player.getCooldowns().tick();
            }
        }
    }
}
