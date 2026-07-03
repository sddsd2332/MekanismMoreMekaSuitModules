package moremekasuitmodules.mixin.minecraft;

import com.google.common.collect.Sets;
import mekanism.api.gear.IModule;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.OpticalCamouflageHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {

    @Unique
    private static final int PERCENTAGE_DAMAGE_LIMIT_MAX_MODULES = 40;

    @Unique
    private static final float PERCENTAGE_DAMAGE_LIMIT_MAX_DAMAGE = 1.0F;

    @Unique
    private static final EntityEquipmentSlot[] PERCENTAGE_DAMAGE_LIMIT_ARMOR_SLOTS = {
            EntityEquipmentSlot.HEAD,
            EntityEquipmentSlot.CHEST,
            EntityEquipmentSlot.LEGS,
            EntityEquipmentSlot.FEET
    };

    @Shadow
    private EntityLivingBase revengeTarget;

    @Shadow
    @Final
    public static DataParameter<Float> HEALTH;

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
        if (isInfiniteModule(revengeTarget) || OpticalCamouflageHelper.isFullyCamouflaged(revengeTarget)) {
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
        if (isInfiniteModule(base) || OpticalCamouflageHelper.isFullyCamouflaged(base)) {
            ci.cancel();
        }
    }

    @Unique
    private boolean isInfiniteModule(EntityLivingBase base) {
        ItemStack head = getSafeItemStackFromSlot(base, EntityEquipmentSlot.HEAD);
        if (head.getItem() instanceof IModuleContainerItem item) {
            return item.hasModule(head, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
        }
        return false;
    }

    @Inject(method = "setHealth", at = @At("HEAD"), cancellable = true)
    public void limitPercentageDamageSetHealth(float health, CallbackInfo ci) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (!canReadEquipment(self)) {
            return;
        }
        if (isInfiniteModule(self)) {
            ci.cancel();
            return;
        }
        float limitedHealth = getPercentageDamageLimitedHealth(health);
        if (limitedHealth <= health) {
            return;
        }
        this.dataManager.set(HEALTH, MathHelper.clamp(limitedHealth, 0.0F, this.getMaxHealth()));
        ci.cancel();
    }

    @Unique
    private float getPercentageDamageLimitedHealth(float health) {
        float currentHealth = this.getHealth();
        if (this.ticksExisted <= 0 || health >= currentHealth || currentHealth <= 0.0F || currentHealth > this.getMaxHealth()) {
            return health;
        }
        int moduleCount = getPercentageDamageLimitModuleCount((EntityLivingBase) (Object) this);
        if (moduleCount <= 0) {
            return health;
        }
        float limitedDamage = getLimitedPercentageDamage(currentHealth - health, moduleCount);
        return Math.max(health, Math.max(0.0F, currentHealth - limitedDamage));
    }

    @Unique
    private float getLimitedPercentageDamage(float originalDamage, int moduleCount) {
        if (moduleCount >= PERCENTAGE_DAMAGE_LIMIT_MAX_MODULES) {
            return Math.min(originalDamage, PERCENTAGE_DAMAGE_LIMIT_MAX_DAMAGE);
        }
        float damageMultiplier = 1.0F - moduleCount / (float) PERCENTAGE_DAMAGE_LIMIT_MAX_MODULES;
        return originalDamage * damageMultiplier;
    }

    @Unique
    private int getPercentageDamageLimitModuleCount(EntityLivingBase base) {
        int total = 0;
        for (EntityEquipmentSlot slot : PERCENTAGE_DAMAGE_LIMIT_ARMOR_SLOTS) {
            ItemStack stack = getSafeItemStackFromSlot(base, slot);
            if (!(stack.getItem() instanceof IModuleContainerItem item)) {
                continue;
            }
            IModule<?> module = item.getModule(stack, MekaSuitMoreModules.PERCENTAGE_DAMAGE_LIMIT_UNIT);
            if (module != null && module.isEnabled()) {
                total += module.getInstalledCount();
            }
        }
        return total;
    }

    @Unique
    private boolean canReadEquipment(EntityLivingBase base) {
        return base != null && base.world != null && base.ticksExisted > 0;
    }

    @Unique
    private ItemStack getSafeItemStackFromSlot(EntityLivingBase base, EntityEquipmentSlot slot) {
        if (!canReadEquipment(base)) {
            return ItemStack.EMPTY;
        }
        return base.getItemStackFromSlot(slot);
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
