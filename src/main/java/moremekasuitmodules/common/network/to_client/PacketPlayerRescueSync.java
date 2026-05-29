package moremekasuitmodules.common.network.to_client;

import io.netty.buffer.ByteBuf;
import mekanism.common.PacketHandler;
import moremekasuitmodules.common.MoreMekaSuitModules;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketPlayerRescueSync implements IMessageHandler<PacketPlayerRescueSync.Message, IMessage> {

    @Override
    public IMessage onMessage(Message message, MessageContext context) {
        PacketHandler.handlePacket(() -> MoreMekaSuitModules.proxy.handlePlayerRescueSync(message.entityId, message.health), PacketHandler.getPlayer(context));
        return null;
    }

    public static class Message implements IMessage {

        private int entityId;
        private float health;

        public Message() {
        }

        public Message(int entityId, float health) {
            this.entityId = entityId;
            this.health = health;
        }

        @Override
        public void fromBytes(ByteBuf dataStream) {
            entityId = dataStream.readInt();
            health = dataStream.readFloat();
        }

        @Override
        public void toBytes(ByteBuf dataStream) {
            dataStream.writeInt(entityId);
            dataStream.writeFloat(health);
        }
    }
}
