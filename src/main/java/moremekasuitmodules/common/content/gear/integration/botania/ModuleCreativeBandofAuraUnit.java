package moremekasuitmodules.common.content.gear.integration.botania;

import baubles.api.BaublesApi;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.IItemHandler;
import vazkii.botania.api.mana.IManaItem;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNotNullByDefault
public class ModuleCreativeBandofAuraUnit implements ICustomModule<ModuleCreativeBandofAuraUnit> {

    @Override
    public void tickServer(IModule<ModuleCreativeBandofAuraUnit> module, EntityPlayer player) {
        if (Loader.isModLoaded("botania")) {
            addMana(module.getContainer(), player);
        }
    }

    @Optional.Method(modid = "botania")
    private void addMana(ItemStack stack, EntityPlayer player) {
        List<ItemStack> getManaItems = new ArrayList<>();
        for (ItemStack itemStack : player.inventory.mainInventory) {
            if (itemStack.getItem() instanceof IManaItem item) {
                if (item.getMana(itemStack) != item.getMaxMana(itemStack) && item.canReceiveManaFromItem(itemStack, stack)) {
                    getManaItems.add(itemStack);
                }
            }
        }
        for (ItemStack itemStack : player.inventory.offHandInventory) {
            if (itemStack.getItem() instanceof IManaItem item) {
                if (item.getMana(itemStack) != item.getMaxMana(itemStack) && item.canReceiveManaFromItem(itemStack, stack)) {
                    getManaItems.add(itemStack);
                }
            }
        }
        for (ItemStack itemStack : player.inventory.armorInventory) {
            if (itemStack.getItem() instanceof IManaItem item) {
                if (item.getMana(itemStack) != item.getMaxMana(itemStack) && item.canReceiveManaFromItem(itemStack, stack)) {
                    getManaItems.add(itemStack);
                }
            }
        }
        IItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); i++) {
            ItemStack itemStack = baubles.getStackInSlot(i);
            if (itemStack.getItem() instanceof IManaItem item) {
                if (item.getMana(itemStack) != item.getMaxMana(itemStack) && item.canReceiveManaFromItem(itemStack, stack)) {
                    getManaItems.add(itemStack);
                }
            }
        }

        if (!getManaItems.isEmpty()) {
            for (ItemStack stackInSlot : getManaItems) {
                if (stackInSlot.getItem() instanceof IManaItem item) {
                    if (item.canReceiveManaFromItem(stackInSlot, stack)) {
                        int in = insert(stackInSlot, getNeeded(stackInSlot), true);
                        if (in == 0) {
                            break;
                        }
                    }
                }
            }
        }
    }

    @Optional.Method(modid = "botania")
    int insert(ItemStack stack, int amount, boolean action) {
        if (stack.getItem() instanceof IManaItem item) {
            if (amount == 0) {
                //"Fail quick" if the given amount is empty
                return amount;
            }
            int needed = getNeeded(stack);
            if (needed == 0) {
                //Fail if we are a full container
                return amount;
            }
            int toAdd = Math.min(amount, needed);
            if (toAdd != 0 && action) {
                //If we want to actually insert the energy, then update the current energy
                // Note: this also will mark that the contents changed
                item.addMana(stack, toAdd);
            }
            return amount - toAdd;
        }
        return 0;
    }

    @Optional.Method(modid = "botania")
    int getNeeded(ItemStack stack) {
        if (stack.getItem() instanceof IManaItem item) {
            return item.getMaxMana(stack) - item.getMana(stack);
        }
        return 0;
    }
}
