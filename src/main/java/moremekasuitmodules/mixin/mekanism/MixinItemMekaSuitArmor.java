package moremekasuitmodules.mixin.mekanism;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import mekanism.api.NBTConstants;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.gear.IModule;
import mekanism.api.math.FloatingLong;
import mekanism.api.security.ISecurityUtils;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.security.item.ItemStackOwnerObject;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.item.gear.ItemSpecialArmor;
import mekanism.common.item.interfaces.IGuiItem;
import mekanism.common.item.interfaces.IItemSustainedInventory;
import mekanism.common.item.interfaces.IJetpackItem;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.lib.attribute.AttributeCache;
import mekanism.common.lib.attribute.IAttributeRefresher;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.lib.frequency.IFrequencyItem;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import mekanism.common.registration.impl.CreativeTabDeferredRegister.ICustomCreativeTabContents;
import mekanism.common.registries.MekanismItems;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.text.BooleanStateDisplay;
import moremekasuitmodules.common.config.MoreModulesConfig;
import moremekasuitmodules.common.content.gear.ModuleEnergyShieldUnit;
import moremekasuitmodules.common.inventory.container.item.PortableModuleQIODashboardContainer;
import moremekasuitmodules.common.item.interfaces.IShieldProvider;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesContainerTypes;
import moremekasuitmodules.common.util.MoreMekaSuitModulesUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;


@Mixin(value = ItemMekaSuitArmor.class, remap = false)
public abstract class MixinItemMekaSuitArmor extends ItemSpecialArmor implements IModuleContainerItem, IModeItem, IJetpackItem, IAttributeRefresher, ICustomCreativeTabContents, IShieldProvider,
        IFrequencyItem, IGuiItem, IItemSustainedInventory {


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
        if (MoreModulesConfig.config.isLoaded() && MoreModulesConfig.config.addALLModueltoMekaSuit.get()) {
            ItemStack fullStack = new ItemStack((ItemMekaSuitArmor) (Object) this);
            MoreMekaSuitModulesUtils.setAllModule(fullStack);
            StorageUtils.getFilledEnergyVariant(fullStack, getMaxEnergy(fullStack));
            MoreMekaSuitModulesUtils.AddSupportedFluidsOrChemicals(fullStack);
            if (hasModule(fullStack, MekaSuitMoreModules.ENERGY_SHIELD_UNIT)) {
                ItemDataUtils.setDouble(fullStack, "ProtectionPoints", getProtectionPoints(fullStack));
            }
            tabOutput.accept(fullStack);
        }
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
        return slot == getEquipmentSlot() ? builder.build() : super.getAttributeModifiers(slot, stack);
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


    @Override
    public void onDestroyed(@NotNull ItemEntity item, @NotNull DamageSource damageSource) {
        InventoryUtils.dropItemContents(item, damageSource);
    }

    @Inject(method = "appendHoverText", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    public void addQIO(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag, CallbackInfo ci) {
        if (type == Type.HELMET && isModuleEnabled(stack, MekaSuitMoreModules.QIO_WIRELESS_UNIT)) {
            ISecurityUtils.INSTANCE.addSecurityTooltip(stack, tooltip);
            MekanismUtils.addFrequencyItemTooltip(stack, tooltip);
            tooltip.add(MekanismLang.HAS_INVENTORY.translateColored(EnumColor.AQUA, EnumColor.GRAY, BooleanStateDisplay.YesNo.of(hasSustainedInventory(stack))));
        }
    }

    @Override
    public ContainerTypeRegistryObject<PortableModuleQIODashboardContainer> getContainerType() {
        return MekaSuitMoreModulesContainerTypes.MODULE_QIO;
    }


    @Override
    public FrequencyType<?> getFrequencyType() {
        return FrequencyType.QIO;
    }

    @Override
    public boolean hasFrequency(ItemStack stack) {
        return isModuleEnabled(stack, MekaSuitMoreModules.QIO_WIRELESS_UNIT) && ItemDataUtils.hasData(stack, NBTConstants.FREQUENCY, Tag.TAG_COMPOUND);
    }


    @Inject(method = "gatherCapabilities", at = @At("TAIL"))
    public void addQIO(List<ItemCapabilityWrapper.ItemCapability> capabilities, ItemStack stack, CompoundTag nbt, CallbackInfo ci) {
        if (stack.getItem() == MekanismItems.MEKASUIT_HELMET.asItem()) {
            capabilities.add(new ItemStackOwnerObject());
        }
    }
}
