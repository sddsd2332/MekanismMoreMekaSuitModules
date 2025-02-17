package moremekasuitmodules.common.network;

import moremekasuitmodules.common.MoreMekaSuitModules;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import moremekasuitmodules.common.network.to_server.PacketSwitchVerticalSpeedPacket;
import moremekasuitmodules.common.network.to_server.PacketSwitchVerticalSpeedPacket.SwitchVerticalSpeedPacketMessage;
public class GMUTPacketHandler {

    public final SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel(MoreMekaSuitModules.MODID);


    public void initialize() {
        netHandler.registerMessage(PacketSwitchVerticalSpeedPacket.class, SwitchVerticalSpeedPacketMessage.class, 0, Side.SERVER);
    }

    public void sendToServer(IMessage message) {
        netHandler.sendToServer(message);
    }

}
