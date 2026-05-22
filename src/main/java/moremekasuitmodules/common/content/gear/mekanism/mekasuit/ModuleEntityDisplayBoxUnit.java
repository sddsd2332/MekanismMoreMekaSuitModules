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
import mekanism.common.MekanismLang;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.MoreMekaSuitModulesLang;
import net.minecraft.util.text.ITextComponent;

@ParametersAreNotNullByDefault
public class ModuleEntityDisplayBoxUnit implements ICustomModule<ModuleEntityDisplayBoxUnit> {

    public static final int DEFAULT_BOX_COLOR = 0xFF3CFE9A;
    public static final int DEFAULT_NAME_COLOR = 0xFFFFFFFF;
    public static final int DEFAULT_DISTANCE_COLOR = 0xFFFFFF55;

    private IModuleConfigItem<Range> range;
    private IModuleConfigItem<MaxBoxes> maxBoxes;
    private IModuleConfigItem<HealthDisplay> healthDisplay;
    private IModuleConfigItem<Integer> boxColor;
    private IModuleConfigItem<Integer> nameColor;
    private IModuleConfigItem<Integer> distanceColor;

    @Override
    public void init(IModule<ModuleEntityDisplayBoxUnit> module, ModuleConfigItemCreator configItemCreator) {
        range = configItemCreator.createConfigItem("range", MekanismLang.MODULE_RANGE, new ModuleEnumData<>(Range.OFF));
        maxBoxes = configItemCreator.createConfigItem("max_boxes", MoreMekaSuitModulesLang.MODULE_MAX_BOXES, new ModuleEnumData<>(MaxBoxes.MED));
        healthDisplay = configItemCreator.createConfigItem("health_display", MoreMekaSuitModulesLang.MODULE_HEALTH_DISPLAY, new ModuleEnumData<>(HealthDisplay.OFF));
        boxColor = configItemCreator.createConfigItem("box_color", MoreMekaSuitModulesLang.MODULE_BOX_COLOR, ModuleColorData.argb(DEFAULT_BOX_COLOR));
        nameColor = configItemCreator.createConfigItem("name_color", MoreMekaSuitModulesLang.MODULE_NAME_COLOR, ModuleColorData.argb(DEFAULT_NAME_COLOR));
        distanceColor = configItemCreator.createConfigItem("distance_color", MoreMekaSuitModulesLang.MODULE_DISTANCE_COLOR, ModuleColorData.argb(DEFAULT_DISTANCE_COLOR));
    }

    public int getRange() {
        return range.get().getRadius();
    }

    public int getBoxColor() {
        return boxColor.get();
    }

    public int getNameColor() {
        return nameColor.get();
    }

    public int getDistanceColor() {
        return distanceColor.get();
    }

    public int getMaxBoxes() {
        return maxBoxes.get().getMaxBoxes();
    }

    public HealthDisplay getHealthDisplay() {
        return healthDisplay.get();
    }

    public enum Range implements IHasTextComponent {
        OFF(0),
        LOW(8),
        MED(16),
        HIGH(32),
        ULTRA(64);

        private final int radius;
        private final ITextComponent label;

        Range(int radius) {
            this.radius = radius;
            this.label = new TextComponentGroup().getString(Integer.toString(radius));
        }

        @Override
        public ITextComponent getTextComponent() {
            return label;
        }

        public int getRadius() {
            return radius;
        }
    }

    public enum MaxBoxes implements IHasTextComponent {
        LOW(64),
        MED(256),
        HIGH(512),
        ULTRA(1024),
        ALL(Integer.MAX_VALUE);

        private final int maxBoxes;
        private final ITextComponent label;

        MaxBoxes(int maxBoxes) {
            this.maxBoxes = maxBoxes;
            this.label = new TextComponentGroup().getString(maxBoxes == Integer.MAX_VALUE ? "All" : Integer.toString(maxBoxes));
        }

        @Override
        public ITextComponent getTextComponent() {
            return label;
        }

        public int getMaxBoxes() {
            return maxBoxes;
        }
    }

    public enum HealthDisplay implements IHasTextComponent {
        OFF("module.mekanism.health_display.off", false, false),
        BAR("module.mekanism.health_display.bar", true, false),
        TEXT("module.mekanism.health_display.text", false, true),
        BOTH("module.mekanism.health_display.both", true, true);

        private final ITextComponent label;
        private final boolean bar;
        private final boolean text;

        HealthDisplay(String translationKey, boolean bar, boolean text) {
            this.label = new TextComponentGroup().translation(translationKey);
            this.bar = bar;
            this.text = text;
        }

        @Override
        public ITextComponent getTextComponent() {
            return label;
        }

        public boolean shouldDrawBar() {
            return bar;
        }

        public boolean shouldDrawText() {
            return text;
        }
    }
}
