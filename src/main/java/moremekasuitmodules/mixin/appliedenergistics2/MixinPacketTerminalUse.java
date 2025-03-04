package moremekasuitmodules.mixin.appliedenergistics2;

import appeng.core.sync.AppEngPacket;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.PacketTerminalUse;
import appeng.items.tools.powered.Terminal;
import mekanism.api.gear.IModule;
import mekanism.common.Mekanism;
import mekanism.common.MekanismItems;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.content.gear.integration.appliedenergistics2.ModuleSmartWirelessUnit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static moremekasuitmodules.common.content.gear.integration.appliedenergistics2.ModuleSmartWirelessUnit.WirelessTerminal;
import static moremekasuitmodules.common.content.gear.integration.appliedenergistics2.ModuleSmartWirelessUnit.WirelessTerminal.*;

@Mixin(PacketTerminalUse.class)
public abstract class MixinPacketTerminalUse extends AppEngPacket {


    @Shadow(remap = false)
    abstract void openGui(ItemStack itemStack, int slotIdx, EntityPlayer player, boolean isBauble);

    @Shadow(remap = false)
    Terminal terminal;

    @Inject(method = "serverPacketData", at = @At(value = "HEAD"), remap = false)
    public void serverPacketData(INetworkInfo manager, AppEngPacket packet, EntityPlayer player, CallbackInfo ci) {
        NonNullList<ItemStack> armor = player.inventory.armorInventory;
        for (int i = 0; i < armor.size(); i++) {
            ItemStack ar = armor.get(i);
            if (ar.getItem() instanceof IModuleContainerItem item && ar.getItem() == MekanismItems.MEKASUIT_HELMET && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == MekanismItems.MEKASUIT_HELMET) {
                IModule<ModuleSmartWirelessUnit> module = item.getModule(ar, MekaSuitMoreModules.SMART_WIRELESS_UNIT);
                if (module != null && module.isEnabled()) {
                    WirelessTerminal mode = WirelessTerminalMode(terminal.name());
                    boolean match = module.getCustomInstance().getMatching();
                    if (module.getCustomInstance().getMode() == mode || !match) {
                        if (!match) { //在匹配模式关闭时，确保打开其他gui的时候还是返回原来的界面
                            module.getCustomInstance().changeMode(mode.ordinal());
                        }
                        openGui(ar, 36 + i, player, false);
                        return;
                    }
                }
            }
        }
    }

    @Unique
    private WirelessTerminal WirelessTerminalMode(String value) {
        return switch (value) {
            case "WIRELESS_CRAFTING_TERMINAL" -> WIRELESS_CRAFTING_TERMINAL;
            case "WIRELESS_PATTERN_TERMINAL" -> WIRELESS_PATTERN_TERMINAL;
            case "WIRELESS_FLUID_TERMINAL" -> WIRELESS_FLUID_TERMINAL;
            default -> WIRELESS_TERMINAL;
        };
    }
}
