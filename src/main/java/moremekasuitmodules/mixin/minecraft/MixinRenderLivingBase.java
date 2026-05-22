package moremekasuitmodules.mixin.minecraft;

import moremekasuitmodules.common.content.gear.mekanism.mekasuit.OpticalCamouflageHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> {

    @Inject(method = "doRender", at = @At("HEAD"), cancellable = true)
    private void hideFullyCamouflagedEntity(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (OpticalCamouflageHelper.isFullyCamouflaged(entity)) {
            ci.cancel();
        }
    }

    @Inject(method = "canRenderName", at = @At("HEAD"), cancellable = true)
    private void hideCamouflagedName(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (OpticalCamouflageHelper.hidesName(entity)) {
            cir.setReturnValue(false);
        }
    }
}
