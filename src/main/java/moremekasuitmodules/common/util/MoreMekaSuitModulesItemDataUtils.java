package moremekasuitmodules.common.util;

import mekanism.api.NBTConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MoreMekaSuitModulesItemDataUtils {

    private MoreMekaSuitModulesItemDataUtils() {
    }

    @NotNull
    public static CompoundTag getDataMap(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(NBTConstants.MEK_DATA, Tag.TAG_COMPOUND)) {
            return tag.getCompound(NBTConstants.MEK_DATA);
        }
        CompoundTag dataMap = new CompoundTag();
        tag.put(NBTConstants.MEK_DATA, dataMap);
        return dataMap;
    }


    @Nullable
    public static CompoundTag getDataMapIfPresent(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBTConstants.MEK_DATA, Tag.TAG_COMPOUND)) {
            return tag.getCompound(NBTConstants.MEK_DATA);
        }
        return null;
    }

    public static float getFloat(ItemStack stack, String key) {
        CompoundTag dataMap = getDataMapIfPresent(stack);
        return dataMap == null ? 0 : dataMap.getFloat(key);
    }

    public static void setFloat(ItemStack stack, String key, float d) {
        getDataMap(stack).putFloat(key, d);
    }
}
