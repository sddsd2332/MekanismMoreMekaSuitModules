package moremekasuitmodules.common.content.gear.integration.toughasnails;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import toughasnails.api.TANCapabilities;
import toughasnails.api.stat.capability.IThirst;

@ParametersAreNotNullByDefault
public class ModuleAutomaticLiquidSupplyUnit implements ICustomModule<ModuleAutomaticLiquidSupplyUnit> {

    @Override
    public void tickServer(IModule<ModuleAutomaticLiquidSupplyUnit> module, EntityPlayer player) {
        if (Loader.isModLoaded("toughasnails")) {
            AutoDrink(module, player);
        }
    }

    @Optional.Method(modid = "toughasnails")
    private void AutoDrink(IModule<ModuleAutomaticLiquidSupplyUnit> module, EntityPlayer player) {
        if (!player.hasCapability(TANCapabilities.THIRST, null)) {
            return;
        }
        IThirst thirst = player.getCapability(TANCapabilities.THIRST, null);
        if (thirst != null) {
            if (thirst.getThirst() < 20) {
                module.useEnergy(player, 100);
                thirst.setThirst(thirst.getThirst() + 1);
                thirst.setHydration(5.0F);
                thirst.setExhaustion(0.0F);
            }
        }
    }


}
