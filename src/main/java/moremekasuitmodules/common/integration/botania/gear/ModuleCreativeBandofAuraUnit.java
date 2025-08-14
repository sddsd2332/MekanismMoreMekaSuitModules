package moremekasuitmodules.common.integration.botania.gear;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.common.Mekanism;
import mekanism.common.integration.curios.CuriosIntegration;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.mana.ManaItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ParametersAreNotNullByDefault
public class ModuleCreativeBandofAuraUnit implements ICustomModule<ModuleCreativeBandofAuraUnit> {

    @Override
    public void tickServer(IModule<ModuleCreativeBandofAuraUnit> module, Player player) {
        addMana(module.getContainer(),player);
    }

    private void addMana(ItemStack stack,Player player) {
        List<ItemStack> getManaItems = new ArrayList<>();
        List<ItemStack> Inventory = new ArrayList<>(player.getInventory().items);
        Inventory.addAll(player.getInventory().armor);
        Inventory.addAll(player.getInventory().offhand);
        if (Mekanism.hooks.CuriosLoaded) {
            getBauble(Inventory, player);
        }
        for (ItemStack manitem : Inventory) {
            Optional<ManaItem> cap = manitem.getCapability(BotaniaForgeCapabilities.MANA_ITEM).resolve();
            if (cap.isPresent()){
                ManaItem item = cap.get();
                if (item.getMana() != item.getMaxMana() && item.canReceiveManaFromItem(stack)){
                    getManaItems.add(manitem);
                }
            }
        }

        if (!getManaItems.isEmpty()) {
            for (ItemStack stackInSlot : getManaItems) {
                Optional<ManaItem> cap = stackInSlot.getCapability(BotaniaForgeCapabilities.MANA_ITEM).resolve();
                if (cap.isPresent()){
                    ManaItem item = cap.get();
                    if (item.canReceiveManaFromItem(stack)){
                        int in = insert(stackInSlot, getNeeded(stackInSlot), true);
                        if (in == 0) {
                            break;
                        }
                    }
                }
            }
        }
    }


    public void getBauble(List<ItemStack> stacks, Player player) {
        Optional<? extends IItemHandler> invOptional = CuriosIntegration.getCuriosInventory(player);
        if (invOptional.isPresent()) {
            IItemHandler inv = invOptional.get();
            for (int i = 0, slots = inv.getSlots(); i < slots; i++) {
                ItemStack stack = inv.getStackInSlot(i);
                stacks.add(stack);
            }
        }
    }


    int insert(ItemStack stack, int amount, boolean action) {
        Optional<ManaItem> cap = stack.getCapability(BotaniaForgeCapabilities.MANA_ITEM).resolve();
        if (cap.isPresent()){
            ManaItem item = cap.get();
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
                item.addMana(toAdd);
            }
            return amount - toAdd;
        }
        return 0;
    }

    int getNeeded(ItemStack stack) {
        Optional<ManaItem> cap = stack.getCapability(BotaniaForgeCapabilities.MANA_ITEM).resolve();
        if (cap.isPresent()){
            ManaItem item = cap.get();
            return item.getMaxMana() - item.getMana();
        }
        return 0;
    }
}
