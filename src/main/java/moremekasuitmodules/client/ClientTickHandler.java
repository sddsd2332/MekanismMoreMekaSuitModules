package moremekasuitmodules.client;

import mekanism.api.gear.IModule;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.ModuleHelper;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.ModuleGravitationalModulatingAdditionalUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientTickHandler {

    public static Minecraft minecraft = Minecraft.getMinecraft();


    @SubscribeEvent(priority = EventPriority.LOW)
    public void onFOVModifier(FOVUpdateEvent event) {
        EntityPlayer player = event.getEntity();
        IModule<ModuleGravitationalModulatingAdditionalUnit> module = ModuleHelper.get().load(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST), MekaSuitMoreModules.GRAVITATIONAL_MODULATING_ADDITIONAL_UNIT);
        if (module != null && module.isEnabled()) {
            boolean fixFOV = module.getCustomInstance().getFixFOV().get();
            if (fixFOV) {
                event.setNewfov(1.0F);
            }
        }
    }

    @SubscribeEvent
    public void GuiScreenEvent(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiGameOver) {
            if (minecraft.player instanceof EntityPlayerSP) {
                ItemStack head = minecraft.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                if (!minecraft.player.isEntityAlive()) {
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
