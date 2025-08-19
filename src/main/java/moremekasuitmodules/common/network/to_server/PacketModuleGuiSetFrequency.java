package moremekasuitmodules.common.network.to_server;

import mekanism.api.security.ISecurityUtils;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.lib.frequency.Frequency;
import mekanism.common.lib.frequency.Frequency.FrequencyIdentity;
import mekanism.common.lib.frequency.FrequencyManager;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.lib.frequency.IFrequencyItem;
import mekanism.common.network.IMekanismPacket;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class PacketModuleGuiSetFrequency<FREQ extends Frequency> implements IMekanismPacket {


    private final FrequencyType<FREQ> type;
    private final FrequencyUpdate updateType;
    private final FrequencyIdentity data;

    private PacketModuleGuiSetFrequency(FrequencyUpdate updateType, FrequencyType<FREQ> type, FrequencyIdentity data) {
        this.updateType = updateType;
        this.type = type;
        this.data = data;
    }


    public static <FREQ extends Frequency> PacketModuleGuiSetFrequency<FREQ> create(FrequencyUpdate updateType, FrequencyType<FREQ> type, FrequencyIdentity data) {
        return new PacketModuleGuiSetFrequency<>(updateType, type, data);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player == null) {
            return;
        }
        ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
        if (stack.getItem() instanceof IModuleContainerItem module && module.isModuleEnabled(stack, MekaSuitMoreModules.QIO_WIRELESS_UNIT)) {
            if (ISecurityUtils.INSTANCE.canAccess(player, stack) && stack.getItem() instanceof IFrequencyItem item) {
                FrequencyManager<FREQ> manager = type.getManager(data, player.getUUID());
                if (updateType == FrequencyUpdate.SET_ITEM) {
                    //Note: We don't bother validating if the frequency is public or not here, as if it isn't then
                    // a new private frequency will just be created for the player who sent a packet they shouldn't
                    // have been able to send due to not knowing what private frequencies exist for other players
                    item.setFrequency(stack, manager.getOrCreateFrequency(data, player.getUUID()));
                } else if (updateType == FrequencyUpdate.REMOVE_ITEM) {
                    if (manager.remove(data.key(), player.getUUID())) {
                        FrequencyIdentity current = item.getFrequencyIdentity(stack);
                        if (current != null && current.equals(data)) {
                            //If the frequency we are removing matches the stored frequency set it to nothing
                            item.setFrequency(stack, null);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(updateType);
        type.write(buffer);
        type.getIdentitySerializer().write(buffer, data);
    }

    public static <FREQ extends Frequency> PacketModuleGuiSetFrequency<FREQ> decode(FriendlyByteBuf buffer) {
        FrequencyUpdate updateType = buffer.readEnum(FrequencyUpdate.class);
        FrequencyType<FREQ> type = FrequencyType.load(buffer);
        FrequencyIdentity data = type.getIdentitySerializer().read(buffer);
        return new PacketModuleGuiSetFrequency<>(updateType, type, data);
    }


    public enum FrequencyUpdate {
        SET_ITEM,
        REMOVE_ITEM;

    }
}
