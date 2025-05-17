package moremekasuitmodules.mixin.iceandfire;

import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import com.github.alexthe666.iceandfire.entity.IAnimalFear;
import com.github.alexthe666.iceandfire.entity.IHumanoid;
import com.github.alexthe666.iceandfire.entity.IVillagerFear;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.MekaSuitMoreModules;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityGorgon.class)
public abstract class MixinEntityGorgon extends EntityMob implements IAnimatedEntity, IVillagerFear, IAnimalFear, IHumanoid {

    public MixinEntityGorgon(World worldIn) {
        super(worldIn);
    }

    /**
     * @author sddsd2332
     * @reason 实现meka的模块屏蔽
     */
    @Inject(method = "isBlindfolded", at = @At("HEAD"), remap = false)
    private static void isBlindfolded(EntityLivingBase attackTarget, CallbackInfoReturnable<Boolean> cir) {
        if (attackTarget != null) {
            ItemStack head = attackTarget.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (head.getItem() instanceof IModuleContainerItem item && item.isModuleEnabled(head, MekaSuitMoreModules.SMART_SHIELDING_UNIT)) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
