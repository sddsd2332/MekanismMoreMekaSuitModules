package moremekasuitmodules.common.content.gear;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import moremekasuitmodules.common.MoreMekaSuitModules;
import net.minecraft.resources.ResourceLocation;

@ParametersAreNotNullByDefault
public record ModuleEnergyShieldControllerUnit(boolean state) implements ICustomModule<ModuleEnergyShieldControllerUnit> {

    public static final ResourceLocation SHIELD_ENABLE = MoreMekaSuitModules.rl("shield_enable");

    public ModuleEnergyShieldControllerUnit(IModule<ModuleEnergyShieldControllerUnit> module){
        this(module.getBooleanConfigOrFalse(SHIELD_ENABLE));
    }

    public boolean getState(){
        return state;
    }

}
