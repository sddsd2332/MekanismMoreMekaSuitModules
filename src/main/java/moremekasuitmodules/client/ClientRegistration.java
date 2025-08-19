package moremekasuitmodules.client;

import mekanism.client.ClientRegistrationUtil;
import moremekasuitmodules.client.gui.qio.GuiModuleQIOItemFrequencySelect;
import moremekasuitmodules.client.gui.qio.GuiPortableModuleQIODashboard;
import moremekasuitmodules.client.key.MoreMekaSuitModulesKeyHandler;
import moremekasuitmodules.client.render.hud.MoreMekaSuitModulesHUD;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesContainerTypes;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = MoreMekaSuitModules.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {


    private ClientRegistration() {
    }

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
    }


    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "shield_hud", MoreMekaSuitModulesHUD.INSTANCE);
    }

    @SubscribeEvent
    public static void registerKeybindings(RegisterKeyMappingsEvent event) {
        MoreMekaSuitModulesKeyHandler.registerKeybindings(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerContainers(RegisterEvent event) {
        event.register(Registries.MENU, helper -> {
            ClientRegistrationUtil.registerScreen(MekaSuitMoreModulesContainerTypes.MODULE_QIO_FREQUENCY_SELECT, GuiModuleQIOItemFrequencySelect::new);
            ClientRegistrationUtil.registerScreen(MekaSuitMoreModulesContainerTypes.MODULE_QIO, GuiPortableModuleQIODashboard::new);
        });
    }
}
