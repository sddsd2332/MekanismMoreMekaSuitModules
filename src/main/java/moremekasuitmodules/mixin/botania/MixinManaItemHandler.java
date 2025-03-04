package moremekasuitmodules.mixin.botania;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.ManaItemHandler;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Mixin(ManaItemHandler.class)
public abstract class MixinManaItemHandler {

    @Shadow(remap = false)
    public static List<ItemStack> getManaItems(EntityPlayer player) {
        return null;
    }

    @Shadow(remap = false)
    public static Map<Integer, ItemStack> getManaBaubles(EntityPlayer player) {
        return null;
    }


    /**
     * @param manaToSend How much mana is to be sent.
     * @param add        If true, the mana will be added from the target item. Set to false to just check.
     * @return If an item received the mana sent.
     * @author sddsd2332
     * @reason 修复添加mana超出问题
     * <p>
     * Dispatches an exact amount of mana to items in a given player's inventory. Note that this method
     * does not automatically remove mana from the item which is exporting.
     */
    @Overwrite(remap = false)
    public static boolean dispatchManaExact(ItemStack stack, EntityPlayer player, int manaToSend, boolean add) {
        if (stack.isEmpty()) {
            return false;
        }
        List<ItemStack> items = getManaItems(player);
        if (items != null) {
            for (ItemStack stackInSlot : items) {
                if (stackInSlot == stack) {
                    continue;
                }
                if (stackInSlot.getItem() instanceof IManaItem manaItemSlot) {
                    if (manaItemSlot.getMana(stackInSlot) + manaToSend <= manaItemSlot.getMaxMana(stackInSlot) && manaItemSlot.canReceiveManaFromItem(stackInSlot, stack)) {
                        if (stack.getItem() instanceof IManaItem item && !item.canExportManaToItem(stack, stackInSlot)) {
                            continue;
                        }
                        if (add) {
                            if (getNeedMana(manaItemSlot, stackInSlot) < manaToSend) {
                                manaToSend = getNeedMana(manaItemSlot, stackInSlot);
                            }
                            manaItemSlot.addMana(stackInSlot, manaToSend);
                        }
                        return true;
                    }
                }
            }
        }
        Map<Integer, ItemStack> baubles = getManaBaubles(player);
        if (baubles != null) {
            for (Entry<Integer, ItemStack> entry : baubles.entrySet()) {
                ItemStack stackInSlot = entry.getValue();
                if (stackInSlot == stack) {
                    continue;
                }
                if (stackInSlot.getItem() instanceof IManaItem manaItemSlot) {
                    if (manaItemSlot.getMana(stackInSlot) + manaToSend <= manaItemSlot.getMaxMana(stackInSlot) && manaItemSlot.canReceiveManaFromItem(stackInSlot, stack)) {
                        if (stack.getItem() instanceof IManaItem item && !item.canExportManaToItem(stack, stackInSlot)) {
                            continue;
                        }
                        if (add) {
                            if (getNeedMana(manaItemSlot, stackInSlot) < manaToSend) {
                                manaToSend = getNeedMana(manaItemSlot, stackInSlot);
                            }
                            manaItemSlot.addMana(stackInSlot, manaToSend);
                        }
                        BotaniaAPI.internalHandler.sendBaubleUpdatePacket(player, entry.getKey());
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Unique
    private static int getNeedMana(IManaItem manaItem, ItemStack stack) {
        return manaItem.getMaxMana(stack) - manaItem.getMana(stack);
    }
}
