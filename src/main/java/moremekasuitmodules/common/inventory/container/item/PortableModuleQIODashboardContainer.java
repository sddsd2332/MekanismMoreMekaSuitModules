package moremekasuitmodules.common.inventory.container.item;

import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.qio.IQIOCraftingWindowHolder;
import mekanism.common.inventory.PortableQIODashboardInventory;
import mekanism.common.inventory.container.QIOItemViewerContainer;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesContainerTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

public class PortableModuleQIODashboardContainer extends QIOItemViewerContainer {

    protected final InteractionHand hand;
    protected final ItemStack stack;


    protected PortableModuleQIODashboardContainer(int id, Inventory inv, InteractionHand hand, ItemStack stack, boolean remote, IQIOCraftingWindowHolder craftingWindowHolder) {
        super(MekaSuitMoreModulesContainerTypes.MODULE_QIO, id, inv, remote, craftingWindowHolder);
        this.hand = hand;
        this.stack = stack;
        addSlotsAndOpen();
    }

    public PortableModuleQIODashboardContainer(int id, Inventory inv, InteractionHand hand, ItemStack stack, boolean remote) {
        this(id, inv, hand, stack, remote, new PortableQIODashboardInventory(stack, inv));
    }

    public InteractionHand getHand() {
        return hand;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public PortableModuleQIODashboardContainer recreate() {
        PortableModuleQIODashboardContainer container = new PortableModuleQIODashboardContainer(containerId, inv, hand, stack, true, craftingWindowHolder);
        sync(container);
        return container;
    }

    @Override
    public @Nullable ICapabilityProvider getSecurityObject() {
        return stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return !stack.isEmpty() && player.getItemBySlot(EquipmentSlot.HEAD).is(stack.getItem()) && stack.getItem() instanceof IModuleContainerItem item && item.isModuleEnabled(stack, MekaSuitMoreModules.QIO_WIRELESS_UNIT);
    }
}
