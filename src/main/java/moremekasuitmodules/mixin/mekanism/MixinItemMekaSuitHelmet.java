package moremekasuitmodules.mixin.mekanism;

import mekanism.api.EnumColor;
import mekanism.api.gas.IGasItem;
import mekanism.api.gear.IModule;
import mekanism.api.mixninapi.EnderMaskMixinHelp;
import mekanism.common.interfaces.IOverlayRenderAware;
import mekanism.common.item.armor.ItemMekaSuitArmor;
import mekanism.common.item.armor.ItemMekaSuitHelmet;
import mekanism.common.util.LangUtils;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.ModuleInfiniteInterceptionAndRescueSystemUnit;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemMekaSuitHelmet.class)
public abstract class MixinItemMekaSuitHelmet extends ItemMekaSuitArmor implements IGasItem, EnderMaskMixinHelp, IOverlayRenderAware {

    public MixinItemMekaSuitHelmet(int renderIndex, EntityEquipmentSlot slot) {
        super(renderIndex, slot);
    }


    @SideOnly(Side.CLIENT)
    @Inject(method = "addInformation", at = @At("TAIL"), remap = false)
    public void addInfiniteModule(ItemStack stack, World world, List<String> tooltip, CallbackInfo ci) {
        addInfiniteModule(stack, tooltip);
    }


    @Unique
    private void addInfiniteModule(ItemStack stack, List<String> tooltip) {
        IModule<ModuleInfiniteInterceptionAndRescueSystemUnit> module = getModule(stack, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
        if (module != null) {
            tooltip.add(EnumColor.ORANGE + LangUtils.localize("tooltip.moremekasuitmodules.warning"));
            boolean source = module.getCustomInstance().getSource();
            boolean sourceIndirect = module.getCustomInstance().getSourceIndirect();
            boolean chunkRemove = module.getCustomInstance().getChunkRemove();
            if (chunkRemove) {
                tooltip.add(LangUtils.localize("tooltip.moremekasuitmodules.rangeExclude"));
            }
            if (source) {
                tooltip.add(LangUtils.localize("tooltip.moremekasuitmodules.damage_true_source_exclude"));
            }
            if (sourceIndirect) {
                tooltip.add(LangUtils.localize("tooltip.moremekasuitmodules.damage_true_source_exclude_indirect"));
            }
            if (source || sourceIndirect) {
                tooltip.add(EnumColor.ORANGE + LangUtils.localize("tooltip.moremekasuitmodules.warning"));
                tooltip.add(EnumColor.ORANGE + LangUtils.localize("tooltip.moremekasuitmodules.damage_true_source_exclude.warning"));
            }
        }
    }


}
