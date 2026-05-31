package moremekasuitmodules.common.content.gear.mekanism.mekatool;

import mekanism.api.energy.IEnergizedItem;
import mekanism.api.gear.IModule;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.config.MoreModulesConfig;
import moremekasuitmodules.common.content.gear.mekanism.MekaLightningEffectHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MekaToolCombatHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLootingLevel(LootingLevelEvent event) {
        EntityPlayer player = getPlayerAttacker(event.getDamageSource());
        if (player == null) {
            return;
        }
        IModule<ModuleLootingAmplificationUnit> module = getToolModule(player, MekaSuitMoreModules.LOOTING_AMPLIFICATION_UNIT);
        if (module == null || !module.isEnabled()) {
            return;
        }
        int bonus = module.getCustomInstance().getEffectiveLevel(module.getInstalledCount());
        if (bonus <= 0 || !consumeToolEnergy(player, MoreModulesConfig.current().config.mekaToolEnergyUsageLootingAmplification.val() * bonus)) {
            return;
        }
        event.setLootingLevel(event.getLootingLevel() + bonus);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingKnockback(LivingKnockBackEvent event) {
        if (!(event.getAttacker() instanceof EntityPlayer player)) {
            return;
        }
        IModule<ModuleKnockbackControlUnit> module = getToolModule(player, MekaSuitMoreModules.KNOCKBACK_CONTROL_UNIT);
        if (module == null || !module.isEnabled()) {
            return;
        }
        float strength = module.getCustomInstance().getEffectiveStrength(module.getInstalledCount());
        if (strength <= 0 || !consumeToolEnergy(player, MoreModulesConfig.current().config.mekaToolEnergyUsageKnockbackControl.val() * strength)) {
            return;
        }
        event.setStrength(event.getStrength() + strength);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDamage(LivingDamageEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (target.world.isRemote || event.getAmount() <= 0 || !target.isEntityAlive()) {
            return;
        }
        EntityPlayer player = getPlayerAttacker(event.getSource());
        if (player == null) {
            return;
        }
        IModule<ModuleExecutionUnit> module = getToolModule(player, MekaSuitMoreModules.EXECUTION_UNIT);
        if (module == null || !module.isEnabled()) {
            return;
        }
        float threshold = module.getCustomInstance().getThreshold(module.getInstalledCount());
        if (threshold <= 0) {
            return;
        }
        float healthAfterDamage = Math.max(0, target.getHealth() - event.getAmount());
        float maxHealth = target.getMaxHealth();
        if (maxHealth <= 0 || healthAfterDamage > maxHealth * threshold) {
            return;
        }
        double usage = MoreModulesConfig.current().config.mekaToolEnergyUsageExecution.val() * Math.max(1, module.getInstalledCount());
        if (!consumeToolEnergy(player, usage)) {
            return;
        }
        float executionDamage = Math.max(event.getAmount(), target.getHealth() + Math.max(20.0F, maxHealth * 4.0F));
        target.setHealth(0.0F);
        event.setAmount(executionDamage);
        MekaLightningEffectHelper.renderEntityImpact(target, player.ticksExisted);
    }

    private static EntityPlayer getPlayerAttacker(DamageSource source) {
        if (source == null) {
            return null;
        }
        Entity trueSource = source.getTrueSource();
        return trueSource instanceof EntityPlayer ? (EntityPlayer) trueSource : null;
    }

    private static <MODULE extends mekanism.api.gear.ICustomModule<MODULE>> IModule<MODULE> getToolModule(EntityPlayer player, mekanism.api.gear.ModuleData<MODULE> moduleData) {
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty() || !(stack.getItem() instanceof IModuleContainerItem container)) {
            return null;
        }
        return container.getModule(stack, moduleData);
    }

    private static boolean consumeToolEnergy(EntityPlayer player, double usage) {
        if (usage <= 0 || player.isCreative()) {
            return true;
        }
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty() || !(stack.getItem() instanceof IEnergizedItem energizedItem)) {
            return false;
        }
        if (energizedItem.extract(stack, usage, false) < usage) {
            return false;
        }
        energizedItem.extract(stack, usage, true);
        return true;
    }
}
