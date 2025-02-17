package moremekasuitmodules.mixin.mekanism;

import cofh.redstoneflux.api.IEnergyContainerItem;
import com.brandon3055.draconicevolution.items.armor.ICustomArmor;
import forestry.api.apiculture.IArmorApiarist;
import ic2.api.item.IHazmatLike;
import ic2.api.item.ISpecialElectricItem;
import mekanism.api.energy.IEnergizedItem;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasItem;
import mekanism.api.gear.IModule;
import mekanism.api.gear.Magnetic;
import mekanism.common.Mekanism;
import mekanism.common.MekanismFluids;
import mekanism.common.MekanismItems;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.integration.MekanismHooks;
import mekanism.common.item.armor.ItemMekaSuitArmor;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.util.ItemNBTHelper;
import moremekasuitmodules.common.MekaSuitMoreModules;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IVisDiscountGear;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mixin(ItemMekaSuitArmor.class)
@Optional.InterfaceList({
        @Optional.Interface(iface = "ic2.api.item.ISpecialElectricItem", modid = MekanismHooks.IC2_MOD_ID),
        @Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyContainerItem", modid = MekanismHooks.REDSTONEFLUX_MOD_ID),
        @Optional.Interface(iface = "ic2.api.item.IHazmatLike", modid = MekanismHooks.IC2_MOD_ID),
        @Optional.Interface(iface = "com.brandon3055.draconicevolution.items.armor.ICustomArmor", modid = MekanismHooks.DraconicEvolution_MOD_ID),
        @Optional.Interface(iface = "forestry.api.apiculture.IArmorApiarist", modid = "forestry"),
        @Optional.Interface(iface = "thaumcraft.api.items.IVisDiscountGear", modid = "thaumcraft"),
        @Optional.Interface(iface = "thaumcraft.api.items.IGoggles", modid = "thaumcraft")
})
public abstract class MixinItemMekaSuitArmor extends ItemArmor implements IEnergizedItem, ISpecialArmor, IModuleContainerItem, IModeItem,
        ISpecialElectricItem, IEnergyContainerItem, IHazmatLike, Magnetic, ICustomArmor, IArmorApiarist, IVisDiscountGear, IGoggles {

    @Shadow(remap = false)
    @Final
    private float absorption;

    public MixinItemMekaSuitArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }


    @Inject(method = "getSubItems", at = @At("TAIL"))
    public void getSubItems(CreativeTabs tabs, NonNullList<ItemStack> items, CallbackInfo ci) {
        if (!isInCreativeTab(tabs)) {
            return;
        }
        ItemStack FullStack2 = new ItemStack(this);
        setAllModule(FullStack2);
        setEnergy(FullStack2, ((IEnergizedItem) FullStack2.getItem()).getMaxEnergy(FullStack2));
        if (FullStack2.getItem() instanceof IGasItem gasItem) {
            if (FullStack2.getItem() == MekanismItems.MEKASUIT_HELMET) {
                gasItem.setGas(FullStack2, new GasStack(MekanismFluids.NutritionalPaste, gasItem.getMaxGas(FullStack2)));
            } else if (FullStack2.getItem() == MekanismItems.MEKASUIT_BODYARMOR) {
                gasItem.setGas(FullStack2, new GasStack(MekanismFluids.Hydrogen, gasItem.getMaxGas(FullStack2)));
            }
        }
        if (Mekanism.hooks.DraconicEvolution) {
            ItemNBTHelper.setFloat(FullStack2, "ProtectionPoints", getProtectionPoints(FullStack2));
        }
        items.add(FullStack2);
    }


    @Override
    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public float getProtectionPoints(ItemStack stack) {
        IModule<?> module = getModule(stack, MekaSuitMoreModules.ENERGY_SHIELD_UNIT);
        if (module != null) {
            int upgradeLevel = module.getInstalledCount();
            if (module.isEnabled()) {
                if (MekanismConfig.current().meka.mekaSuitShield.val()) {
                    return MekanismConfig.current().meka.mekaSuitShieldCapacity.val() * absorption * (int) Math.pow(2, upgradeLevel);
                } else {
                    return MekanismConfig.current().meka.mekaSuitShieldCapacity.val() * absorption * upgradeLevel;
                }
            } else {
                return ItemNBTHelper.getFloat(stack, "ProtectionPoints", 0);
            }
        } else {
            return 0.0F;
        }
    }

    @Override
    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public float getRecoveryRate(ItemStack stack) {
        IModule<?> module = getModule(stack, MekaSuitMoreModules.ENERGY_SHIELD_UNIT);
        if (module != null && module.isEnabled()) {
            int upgradeLevel = module.getInstalledCount();
            if (MekanismConfig.current().meka.mekaSuitRecovery.val()) {
                return MekanismConfig.current().meka.mekaSuitRecoveryRate.val() * (int) Math.pow(2, upgradeLevel);
            } else {
                return MekanismConfig.current().meka.mekaSuitRecoveryRate.val() * (1.0F + upgradeLevel);
            }
        } else {
            return 0.0F;
        }
    }

    @Override
    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public float getSpeedModifier(ItemStack stack, EntityPlayer player) {
        return 0.0F;
    }

    @Override
    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public float getJumpModifier(ItemStack stack, EntityPlayer player) {
        return 0.0F;
    }

    @Override
    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public boolean hasHillStep(ItemStack stack, EntityPlayer player) {
        return false;
    }

    @Override
    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public float getFireResistance(ItemStack stack) {
        return 0.0F;
    }

    @Override
    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public boolean[] hasFlight(ItemStack stack) {
        return new boolean[]{false, false, false};
    }

    @Override
    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public float getFlightSpeedModifier(ItemStack stack, EntityPlayer player) {
        return 0;
    }

    @Override
    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public float getFlightVModifier(ItemStack stack, EntityPlayer player) {
        return 0;
    }

    @Override
    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public int getEnergyPerProtectionPoint() {
        return MekanismConfig.current().meka.mekaSuitShieldRestoresEnergy.val();
    }

    @Override
    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public void modifyEnergy(ItemStack stack, int modify) {
        IModule<?> module = getModule(stack, MekaSuitMoreModules.ENERGY_SHIELD_UNIT);
        if (module != null) {
            double energy = getEnergy(stack);
            energy += modify;
            if (energy > getMaxEnergy(stack)) {
                energy = getMaxEnergy(stack);
            } else if (energy < 0) {
                energy = 0;
            }
            setEnergy(stack, energy);
        }
    }

    @Override
    @Optional.Method(modid = "forestry")
    public boolean protectEntity(@Nonnull EntityLivingBase entity, @Nonnull ItemStack armor, @Nullable String cause, boolean doProtect) {
        return isModuleEnabled(armor, MekaSuitMoreModules.BEE_CONTROL_UNIT);
    }

    @Override
    @Optional.Method(modid = "thaumcraft")
    public int getVisDiscount(ItemStack stack, EntityPlayer player) {
        IModule<?> module = getModule(stack, MekaSuitMoreModules.MAGIC_OPTIMIZATION_UNIT);
        if (module != null && module.isEnabled()) {
            return module.getInstalledCount();
        }
        return 0;
    }

    @Override
    @Optional.Method(modid = "thaumcraft")
    public boolean showIngamePopups(ItemStack stack, EntityLivingBase player) {
        return isModuleEnabled(stack, MekaSuitMoreModules.GOGGLES_OF_REVEALING_UNIT);
    }

}
