package moremekasuitmodules.mixin.minecraft;

import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.MekaSuitMoreModules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {


    @Shadow
    private EntityLivingBase revengeTarget;

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    /**
     * @author sddsd2332
     * @reason 检查是否有复仇目标是否有无限模块
     */
    @Inject(method = "getRevengeTarget",at = @At("HEAD"), cancellable = true)
    public void getRevengeTarget(CallbackInfoReturnable<EntityLivingBase> cir){
        if (isInfiniteModule(revengeTarget)){
            revengeTarget = null;
            cir.setReturnValue(null);
            cir.cancel();
        }
    }

    /**
     * @author sddsd2332
     * @reason 取消攻击带有无限拦截单元模块的生物
     */
    @Inject(method = "setRevengeTarget",at = @At("HEAD"), cancellable = true)
    public void setRevengeTarget(EntityLivingBase base, CallbackInfo ci){
        if (isInfiniteModule(base)){
            ci.cancel();
        }
    }

    @Unique
    private boolean isInfiniteModule(EntityLivingBase base) {
        if (base != null) {
            ItemStack head = base.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (head.getItem() instanceof IModuleContainerItem item) {
                return item.hasModule(head, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
            }
        }
        return false;
    }

}
