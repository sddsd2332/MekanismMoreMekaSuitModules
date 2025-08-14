package moremekasuitmodules.client;

import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.config.MoreModulesConfig;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientTickHandler {

    public static Minecraft minecraft = Minecraft.getInstance();

    @SubscribeEvent
    public void GuiScreenEvent(ScreenEvent.Opening event) {
        if (MoreModulesConfig.config.isLoaded() && !MoreModulesConfig.config.mekaSuitOverloadProtection.get()) {
            return;
        }
        if (event.getNewScreen() instanceof DeathScreen) {
            if (minecraft.player instanceof LocalPlayer) {
                ItemStack head = minecraft.player.getItemBySlot(EquipmentSlot.HEAD);
                if (!minecraft.player.isAlive()) {
                    if (head.getItem() instanceof IModuleContainerItem item) {
                        if (item.isModuleEnabled(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT) || item.isModuleEnabled(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT) || item.hasModule(head, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT)) {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }
}
