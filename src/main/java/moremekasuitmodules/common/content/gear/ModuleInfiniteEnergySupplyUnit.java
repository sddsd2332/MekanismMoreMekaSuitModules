package moremekasuitmodules.common.content.gear;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import net.minecraft.world.entity.player.Player;

@ParametersAreNotNullByDefault
public class ModuleInfiniteEnergySupplyUnit implements ICustomModule<ModuleInfiniteEnergySupplyUnit> {

    @Override
    public void tickClient(IModule<ModuleInfiniteEnergySupplyUnit> module, Player player) {
        this.tickServer(module, player);
    }

    @Override
    public void tickServer(IModule<ModuleInfiniteEnergySupplyUnit> module, Player player) {
        IEnergyContainer energyContainer = module.getEnergyContainer();
        if (energyContainer != null && !energyContainer.getNeeded().isZero()) {
            energyContainer.insert(energyContainer.getNeeded(), Action.EXECUTE, AutomationType.MANUAL);
        }
    }
}
