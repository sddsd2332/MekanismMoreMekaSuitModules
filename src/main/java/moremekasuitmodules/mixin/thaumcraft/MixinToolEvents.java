package moremekasuitmodules.mixin.thaumcraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.common.lib.events.ToolEvents;

@Mixin(ToolEvents.class)
public class MixinToolEvents {

    @Redirect(method = "livingDrops",at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;getHeldItem(Lnet/minecraft/util/EnumHand;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack fixActiveHand(EntityPlayer instance, EnumHand hand){
        if (hand != null){
            return instance.getHeldItem(hand);
        }else {
            return ItemStack.EMPTY;
        }
    }
}
