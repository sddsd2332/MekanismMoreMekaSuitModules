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
public class ModuleKnockbackControlUnit implements ICustomModule<ModuleKnockbackControlUnit> {

    public static final int MAX_MODULES = 10;

    private IModuleConfigItem<KnockbackStrength> knockbackStrength;

    @Override
    public void init(IModule<ModuleKnockbackControlUnit> module, ModuleConfigItemCreator configItemCreator) {
        int unlockedLevels = KnockbackStrength.getUnlockedLevelCount(module.getInstalledCount());
        knockbackStrength = configItemCreator.createConfigItem("knockback_strength", MoreMekaSuitModulesLang.MODULE_KNOCKBACK_STRENGTH,
                new ModuleEnumData<>(KnockbackStrength.values()[unlockedLevels - 1], unlockedLevels));
    }

    public float getEffectiveStrength(int installedCount) {
        int effectiveModules = Math.min(Math.min(installedCount, MAX_MODULES), knockbackStrength.get().getModules());
        return effectiveModules * 0.2F;
    }

    public enum KnockbackStrength implements IHasTextComponent {
        LOW(2),
        NORMAL(4),
        MEDIUM(6),
        HIGH(8),
        ULTRA(10);

        private final int modules;
        private final ITextComponent label;

        KnockbackStrength(int modules) {
            this.modules = modules;
            this.label = new TextComponentGroup().getString(Integer.toString(modules));
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
