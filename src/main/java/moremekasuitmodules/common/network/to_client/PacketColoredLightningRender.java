package moremekasuitmodules.common.network.to_client;

import io.netty.buffer.ByteBuf;
import mekanism.common.PacketHandler;
import moremekasuitmodules.common.MoreMekaSuitModules;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketColoredLightningRender implements IMessageHandler<PacketColoredLightningRender.Message, IMessage> {

    @Override
    public IMessage onMessage(Message message, MessageContext context) {
        PacketHandler.handlePacket(() -> MoreMekaSuitModules.proxy.handleColoredLightning(message.renderer, message.start, message.end, message.segments, message.color), PacketHandler.getPlayer(context));
        return null;
    }

    public static class Message implements IMessage {

        private int renderer;
        private Vec3d start = Vec3d.ZERO;
        private Vec3d end = Vec3d.ZERO;
        private int segments;
        private int color;

        public Message() {
        }

        public Message(int renderer, Vec3d start, Vec3d end, int segments, int color) {
            this.renderer = renderer;
            this.start = start;
            this.end = end;
            this.segments = segments;
            this.color = color;
        }

        @Override
        public void fromBytes(ByteBuf dataStream) {
            renderer = dataStream.readInt();
            start = new Vec3d(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
            end = new Vec3d(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
            segments = dataStream.readInt();
            color = dataStream.readInt();
        }

        @Override
        public void toBytes(ByteBuf dataStream) {
            dataStream.writeInt(renderer);
            dataStream.writeDouble(start.x);
            dataStream.writeDouble(start.y);
            dataStream.writeDouble(start.z);
            dataStream.writeDouble(end.x);
            dataStream.writeDouble(end.y);
            dataStream.writeDouble(end.z);
            dataStream.writeInt(segments);
            dataStream.writeInt(color);
        }
    }
}
