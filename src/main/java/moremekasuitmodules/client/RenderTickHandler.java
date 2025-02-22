package moremekasuitmodules.client;

import mekanism.api.EnumColor;
import mekanism.api.gear.IModule;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.util.LangUtils;
import moremekasuitmodules.common.MekaSuitMoreModules;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderTickHandler {

    @SubscribeEvent
    public void addTooltips(ItemTooltipEvent event) {
        List<String> tip = event.getToolTip();
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IModuleContainerItem item) {
            IModule<?> module = item.getModule(stack, MekaSuitMoreModules.CHAOS_RESISTANCE_UNIT);
            if (module != null && module.isEnabled()) {
                //手动匹配位置 非常坏
                String knock = I18n.translateToLocal("attribute.name.generic.knockbackResistance");
                String s = TextFormatting.BLUE + " +1.5 " + knock;
                String s1 = TextFormatting.BLUE + " +3 " + knock;
                String s2 = TextFormatting.BLUE + " +4 " + knock;
                String moduleInstall = EnumColor.ORANGE + " +" + module.getInstalledCount() + "% " + LangUtils.localize("tooltip.chaos.resistance");
                //确保击退抗性下一个是我们这个
                if (tip.contains(s)) {
                    tip.add(tip.indexOf(s) + 1, moduleInstall);
                } else if (tip.contains(s1)) {
                    tip.add(tip.indexOf(s1) + 1, moduleInstall);
                } else if (tip.contains(s2)) {
                    tip.add(tip.indexOf(s2) + 1, moduleInstall);
                }
            }
        }
    }
}
