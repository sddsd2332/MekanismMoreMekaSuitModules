package moremekasuitmodules.common.network.to_server;

import io.netty.buffer.ByteBuf;
import mekanism.common.PacketHandler;
import mekanism.common.content.gear.Module;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.EntityModuleHelper;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.ModuleGravitationalModulatingAdditionalUnit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import moremekasuitmodules.common.network.to_server.PacketSwitchVerticalSpeedPacket.SwitchVerticalSpeedPacketMessage;

public class PacketSwitchVerticalSpeedPacket implements IMessageHandler<SwitchVerticalSpeedPacketMessage, IMessage> {


    @Override
    public IMessage onMessage(SwitchVerticalSpeedPacketMessage message, MessageContext context) {
        EntityPlayer player = PacketHandler.getPlayer(context);
        if (player == null) {
            return null;
        }
        PacketHandler.handlePacket(() -> {
            Module<ModuleGravitationalModulatingAdditionalUnit> module = EntityModuleHelper.findArmorEnabledModule(player, MekaSuitMoreModules.GRAVITATIONAL_MODULATING_ADDITIONAL_UNIT);
            if (module != null) {
                module.getCustomInstance().changeMode(module,player, module.getContainer(), message.shift, true);
            }
        }, player);
        return null;
    }

    public static class SwitchVerticalSpeedPacketMessage implements IMessage {

        private int shift;

        public SwitchVerticalSpeedPacketMessage() {
        }

        public SwitchVerticalSpeedPacketMessage(int shift) {
            this.shift = shift;
        }

        @Override
        public void fromBytes(ByteBuf dataStream) {
            shift = dataStream.readInt();
        }

        @Override
        public void toBytes(ByteBuf dataStream) {
            dataStream.writeInt(shift);
        }
    }

}
