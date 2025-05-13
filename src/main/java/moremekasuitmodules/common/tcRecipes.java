package moremekasuitmodules.common;


import mekanism.common.MekanismItems;
import moremekasuitmodules.common.config.MoreModulesConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.ItemsTC;

public class tcRecipes {

    public static void addRecipes() {
        if (Loader.isModLoaded("thaumcraft") && MoreModulesConfig.current().config.TCRecipes.val()) {
            addInfusionCraftingRecipe();
        }
    }


    @Optional.Method(modid = "thaumcraft")
    public static void addInfusionCraftingRecipe() {
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:goggles_of_revealing_unit"), new InfusionRecipe("INFUSION", new ItemStack(MekaSuitMoreModulesItem.MODULE_GOGGLES_OF_REVEALING), 6, new AspectList().add(Aspect.METAL, 100).add(Aspect.SENSES, 100).add(Aspect.ENERGY, 100).add(Aspect.ELDRITCH, 100).add(Aspect.VOID, 100), new ItemStack(MekanismItems.ModuleBase), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.goggles, 1, 32767), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.fabric)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:magic_optimization_unit"), new InfusionRecipe("INFUSION", new ItemStack(MekaSuitMoreModulesItem.MODULE_MAGIC_OPTIMIZATION), 2, new AspectList().add(Aspect.METAL, 100).add(Aspect.ENERGY, 100).add(Aspect.MAGIC, 100).add(Aspect.AURA, 100), new ItemStack(MekanismItems.ModuleBase), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:warp_clear_base_unit"), new InfusionRecipe("BATHSALTS", new ItemStack(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_BASE), 2, new AspectList().add(Aspect.MIND, 100).add(Aspect.SENSES, 100).add(Aspect.ORDER, 100).add(Aspect.ELDRITCH, 100), new ItemStack(MekanismItems.ModuleBase), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.bathSalts), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.bathSalts), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.bathSalts), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.bathSalts)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:warp_clear_advanced_unit"), new InfusionRecipe("SANESOAP", new ItemStack(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_ADVANCED), 4, new AspectList().add(Aspect.MIND, 200).add(Aspect.SENSES, 200).add(Aspect.ORDER, 200).add(Aspect.ELDRITCH, 200), new ItemStack(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_BASE), new ItemStack(ItemsTC.sanitySoap), new ItemStack(ItemsTC.bathSalts), new ItemStack(ItemsTC.sanitySoap), new ItemStack(ItemsTC.bathSalts), new ItemStack(ItemsTC.sanitySoap), new ItemStack(ItemsTC.bathSalts), new ItemStack(ItemsTC.sanitySoap), new ItemStack(ItemsTC.bathSalts)));
        AspectList aspectList = new AspectList();
        aspectList.add(Aspect.AIR, 1000).add(Aspect.EARTH, 1000).add(Aspect.FIRE, 1000).add(Aspect.WATER, 1000).add(Aspect.ORDER, 1000).add(Aspect.ENTROPY, 1000);

        if (MoreModulesConfig.current().config.TCAspectRecipes.val()) {
            Aspect.aspects.forEach((string, aspect) -> {
                if (!aspect.isPrimal()) {
                    aspectList.add(aspect, 1000);
                }
            });
        }

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:warp_clear_ultimate_unit"), new InfusionRecipe("", new ItemStack(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_ULTIMATE), 10, aspectList, new ItemStack(MekanismItems.ModuleBase),
                new ItemStack(ItemsTC.causalityCollapser), new ItemStack(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_BASE), new ItemStack(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_ADVANCED), new ItemStack(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_BASE),
                new ItemStack(ItemsTC.causalityCollapser), new ItemStack(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_BASE), new ItemStack(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_ADVANCED), new ItemStack(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_BASE),
                new ItemStack(ItemsTC.causalityCollapser), new ItemStack(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_BASE), new ItemStack(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_ADVANCED), new ItemStack(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_BASE),
                new ItemStack(ItemsTC.causalityCollapser), new ItemStack(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_BASE), new ItemStack(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_ADVANCED), new ItemStack(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_BASE)));
    }

}
