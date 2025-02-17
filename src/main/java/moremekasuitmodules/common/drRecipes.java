package moremekasuitmodules.common;

import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.fusioncrafting.SimpleFusionRecipe;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.lib.RecipeManager;
import mekanism.common.Mekanism;
import mekanism.common.MekanismBlocks;
import mekanism.common.MekanismItems;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.MekanismHooks;
import mekanism.common.tier.InductionCellTier;
import mekanism.common.util.ItemDataUtils;
import net.foxmcloud.draconicadditions.DAFeatures;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Optional;

public class drRecipes {

    public static void addRecipes() {
        if (Mekanism.hooks.DraconicEvolution) {
            if (MekanismConfig.current().mekce.DRrecipes.val()) {
                addDRrecipes();
            }
            if (Mekanism.hooks.DraconicAdditions) {
                if (MekanismConfig.current().mekce.DRAdditionsrecipes.val()) {
                    addDRAdditionsrecipes();
                }
            }
        }
    }

    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public static void addDRrecipes() {
        addDRSimpleFusionRecipe(new ItemStack(MekaSuitMoreModulesItem.MODULE_ENERGY_SHIELD), new ItemStack(MekanismItems.ModuleBase), 256000000L, 3,
                DEFeatures.chaoticCore, "PoloniumPellet", MekanismItems.HDPE_SHEET, DEFeatures.chaosShard, MekanismItems.HDPE_SHEET, "PoloniumPellet", DEFeatures.chaoticCore,
                "PoloniumPellet", MekanismItems.CosmicMatter, "alloyUltimate", "circuitUltimate", "alloyUltimate", MekanismItems.CosmicMatter, "PoloniumPellet",
                MekanismItems.HDPE_SHEET, "alloyUltimate", "PlutoniumPellet", new ItemStack(DEFeatures.toolUpgrade, 1, 8), "PlutoniumPellet", "alloyUltimate", MekanismItems.HDPE_SHEET,
                DEFeatures.chaosShard, "circuitUltimate", new ItemStack(DEFeatures.toolUpgrade, 1, 9), new ItemStack(DEFeatures.toolUpgrade, 1, 9), "circuitUltimate", DEFeatures.chaosShard,
                MekanismItems.HDPE_SHEET, "alloyUltimate", "PlutoniumPellet", new ItemStack(DEFeatures.toolUpgrade, 1, 8), "PlutoniumPellet", "alloyUltimate", MekanismItems.HDPE_SHEET,
                "PoloniumPellet", MekanismItems.CosmicMatter, "alloyUltimate", "circuitUltimate", "alloyUltimate", MekanismItems.CosmicMatter, "PoloniumPellet",
                DEFeatures.chaoticCore, "PoloniumPellet", MekanismItems.HDPE_SHEET, DEFeatures.chaosShard, MekanismItems.HDPE_SHEET, "PoloniumPellet", DEFeatures.chaoticCore);

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


        addDRSimpleFusionRecipe(new ItemStack(MekaSuitMoreModulesItem.MODULE_ADVANCED_INTERCEPTION_SYSTEM), new ItemStack(MekanismItems.ModuleBase), Integer.MAX_VALUE * 53L, 3,
                DAFeatures.chaosHeart, inductionCell, DAFeatures.chaosStabilizerCore, inductionCell, DAFeatures.chaosHeart,
                DAFeatures.chaosHeart, chaoticHelm, "PlutoniumPellet", chaoticChest, DAFeatures.chaosHeart,
                inductionCell, MekanismItems.CosmicMatter, "PoloniumPellet", MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE, "PoloniumPellet", MekanismItems.CosmicMatter, inductionCell,
                "PoloniumPellet", MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE, MekaSuitMoreModulesItem.MODULE_ENERGY_SHIELD, MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE, "PoloniumPellet",
                DAFeatures.chaosStabilizerCore, "PlutoniumPellet", MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE, MekaSuitMoreModulesItem.MODULE_ENERGY_SHIELD, "circuitUltimate", MekaSuitMoreModulesItem.MODULE_ENERGY_SHIELD, MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE, "PlutoniumPellet", DAFeatures.chaosStabilizerCore,
                "PoloniumPellet", MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE, MekaSuitMoreModulesItem.MODULE_ENERGY_SHIELD, MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE, "PoloniumPellet",
                inductionCell, MekanismItems.CosmicMatter, "PoloniumPellet", MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE, "PoloniumPellet", MekanismItems.CosmicMatter, inductionCell,
                DAFeatures.chaosHeart, chaoticLegs, "PlutoniumPellet", chaoticBoots, DAFeatures.chaosHeart,
                DAFeatures.chaosHeart, inductionCell, DAFeatures.chaosStabilizerCore, inductionCell, DAFeatures.chaosHeart
        );

    }


    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public static void addDRSimpleFusionRecipe(ItemStack out, ItemStack input, long energyCost, int craftingTier, Object... ingredients) {
        IFusionRecipe recipe = new SimpleFusionRecipe(out, input, energyCost, craftingTier, ingredients);
        RecipeManager.FUSION_REGISTRY.add(recipe);
    }
}
