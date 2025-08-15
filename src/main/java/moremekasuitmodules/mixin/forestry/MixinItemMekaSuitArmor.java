package moremekasuitmodules.mixin.forestry;

import forestry.api.apiculture.IArmorApiarist;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.item.armor.ItemMekaSuitArmor;
import moremekasuitmodules.common.MekaSuitMoreModules;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mixin(ItemMekaSuitArmor.class)
public abstract class MixinItemMekaSuitArmor extends ItemArmor implements IModuleContainerItem, IArmorApiarist {


    public MixinItemMekaSuitArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }

    @Override
    public boolean protectEntity(@Nonnull EntityLivingBase entity, @Nonnull ItemStack armor, @Nullable String cause, boolean doProtect) {
        return isModuleEnabled(armor, MekaSuitMoreModules.BEE_CONTROL_UNIT);
    }
}
