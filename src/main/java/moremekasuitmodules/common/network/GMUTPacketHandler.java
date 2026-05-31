package moremekasuitmodules.common.network;

import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.network.to_client.PacketColoredLightningRender;
import moremekasuitmodules.common.network.to_client.PacketImpactWave;
import moremekasuitmodules.common.network.to_client.PacketOreMiningWave;
import moremekasuitmodules.common.network.to_client.PacketPlayerRescueSync;
import moremekasuitmodules.common.network.to_client.PacketOreVisualRemove;
import moremekasuitmodules.common.network.to_client.PacketOreVisualScan;
import moremekasuitmodules.common.network.to_server.PacketSwitchVerticalSpeedPacket;
import moremekasuitmodules.common.network.to_server.PacketSwitchVerticalSpeedPacket.SwitchVerticalSpeedPacketMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class GMUTPacketHandler {

    public final SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel(MoreMekaSuitModules.MODID);


    public void initialize() {
        netHandler.registerMessage(PacketSwitchVerticalSpeedPacket.class, SwitchVerticalSpeedPacketMessage.class, 0, Side.SERVER);
        netHandler.registerMessage(PacketOreVisualScan.class, PacketOreVisualScan.Message.class, 1, Side.CLIENT);
        netHandler.registerMessage(PacketOreVisualRemove.class, PacketOreVisualRemove.Message.class, 2, Side.CLIENT);
        netHandler.registerMessage(PacketOreMiningWave.class, PacketOreMiningWave.Message.class, 3, Side.CLIENT);
        netHandler.registerMessage(PacketPlayerRescueSync.class, PacketPlayerRescueSync.Message.class, 4, Side.CLIENT);
        netHandler.registerMessage(PacketColoredLightningRender.class, PacketColoredLightningRender.Message.class, 5, Side.CLIENT);
        netHandler.registerMessage(PacketImpactWave.class, PacketImpactWave.Message.class, 6, Side.CLIENT);
    }

    public void sendToServer(IMessage message) {
        netHandler.sendToServer(message);
    }

    public void sendTo(IMessage message, EntityPlayerMP player) {
        netHandler.sendTo(message, player);
    }

    public void sendToAllTracking(IMessage message, Entity entity) {
        netHandler.sendToAllTracking(message, entity);
    }

    public void sendToAllTracking(IMessage message, int dimension, double x, double y, double z) {
        netHandler.sendToAllTracking(message, new TargetPoint(dimension, x, y, z, 1));
    }

}
