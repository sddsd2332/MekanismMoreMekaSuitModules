package moremekasuitmodules.mixin.minecraft;

import moremekasuitmodules.common.content.gear.mekanism.mekasuit.OpticalCamouflageHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderManager.class)
public abstract class MixinRenderManager {

    @Redirect(method = "renderEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/Render;doRenderShadowAndFire(Lnet/minecraft/entity/Entity;DDDFF)V"))
    private void renderCamouflageShadowWhenExposed(Render<Entity> render, Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
        if (entityIn instanceof EntityLivingBase living && OpticalCamouflageHelper.isFullyCamouflaged(living)) {
            if (OpticalCamouflageHelper.shouldRenderShadow(entityIn)) {
                boolean invisible = entityIn.isInvisible();
                try {
                    entityIn.setInvisible(false);
                    render.doRenderShadowAndFire(entityIn, x, y, z, yaw, partialTicks);
                } finally {
                    entityIn.setInvisible(invisible);
                }
            }
        } else {
            render.doRenderShadowAndFire(entityIn, x, y, z, yaw, partialTicks);
        }
    }
}
