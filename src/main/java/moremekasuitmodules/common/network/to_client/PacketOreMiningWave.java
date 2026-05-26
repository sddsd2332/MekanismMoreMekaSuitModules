package moremekasuitmodules.common.network.to_client;

import io.netty.buffer.ByteBuf;
import mekanism.common.PacketHandler;
import moremekasuitmodules.common.MoreMekaSuitModules;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOreMiningWave implements IMessageHandler<PacketOreMiningWave.Message, IMessage> {

    @Override
    public IMessage onMessage(Message message, MessageContext context) {
        PacketHandler.handlePacket(() -> MoreMekaSuitModules.proxy.handleOreMiningWave(message.center, message.radius, message.color, message.durationTicks), PacketHandler.getPlayer(context));
        return null;
    }

    public static class Message implements IMessage {

        private BlockPos center = BlockPos.ORIGIN;
        private int radius;
        private int color;
        private int durationTicks;

        public Message() {
        }

        public Message(BlockPos center, int radius, int color, int durationTicks) {
            this.center = center;
            this.radius = radius;
            this.color = color;
            this.durationTicks = durationTicks;
        }

        @Override
        public void fromBytes(ByteBuf dataStream) {
            center = new BlockPos(dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
            radius = dataStream.readInt();
            color = dataStream.readInt();
            durationTicks = dataStream.readInt();
        }

        @Override
        public void toBytes(ByteBuf dataStream) {
            dataStream.writeInt(center.getX());
            dataStream.writeInt(center.getY());
            dataStream.writeInt(center.getZ());
            dataStream.writeInt(radius);
            dataStream.writeInt(color);
            dataStream.writeInt(durationTicks);
        }
    }
}
