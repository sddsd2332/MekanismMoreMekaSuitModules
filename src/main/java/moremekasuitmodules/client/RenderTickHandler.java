package moremekasuitmodules.client;

import mekanism.api.EnumColor;
import mekanism.api.gear.IModule;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.util.LangUtils;
import moremekasuitmodules.common.MekaSuitMoreModules;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTickHandler {

    @SubscribeEvent
    public void filterTooltips(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IModuleContainerItem item) {
            IModule<?> module = item.getModule(stack, MekaSuitMoreModules.CHAOS_RESISTANCE_UNIT);
            if (module != null && module.isEnabled()) {
                event.getToolTip().add(EnumColor.ORANGE + " +" + module.getInstalledCount() + "% " + LangUtils.localize("tooltip.chaos.resistance"));
            }
        }
    }
}
