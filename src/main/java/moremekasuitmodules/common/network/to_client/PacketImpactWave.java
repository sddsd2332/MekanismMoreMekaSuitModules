package moremekasuitmodules.common.network.to_client;

import io.netty.buffer.ByteBuf;
import mekanism.common.PacketHandler;
import moremekasuitmodules.common.MoreMekaSuitModules;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketImpactWave implements IMessageHandler<PacketImpactWave.Message, IMessage> {

    @Override
    public IMessage onMessage(Message message, MessageContext context) {
        PacketHandler.handlePacket(() -> MoreMekaSuitModules.proxy.handleImpactWave(message.x, message.y, message.z, message.radius, message.color, message.durationTicks, message.sourceEntityId, message.fallDistance), PacketHandler.getPlayer(context));
        return null;
    }

    public static class Message implements IMessage {

        private double x;
        private double y;
        private double z;
        private float radius;
        private int color;
        private int durationTicks;
        private int sourceEntityId;
        private float fallDistance;

        public Message() {
        }

        public Message(double x, double y, double z, float radius, int color, int durationTicks, int sourceEntityId, float fallDistance) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.radius = radius;
            this.color = color;
            this.durationTicks = durationTicks;
            this.sourceEntityId = sourceEntityId;
            this.fallDistance = fallDistance;
        }

        @Override
        public void fromBytes(ByteBuf dataStream) {
            x = dataStream.readDouble();
            y = dataStream.readDouble();
            z = dataStream.readDouble();
            radius = dataStream.readFloat();
            color = dataStream.readInt();
            durationTicks = dataStream.readInt();
            sourceEntityId = dataStream.readInt();
            fallDistance = dataStream.readFloat();
        }

        @Override
        public void toBytes(ByteBuf dataStream) {
            dataStream.writeDouble(x);
            dataStream.writeDouble(y);
            dataStream.writeDouble(z);
            dataStream.writeFloat(radius);
            dataStream.writeInt(color);
            dataStream.writeInt(durationTicks);
            dataStream.writeInt(sourceEntityId);
            dataStream.writeFloat(fallDistance);
        }
    }
}
