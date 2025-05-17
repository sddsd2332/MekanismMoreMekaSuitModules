package moremekasuitmodules.mixin.minecraft;

import mekanism.api.gear.IModule;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.ModuleInfiniteInterceptionAndRescueSystemUnit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(EntityDamageSource.class)
public class MixinEntityDamageSource {

    @Shadow
    @Nullable
    protected Entity damageSourceEntity;

    /**
     * @author sddsd2332
     * @reason 如果伤害来源的实体是否有无限模块, 且无限模块的是否开启了直接伤害排除, 如果有，则设置为无
     */
    @Inject(method = "getTrueSource", at = @At("HEAD"), cancellable = true)
    public void isInfiniteModule(CallbackInfoReturnable<Entity> cir) {
        if (damageSourceEntity instanceof EntityLivingBase base && isInfiniteModule(base)) {
            cir.setReturnValue(null);
            cir.cancel();
        }
    }


    @Unique
    private boolean isInfiniteModule(EntityLivingBase base) {
        if (base != null) {
            ItemStack head = base.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (head.getItem() instanceof IModuleContainerItem item) {
                IModule<ModuleInfiniteInterceptionAndRescueSystemUnit> module = item.getModule(head, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
                if (module != null && module.isEnabled()) {
                    return module.getCustomInstance().getSource();
                }
            }
        }
        return false;
    }
}
