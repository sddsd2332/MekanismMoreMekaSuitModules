package moremekasuitmodules.mixin.draconicevolution;

import com.brandon3055.draconicevolution.items.armor.ICustomArmor;
import mekanism.api.energy.IEnergizedItem;
import mekanism.api.gear.IModule;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.item.armor.ItemMekaSuitArmor;
import mekanism.common.util.ItemNBTHelper;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.config.MoreModulesConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemMekaSuitArmor.class, remap = false)
public abstract class MixinItemMekaSuitArmor extends ItemArmor implements IEnergizedItem, IModuleContainerItem, ICustomArmor {


    public MixinItemMekaSuitArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }


    @Shadow
    @Final
    private float absorption;

    @Inject(method = "addALLItemStack", at = @At("HEAD"))
    public void addallItem(ItemStack stack, CallbackInfo ci) {
        ItemNBTHelper.setFloat(stack, "ProtectionPoints", getProtectionPoints(stack));
    }

    @Override
    public float getProtectionPoints(ItemStack stack) {
        IModule<?> module = getModule(stack, MekaSuitMoreModules.ENERGY_SHIELD_UNIT);
        if (module != null) {
            int upgradeLevel = module.getInstalledCount();
            if (module.isEnabled()) {
                if (MoreModulesConfig.current().config.mekaSuitShield.val()) {
                    return MoreModulesConfig.current().config.mekaSuitShieldCapacity.val() * absorption * (int) Math.pow(2, upgradeLevel);
                } else {
                    return MoreModulesConfig.current().config.mekaSuitShieldCapacity.val() * absorption * upgradeLevel;
                }
            } else {
                return ItemNBTHelper.getFloat(stack, "ProtectionPoints", 0);
            }
        } else {
            return 0.0F;
        }
    }

    @Override

    public float getRecoveryRate(ItemStack stack) {
        IModule<?> module = getModule(stack, MekaSuitMoreModules.ENERGY_SHIELD_UNIT);
        if (module != null && module.isEnabled()) {
            int upgradeLevel = module.getInstalledCount();
            if (MoreModulesConfig.current().config.mekaSuitRecovery.val()) {
                return MoreModulesConfig.current().config.mekaSuitRecoveryRate.val() * (int) Math.pow(2, upgradeLevel);
            } else {
                return MoreModulesConfig.current().config.mekaSuitRecoveryRate.val() * (1.0F + upgradeLevel);
            }
        } else {
            return 0.0F;
        }
    }

    @Override

    public float getSpeedModifier(ItemStack stack, EntityPlayer player) {
        return 0.0F;
    }

    @Override

    public float getJumpModifier(ItemStack stack, EntityPlayer player) {
        return 0.0F;
    }

    @Override

    public boolean hasHillStep(ItemStack stack, EntityPlayer player) {
        return false;
    }

    @Override

    public float getFireResistance(ItemStack stack) {
        return 0.0F;
    }

    @Override

    public boolean[] hasFlight(ItemStack stack) {
        return new boolean[]{false, false, false};
    }

    @Override

    public float getFlightSpeedModifier(ItemStack stack, EntityPlayer player) {
        return 0;
    }

    @Override

    public float getFlightVModifier(ItemStack stack, EntityPlayer player) {
        return 0;
    }

    @Override

    public int getEnergyPerProtectionPoint() {
        return MoreModulesConfig.current().config.mekaSuitShieldRestoresEnergy.val();
    }

    @Override

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

}
