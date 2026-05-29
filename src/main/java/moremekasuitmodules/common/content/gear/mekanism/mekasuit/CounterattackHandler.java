package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import mekanism.api.gear.IModule;
import mekanism.common.MekanismDamageSource;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.content.gear.mekanism.MekaLightningEffectHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CounterattackHandler {

    public static final String COUNTERATTACK_DAMAGE_NAME = "counterattack";
    // MekanismDamageSource 会自动加上 mekanism. 前缀，实际伤害类型为 mekanism.counterattack。
    public static final MekanismDamageSource COUNTERATTACKDAMAGE = new MekanismDamageSource(COUNTERATTACK_DAMAGE_NAME).setMagicDamage();
    private static final String COUNTERATTACK_DAMAGE_TYPE = COUNTERATTACKDAMAGE.getDamageType();

    private static final EntityEquipmentSlot[] ARMOR_SLOTS = {
            EntityEquipmentSlot.HEAD,
            EntityEquipmentSlot.CHEST,
            EntityEquipmentSlot.LEGS,
            EntityEquipmentSlot.FEET
    };

    // 在伤害刚进入实体时反击，早于护甲/药水/护盾等后续减伤；即使本次伤害之后被取消，也会先完成反击。
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onLivingAttack(LivingAttackEvent event) {
        EntityLivingBase defender = event.getEntityLiving();
        DamageSource damageSource = event.getSource();
        if (defender == null || defender.world == null || defender.world.isRemote || event.getAmount() <= 0.0F || damageSource == null || isCounterattackDamage(damageSource)) {
            return;
        }

        Entity source = damageSource.getTrueSource();
        if (!(source instanceof EntityLivingBase attacker)) {
            return;
        }
        if (attacker == defender || !attacker.isEntityAlive()) {
            return;
        }

        int counterModules = getEffectiveCounterModules(defender);
        if (counterModules <= 0) {
            return;
        }

        // 每 10 个有效反击单元返还 100% 入场伤害；四件全部装满时为 40 / 10 = 4 倍。
        float counterDamage = event.getAmount() * counterModules / ModuleCounterattackUnit.DAMAGE_DIVISOR;
        if (counterDamage > 0.0F) {
            if (attacker.attackEntityFrom(COUNTERATTACKDAMAGE.setTrueSource(defender), counterDamage)) {
                MekaLightningEffectHelper.renderEntityImpact(attacker, defender.ticksExisted);
            }
        }
    }

    public static boolean isCounterattackDamage(DamageSource source) {
        // 反击伤害和原版荆棘伤害都不再触发反击，避免双方都有反击单元时递归互打。
        return source != null && (COUNTERATTACK_DAMAGE_TYPE.equals(source.getDamageType())
                || source instanceof EntityDamageSource damageSource && damageSource.getIsThornsDamage());
    }

    public static int getEffectiveCounterModules(EntityLivingBase wearer) {
        int total = 0;
        for (EntityEquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = wearer.getItemStackFromSlot(slot);
            if (!(stack.getItem() instanceof IModuleContainerItem item)) {
                continue;
            }
            IModule<ModuleCounterattackUnit> module = item.getModule(stack, MekaSuitMoreModules.COUNTERATTACK_UNIT);
            if (module != null && module.isEnabled()) {
                // 每件护甲按自身安装数量和配置等级取有效数量，最后四件相加。
                total += module.getCustomInstance().getEffectiveCount(module.getInstalledCount());
            }
        }
        return total;
    }
}
