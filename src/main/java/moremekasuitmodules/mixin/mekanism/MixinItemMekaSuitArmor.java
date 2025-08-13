package moremekasuitmodules.mixin.mekanism;

import mekanism.api.energy.IEnergyContainer;
import mekanism.api.gear.IModule;
import mekanism.common.attachments.IAttachmentAware;
import mekanism.common.capabilities.ICapabilityAware;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.item.gear.ItemSpecialArmor;
import mekanism.common.item.interfaces.IJetpackItem;
import mekanism.common.registration.impl.CreativeTabDeferredRegister.ICustomCreativeTabContents;
import mekanism.common.util.StorageUtils;
import moremekasuitmodules.common.config.MoreModulesConfig;
import moremekasuitmodules.common.content.gear.ModuleEnergyShieldControllerUnit;
import moremekasuitmodules.common.content.gear.ModuleEnergyShieldUnit;
import moremekasuitmodules.common.item.interfaces.IShieldProvider;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemMekaSuitArmor.class)
public abstract class MixinItemMekaSuitArmor extends ItemSpecialArmor implements IModuleContainerItem, IJetpackItem, ICustomCreativeTabContents, IAttachmentAware, ICapabilityAware, IShieldProvider {


    @Shadow @Final private float absorption;

    protected MixinItemMekaSuitArmor(Holder<ArmorMaterial> material, Type armorType, Properties properties) {
        super(material, armorType, properties);
    }



    //护盾保护点数
    @Override
    public double getProtectionPoints(ItemStack stack) {
        IModule<ModuleEnergyShieldUnit> module =getEnabledModule(stack, MekaSuitMoreModules.ENERGY_SHIELD_UNIT);
        if (module != null) {
            return module.getCustomInstance().getProtectionPoints(module, absorption,stack.getOrDefault(MoreMekaSuitModulesDataComponents.PROTECTION_POINTS,0d));
        } else {
            return 0.0d;
        }
    }

    //护盾能量恢复速率
    @Override
    public double getRecoveryRate(ItemStack stack) {
        IModule<ModuleEnergyShieldUnit> module = getEnabledModule(stack, MekaSuitMoreModules.ENERGY_SHIELD_UNIT);
        if (module != null) {
            return module.getCustomInstance().getRecoveryRate(module);
        } else {
            return 0;
        }
    }


    //恢复护盾点数需要多少能量
    @Override
    public int getEnergyPerProtectionPoint() {
        return MoreModulesConfig.config.mekaSuitShieldRestoresEnergy.get();
    }

    //能量消耗使用
    @Override
    public void modifyEnergy(ItemStack stack, long amount, LivingEntity entity) {
        IModule<ModuleEnergyShieldUnit> module = getEnabledModule(stack, MekaSuitMoreModules.ENERGY_SHIELD_UNIT);
        if (module != null) {
            module.useEnergy(entity,stack,amount);
        }
    }

    //是否启用护盾
    @Override
    public boolean isEnableShield(ItemStack stack, LivingEntity entity) {
        IModule<ModuleEnergyShieldControllerUnit> module = getEnabledModule(stack, MekaSuitMoreModules.ENERGY_SHIELD_CONTROLLER_UNIT);
        if (module != null) {
            return module.getCustomInstance().getState();
        } else {
            return false;
        }
    }

    //物品当前能量
    @Override
    public long getEnergyStored(ItemStack stack) {
        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
        return energyContainer == null ? 0 : energyContainer.getEnergy();
    }

    //物品最大能量
    @Override
    public long getMaxEnergyStored(ItemStack stack) {
        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
        return energyContainer == null ? 0 : energyContainer.getMaxEnergy();
    }


}
