package moremekasuitmodules.common.network.to_client;

import io.netty.buffer.ByteBuf;
import mekanism.common.PacketHandler;
import moremekasuitmodules.common.MoreMekaSuitModules;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOreVisualRemove implements IMessageHandler<PacketOreVisualRemove.Message, IMessage> {

    @Override
    public IMessage onMessage(Message message, MessageContext context) {
        PacketHandler.handlePacket(() -> MoreMekaSuitModules.proxy.handleOreVisualRemove(message.pos), PacketHandler.getPlayer(context));
        return null;
    }

    public static class Message implements IMessage {

        private BlockPos pos;

        public Message() {
        }

        public Message(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public void fromBytes(ByteBuf dataStream) {
            pos = new BlockPos(dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
        }

        @Override
        public void toBytes(ByteBuf dataStream) {
            dataStream.writeInt(pos.getX());
            dataStream.writeInt(pos.getY());
            dataStream.writeInt(pos.getZ());
        }
    }
}
