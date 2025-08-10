package moremekasuitmodules.mixin.iceandfire;

import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.integration.iceandfire.iceAndFireModules;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityGorgon.class)
public class MixinEntityGorgon {

    /**
     * @author sddsd2332
     * @reason 实现meka的模块屏蔽
     */
    @Inject(method = "isBlindfolded", at = @At("HEAD"), remap = false, cancellable = true)
    private static void isBlindfolded(LivingEntity attackTarget, CallbackInfoReturnable<Boolean> cir) {
        if (attackTarget != null) {
            ItemStack head = attackTarget.getItemBySlot(EquipmentSlot.HEAD);
            if (head.getItem() instanceof IModuleContainerItem item && item.isModuleEnabled(head, iceAndFireModules.SMART_SHIELDING_UNIT)) {
                cir.setReturnValue(true);
            }
        }
    }

}
