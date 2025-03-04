package moremekasuitmodules.mixin.appliedenergistics2;

import appeng.client.gui.implementations.GuiCraftingCPU;
import appeng.client.gui.implementations.GuiCraftingStatus;
import appeng.client.gui.widgets.GuiTabButton;
import mekanism.api.gear.IModule;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.content.gear.integration.appliedenergistics2.ModuleSmartWirelessUnit;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiCraftingStatus.class)
public class MixinGuiCraftingStatus extends GuiCraftingCPU {

    @Shadow(remap = false)
    private ItemStack myIcon;

    @Shadow(remap = false)
    private GuiTabButton originalGuiBtn;

    public MixinGuiCraftingStatus(InventoryPlayer inventoryPlayer, Object te) {
        super(inventoryPlayer, te);
    }

    @Inject(method = "initGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"), cancellable = true)
    public void isModule(CallbackInfo ci) {
        if (!this.myIcon.isEmpty()) {
            if (myIcon.getItem() instanceof IModuleContainerItem item) {
                IModule<ModuleSmartWirelessUnit> module = item.getModule(myIcon, MekaSuitMoreModules.SMART_WIRELESS_UNIT);
                if (module != null) {
                    myIcon = module.getData().getStack();
                    this.buttonList.add(this.originalGuiBtn = new GuiTabButton(this.guiLeft + 213, this.guiTop - 4, this.myIcon, module.getData().getRarity().getColor() + myIcon.getDisplayName(), this.itemRender));
                    this.originalGuiBtn.setHideEdge(13);
                    ci.cancel();
                }
            }
        }
    }


}
