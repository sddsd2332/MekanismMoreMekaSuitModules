package moremekasuitmodules.common.content.gear.integration.appliedenergistics2;

import mekanism.api.IIncrementalEnum;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IHUDElement;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleBooleanData;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import mekanism.api.gear.config.ModuleEnumData;
import mekanism.api.math.MathUtils;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.TextComponentGroup;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.GMUTLang;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@ParametersAreNotNullByDefault
public class ModuleSmartWirelessUnit implements ICustomModule<ModuleSmartWirelessUnit> {


    private IModuleConfigItem<WirelessTerminal> wirelessTerminalIModuleConfigItem;
    private IModuleConfigItem<Boolean> wirelessMatching;

    @Override
    public void init(IModule<ModuleSmartWirelessUnit> module, ModuleConfigItemCreator configItemCreator) {
        wirelessMatching = configItemCreator.createConfigItem("wireless_Matching", GMUTLang.MODULE_AE_MATCHING, new ModuleBooleanData());
        wirelessTerminalIModuleConfigItem = configItemCreator.createConfigItem("wireless_terminal_mode", GMUTLang.MODULE_AE, new ModuleEnumData<>(WirelessTerminal.WIRELESS_TERMINAL));

    }

    @Override
    public void addHUDElements(IModule<ModuleSmartWirelessUnit> module, EntityPlayer player, Consumer<IHUDElement> hudElementAdder) {
        if (module.isEnabled()) {
            hudElementAdder.accept(ModuleHelper.get().hudElement(getMode().getHUDIcon(), getMode().getWireless(), IHUDElement.HUDColor.REGULAR));
        }
    }

    @Override
    public void changeMode(IModule<ModuleSmartWirelessUnit> module, EntityPlayer player, ItemStack stack, int shift, boolean displayChangeMessage) {
        WirelessTerminal currentMode = wirelessTerminalIModuleConfigItem.get();
        WirelessTerminal newMode = currentMode.adjust(shift);
        if (currentMode != newMode) {
            newMode = WirelessTerminal.byIndexStatic(newMode.ordinal()); //检查模式是否启用
            wirelessTerminalIModuleConfigItem.set(newMode);
            if (displayChangeMessage) {
                module.displayModeChange(player, LangUtils.localize("module.mekanism.wireless_terminal_mode"), newMode);
            }
        }
    }

    public Boolean getMatching() {
        return wirelessMatching.get();
    }

    public WirelessTerminal getMode() {
        return wirelessTerminalIModuleConfigItem.get();
    }

    public void changeMode(int mode) {
        WirelessTerminal currentMode = wirelessTerminalIModuleConfigItem.get();
        WirelessTerminal newMode = WirelessTerminal.byIndexStatic(mode);
        if (currentMode != newMode) {
            wirelessTerminalIModuleConfigItem.set(newMode);
        }
    }

    public enum WirelessTerminal implements IHasTextComponent, IIncrementalEnum<WirelessTerminal> {
        WIRELESS_TERMINAL("module.mekanism.terminal", "terminal.png"),
        WIRELESS_CRAFTING_TERMINAL("module.mekanism.crafting", "crafting.png"),
        WIRELESS_PATTERN_TERMINAL("module.mekanism.pattern", "pattern.png"),
        WIRELESS_FLUID_TERMINAL("module.mekanism.fluid", "fluid.png");

        private static final WirelessTerminal[] MODES = values();
        private final BooleanSupplier checkEnabled;
        private final String Wireless;
        private final ResourceLocation hudIcon;

        WirelessTerminal(String Wireless, String hudIcon) {
            this(() -> true, Wireless, hudIcon);
        }

        WirelessTerminal(BooleanSupplier canAdd, String Wireless, String hudIcon) {
            this.checkEnabled = canAdd;
            this.Wireless = Wireless;
            this.hudIcon = MekanismUtils.getResource(MoreMekaSuitModules.MODID, MekanismUtils.ResourceType.GUI_HUD, hudIcon);
        }


        public boolean isEnabled() {
            return checkEnabled.getAsBoolean();
        }

        public String getWireless() {
            return LangUtils.localize(Wireless);
        }

        public static WirelessTerminal byIndexStatic(int index) {
            WirelessTerminal mode = MathUtils.getByIndexMod(MODES, index);
            return mode.isEnabled() ? mode : WIRELESS_TERMINAL;
        }

        public ResourceLocation getHUDIcon() {
            return hudIcon;
        }

        @Nonnull
        @Override
        public WirelessTerminal byIndex(int index) {
            return MathUtils.getByIndexMod(MODES, index);
        }

        @Override
        public ITextComponent getTextComponent() {
            return new TextComponentGroup().string(getWireless());
        }
    }

}
