package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleBooleanData;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.GMUTLang;

@ParametersAreNotNullByDefault
public class ModuleInfiniteInterceptionAndRescueSystemUnit implements ICustomModule<ModuleInfiniteInterceptionAndRescueSystemUnit> {


    private IModuleConfigItem<Boolean> damagesource;
    private IModuleConfigItem<Boolean> damagesourceIndirect;
    private IModuleConfigItem<Boolean> chunkRemove;

    @Override
    public void init(IModule<ModuleInfiniteInterceptionAndRescueSystemUnit> module, ModuleConfigItemCreator configItemCreator) {
        damagesource = configItemCreator.createConfigItem("damagesource", GMUTLang.MODULE_DAMAGE_SOURCE, new ModuleBooleanData());
        damagesourceIndirect = configItemCreator.createConfigItem("damagesourceIndirect", GMUTLang.MODULE_DAMAGE_SOURCE_INDIRECT, new ModuleBooleanData());
        chunkRemove = configItemCreator.createConfigItem("chunkRemove",GMUTLang.MODULE_CHUNK_REMOVE,new ModuleBooleanData(false));
    }

    public Boolean getSource() {
        return damagesource.get();
    }
    public Boolean getSourceIndirect() {
        return damagesourceIndirect.get();
    }

    public boolean getChunkRemove() {
        return chunkRemove.get();
    }
}
