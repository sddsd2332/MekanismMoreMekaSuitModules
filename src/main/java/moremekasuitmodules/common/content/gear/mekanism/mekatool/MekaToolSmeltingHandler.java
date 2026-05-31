package moremekasuitmodules.common.content.gear.mekanism.mekatool;

import mekanism.api.energy.IEnergizedItem;
import mekanism.api.gear.IModule;
import mekanism.common.MekanismModules;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.Module;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.config.MoreModulesConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class MekaToolSmeltingHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if (player == null || player.world.isRemote) {
            return;
        }
        ItemStack tool = player.getHeldItemMainhand();
        if (!isSmeltingEnabled(tool)) {
            return;
        }
        disableSilkTouch(tool);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        EntityPlayer player = event.getHarvester();
        if (player == null || event.getWorld().isRemote || event.getDrops().isEmpty()) {
            return;
        }
        ItemStack tool = player.getHeldItemMainhand();
        if (!isSmeltingEnabled(tool)) {
            return;
        }
        disableSilkTouch(tool);
        double usage = MoreModulesConfig.current().config.mekaToolEnergyUsageSmelting.val();
        DropConversion conversion = convertStacks(player, tool, usage, event.getDrops());
        if (conversion.changed) {
            event.getDrops().clear();
            event.getDrops().addAll(conversion.stacks);
            event.setDropChance(1.0F);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntityLiving().world.isRemote || event.getDrops().isEmpty()) {
            return;
        }
        Entity trueSource = event.getSource().getTrueSource();
        if (!(trueSource instanceof EntityPlayer player)) {
            return;
        }
        ItemStack tool = player.getHeldItemMainhand();
        if (!isSmeltingEnabled(tool)) {
            return;
        }
        double usage = MoreModulesConfig.current().config.mekaToolEnergyUsageSmelting.val();
        List<ItemStack> stacks = new ArrayList<>();
        for (EntityItem drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (!stack.isEmpty()) {
                stacks.add(stack.copy());
            }
        }
        DropConversion conversion = convertStacks(player, tool, usage, stacks);
        if (conversion.changed) {
            event.getDrops().clear();
            for (ItemStack stack : conversion.stacks) {
                if (!stack.isEmpty()) {
                    event.getDrops().add(new EntityItem(event.getEntityLiving().world, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, stack));
                }
            }
        }
    }

    private static DropConversion convertStacks(EntityPlayer player, ItemStack tool, double usage, List<ItemStack> drops) {
        List<ItemStack> converted = new ArrayList<>();
        boolean changed = false;
        for (ItemStack drop : drops) {
            if (drop.isEmpty()) {
                continue;
            }
            ItemStack result = FurnaceRecipes.instance().getSmeltingResult(drop);
            if (result.isEmpty()) {
                converted.add(drop);
                continue;
            }
            int smeltable = getAffordableCount(player, tool, usage, drop.getCount());
            if (smeltable <= 0) {
                converted.add(drop);
                continue;
            }
            ItemStack smelted = result.copy();
            smelted.setCount(result.getCount() * smeltable);
            converted.add(smelted);
            if (smeltable < drop.getCount()) {
                ItemStack remainder = drop.copy();
                remainder.setCount(drop.getCount() - smeltable);
                converted.add(remainder);
            }
            changed = true;
        }
        return new DropConversion(converted, changed);
    }

    private static class DropConversion {
        private final List<ItemStack> stacks;
        private final boolean changed;

        private DropConversion(List<ItemStack> stacks, boolean changed) {
            this.stacks = stacks;
            this.changed = changed;
        }
    }

    private static boolean isSmeltingEnabled(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof IModuleContainerItem container)) {
            return false;
        }
        IModule<?> module = container.getModule(stack, MekaSuitMoreModules.SMELTING_UNIT);
        return module != null && module.isEnabled();
    }

    private static void disableSilkTouch(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof IModuleContainerItem container) || !container.isModuleEnabled(stack, MekanismModules.SILK_TOUCH_UNIT)) {
            return;
        }
        IModule<?> silk = container.getModule(stack, MekanismModules.SILK_TOUCH_UNIT);
        if (silk instanceof Module<?> module) {
            module.setDisabledForce(false);
        }
    }

    private static int getAffordableCount(EntityPlayer player, ItemStack tool, double usage, int count) {
        if (count <= 0) {
            return 0;
        }
        if (usage <= 0 || player.isCreative()) {
            return count;
        }
        if (!(tool.getItem() instanceof IEnergizedItem energizedItem)) {
            return 0;
        }
        double available = energizedItem.extract(tool, usage * count, false);
        int affordable = Math.min(count, (int) Math.floor(available / usage));
        if (affordable > 0) {
            energizedItem.extract(tool, usage * affordable, true);
        }
        return affordable;
    }
}
