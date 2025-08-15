package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import com.google.common.collect.Multimap;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.UUID;

@ParametersAreNotNullByDefault
public class ModuleHPBootsUnit implements ICustomModule<ModuleHPBootsUnit> {

    @Override
    public void multimapModule(IModule<ModuleHPBootsUnit> module, ItemStack stack, EntityEquipmentSlot slot, Multimap<String, AttributeModifier> multimap) {
        UUID uuid = new UUID((stack.getTranslationKey() + slot).hashCode(), 0L);
        //如果物品是盔甲且是在对应对应栏
        if (stack.getItem() instanceof ItemArmor armor && slot == armor.armorType) {
            multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(uuid, "HP Boots Unit", module.getInstalledCount(), 0));
            //如果物品不是盔甲且在主手
        }else if (!(stack.getItem() instanceof ItemArmor) && slot == EntityEquipmentSlot.MAINHAND){
            multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(uuid, "HP Boots Unit", module.getInstalledCount(), 0));
        }
    }
}
