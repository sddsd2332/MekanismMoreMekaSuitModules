package moremekasuitmodules.common;

import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.fusioncrafting.SimpleFusionRecipe;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.lib.RecipeManager;
import mekanism.common.Mekanism;
import mekanism.common.MekanismBlocks;
import mekanism.common.MekanismItems;
import mekanism.common.integration.MekanismHooks;
import mekanism.common.tier.InductionCellTier;
import mekanism.common.util.ItemDataUtils;
import moremekasuitmodules.common.config.MoreModulesConfig;
import net.foxmcloud.draconicadditions.DAFeatures;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Optional;

public class drRecipes {

    public static void addRecipes() {
        if (Mekanism.hooks.DraconicEvolution) {
            if (MoreModulesConfig.current().config.DRrecipes.val()) {
                addDRrecipes();
            }
            if (Mekanism.hooks.DraconicAdditions) {
                if (MoreModulesConfig.current().config.DRAdditionsrecipes.val()) {
                    addDRAdditionsrecipes();
                }
            }
        }
    }

    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public static void addDRrecipes() {
        addDRSimpleFusionRecipe(new ItemStack(MekaSuitMoreModulesItem.MODULE_ENERGY_SHIELD), new ItemStack(MekanismItems.ModuleBase), 256000000L, 3,
                MekanismItems.HDPE_SHEET, MekanismItems.HDPE_SHEET, MekanismItems.HDPE_SHEET, MekanismItems.HDPE_SHEET,
                "PoloniumPellet", "PoloniumPellet", "PoloniumPellet", "PoloniumPellet",
                "alloyUltimate", "alloyUltimate", "alloyUltimate", "alloyUltimate",
                "PlutoniumPellet", "PlutoniumPellet", "PlutoniumPellet", "PlutoniumPellet",
                DEFeatures.chaoticCore, DEFeatures.chaoticCore, DEFeatures.chaoticCore, DEFeatures.chaoticCore,
                new ItemStack(DEFeatures.toolUpgrade, 1, 8), new ItemStack(DEFeatures.toolUpgrade, 1, 8), new ItemStack(DEFeatures.toolUpgrade, 1, 9), new ItemStack(DEFeatures.toolUpgrade, 1, 9),
                "circuitUltimate", "circuitUltimate", "circuitUltimate", "circuitUltimate",
                DEFeatures.chaosShard, DEFeatures.chaosShard, DEFeatures.chaosShard, DEFeatures.chaosShard,
                MekanismItems.CosmicMatter, MekanismItems.CosmicMatter, MekanismItems.CosmicMatter, MekanismItems.CosmicMatter,
                "alloyUltimate", "alloyUltimate", "alloyUltimate", "alloyUltimate",
                "PoloniumPellet", "PoloniumPellet", "PoloniumPellet", "PoloniumPellet",
                MekanismItems.HDPE_SHEET, MekanismItems.HDPE_SHEET, MekanismItems.HDPE_SHEET, MekanismItems.HDPE_SHEET);
        ItemStack particleGenerator = new ItemStack(DEFeatures.particleGenerator, 1, 2);

        addDRSimpleFusionRecipe(new ItemStack(MekaSuitMoreModulesItem.MODULE_CHAOS_RESISTANCE), new ItemStack(MekanismItems.ModuleBase), 256000000L, 3,
                particleGenerator, particleGenerator, particleGenerator, particleGenerator,
                DEFeatures.draconicBlock, DEFeatures.draconicBlock, DEFeatures.draconicBlock, DEFeatures.draconicBlock,
                new ItemStack(DEFeatures.toolUpgrade, 1, 8), new ItemStack(DEFeatures.toolUpgrade, 1, 8), new ItemStack(DEFeatures.toolUpgrade, 1, 8), new ItemStack(DEFeatures.toolUpgrade, 1, 8),
                DEFeatures.dragonHeart, DEFeatures.dragonHeart, DEFeatures.dragonHeart, DEFeatures.dragonHeart,
                new ItemStack(DEFeatures.toolUpgrade, 1, 8), new ItemStack(DEFeatures.toolUpgrade, 1, 8), new ItemStack(DEFeatures.toolUpgrade, 1, 8), new ItemStack(DEFeatures.toolUpgrade, 1, 8),
                DEFeatures.draconicBlock, DEFeatures.draconicBlock, DEFeatures.draconicBlock, DEFeatures.draconicBlock,
                particleGenerator, particleGenerator, particleGenerator, particleGenerator);

        addDRSimpleFusionRecipe(new ItemStack(MekaSuitMoreModulesItem.MODULE_CHAOS_VORTEX_STABILIZATION), new ItemStack(MekanismItems.ModuleBase), 2000000L, 2,
                DEFeatures.draconicIngot, DEFeatures.draconicIngot, DEFeatures.draconicIngot, DEFeatures.draconicIngot,
                new ItemStack(DEFeatures.chaosShard, 1, 3), new ItemStack(DEFeatures.chaosShard, 1, 3), new ItemStack(DEFeatures.chaosShard, 1, 3), new ItemStack(DEFeatures.chaosShard, 1, 3),
                new ItemStack(DEFeatures.reactorPart, 1, 4), new ItemStack(DEFeatures.reactorPart, 1, 4), new ItemStack(DEFeatures.reactorPart, 1, 4), new ItemStack(DEFeatures.reactorPart, 1, 4),
                new ItemStack(DEFeatures.chaosShard, 1, 3), new ItemStack(DEFeatures.chaosShard, 1, 3), new ItemStack(DEFeatures.chaosShard, 1, 3), new ItemStack(DEFeatures.chaosShard, 1, 3),
                DEFeatures.draconicIngot, DEFeatures.draconicIngot, DEFeatures.draconicIngot, DEFeatures.draconicIngot);
    }

    @Optional.Method(modid = MekanismHooks.DraconicAdditions_MOD_ID)
    public static void addDRAdditionsrecipes() {
        ItemStack inductionCell = new ItemStack(MekanismBlocks.BasicBlock2, 1, 3);
        if (!inductionCell.hasTagCompound()) {
            inductionCell.setTagCompound(new NBTTagCompound());
        }
        inductionCell.getTagCompound().setInteger("tier", 3);
        inductionCell.getTagCompound().setTag("mekData", new NBTTagCompound());
        ItemDataUtils.setDouble(inductionCell, "energyStored", InductionCellTier.ULTIMATE.getMaxEnergy());


        ItemStack chaoticHelm = new ItemStack(DAFeatures.chaoticHelm);
        if (!chaoticHelm.hasTagCompound()) {
            chaoticHelm.setTagCompound(new NBTTagCompound());
        }
        for (String upgrade : DAFeatures.chaoticHelm.getValidUpgrades(chaoticHelm)) {
            UpgradeHelper.setUpgradeLevel(chaoticHelm, upgrade, DAFeatures.chaoticHelm.getMaxUpgradeLevel(chaoticHelm, upgrade));
        }
        DAFeatures.chaoticHelm.modifyEnergy(chaoticHelm, DAFeatures.chaoticHelm.getMaxEnergyStored(chaoticHelm));
        chaoticHelm.getTagCompound().setBoolean("isStable", true);

        ItemStack chaoticChest = new ItemStack(DAFeatures.chaoticChest);
        if (!chaoticChest.hasTagCompound()) {
            chaoticChest.setTagCompound(new NBTTagCompound());
        }
        for (String upgrade : DAFeatures.chaoticChest.getValidUpgrades(chaoticChest)) {
            UpgradeHelper.setUpgradeLevel(chaoticChest, upgrade, DAFeatures.chaoticChest.getMaxUpgradeLevel(chaoticChest, upgrade));
        }
        DAFeatures.chaoticChest.modifyEnergy(chaoticChest, DAFeatures.chaoticChest.getMaxEnergyStored(chaoticChest));
        chaoticChest.getTagCompound().setBoolean("isStable", true);

        ItemStack chaoticLegs = new ItemStack(DAFeatures.chaoticLegs);
        if (!chaoticLegs.hasTagCompound()) {
            chaoticLegs.setTagCompound(new NBTTagCompound());
        }
        for (String upgrade : DAFeatures.chaoticLegs.getValidUpgrades(chaoticLegs)) {
            UpgradeHelper.setUpgradeLevel(chaoticLegs, upgrade, DAFeatures.chaoticLegs.getMaxUpgradeLevel(chaoticLegs, upgrade));
        }
        DAFeatures.chaoticLegs.modifyEnergy(chaoticLegs, DAFeatures.chaoticLegs.getMaxEnergyStored(chaoticLegs));
        chaoticLegs.getTagCompound().setBoolean("isStable", true);

        ItemStack chaoticBoots = new ItemStack(DAFeatures.chaoticBoots);
        if (!chaoticBoots.hasTagCompound()) {
            chaoticBoots.setTagCompound(new NBTTagCompound());
        }
        for (String upgrade : DAFeatures.chaoticBoots.getValidUpgrades(chaoticBoots)) {
            UpgradeHelper.setUpgradeLevel(chaoticBoots, upgrade, DAFeatures.chaoticBoots.getMaxUpgradeLevel(chaoticBoots, upgrade));
        }
        DAFeatures.chaoticBoots.modifyEnergy(chaoticBoots, DAFeatures.chaoticBoots.getMaxEnergyStored(chaoticBoots));
        chaoticBoots.getTagCompound().setBoolean("isStable", true);

        addDRSimpleFusionRecipe(new ItemStack(MekaSuitMoreModulesItem.MODULE_ADVANCED_INTERCEPTION_SYSTEM), new ItemStack(MekanismItems.ModuleBase), 2000000000 * 52L, 3,
                inductionCell, inductionCell, inductionCell, inductionCell,
                "PlutoniumPellet", "PlutoniumPellet", "PlutoniumPellet", "PlutoniumPellet",
                MekanismItems.CosmicMatter, MekanismItems.CosmicMatter, MekanismItems.CosmicMatter, MekanismItems.CosmicMatter,
                DAFeatures.chaosHeart, DAFeatures.chaosHeart, DAFeatures.chaosHeart, DAFeatures.chaosHeart,
                MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE, MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE, MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE, MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE,
                MekaSuitMoreModulesItem.MODULE_ENERGY_SHIELD, chaoticHelm, chaoticChest, MekaSuitMoreModulesItem.MODULE_ENERGY_SHIELD,
                "PlutoniumPellet", "PlutoniumPellet", "PlutoniumPellet", "PlutoniumPellet",
                MekaSuitMoreModulesItem.MODULE_ENERGY_SHIELD, chaoticLegs, chaoticBoots, MekaSuitMoreModulesItem.MODULE_ENERGY_SHIELD,
                MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE, MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE, MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE, MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE,
                DAFeatures.chaosHeart, DAFeatures.chaosHeart, DAFeatures.chaosHeart, DAFeatures.chaosHeart,
                DAFeatures.chaosStabilizerCore, DAFeatures.chaosStabilizerCore, DAFeatures.chaosStabilizerCore, DAFeatures.chaosStabilizerCore,
                "PlutoniumPellet", "PlutoniumPellet", "PlutoniumPellet", "PlutoniumPellet",
                inductionCell, inductionCell, inductionCell, inductionCell
        );

    }


    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public static void addDRSimpleFusionRecipe(ItemStack out, ItemStack input, long energyCost, int craftingTier, Object... ingredients) {
        IFusionRecipe recipe = new SimpleFusionRecipe(out, input, energyCost, craftingTier, ingredients);
        RecipeManager.FUSION_REGISTRY.add(recipe);
    }
}
