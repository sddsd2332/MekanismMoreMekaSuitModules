package moremekasuitmodules.mixin.advancedRocketry;

import mekanism.common.item.armor.ItemMekaSuitArmor;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileChemicalReactor;
import zmaster587.libVulpes.recipe.RecipesMachine;

@Mixin(TileChemicalReactor.class)
public class MixinTileChemicalReactor {

    //取消meka套的在化学结晶机里可以附魔，改用模块实现
    @Inject(method = "registerRecipe", at = @At(value = "HEAD"),remap = false, cancellable = true)
    private static void registerRecipe(RecipesMachine recipesMachine, Item item, CallbackInfo ci) {
        if (item instanceof ItemMekaSuitArmor) {
            ci.cancel();
        }
    }
}
