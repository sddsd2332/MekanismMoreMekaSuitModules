package appeng.api.util;

import appeng.api.config.Settings;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Set;

//Copied from AE for use with the culling code
public interface IConfigManager {
    Set<Settings> getSettings();

    void registerSetting(Settings var1, Enum<?> var2);

    Enum<?> getSetting(Settings var1);

    Enum<?> putSetting(Settings var1, Enum<?> var2);

    void writeToNBT(NBTTagCompound var1);

    void readFromNBT(NBTTagCompound var1);
}

