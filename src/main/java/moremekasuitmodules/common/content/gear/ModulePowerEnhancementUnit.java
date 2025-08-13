package moremekasuitmodules.common.content.gear;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import moremekasuitmodules.common.MoreMekaSuitModules;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

@ParametersAreNotNullByDefault
public class ModulePowerEnhancementUnit implements ICustomModule<ModulePowerEnhancementUnit> {


    private static final ResourceLocation ATTACK_DAMAGE_HEAD = MoreMekaSuitModules.rl("attack_damage.head");
    private static final ResourceLocation ATTACK_DAMAGE_CHEST = MoreMekaSuitModules.rl("attack_damage.chest");
    private static final ResourceLocation ATTACK_DAMAGE_LEGS = MoreMekaSuitModules.rl("attack_damage.legs");
    private static final ResourceLocation ATTACK_DAMAGE_FEET = MoreMekaSuitModules.rl("attack_damage.feet");
    private static final ResourceLocation ATTACK_SPEED_HEAD = MoreMekaSuitModules.rl("attack_speed.head");
    private static final ResourceLocation ATTACK_SPEED_CHEST = MoreMekaSuitModules.rl("attack_speed.chest");
    private static final ResourceLocation ATTACK_SPEED_LEGS = MoreMekaSuitModules.rl("attack_speed.legs");
    private static final ResourceLocation ATTACK_SPEED_FEET = MoreMekaSuitModules.rl("attack_speed.feet");

    @Override
    public void adjustAttributes(IModule<ModulePowerEnhancementUnit> module, ItemAttributeModifierEvent event) {
        AttributeModifier modifier_attack_damage_head = new AttributeModifier(ATTACK_DAMAGE_HEAD, module.getInstalledCount(), AttributeModifier.Operation.ADD_VALUE);
        AttributeModifier modifier_attack_damage_chest = new AttributeModifier(ATTACK_DAMAGE_CHEST, module.getInstalledCount(), AttributeModifier.Operation.ADD_VALUE);
        AttributeModifier modifier_attack_damage_legs = new AttributeModifier(ATTACK_DAMAGE_LEGS, module.getInstalledCount(), AttributeModifier.Operation.ADD_VALUE);
        AttributeModifier modifier_attack_damage_feet = new AttributeModifier(ATTACK_DAMAGE_FEET, module.getInstalledCount(), AttributeModifier.Operation.ADD_VALUE);
        AttributeModifier modifier_attack_spedd_head = new AttributeModifier(ATTACK_SPEED_HEAD, module.getInstalledCount(), AttributeModifier.Operation.ADD_VALUE);
        AttributeModifier modifier_attack_spedd_chest = new AttributeModifier(ATTACK_SPEED_CHEST, module.getInstalledCount(), AttributeModifier.Operation.ADD_VALUE);
        AttributeModifier modifier_attack_spedd_legs = new AttributeModifier(ATTACK_SPEED_LEGS, module.getInstalledCount(), AttributeModifier.Operation.ADD_VALUE);
        AttributeModifier modifier_attack_spedd_feet = new AttributeModifier(ATTACK_SPEED_FEET, module.getInstalledCount(), AttributeModifier.Operation.ADD_VALUE);
        if (event.getItemStack().getItem() instanceof ArmorItem item) {
            switch (item.getType()) {
                case HELMET -> {
                    event.addModifier(Attributes.ATTACK_DAMAGE, modifier_attack_damage_head, EquipmentSlotGroup.HEAD);
                    event.addModifier(Attributes.ATTACK_SPEED, modifier_attack_spedd_head, EquipmentSlotGroup.HEAD);
                }
                case CHESTPLATE -> {
                    event.addModifier(Attributes.ATTACK_DAMAGE, modifier_attack_damage_chest, EquipmentSlotGroup.CHEST);
                    event.addModifier(Attributes.ATTACK_SPEED, modifier_attack_spedd_chest, EquipmentSlotGroup.CHEST);
                }
                case LEGGINGS -> {
                    event.addModifier(Attributes.ATTACK_DAMAGE, modifier_attack_damage_legs, EquipmentSlotGroup.LEGS);
                    event.addModifier(Attributes.ATTACK_SPEED, modifier_attack_spedd_legs, EquipmentSlotGroup.LEGS);
                }
                case BOOTS -> {
                    event.addModifier(Attributes.ATTACK_DAMAGE, modifier_attack_damage_feet, EquipmentSlotGroup.FEET);
                    event.addModifier(Attributes.ATTACK_SPEED, modifier_attack_spedd_feet, EquipmentSlotGroup.FEET);
                }
                case BODY -> {
                }
            }
        }
    }
}
