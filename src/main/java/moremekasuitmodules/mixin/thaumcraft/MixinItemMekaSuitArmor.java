package moremekasuitmodules.mixin.thaumcraft;

import mekanism.api.gear.IModule;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.item.armor.ItemMekaSuitArmor;
import moremekasuitmodules.common.MekaSuitMoreModules;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IVisDiscountGear;

@Mixin(ItemMekaSuitArmor.class)
public abstract class MixinItemMekaSuitArmor extends ItemArmor implements IModuleContainerItem, IVisDiscountGear, IGoggles {

    public MixinItemMekaSuitArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }

    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player) {
        IModule<?> module = getModule(stack, MekaSuitMoreModules.MAGIC_OPTIMIZATION_UNIT);
        if (module != null && module.isEnabled()) {
            return module.getInstalledCount();
        }
        return 0;
    }

    @Override
    public boolean showIngamePopups(ItemStack stack, EntityLivingBase player) {
        return isModuleEnabled(stack, MekaSuitMoreModules.GOGGLES_OF_REVEALING_UNIT);
    }
}
