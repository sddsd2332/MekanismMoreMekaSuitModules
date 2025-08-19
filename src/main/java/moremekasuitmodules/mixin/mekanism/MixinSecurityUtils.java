package moremekasuitmodules.mixin.mekanism;

import mekanism.api.functions.TriConsumer;
import mekanism.api.security.ISecurityUtils;
import mekanism.common.util.SecurityUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SecurityUtils.class, remap = false)
public abstract class MixinSecurityUtils implements ISecurityUtils {

    @Shadow
    public abstract boolean tryClaimItem(Level level, Player player, ItemStack stack);

    @Inject(method = "claimOrOpenGui", at = @At("HEAD"), cancellable = true)
    public void addModule(Level level, Player player, InteractionHand hand, TriConsumer<ServerPlayer, InteractionHand, ItemStack> openGui, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (hand == null) {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!tryClaimItem(level, player, stack)) {
                if (!canAccessOrDisplayError(player, stack)) {
                    cir.setReturnValue(InteractionResultHolder.fail(stack));
                } else if (!level.isClientSide) {
                    openGui.accept((ServerPlayer) player, null, stack);
                }
            }
            cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, level.isClientSide));
        }
    }

}

