package moremekasuitmodules.common.network.to_server;

import mekanism.common.MekanismLang;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.item.interfaces.IGuiItem;
import mekanism.common.network.IMekanismPacket;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesContainerTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.BiFunction;

public class PacketModuleGuiButtonPress implements IMekanismPacket {

    private ClickedItemButton itemButton;
    private InteractionHand hand;

    public PacketModuleGuiButtonPress(ClickedItemButton buttonClicked, InteractionHand hand) {
        this.itemButton = buttonClicked;
        this.hand = hand;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player == null) {
            return;
        }
        ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
        if (stack.getItem() instanceof IModuleContainerItem item) {
            if (item.isModuleEnabled(stack, MekaSuitMoreModules.QIO_WIRELESS_UNIT)) {
                if (stack.getItem() instanceof IGuiItem) {
                    MenuProvider provider = itemButton.getProvider(stack, hand);
                    if (provider != null) {
                        NetworkHooks.openScreen(player, provider, buf -> {
                            buf.writeNullable(hand, FriendlyByteBuf::writeEnum);
                            buf.writeItem(stack);
                        });
                    }
                }
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(itemButton);
        buffer.writeNullable(hand, FriendlyByteBuf::writeEnum);
    }

    public static PacketModuleGuiButtonPress decode(FriendlyByteBuf buffer) {
        return new PacketModuleGuiButtonPress(buffer.readEnum(ClickedItemButton.class), buffer.readNullable(b -> b.readEnum(InteractionHand.class)));
    }

    public enum ClickedItemButton {
        BACK_BUTTON((stack, hand) -> {
            if (stack.getItem() instanceof IGuiItem guiItem) {
                return guiItem.getContainerType().getProvider(stack.getHoverName(), hand, stack);
            }
            return null;
        }),
        QIO_FREQUENCY_SELECT((stack, hand) -> MekaSuitMoreModulesContainerTypes.MODULE_QIO_FREQUENCY_SELECT.getProvider(MekanismLang.QIO_FREQUENCY_SELECT, hand, stack));

        private final BiFunction<ItemStack, InteractionHand, MenuProvider> providerFromItem;

        ClickedItemButton(BiFunction<ItemStack, InteractionHand, MenuProvider> providerFromItem) {
            this.providerFromItem = providerFromItem;
        }

        public MenuProvider getProvider(ItemStack stack, InteractionHand hand) {
            return providerFromItem.apply(stack, hand);
        }
    }
}
