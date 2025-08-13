package moremekasuitmodules.common.util;

import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.fluid.IExtendedFluidHandler;
import mekanism.api.gear.ModuleData;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.content.gear.IModuleItem;
import mekanism.common.content.gear.ModuleContainer;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.registration.impl.ModuleRegistryObject;
import mekanism.common.registries.MekanismDataComponents;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.MoreMekaSuitModulesLang;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

@SuppressWarnings("removal")
public class MoreMekaSuitModulesUtils {

    public static ResourceLocation getResource(ResourceType type, String name) {
        return MoreMekaSuitModules.rl(type.getPrefix() + name);
    }

    public static Component ShieldModuleWarning() {
        return moduleWarning(MekaSuitMoreModules.ENERGY_SHIELD_UNIT, MoreMekaSuitModulesLang.MODULE_SHIELD_DEPLETED);
    }

    public static Component moduleWarning(ModuleRegistryObject<?> moduleData) {
        return moduleWarning(moduleData, MoreMekaSuitModulesLang.MODULE_EMERGENCY_RESCUE);
    }

    public static Component moduleWarning(ModuleRegistryObject<?> moduleData, Object message) {
        return MekanismLang.LOG_FORMAT.translateColored(EnumColor.RED, moduleData, EnumColor.YELLOW, message);
    }



    public static void addSupportedFluidsOrChemicals(ItemStack stack) {
        addFluidStack(stack, false);
        addChemicals(stack, false);
    }


    public static void addFluidStack(ItemStack stack, boolean isInsert) {
        IFluidHandlerItem fluidHandler = Capabilities.FLUID.getCapability(stack);
        if (fluidHandler instanceof IExtendedFluidHandler fluidHandlerItem) {
            for (int tank = 0; tank < fluidHandlerItem.getTanks(); tank++) {
                for (Fluid fluid : BuiltInRegistries.FLUID) {
                    int getFluidTankCapacity = fluidHandlerItem.getTankCapacity(tank);
                    FluidStack fluidStack = new FluidStack(fluid, getFluidTankCapacity);
                    if (fluidHandlerItem.isFluidValid(tank, fluidStack)) {
                        if (isInsert) {
                            fluidHandlerItem.insertFluid(tank, fluidStack, Action.EXECUTE);
                        } else {
                            fluidHandlerItem.setFluidInTank(tank, fluidStack);
                        }

                    }
                }
            }
        }
    }

    public static void addChemicals(ItemStack stack, boolean isInsert) {
        IChemicalHandler handler = Capabilities.CHEMICAL.getCapability(stack);
        if (handler != null) {
            for (int tank = 0; tank < handler.getChemicalTanks(); tank++) {
                long getTankCapacity = handler.getChemicalTankCapacity(tank);
                for (Chemical chemical : MekanismAPI.CHEMICAL_REGISTRY) {
                    ChemicalStack chemicalStack = new ChemicalStack(chemical, getTankCapacity);
                    if (handler.isValid(tank, chemicalStack)) {
                        if (isInsert) {
                            handler.insertChemical(tank, chemicalStack, Action.EXECUTE);
                        } else {
                            handler.setChemicalInTank(tank, chemicalStack);
                        }
                    }
                }
            }
        }
    }

    public enum ResourceType {
        GUI("gui"),
        GUI_BUTTON("gui/button"),
        GUI_BAR("gui/bar"),
        GUI_GAUGE("gui/gauge"),
        GUI_HUD("gui/hud"),
        GUI_ICONS("gui/icons"),
        GUI_PROGRESS("gui/progress"),
        GUI_RADIAL("gui/radial"),
        GUI_SLOT("gui/slot"),
        GUI_TAB("gui/tabs"),
        SOUND("sound"),
        RENDER("render"),
        TEXTURE_BLOCKS("textures/block"),
        TEXTURE_ITEMS("textures/item"),
        MODEL("models"),
        INFUSE("infuse"),
        PIGMENT("pigment"),
        SLURRY("slurry");

        private final String prefix;

        ResourceType(String s) {
            prefix = s;
        }

        public String getPrefix() {
            return prefix + "/";
        }
    }
}
