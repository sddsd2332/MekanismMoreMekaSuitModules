package moremekasuitmodules.mixin.minecraft;

import moremekasuitmodules.common.content.gear.mekanism.mekasuit.OpticalCamouflageHelper;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerDeadmau5Head.class)
public abstract class MixinLayerDeadmau5Head {

    @Inject(method = "doRenderLayer", at = @At("HEAD"), cancellable = true)
    private void hideCamouflagedHeadLayer(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (OpticalCamouflageHelper.isSlotCamouflaged(entitylivingbaseIn, EntityEquipmentSlot.HEAD)) {
            ci.cancel();
        }
    }
}
