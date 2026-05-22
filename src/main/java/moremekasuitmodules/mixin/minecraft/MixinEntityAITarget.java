package moremekasuitmodules.mixin.minecraft;

import moremekasuitmodules.common.content.gear.mekanism.mekasuit.OpticalCamouflageHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(EntityAITarget.class)
public abstract class MixinEntityAITarget {

    @Inject(method = "isSuitableTarget(Lnet/minecraft/entity/EntityLiving;Lnet/minecraft/entity/EntityLivingBase;ZZ)Z", at = @At("HEAD"), cancellable = true)
    private static void rejectFullyCamouflagedTargets(EntityLiving attacker, @Nullable EntityLivingBase target, boolean includeInvincibles, boolean checkSight, CallbackInfoReturnable<Boolean> cir) {
        if (OpticalCamouflageHelper.isFullyCamouflaged(target)) {
            cir.setReturnValue(false);
        }
    }
}
