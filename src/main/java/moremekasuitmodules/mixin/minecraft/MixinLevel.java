package moremekasuitmodules.mixin.minecraft;

import mekanism.api.gear.IModule;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.content.gear.ModuleInfiniteInterceptionAndRescueSystemUnit;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.function.Predicate;

@Mixin(Level.class)
public class MixinLevel {

    @Inject(method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void SkipisInfiniteModuleEntity(Entity p_46536_, AABB p_46537_, Predicate<? super Entity> p_46538_, CallbackInfoReturnable<List<Entity>> cir, List<Entity> list) {
        list.removeIf(entity -> entity instanceof LivingEntity base && isInfiniteModule(base));
    }


    @Inject(method = "getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;Ljava/util/List;I)V", at = @At("TAIL"))
    public <T extends Entity> void SkipisInfiniteModuleEntity(EntityTypeTest<Entity, T> p_261885_, AABB p_262086_, Predicate<? super T> p_261688_, List<? super T> list, int p_261858_, CallbackInfo ci) {
        list.removeIf(entity -> entity instanceof LivingEntity base && isInfiniteModule(base));
    }

    @Unique
    private boolean isInfiniteModule(LivingEntity base) {
        if (base != null) {
            ItemStack head = base.getItemBySlot(EquipmentSlot.HEAD);
            if (head.getItem() instanceof IModuleContainerItem item) {
                IModule<ModuleInfiniteInterceptionAndRescueSystemUnit> module = item.getEnabledModule(head, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
                if (module != null) {
                    return module.getCustomInstance().getChunkRemove();
                }
            }
        }
        return false;
    }
}
