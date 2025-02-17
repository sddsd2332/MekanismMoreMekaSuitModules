package moremekasuitmodules.common.content.gear.integration.toughasnails;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import toughasnails.api.TANCapabilities;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.temperature.Temperature;

@ParametersAreNotNullByDefault
public class ModuleIntelligentTemperatureRegulationUnit implements ICustomModule<ModuleIntelligentTemperatureRegulationUnit> {

    @Override
    public void tickServer(IModule<ModuleIntelligentTemperatureRegulationUnit> module, EntityPlayer player) {
        if (Loader.isModLoaded("toughasnails")) {
            Temperaturestable(module, player);
        }
    }

    @Optional.Method(modid = "toughasnails")
    private void Temperaturestable(IModule<ModuleIntelligentTemperatureRegulationUnit> module, EntityPlayer player) {
        if (!player.hasCapability(TANCapabilities.TEMPERATURE, null)) {
            return;
        }
        ITemperature temperature = player.getCapability(TANCapabilities.TEMPERATURE, null);
        if (temperature != null) {
            if (temperature.getTemperature().getRawValue() != 14) {
                temperature.setChangeTime(1);
                if (temperature.getTemperature().getRawValue() < 14) {
                    module.useEnergy(player, 100);
                    temperature.setTemperature(new Temperature(temperature.getTemperature().getRawValue() + 1));
                } else if (temperature.getTemperature().getRawValue() > 14) {
                    module.useEnergy(player, 100);
                    temperature.setTemperature(new Temperature(temperature.getTemperature().getRawValue() - 1));
                }
            }
        }
    }

}
