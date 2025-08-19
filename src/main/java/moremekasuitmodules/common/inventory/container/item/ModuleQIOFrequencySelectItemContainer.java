package moremekasuitmodules.common.inventory.container.item;

import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.inventory.container.IEmptyContainer;
import mekanism.common.inventory.container.item.FrequencyItemContainer;
import mekanism.common.lib.frequency.FrequencyType;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesContainerTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModuleQIOFrequencySelectItemContainer extends FrequencyItemContainer<QIOFrequency> implements IEmptyContainer {

    public ModuleQIOFrequencySelectItemContainer(int id, Inventory inv, InteractionHand hand, ItemStack stack) {
        super(MekaSuitMoreModulesContainerTypes.MODULE_QIO_FREQUENCY_SELECT, id, inv, hand, stack);
    }

    @Override
    public FrequencyType<QIOFrequency> getFrequencyType() {
        return FrequencyType.QIO;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return !this.stack.isEmpty() && player.getItemBySlot(EquipmentSlot.HEAD).is(stack.getItem()) && stack.getItem() instanceof IModuleContainerItem item && item.isModuleEnabled(stack, MekaSuitMoreModules.QIO_WIRELESS_UNIT);
    }
}
