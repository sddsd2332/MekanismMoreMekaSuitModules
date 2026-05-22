package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.MoreMekaSuitModulesLang;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

@ParametersAreNotNullByDefault
public class ModuleOpticalCamouflageUnit implements ICustomModule<ModuleOpticalCamouflageUnit> {

    @Override
    public boolean canChangeModeWhenDisabled(IModule<ModuleOpticalCamouflageUnit> module) {
        return true;
    }

    @Override
    public void changeMode(IModule<ModuleOpticalCamouflageUnit> module, EntityPlayer player, ItemStack stack, int shift, boolean displayChangeMessage) {
        module.toggleEnabled(player, MoreMekaSuitModulesLang.MODULE_OPTICAL_CAMOUFLAGE.getTranslationKey());
    }
}
