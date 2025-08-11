package moremekasuitmodules.common.util;


import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.fluid.IExtendedFluidHandler;
import mekanism.api.gear.ModuleData;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.registration.impl.ModuleRegistryObject;
import mekanism.common.util.ItemDataUtils;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.MoreMekaSuitModulesLang;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

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


    public static void setAllModule(ItemStack stack) {
        for (ModuleData<?> module : MekanismAPI.moduleRegistry().getValues()) {
            if (ModuleHelper.get().getSupported(stack).contains(module)) {
                setModule(stack, module);
            }
        }
    }

    public static void setModule(ItemStack stack, ModuleData<?> type) {
        if (!ItemDataUtils.hasData(stack, NBTConstants.MODULES, Tag.TAG_COMPOUND)) {
            ItemDataUtils.setCompound(stack, NBTConstants.MODULES, new CompoundTag());
        }
        ItemDataUtils.getCompound(stack, NBTConstants.MODULES).put(type.getRegistryName().toString(), new CompoundTag());
        ItemDataUtils.getCompound(stack, NBTConstants.MODULES).getCompound(type.getRegistryName().toString()).putInt(NBTConstants.AMOUNT, type.getMaxStackSize());
        ModuleHelper.get().load(stack, type).save(null);
        ModuleHelper.get().load(stack, type).onAdded(false);
    }

    public static void AddSupportedFluidsOrChemicals(ItemStack stack) {
        addFluidStack(stack, false);
        addGasStack(stack, false);
        addInfuseStack(stack, false);
        addPigmentStack(stack, false);
        addSlurryStack(stack, false);
    }

    public static void addFluidStack(ItemStack stack, boolean isInsert) {
        Optional<IFluidHandlerItem> fluidCapability = FluidUtil.getFluidHandler(stack).resolve();
        if (fluidCapability.isPresent()) {
            IFluidHandlerItem fluidHandler = fluidCapability.get();
            if (fluidHandler instanceof IExtendedFluidHandler fluidHandlerItem) {
                for (int tank = 0; tank < fluidHandlerItem.getTanks(); tank++) {
                    for (Fluid fluid : ForgeRegistries.FLUIDS) {
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
    }

    public static void addGasStack(ItemStack stack, boolean isInsert) {
        Optional<IGasHandler> cap = stack.getCapability(Capabilities.GAS_HANDLER).resolve();
        if (cap.isPresent()) {
            IGasHandler handler = cap.get();
            for (int tank = 0; tank < handler.getTanks(); tank++) {
                long getTankCapacity = handler.getTankCapacity(tank);
                for (Gas gas : MekanismAPI.gasRegistry()) {
                    GasStack gasStack = new GasStack(gas, getTankCapacity);
                    if (handler.isValid(tank, gasStack)) {
                        if (isInsert) {
                            handler.insertChemical(tank, gasStack, Action.EXECUTE);
                        } else {
                            handler.setChemicalInTank(tank, gasStack);
                        }
                    }
                }
            }
        }
    }

    public static void addInfuseStack(ItemStack stack, boolean isInsert) {
        Optional<IInfusionHandler> cap = stack.getCapability(Capabilities.INFUSION_HANDLER).resolve();
        if (cap.isPresent()) {
            IInfusionHandler handler = cap.get();
            for (int tank = 0; tank < handler.getTanks(); tank++) {
                long getTankCapacity = handler.getTankCapacity(tank);
                for (InfuseType infuse : MekanismAPI.infuseTypeRegistry()) {
                    InfusionStack infuseStack = new InfusionStack(infuse, getTankCapacity);
                    if (handler.isValid(tank, infuseStack)) {
                        if (isInsert) {
                            handler.insertChemical(tank, infuseStack, Action.EXECUTE);
                        } else {
                            handler.setChemicalInTank(tank, infuseStack);
                        }
                    }
                }
            }
        }
    }

    public static void addPigmentStack(ItemStack stack, boolean isInsert) {
        Optional<IPigmentHandler> cap = stack.getCapability(Capabilities.PIGMENT_HANDLER).resolve();
        if (cap.isPresent()) {
            IPigmentHandler handler = cap.get();
            for (int tank = 0; tank < handler.getTanks(); tank++) {
                long getTankCapacity = handler.getTankCapacity(tank);
                for (Pigment pigment : MekanismAPI.pigmentRegistry()) {
                    PigmentStack pigmentStack = new PigmentStack(pigment, getTankCapacity);
                    if (handler.isValid(tank, pigmentStack)) {
                        if (isInsert) {
                            handler.insertChemical(tank, pigmentStack, Action.EXECUTE);
                        } else {
                            handler.setChemicalInTank(tank, pigmentStack);
                        }
                    }
                }
            }
        }
    }

    public static void addSlurryStack(ItemStack stack, boolean isInsert) {
        Optional<ISlurryHandler> cap = stack.getCapability(Capabilities.SLURRY_HANDLER).resolve();
        if (cap.isPresent()) {
            ISlurryHandler handler = cap.get();
            for (int tank = 0; tank < handler.getTanks(); tank++) {
                long getTankCapacity = handler.getTankCapacity(tank);
                for (Slurry slurry : MekanismAPI.slurryRegistry()) {
                    SlurryStack slurryStack = new SlurryStack(slurry, getTankCapacity);
                    if (handler.isValid(tank, slurryStack)) {
                        if (isInsert) {
                            handler.insertChemical(tank, slurryStack, Action.EXECUTE);
                        } else {
                            handler.setChemicalInTank(tank, slurryStack);
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
