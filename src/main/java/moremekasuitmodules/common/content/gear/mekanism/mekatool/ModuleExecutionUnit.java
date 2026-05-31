package moremekasuitmodules.common.content.gear.mekanism.mekatool;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import mekanism.api.gear.config.ModuleEnumData;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.TextComponentGroup;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.MoreMekaSuitModulesLang;
import net.minecraft.util.text.ITextComponent;

@ParametersAreNotNullByDefault
public class ModuleExecutionUnit implements ICustomModule<ModuleExecutionUnit> {

    public static final int MAX_MODULES = 10;

    private IModuleConfigItem<ExecutionLevel> executionLevel;

    @Override
    public void init(IModule<ModuleExecutionUnit> module, ModuleConfigItemCreator configItemCreator) {
        int unlockedLevels = ExecutionLevel.getUnlockedLevelCount(module.getInstalledCount());
        executionLevel = configItemCreator.createConfigItem("execution_level", MoreMekaSuitModulesLang.MODULE_EXECUTION_LEVEL,
                new ModuleEnumData<>(ExecutionLevel.values()[unlockedLevels - 1], unlockedLevels));
    }

    public float getThreshold(int installedCount) {
        int effectiveModules = Math.min(Math.min(installedCount, MAX_MODULES), executionLevel.get().getModules());
        return effectiveModules * 0.02F;
    }

    public enum ExecutionLevel implements IHasTextComponent {
        LOW(2),
        NORMAL(4),
        MEDIUM(6),
        HIGH(8),
        ULTRA(10);

        private final int modules;
        private final ITextComponent label;

        ExecutionLevel(int modules) {
            this.modules = modules;
            this.label = new TextComponentGroup().getString(Integer.toString(modules * 2) + "%");
        }

        @Override
        public ITextComponent getTextComponent() {
            return label;
        }

        public int getModules() {
            return modules;
        }

        public static int getUnlockedLevelCount(int installedCount) {
            return Math.max(1, Math.min(values().length, (Math.max(1, installedCount) + 1) / 2));
        }
    }
}
