package moremekasuitmodules.common.network.to_server;

import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.item.interfaces.IGuiItem;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.util.SecurityUtils;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class PacketOpenModuleQIO implements IMekanismPacket {

    private final EquipmentSlot slot;

    public PacketOpenModuleQIO(EquipmentSlot slot) {
        this.slot = slot;
    }


    @Override
    public void handle(NetworkEvent.Context context) {
        Player player = context.getSender();
        if (player != null) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.getItem() instanceof IModuleContainerItem item) {
                if (item.isModuleEnabled(stack, MekaSuitMoreModules.QIO_WIRELESS_UNIT)) {
                    if (stack.getItem() instanceof IGuiItem guiItem) {
                        SecurityUtils.get().claimOrOpenGui(player.level(), player, null, guiItem.getContainerType()::tryOpenGui);
                    }
                }
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(slot);
    }

    public static PacketOpenModuleQIO decode(FriendlyByteBuf buffer) {
        return new PacketOpenModuleQIO(buffer.readEnum(EquipmentSlot.class));
    }

}
