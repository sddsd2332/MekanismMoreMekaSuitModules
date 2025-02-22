package moremekasuitmodules.mixin.gregtech;

import gregtech.common.pipelike.cable.BlockCable;
import mekanism.api.gear.ModuleData;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.MekaSuitMoreModules;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockCable.class)
public class MixinBlockCable {

    @Inject(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z"), cancellable = true)
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn, CallbackInfo ci) {
        if (entityIn instanceof EntityLivingBase livingBase) {
            boolean helmet = isInsulated(livingBase.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
            boolean chest = isInsulated(livingBase.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
            boolean legs = isInsulated(livingBase.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
            boolean feet = isInsulated(livingBase.getItemStackFromSlot(EntityEquipmentSlot.FEET));
            if (helmet && chest && legs && feet) {
                ci.cancel();
            }
        }
    }

    @Unique
    private boolean isInsulated(ItemStack stack) {
        return ModuleInstallation(stack, MekaSuitMoreModules.INSULATED_UNIT);
    }

    @Unique
    private boolean ModuleInstallation(ItemStack stack, ModuleData<?> data) {
        if (stack.getItem() instanceof IModuleContainerItem item) {
            return item.isModuleEnabled(stack, data);
        }
        return false;
    }
}
