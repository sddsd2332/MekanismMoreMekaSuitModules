package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.energy.IEnergizedItem;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import net.minecraft.entity.player.EntityPlayer;

@ParametersAreNotNullByDefault
public class ModuleInfiniteEnergySupplyUnit implements ICustomModule<ModuleInfiniteEnergySupplyUnit> {

    @Override
    public void tickClient(IModule<ModuleInfiniteEnergySupplyUnit> module, EntityPlayer player) {
        this.tickServer(module,player);
    }

    @Override
    public void tickServer(IModule<ModuleInfiniteEnergySupplyUnit> module, EntityPlayer player) {
        IEnergizedItem energyContainer = module.getEnergyContainer();
        if (energyContainer != null) {
            energyContainer.insert(module.getContainer(), energyContainer.getMaxEnergy(module.getContainer()), true);
        }
    }



}
