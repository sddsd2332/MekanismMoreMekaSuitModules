package moremekasuitmodules.mixin.appliedenergistics2;

import appeng.api.AEApi;
import appeng.api.features.INetworkEncodable;
import appeng.container.slot.AppEngSlot;
import appeng.container.slot.SlotRestrictedInput;
import mekanism.common.content.gear.IModuleContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlotRestrictedInput.class)
public abstract class MixinSlotRestrictedInput extends AppEngSlot {


    public MixinSlotRestrictedInput(IItemHandler inv, int idx, int x, int y) {
        super(inv, idx, x, y);
    }


    @Inject(method = "isItemValid", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 13), cancellable = true)
    public void isItemValid(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof IModuleContainerItem){
            cir.setReturnValue(stack.getItem() instanceof INetworkEncodable && AEApi.instance().registries().wireless().isWirelessTerminal(stack) && AEApi.instance().registries().wireless().getWirelessTerminalHandler(stack).canHandle(stack));
        }else {
            cir.setReturnValue(stack.getItem() instanceof INetworkEncodable || AEApi.instance().registries().wireless().isWirelessTerminal(stack));
        }
        cir.cancel();
    }

}
