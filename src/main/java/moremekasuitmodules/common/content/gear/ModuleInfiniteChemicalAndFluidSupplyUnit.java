package moremekasuitmodules.common.content.gear;

import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
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
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleBooleanData;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.integration.curios.CuriosIntegration;
import moremekasuitmodules.common.MoreMekaSuitModulesLang;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ParametersAreNotNullByDefault
public class ModuleInfiniteChemicalAndFluidSupplyUnit implements ICustomModule<ModuleInfiniteChemicalAndFluidSupplyUnit> {

    private IModuleConfigItem<Boolean> isArmor;
    private IModuleConfigItem<Boolean> isInventory;
    private IModuleConfigItem<Boolean> isOffhand;
    private IModuleConfigItem<Boolean> isCurios;


    @Override
    public void init(IModule<ModuleInfiniteChemicalAndFluidSupplyUnit> module, ModuleConfigItemCreator configItemCreator) {
        isArmor = configItemCreator.createConfigItem("armor_supply", MoreMekaSuitModulesLang.MODULE_SUPPLY_ARMOR, new ModuleBooleanData());
        isInventory = configItemCreator.createConfigItem("inventory_supply", MoreMekaSuitModulesLang.MODULE_SUPPLY_INVENTORY, new ModuleBooleanData(false));
        isOffhand = configItemCreator.createConfigItem("offhand_supply", MoreMekaSuitModulesLang.MODULE_SUPPLY_OFFHAND, new ModuleBooleanData(false));
        if (Mekanism.hooks.CuriosLoaded) {
            isCurios = configItemCreator.createConfigItem("curios_supply", MoreMekaSuitModulesLang.MODULE_SUPPLY_CURIOS, new ModuleBooleanData(false));
        }

    }

    @Override
    public void tickClient(IModule<ModuleInfiniteChemicalAndFluidSupplyUnit> module, Player player) {
        this.tickServer(module, player);
    }

    @Override
    public void tickServer(IModule<ModuleInfiniteChemicalAndFluidSupplyUnit> module, Player player) {
        if (module.isEnabled()) {
            List<ItemStack> inventory = new ArrayList<>();
            if (isArmor.get()) {
                inventory.addAll(player.getInventory().armor);
            }
            if (isInventory.get()) {
                inventory.addAll(player.getInventory().items);
            }
            if (isOffhand.get()) {
                inventory.addAll(player.getInventory().offhand);
            }
            if (Mekanism.hooks.CuriosLoaded) {
                if (isCurios.get()) {
                    getCurios(inventory, player);
                }
            }
            if (!inventory.isEmpty()){
                inventory.forEach(stack -> {
                    addFluidStack(stack);
                    addGasStack(stack);
                    addInfuseStack(stack);
                    addPigmentStack(stack);
                    addSlurryStack(stack);
                });
            }
        }
    }

    public void getCurios(List<ItemStack> stacks, Player player) {
        Optional<? extends IItemHandler> invOptional = CuriosIntegration.getCuriosInventory(player);
        if (invOptional.isPresent()) {
            IItemHandler inv = invOptional.get();
            for (int i = 0, slots = inv.getSlots(); i < slots; i++) {
                stacks.add(inv.getStackInSlot(i));
            }
        }
    }

    private void addFluidStack(ItemStack stack) {
        Optional<IFluidHandlerItem> fluidCapability = FluidUtil.getFluidHandler(stack).resolve();
        if (fluidCapability.isPresent()) {
            IFluidHandlerItem fluidHandler = fluidCapability.get();
            if (fluidHandler instanceof IExtendedFluidHandler fluidHandlerItem) {
                for (int tank = 0; tank < fluidHandlerItem.getTanks(); tank++) {
                    for (Fluid fluid : ForgeRegistries.FLUIDS) {
                        int getFluidTankCapacity = fluidHandlerItem.getTankCapacity(tank);
                        FluidStack fluidStack = new FluidStack(fluid, getFluidTankCapacity);
                        if (fluidHandlerItem.isFluidValid(tank, fluidStack)) {
                            fluidHandlerItem.insertFluid(tank, fluidStack, Action.EXECUTE);
                        }
                    }
                }
            }
        }
    }

    private void addGasStack(ItemStack stack) {
        Optional<IGasHandler> cap = stack.getCapability(Capabilities.GAS_HANDLER).resolve();
        if (cap.isPresent()) {
            IGasHandler handler = cap.get();
            for (int tank = 0; tank < handler.getTanks(); tank++) {
                long getTankCapacity = handler.getTankCapacity(tank);
                for (Gas gas : MekanismAPI.gasRegistry()) {
                    GasStack gasStack = new GasStack(gas, getTankCapacity);
                    if (handler.isValid(tank, gasStack)) {
                        handler.insertChemical(tank, gasStack, Action.EXECUTE);
                    }
                }
            }
        }
    }


    private void addInfuseStack(ItemStack stack) {
        Optional<IInfusionHandler> cap = stack.getCapability(Capabilities.INFUSION_HANDLER).resolve();
        if (cap.isPresent()) {
            IInfusionHandler handler = cap.get();
            for (int tank = 0; tank < handler.getTanks(); tank++) {
                long getTankCapacity = handler.getTankCapacity(tank);
                for (InfuseType infuse : MekanismAPI.infuseTypeRegistry()) {
                    InfusionStack infuseStack = new InfusionStack(infuse, getTankCapacity);
                    if (handler.isValid(tank, infuseStack)) {
                        handler.insertChemical(tank, infuseStack, Action.EXECUTE);
                    }
                }
            }
        }
    }

    private void addPigmentStack(ItemStack stack) {
        Optional<IPigmentHandler> cap = stack.getCapability(Capabilities.PIGMENT_HANDLER).resolve();
        if (cap.isPresent()) {
            IPigmentHandler handler = cap.get();
            for (int tank = 0; tank < handler.getTanks(); tank++) {
                long getTankCapacity = handler.getTankCapacity(tank);
                for (Pigment pigment : MekanismAPI.pigmentRegistry()) {
                    PigmentStack pigmentStack = new PigmentStack(pigment, getTankCapacity);
                    if (handler.isValid(tank, pigmentStack)) {
                        handler.insertChemical(tank, pigmentStack, Action.EXECUTE);
                    }
                }
            }
        }
    }

    private void addSlurryStack(ItemStack stack) {
        Optional<ISlurryHandler> cap = stack.getCapability(Capabilities.SLURRY_HANDLER).resolve();
        if (cap.isPresent()) {
            ISlurryHandler handler = cap.get();
            for (int tank = 0; tank < handler.getTanks(); tank++) {
                long getTankCapacity = handler.getTankCapacity(tank);
                for (Slurry slurry : MekanismAPI.slurryRegistry()) {
                    SlurryStack slurryStack = new SlurryStack(slurry, getTankCapacity);
                    if (handler.isValid(tank, slurryStack)) {
                        handler.insertChemical(tank, slurryStack, Action.EXECUTE);
                    }
                }
            }
        }
    }

}
