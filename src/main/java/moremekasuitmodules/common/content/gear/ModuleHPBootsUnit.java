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
public class ModuleHPBootsUnit implements ICustomModule<ModuleHPBootsUnit> {


    private static final ResourceLocation HP_BOOTS_HEAD = MoreMekaSuitModules.rl("hp_boots.head");
    private static final ResourceLocation HP_BOOTS_CHEST = MoreMekaSuitModules.rl("hp_boots.chest");
    private static final ResourceLocation HP_BOOTS_LEGS = MoreMekaSuitModules.rl("hp_boots.legs");
    private static final ResourceLocation HP_BOOTS_FEET = MoreMekaSuitModules.rl("hp_boots.feet");

    @Override
    public void adjustAttributes(IModule<ModuleHPBootsUnit> module, ItemAttributeModifierEvent event) {
        AttributeModifier head = new AttributeModifier(HP_BOOTS_HEAD, module.getInstalledCount(), AttributeModifier.Operation.ADD_VALUE);
        AttributeModifier chest = new AttributeModifier(HP_BOOTS_CHEST, module.getInstalledCount(), AttributeModifier.Operation.ADD_VALUE);
        AttributeModifier legs = new AttributeModifier(HP_BOOTS_LEGS, module.getInstalledCount(), AttributeModifier.Operation.ADD_VALUE);
        AttributeModifier feet = new AttributeModifier(HP_BOOTS_FEET, module.getInstalledCount(), AttributeModifier.Operation.ADD_VALUE);
        if (event.getItemStack().getItem() instanceof ArmorItem item) {
            switch (item.getType()) {
                case HELMET -> {
                    event.addModifier(Attributes.MAX_HEALTH, head, EquipmentSlotGroup.HEAD);
                }
                case CHESTPLATE -> {
                    event.addModifier(Attributes.MAX_HEALTH, chest, EquipmentSlotGroup.CHEST);
                }
                case LEGGINGS -> {
                    event.addModifier(Attributes.MAX_HEALTH, legs, EquipmentSlotGroup.LEGS);
                }
                case BOOTS -> {
                    event.addModifier(Attributes.MAX_HEALTH, feet, EquipmentSlotGroup.FEET);
                }
                case BODY -> {
                }
            }
        }
    }
}
