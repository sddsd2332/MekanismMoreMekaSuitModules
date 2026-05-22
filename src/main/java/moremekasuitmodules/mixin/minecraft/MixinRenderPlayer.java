package moremekasuitmodules.mixin.minecraft;

import moremekasuitmodules.common.content.gear.mekanism.mekasuit.OpticalCamouflageHelper;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer {

    @Inject(method = "setModelVisibilities", at = @At("TAIL"))
    private void hideCamouflagedModelParts(AbstractClientPlayer clientPlayer, CallbackInfo ci) {
        if (OpticalCamouflageHelper.isFullyCamouflaged(clientPlayer)) {
            return;
        }
        ModelPlayer model = ((RenderPlayer) (Object) this).getMainModel();
        if (OpticalCamouflageHelper.isSlotCamouflaged(clientPlayer, EntityEquipmentSlot.HEAD)) {
            model.bipedHead.showModel = false;
            model.bipedHeadwear.showModel = false;
        }
        if (OpticalCamouflageHelper.isSlotCamouflaged(clientPlayer, EntityEquipmentSlot.CHEST)) {
            model.bipedBody.showModel = false;
            model.bipedBodyWear.showModel = false;
            model.bipedLeftArm.showModel = false;
            model.bipedLeftArmwear.showModel = false;
            model.bipedRightArm.showModel = false;
            model.bipedRightArmwear.showModel = false;
        }
        if (OpticalCamouflageHelper.isSlotCamouflaged(clientPlayer, EntityEquipmentSlot.LEGS)) {
            model.bipedLeftLeg.showModel = false;
            model.bipedLeftLegwear.showModel = false;
            model.bipedRightLeg.showModel = false;
            model.bipedRightLegwear.showModel = false;
        }
    }

    @Inject(method = "renderRightArm", at = @At("HEAD"), cancellable = true)
    private void hideRightArm(AbstractClientPlayer clientPlayer, CallbackInfo ci) {
        if (OpticalCamouflageHelper.isSlotCamouflaged(clientPlayer, EntityEquipmentSlot.CHEST)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLeftArm", at = @At("HEAD"), cancellable = true)
    private void hideLeftArm(AbstractClientPlayer clientPlayer, CallbackInfo ci) {
        if (OpticalCamouflageHelper.isSlotCamouflaged(clientPlayer, EntityEquipmentSlot.CHEST)) {
            ci.cancel();
        }
    }
}
