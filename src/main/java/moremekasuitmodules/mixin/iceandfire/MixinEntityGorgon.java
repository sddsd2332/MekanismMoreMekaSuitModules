package moremekasuitmodules.mixin.iceandfire;

import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import com.github.alexthe666.iceandfire.entity.IAnimalFear;
import com.github.alexthe666.iceandfire.entity.IHumanoid;
import com.github.alexthe666.iceandfire.entity.IVillagerFear;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.MekaSuitMoreModules;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityGorgon.class)
public abstract class MixinEntityGorgon extends EntityMob implements IAnimatedEntity, IVillagerFear, IAnimalFear, IHumanoid {

    public MixinEntityGorgon(World worldIn) {
        super(worldIn);
    }

    /**
     * @author sddsd2332
     * @reason 覆盖实现meka的模块屏蔽
     */
    @Overwrite(remap = false)
    public static boolean isBlindfolded(EntityLivingBase attackTarget) {
        if (attackTarget != null) {
            ItemStack head = attackTarget.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            return head.getItem() == IafItemRegistry.blindfold || head.getItem() instanceof IModuleContainerItem item && item.isModuleEnabled(head, MekaSuitMoreModules.SMART_SHIELDING_UNIT);
        }
        return false;
    }
}
