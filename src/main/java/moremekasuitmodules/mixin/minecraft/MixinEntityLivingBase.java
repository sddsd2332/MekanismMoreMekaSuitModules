package moremekasuitmodules.mixin.minecraft;

import com.google.common.collect.Sets;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.MekaSuitMoreModules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
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

import java.util.Set;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {


    @Shadow
    private EntityLivingBase revengeTarget;

    @Shadow
    public abstract AbstractAttributeMap getAttributeMap();

    @Shadow
    public abstract float getMaxHealth();

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract void setHealth(float health);

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    /**
     * @author sddsd2332
     * @reason 检查是否有复仇目标是否有无限模块
     */
    @Inject(method = "getRevengeTarget", at = @At("HEAD"), cancellable = true)
    public void getRevengeTarget(CallbackInfoReturnable<EntityLivingBase> cir) {
        if (isInfiniteModule(revengeTarget)) {
            revengeTarget = null;
            cir.setReturnValue(null);
            cir.cancel();
        }
    }

    /**
     * @author sddsd2332
     * @reason 取消攻击带有无限拦截单元模块的生物
     */
    @Inject(method = "setRevengeTarget", at = @At("HEAD"), cancellable = true)
    public void setRevengeTarget(EntityLivingBase base, CallbackInfo ci) {
        if (isInfiniteModule(base)) {
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


    //移植1.21.1版本的最大生命值属性修复
    @Inject(method = "onUpdate", at = @At("TAIL"))
    public void refreshDirtyAttributes(CallbackInfo ci) {
        Set<IAttributeInstance> set = Sets.newHashSet(getAttributeMap().getAllAttributes());
        set.forEach(attributeinstance -> onAttributeUpdated(attributeinstance.getAttribute()));
        set.clear();
    }

    @Unique
    private void onAttributeUpdated(IAttribute attribute) {
        if (attribute.getName().equals(SharedMonsterAttributes.MAX_HEALTH.getName())) {
            float f = this.getMaxHealth();
            if (this.getHealth() > f) {
                this.setHealth(f);
            }
        }
    }
}
