package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasItem;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.common.util.GasUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

@ParametersAreNotNullByDefault
public class ModuleInfiniteGasSupplyUnit implements ICustomModule<ModuleInfiniteGasSupplyUnit> {

    @Override
    public void tickClient(IModule<ModuleInfiniteGasSupplyUnit> module, EntityPlayer player) {
        this.tickServer(module, player);
    }

    @Override
    public void tickServer(IModule<ModuleInfiniteGasSupplyUnit> module, EntityPlayer player) {
        ItemStack stack = module.getContainer();
        if (stack.getItem() instanceof IGasItem gasItem) {
            for (Gas gas : GasRegistry.getRegisteredGasses()) {
                if (gasItem.canReceiveGas(module.getContainer(), gas)) {
                    GasUtils.addGas(stack, new GasStack(gas, gasItem.getMaxGas(stack)));
                    break;
                }

            }
        }
    }


}
