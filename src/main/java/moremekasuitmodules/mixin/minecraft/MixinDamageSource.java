package moremekasuitmodules.mixin.minecraft;

import mekanism.api.gear.IModule;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.content.gear.ModuleInfiniteInterceptionAndRescueSystemUnit;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(DamageSource.class)
public class MixinDamageSource {

    @Shadow
    @Final
    @Nullable
    private Entity directEntity;

    @Shadow
    @Final
    @Nullable
    private Entity causingEntity;


    @Inject(method = "getEntity", at = @At("HEAD"), cancellable = true)
    public void isInfiniteModule(CallbackInfoReturnable<Entity> cir) {
        if (causingEntity instanceof LivingEntity base && isInfiniteModule(base, true)) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "getDirectEntity", at = @At("HEAD"), cancellable = true)
    public void isInfiniteModuleIndirect(CallbackInfoReturnable<Entity> cir) {
        if (directEntity instanceof LivingEntity base && isInfiniteModule(base, false)) {
            cir.setReturnValue(null);
        }
    }

    @Unique
    private boolean isInfiniteModule(LivingEntity base, boolean getSource) {
        if (base != null) {
            ItemStack head = base.getItemBySlot(EquipmentSlot.HEAD);
            if (head.getItem() instanceof IModuleContainerItem item) {
                IModule<ModuleInfiniteInterceptionAndRescueSystemUnit> module = item.getEnabledModule(head, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
                if (module != null) {
                    if (getSource) {
                        return module.getCustomInstance().getSource();
                    } else {
                        return module.getCustomInstance().getSourceIndirect();
                    }
                }
            }
        }
        return false;
    }
}
