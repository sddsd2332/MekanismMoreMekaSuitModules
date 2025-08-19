package moremekasuitmodules.common.network;

import mekanism.common.network.BasePacketHandler;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.network.to_server.PacketModuleGuiButtonPress;
import moremekasuitmodules.common.network.to_server.PacketModuleGuiSetFrequency;
import moremekasuitmodules.common.network.to_server.PacketOpenModuleQIO;
import net.minecraftforge.network.simple.SimpleChannel;

public class MoreMekaSuitModulesPacketHandler extends BasePacketHandler {

    private final SimpleChannel netHandler = createChannel(MoreMekaSuitModules.rl(MoreMekaSuitModules.MODID), MoreMekaSuitModules.instance.versionNumber);

    @Override
    protected SimpleChannel getChannel() {
        return netHandler;
    }

    @Override
    public void initialize() {
        registerClientToServer(PacketOpenModuleQIO.class, PacketOpenModuleQIO::decode);
        registerClientToServer(PacketModuleGuiButtonPress.class, PacketModuleGuiButtonPress::decode);
        registerClientToServer(PacketModuleGuiSetFrequency.class, PacketModuleGuiSetFrequency::decode);
    }
}
