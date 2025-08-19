package moremekasuitmodules.client.key;

import mekanism.client.ClientRegistrationUtil;
import mekanism.client.key.MekKeyBindingBuilder;
import mekanism.client.sound.SoundHandler;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.registries.MekanismSounds;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.MoreMekaSuitModulesLang;
import moremekasuitmodules.common.network.to_server.PacketOpenModuleQIO;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class MoreMekaSuitModulesKeyHandler {

    public static final KeyMapping OpenQIOModule = new MekKeyBindingBuilder().description(MoreMekaSuitModulesLang.MODULE_QIO_OPEN).conflictInGame().keyCode(GLFW.GLFW_KEY_Y)
            .onKeyDown((kb, isRepeat) -> OpenTheUnit()).build();

    public static void registerKeybindings(RegisterKeyMappingsEvent event) {
        ClientRegistrationUtil.registerKeyBindings(event, OpenQIOModule);
    }

    public static void OpenTheUnit() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
            if (stack.getItem() instanceof IModuleContainerItem item) {
                if (item.isModuleEnabled(stack, MekaSuitMoreModules.QIO_WIRELESS_UNIT)) {
                    MoreMekaSuitModules.packetHandler().sendToServer(new PacketOpenModuleQIO(EquipmentSlot.HEAD));
                    SoundHandler.playSound(MekanismSounds.HYDRAULIC);
                }
            }
        }
    }
}
