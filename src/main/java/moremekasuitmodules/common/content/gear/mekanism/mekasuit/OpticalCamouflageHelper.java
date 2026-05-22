package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.MekaSuitMoreModules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public final class OpticalCamouflageHelper {

    private static final EntityEquipmentSlot[] ARMOR_SLOTS = {
            EntityEquipmentSlot.HEAD,
            EntityEquipmentSlot.CHEST,
            EntityEquipmentSlot.LEGS,
            EntityEquipmentSlot.FEET
    };

    private OpticalCamouflageHelper() {
    }

    public static boolean isSlotCamouflaged(EntityLivingBase entity, EntityEquipmentSlot slot) {
        return entity != null && isCamouflageUsable(entity) && isModuleEnabled(entity.getItemStackFromSlot(slot));
    }

    public static boolean hidesName(EntityLivingBase entity) {
        return isSlotCamouflaged(entity, EntityEquipmentSlot.HEAD);
    }

    public static boolean isFullyCamouflaged(EntityLivingBase entity) {
        if (entity == null || !isCamouflageUsable(entity)) {
            return false;
        }
        for (EntityEquipmentSlot slot : ARMOR_SLOTS) {
            if (!isModuleEnabled(entity.getItemStackFromSlot(slot))) {
                return false;
            }
        }
        return true;
    }

    public static boolean shouldRenderShadow(Entity entity) {
        if (entity == null || entity.world == null) {
            return false;
        }
        return entity.world.getLight(new BlockPos(entity)) > 7;
    }

    private static boolean isCamouflageUsable(EntityLivingBase entity) {
        return entity.isEntityAlive() && !entity.isBurning();
    }

    private static boolean isModuleEnabled(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() instanceof IModuleContainerItem item
                && item.isModuleEnabled(stack, MekaSuitMoreModules.OPTICAL_CAMOUFLAGE_UNIT);
    }
}
