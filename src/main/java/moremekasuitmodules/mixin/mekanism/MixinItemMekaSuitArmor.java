package moremekasuitmodules.mixin.mekanism;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import mekanism.api.MekanismAPI;
import mekanism.api.NBTConstants;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.gear.IModule;
import mekanism.api.gear.ModuleData;
import mekanism.api.math.FloatingLong;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.item.gear.ItemSpecialArmor;
import mekanism.common.item.interfaces.IJetpackItem;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.lib.attribute.AttributeCache;
import mekanism.common.lib.attribute.IAttributeRefresher;
import mekanism.common.registration.impl.CreativeTabDeferredRegister.ICustomCreativeTabContents;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.registries.MekanismGases;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.FluidUtils;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.StorageUtils;
import moremekasuitmodules.common.config.MoreModulesConfig;
import moremekasuitmodules.common.content.gear.ModuleEnergyShieldUnit;
import moremekasuitmodules.common.item.interfaces.IShieldProvider;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;


@Mixin(value = ItemMekaSuitArmor.class, remap = false)
public abstract class MixinItemMekaSuitArmor extends ItemSpecialArmor implements IModuleContainerItem, IModeItem, IJetpackItem, IAttributeRefresher, ICustomCreativeTabContents, IShieldProvider {


    protected MixinItemMekaSuitArmor(ArmorMaterial material, Type armorType, Properties properties) {
        super(material, armorType, properties);
    }

    @Shadow
    protected abstract FloatingLong getMaxEnergy(ItemStack stack);

    @Shadow
    @Final
    private AttributeCache attributeCache;

    @Shadow
    @Final
    private float absorption;

    @Inject(method = "addItems", at = @At("TAIL"), remap = false)
    public void addALL(CreativeModeTab.Output tabOutput, CallbackInfo ci) {
        ItemStack fullStack = new ItemStack((ItemMekaSuitArmor) (Object) this);
        setAllModule(fullStack);
        StorageUtils.getFilledEnergyVariant(fullStack, getMaxEnergy(fullStack));
        if (fullStack.getItem() instanceof ItemMekaSuitArmor armor) {
            if (armor.getType().equals(Type.HELMET)) {
                FluidUtils.getFilledVariant(fullStack, MekanismConfig.gear.mekaSuitNutritionalMaxStorage, MekanismFluids.NUTRITIONAL_PASTE);
            } else if (armor.getType().equals(Type.CHESTPLATE)) {
                ChemicalUtil.getFilledVariant(fullStack, MekanismConfig.gear.mekaSuitJetpackMaxStorage, MekanismGases.HYDROGEN);
            }
        }
        ItemDataUtils.setDouble(fullStack, "ProtectionPoints", getProtectionPoints(fullStack));
        tabOutput.accept(fullStack);
    }

    @Unique
    public void setAllModule(ItemStack stack) {
        for (ModuleData<?> module : MekanismAPI.moduleRegistry().getValues()) {
            if (ModuleHelper.get().getSupported(stack).contains(module)) {
                setModule(stack, module);
            }
        }
    }


    @Unique
    public void setModule(ItemStack stack, ModuleData<?> type) {
        if (!ItemDataUtils.hasData(stack, NBTConstants.MODULES, Tag.TAG_COMPOUND)) {
            ItemDataUtils.setCompound(stack, NBTConstants.MODULES, new CompoundTag());
        }
        ItemDataUtils.getCompound(stack, NBTConstants.MODULES).put(type.getRegistryName().toString(), new CompoundTag());
        ItemDataUtils.getCompound(stack, NBTConstants.MODULES).getCompound(type.getRegistryName().toString()).putInt(NBTConstants.AMOUNT, type.getMaxStackSize());
        ModuleHelper.get().load(stack, type).save(null);
        ModuleHelper.get().load(stack, type).onAdded(false);
    }


    /**
     * @author sddsd2332
     * @reason 覆盖便于添加模块的属性
     */
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@NotNull EquipmentSlot slot, @NotNull ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        if (slot == getEquipmentSlot()) {
            builder.putAll(attributeCache.get());
            UUID uuid = new UUID((stack.getDescriptionId() + slot).hashCode(), 0L);
            if (stack.getItem() instanceof ArmorItem armor && slot == armor.getType().getSlot()) {
                IModule<?> module = getModule(stack, MekaSuitMoreModules.POWER_ENHANCEMENT_UNIT);
                if (module != null && module.isEnabled()) {
                    builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid, "Power Enhancement Unit", module.getInstalledCount(), AttributeModifier.Operation.ADDITION));
                    builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(uuid, "Power Enhancement Unit", module.getInstalledCount(), AttributeModifier.Operation.ADDITION));
                }
                IModule<?> HPmodule = getModule(stack, MekaSuitMoreModules.HP_BOOTS_UNIT);
                if (HPmodule != null) {
                    builder.put(Attributes.MAX_HEALTH, new AttributeModifier(uuid, "Hp Boots Unit", HPmodule.getInstalledCount(), AttributeModifier.Operation.ADDITION));
                }
            }
        }
        return slot == getEquipmentSlot() ? builder.build(): super.getAttributeModifiers(slot,stack);
    }


    //护盾保护点数
    @Override
    public double getProtectionPoints(ItemStack stack) {
        IModule<ModuleEnergyShieldUnit> module = getModule(stack, MekaSuitMoreModules.ENERGY_SHIELD_UNIT);
        if (module != null) {
            return module.getCustomInstance().getProtectionPoints(module, absorption, ItemDataUtils.getDouble(stack, "ProtectionPoints"));
        } else {
            return 0.0d;
        }
    }

    //护盾能量恢复速率
    @Override
    public double getRecoveryRate(ItemStack stack) {
        IModule<ModuleEnergyShieldUnit> module = getModule(stack, MekaSuitMoreModules.ENERGY_SHIELD_UNIT);
        if (module != null && module.isEnabled()) {
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
    public void modifyEnergy(ItemStack stack, int amount, LivingEntity entity) {
        IModule<ModuleEnergyShieldUnit> module = getModule(stack, MekaSuitMoreModules.ENERGY_SHIELD_UNIT);
        if (module != null && module.isEnabled()) {
            module.useEnergy(entity, FloatingLong.create(amount));
        }
    }

    //是否启用护盾
    @Override
    public boolean isEnableShield(ItemStack stack, LivingEntity entity) {
        IModule<ModuleEnergyShieldUnit> module = getModule(stack, MekaSuitMoreModules.ENERGY_SHIELD_UNIT);
        if (module != null && module.isEnabled()) {
            return module.getCustomInstance().getEnableShield();
        } else {
            return false;
        }
    }

    //物品当前能量
    @Override
    public int getEnergyStored(ItemStack stack) {
        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
        return energyContainer == null ? FloatingLong.ZERO.intValue() : energyContainer.getEnergy().intValue();
    }

    //物品最大能量
    @Override
    public int getMaxEnergyStored(ItemStack stack) {
        return getMaxEnergy(stack).intValue();
    }

}
