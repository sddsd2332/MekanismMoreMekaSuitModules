package moremekasuitmodules.common.network.to_client;

import io.netty.buffer.ByteBuf;
import mekanism.common.PacketHandler;
import moremekasuitmodules.common.MoreMekaSuitModules;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PacketOreVisualScan implements IMessageHandler<PacketOreVisualScan.Message, IMessage> {

    @Override
    public IMessage onMessage(Message message, MessageContext context) {
        PacketHandler.handlePacket(() -> MoreMekaSuitModules.proxy.handleOreVisualScan(message.center, message.entries), PacketHandler.getPlayer(context));
        return null;
    }

    public static class Message implements IMessage {

        private List<OreEntry> entries = new ArrayList<>();
        private BlockPos center = BlockPos.ORIGIN;

        public Message() {
        }

        public Message(BlockPos center, List<OreEntry> entries) {
            this.center = center;
            this.entries = entries;
        }

        @Override
        public void fromBytes(ByteBuf dataStream) {
            center = new BlockPos(dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
            int size = dataStream.readInt();
            entries = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                BlockPos pos = new BlockPos(dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
                int color = dataStream.readInt();
                String oreName = readString(dataStream);
                String displayName = readString(dataStream);
                entries.add(new OreEntry(pos, oreName, displayName, color));
            }
        }

        @Override
        public void toBytes(ByteBuf dataStream) {
            dataStream.writeInt(center.getX());
            dataStream.writeInt(center.getY());
            dataStream.writeInt(center.getZ());
            dataStream.writeInt(entries.size());
            for (OreEntry entry : entries) {
                dataStream.writeInt(entry.pos.getX());
                dataStream.writeInt(entry.pos.getY());
                dataStream.writeInt(entry.pos.getZ());
                dataStream.writeInt(entry.color);
                writeString(dataStream, entry.oreName);
                writeString(dataStream, entry.displayName);
            }
        }

        private static String readString(ByteBuf dataStream) {
            int length = dataStream.readShort();
            byte[] bytes = new byte[length];
            dataStream.readBytes(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }

        private static void writeString(ByteBuf dataStream, String value) {
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            dataStream.writeShort(bytes.length);
            dataStream.writeBytes(bytes);
        }
    }

    public static class OreEntry {
        private final BlockPos pos;
        private final String oreName;
        private final String displayName;
        private final int color;

        public OreEntry(BlockPos pos, String oreName, int color) {
            this(pos, oreName, oreName, color);
        }

        public OreEntry(BlockPos pos, String oreName, String displayName, int color) {
            this.pos = pos;
            this.oreName = oreName;
            this.displayName = displayName;
            this.color = color;
        }

        public BlockPos getPos() {
            return pos;
        }

        public String getOreName() {
            return oreName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getColor() {
            return color;
        }
    }
}
