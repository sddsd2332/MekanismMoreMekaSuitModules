package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gas.GasStack;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.common.MekanismFluids;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.inventory.slot.gas.GasInventorySlot;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.config.MoreModulesConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

@ParametersAreNotNullByDefault
public class ModuleOxygenSupplyUnit implements ICustomModule<ModuleOxygenSupplyUnit> {

    public static final int OXYGEN_TRANSFER_RATE = 256;

    @Override
    public void tickServer(IModule<ModuleOxygenSupplyUnit> module, EntityPlayer player) {
        int usage = getOxygenUsage(module.getContainer());
        if (usage > 0) {
            GasInventorySlot.useGas(module.getContainer(), MekanismFluids.Oxygen, usage);
        }
    }

    public static int getOxygenRate(ItemStack stack) {
        return OXYGEN_TRANSFER_RATE;
    }

    public static int getOxygenCapacity(ItemStack stack) {
        return isOxygenModuleEnabled(stack) ? MoreModulesConfig.current().config.mekaSuitOxygenCapacity.val() : 0;
    }

    public static int getOxygenUsage(ItemStack stack) {
        return isOxygenModuleEnabled(stack) ? MoreModulesConfig.current().config.mekaSuitOxygenUsage.val() : 0;
    }

    public static boolean isOxygenModuleEnabled(ItemStack stack) {
        return stack.getItem() instanceof IModuleContainerItem item && item.isModuleEnabled(stack, MekaSuitMoreModules.OXYGEN_SUPPLY_UNIT);
    }

    public static boolean hasOxygen(ItemStack stack) {
        GasStack gasStack = GasInventorySlot.getContainedGas(stack, MekanismFluids.Oxygen);
        return gasStack != null && gasStack.amount > 0;
    }
}
