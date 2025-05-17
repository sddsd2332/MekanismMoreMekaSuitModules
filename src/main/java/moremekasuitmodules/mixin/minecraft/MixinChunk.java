package moremekasuitmodules.mixin.minecraft;

import com.google.common.base.Predicate;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.MekaSuitMoreModules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Chunk.class)
public abstract class MixinChunk implements net.minecraftforge.common.capabilities.ICapabilityProvider {


    /**
     * @author sddsd2332
     * @reason 检查范围内的实体是否已经启用了无限模块, 并且该模块已经启用了范围排除，如果启用了范围排除，则移除该实体
     */
    @Inject(method = "getEntitiesWithinAABBForEntity", at = @At("TAIL"))
    public void SkipisInfiniteModuleEntity(Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate<? super Entity> filter, CallbackInfo ci) {
        listToFill.removeIf(entity -> entity instanceof EntityLivingBase base && isInfiniteModule(base));
    }

    /**
     * @author sddsd2332
     * @reason 检查范围内的实体是否已经启用了无限模块，如果启用了则排除该实体，如果启用了范围排除，则移除该实体
     */
    @Inject(method = "getEntitiesOfTypeWithinAABB", at = @At("TAIL"))
    public <T extends Entity> void SkipisInfiniteModuleEntity(Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> filter, CallbackInfo ci) {
        listToFill.removeIf(entity -> entity instanceof EntityLivingBase base && isInfiniteModule(base));
    }


    @Unique
    private boolean isInfiniteModule(EntityLivingBase base) {
        if (base != null) {
            ItemStack head = base.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            return head.getItem() instanceof IModuleContainerItem item && item.hasModule(head, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
        }
        return false;
    }


}
