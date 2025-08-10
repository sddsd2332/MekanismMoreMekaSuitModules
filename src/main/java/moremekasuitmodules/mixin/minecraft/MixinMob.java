package moremekasuitmodules.mixin.minecraft;

import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Mob.class)
public class MixinMob {

    @Shadow @Nullable private LivingEntity target;


    /**
     * @author sddsd2332
     * @reason 取消攻击带有无限拦截单元模块的生物
     */
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    public void setAttackTarget(LivingEntity base, CallbackInfo ci) {
        if (isInfiniteModule(base)) {
            ci.cancel();
        }
    }


    /**
     * @author sddsd2332
     * @reason 检查是否有攻击目标是否有无限模块
     */
    @Inject(method = "getTarget",at = @At("HEAD"), cancellable = true)
    public void getAttackTarget(CallbackInfoReturnable<LivingEntity> cir) {
        if (isInfiniteModule(target)){
            target = null;
            cir.setReturnValue(null);
        }
    }

    @Unique
    private boolean isInfiniteModule(LivingEntity base) {
        if (base != null) {
            ItemStack head = base.getItemBySlot(EquipmentSlot.HEAD);
            if (head.getItem() instanceof IModuleContainerItem item) {
                return item.hasModule(head, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
            }
        }
        return false;
    }
}
