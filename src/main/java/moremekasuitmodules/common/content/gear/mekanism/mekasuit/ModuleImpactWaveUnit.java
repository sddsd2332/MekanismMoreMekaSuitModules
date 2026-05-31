package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleColorData;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import mekanism.api.gear.config.ModuleEnumData;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.TextComponentGroup;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.MoreMekaSuitModulesLang;
import net.minecraft.util.text.ITextComponent;

@ParametersAreNotNullByDefault
public class ModuleImpactWaveUnit implements ICustomModule<ModuleImpactWaveUnit> {

    public static final int DEFAULT_WAVE_COLOR = 0x88B8F7FF;
    public static final int MAX_MODULES = 4;

    private IModuleConfigItem<TriggerHeight> triggerHeight;
    private IModuleConfigItem<ImpactRadius> radius;
    private IModuleConfigItem<DamageScale> damageScale;
    private IModuleConfigItem<Integer> waveColor;

    @Override
    public void init(IModule<ModuleImpactWaveUnit> module, ModuleConfigItemCreator configItemCreator) {
        int unlockedLevels = Math.max(1, Math.min(module.getInstalledCount(), MAX_MODULES));
        triggerHeight = configItemCreator.createConfigItem("trigger_height", MoreMekaSuitModulesLang.MODULE_IMPACT_TRIGGER_HEIGHT,
                new ModuleEnumData<>(TriggerHeight.LOW, unlockedLevels));
        radius = configItemCreator.createConfigItem("radius", MoreMekaSuitModulesLang.MODULE_IMPACT_RADIUS,
                new ModuleEnumData<>(ImpactRadius.values()[unlockedLevels - 1], unlockedLevels));
        damageScale = configItemCreator.createConfigItem("damage", MoreMekaSuitModulesLang.MODULE_IMPACT_DAMAGE,
                new ModuleEnumData<>(DamageScale.values()[unlockedLevels - 1], unlockedLevels));
        waveColor = configItemCreator.createConfigItem("wave_color", MoreMekaSuitModulesLang.MODULE_IMPACT_WAVE_COLOR,
                ModuleColorData.argb(DEFAULT_WAVE_COLOR));
    }

    public float getTriggerHeight() {
        return triggerHeight.get().getHeight();
    }

    public float getRadius() {
        return radius.get().getRadius();
    }

    public float getDamageMultiplier() {
        return damageScale.get().getMultiplier();
    }

    public int getWaveColor() {
        return waveColor.get();
    }

    public enum TriggerHeight implements IHasTextComponent {
        LOW(20),
        MED(15),
        HIGH(10),
        ULTRA(5);

        private final float height;
        private final ITextComponent label;

        TriggerHeight(float height) {
            this.height = height;
            this.label = new TextComponentGroup().getString(Integer.toString((int) height));
        }

        @Override
        public ITextComponent getTextComponent() {
            return label;
        }

        public float getHeight() {
            return height;
        }
    }

    public enum ImpactRadius implements IHasTextComponent {
        LOW(5),
        MED(10),
        HIGH(15),
        ULTRA(20);

        private final float radius;
        private final ITextComponent label;

        ImpactRadius(float radius) {
            this.radius = radius;
            this.label = new TextComponentGroup().getString(Integer.toString((int) radius));
        }

        @Override
        public ITextComponent getTextComponent() {
            return label;
        }

        public float getRadius() {
            return radius;
        }
    }

    public enum DamageScale implements IHasTextComponent {
        LOW(0.45F),
        MED(0.65F),
        HIGH(0.85F),
        ULTRA(1.05F);

        private final float multiplier;
        private final ITextComponent label;

        DamageScale(float multiplier) {
            this.multiplier = multiplier;
            this.label = new TextComponentGroup().getString(Float.toString(multiplier));
        }

        @Override
        public ITextComponent getTextComponent() {
            return label;
        }

        public float getMultiplier() {
            return multiplier;
        }
    }
}
