package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

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
public class ModuleCounterattackUnit implements ICustomModule<ModuleCounterattackUnit> {

    public static final int MAX_MODULES_PER_ARMOR = 10;
    public static final float DAMAGE_DIVISOR = 10.0F;

    private IModuleConfigItem<CounterLevel> counterLevel;

    @Override
    public void init(IModule<ModuleCounterattackUnit> module, ModuleConfigItemCreator configItemCreator) {
        int unlockedLevels = CounterLevel.getUnlockedLevelCount(module.getInstalledCount());
        counterLevel = configItemCreator.createConfigItem("counter_level", MoreMekaSuitModulesLang.MODULE_COUNTER_LEVEL,
              new ModuleEnumData<>(CounterLevel.values()[unlockedLevels - 1], unlockedLevels));
    }

    public int getEffectiveCount(int installedCount) {
        return Math.min(Math.min(installedCount, MAX_MODULES_PER_ARMOR), counterLevel.get().getMaxModules());
    }

    public enum CounterLevel implements IHasTextComponent {
        LOW(2),
        NORMAL(4),
        MEDIUM(6),
        HIGH(8),
        ULTRA(10);

        private final int maxModules;
        private final ITextComponent label;

        CounterLevel(int maxModules) {
            this.maxModules = maxModules;
            this.label = new TextComponentGroup().getString(Integer.toString(maxModules));
        }

        @Override
        public ITextComponent getTextComponent() {
            return label;
        }

        public int getMaxModules() {
            return maxModules;
        }

        public static int getUnlockedLevelCount(int installedCount) {
            return Math.max(1, Math.min(values().length, (Math.max(1, installedCount) + 1) / 2));
        }
    }
}
