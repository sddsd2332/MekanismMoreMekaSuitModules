package moremekasuitmodules.mixin.botania;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.botania.api.mana.ICreativeManaProvider;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;
import vazkii.botania.common.item.ItemManaTablet;
import vazkii.botania.common.item.ItemMod;

@Mixin(ItemManaTablet.class)
public abstract class MixinItemManaTablet extends ItemMod implements IManaItem, ICreativeManaProvider, IManaTooltipDisplay {


    public MixinItemManaTablet(String name) {
        super(name);
    }

    /**
     * @author sddsd2332
     * @reason 修复 hsvToRGB 超出问题
     */
    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MathHelper.hsvToRGB(Math.max(Math.max(0.0F, (float) (1 - getDurabilityForDisplay(stack))), 1F) / 3.0F, 1.0F, 1.0F);
    }
}
