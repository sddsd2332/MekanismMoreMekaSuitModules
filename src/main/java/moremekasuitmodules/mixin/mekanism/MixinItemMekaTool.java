package moremekasuitmodules.mixin.mekanism;

import mekanism.api.MekanismAPI;
import mekanism.api.NBTConstants;
import mekanism.api.gear.ModuleData;
import mekanism.api.math.FloatingLongSupplier;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.item.ItemEnergized;
import mekanism.common.item.gear.ItemMekaTool;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.StorageUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ItemMekaTool.class, remap = false)
public abstract class MixinItemMekaTool extends ItemEnergized {

    public MixinItemMekaTool(FloatingLongSupplier chargeRateSupplier, FloatingLongSupplier maxEnergySupplier, Properties properties) {
        super(chargeRateSupplier, maxEnergySupplier, properties);
    }

    @Override
    public void addItems(CreativeModeTab.Output tabOutput) {
        super.addItems(tabOutput);
        ItemStack fullStack = new ItemStack((ItemMekaTool) (Object) this);
        setAllModule(fullStack);
        StorageUtils.getFilledEnergyVariant(fullStack, getMaxEnergy(fullStack));
        tabOutput.accept(fullStack);
    }

    @Unique
    public void setAllModule(ItemStack stack) {
        for (ModuleData<?> module : MekanismAPI.moduleRegistry().getValues()) {
            if (ModuleHelper.get().getSupported(stack).contains(module)) {
                setModule(stack, module);
            }
        }
    }


    @Unique
    public void setModule(ItemStack stack, ModuleData<?> type) {
        if (!ItemDataUtils.hasData(stack, NBTConstants.MODULES, Tag.TAG_COMPOUND)) {
            ItemDataUtils.setCompound(stack, NBTConstants.MODULES, new CompoundTag());
        }
        ItemDataUtils.getCompound(stack, NBTConstants.MODULES).put(type.getRegistryName().toString(), new CompoundTag());
        ItemDataUtils.getCompound(stack, NBTConstants.MODULES).getCompound(type.getRegistryName().toString()).putInt(NBTConstants.AMOUNT, type.getMaxStackSize());
        ModuleHelper.get().load(stack, type).save(null);
        ModuleHelper.get().load(stack, type).onAdded(false);
    }
}
