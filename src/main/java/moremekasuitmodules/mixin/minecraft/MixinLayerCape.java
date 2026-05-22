package moremekasuitmodules.mixin.minecraft;

import moremekasuitmodules.common.content.gear.mekanism.mekasuit.OpticalCamouflageHelper;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerCape.class)
public abstract class MixinLayerCape {

    @Inject(method = "doRenderLayer", at = @At("HEAD"), cancellable = true)
    private void hideCamouflagedCape(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (OpticalCamouflageHelper.isSlotCamouflaged(entitylivingbaseIn, EntityEquipmentSlot.CHEST)) {
            ci.cancel();
        }
    }
}
