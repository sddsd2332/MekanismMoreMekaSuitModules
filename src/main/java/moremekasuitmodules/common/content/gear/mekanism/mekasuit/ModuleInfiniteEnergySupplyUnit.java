package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.energy.IEnergyContainer;
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
        IEnergyContainer energyContainer = module.getEnergyContainer();
        if (energyContainer != null) {
            energyContainer.insert(energyContainer.getNeeded(), Action.EXECUTE, AutomationType.MANUAL);
        }
    }



}
