package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNotNullByDefault
public class ModularHighSpeedCoolingUnit implements ICustomModule<ModularHighSpeedCoolingUnit> {

    @Override
    public void tickServer(IModule<ModularHighSpeedCoolingUnit> module, EntityPlayer player) {
        List<ItemStack> itemStack = new ArrayList<>(player.inventory.mainInventory);
        List<ItemStack> NeedToCoolDown = new ArrayList<>();
        for (ItemStack stack : itemStack) {
            if (!stack.isEmpty() && player.getCooldownTracker().hasCooldown(stack.getItem())) {
                NeedToCoolDown.add(stack);
            }
        }

        if (!NeedToCoolDown.isEmpty()) {
            for (int i = 0; i < module.getInstalledCount(); i++){
                player.getCooldownTracker().tick();
            }
        }
    }
}

