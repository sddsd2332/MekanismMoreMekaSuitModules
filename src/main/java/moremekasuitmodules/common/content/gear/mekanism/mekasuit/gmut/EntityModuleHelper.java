package moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut;

import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.ModuleData;
import mekanism.common.content.gear.Module;
import mekanism.common.content.gear.ModuleHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class EntityModuleHelper {
    public static <T extends ICustomModule<T>> Module<T> findArmorEnabledModule(Entity entity, ModuleData<T> type) {
        for (ItemStack itemStack : entity.getArmorInventoryList()) {
            Module<T> module = ModuleHelper.get().load(itemStack, type);
            if (module != null && module.isEnabled()) {
                return module;
            }
        }

        return null;
    }

}
