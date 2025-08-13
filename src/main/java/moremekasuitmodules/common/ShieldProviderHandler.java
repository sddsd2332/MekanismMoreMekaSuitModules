package moremekasuitmodules.common;

import moremekasuitmodules.common.config.MoreModulesConfig;
import moremekasuitmodules.common.item.interfaces.IShieldProvider;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesDataComponents;
import moremekasuitmodules.common.util.MoreMekaSuitModulesUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.List;

//护盾处理事件
public class ShieldProviderHandler {

    //先计算护盾能否承受，在计算盔甲
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerAttacked(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || event.isCanceled() || event.getAmount() <= 0) {
            return;
        }
        if (player.level().isClientSide()) {
            return;
        }

        ArmorSummery summery = new ArmorSummery().getSummery(player);

        float hitAmount = applyModDamageAdjustments(summery, event);

        if (applyArmorDamageBlocking(event, summery)) {
            return;
        }

        if (summery == null || summery.protectionPoints <= 0 || !getShieldState(player)) {
            return;
        }
        //取消本次伤害，并开始计算护盾
        event.setCanceled(true);


        if ((float) player.invulnerableTime > 20 - 4) {
            return;
        }
        double newEntropy = Math.min(summery.entropy + 1 + (hitAmount / 20), 100F);


        //Divide the damage between the armor pieces based on how many of the protection points each piece has
        double totalAbsorbed = 0;
        double remainingPoints = 0;

        for (int i = 0; i < summery.allocation.length; i++) {
            if (summery.allocation[i] == 0) continue;
            ItemStack armorPiece = summery.armorStacks.get(i);

            double dmgShear = summery.allocation[i] / summery.protectionPoints;
            double dmg = dmgShear * hitAmount;

            double absorbed = Math.min(dmg, summery.allocation[i]);
            totalAbsorbed += absorbed;
            summery.allocation[i] -= absorbed;
            remainingPoints += summery.allocation[i];
            armorPiece.set(MoreMekaSuitModulesDataComponents.PROTECTION_POINTS, summery.allocation[i]);
            armorPiece.set(MoreMekaSuitModulesDataComponents.SHIELD_ENTROPY, newEntropy);
            //   ItemDataUtils.setDouble(armorPiece, "ProtectionPoints", summery.allocation[i]);
            //     ItemDataUtils.setDouble(armorPiece, "ShieldEntropy", newEntropy);
        }

        if (remainingPoints > 0) {
            player.invulnerableTime = 20;
            //如果护盾无法完全移除，则重新设置伤害
        } else if (hitAmount - totalAbsorbed > 0) {
            player.hurt(event.getSource(), (float) (hitAmount - totalAbsorbed));
        }

    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player) || event.isCanceled()) {
            return;
        }

        if (player.level().isClientSide()) {
            return;
        }
        ArmorSummery summery = new ArmorSummery().getSummery(player);

        if (summery == null) {
            return;
        }

        if (summery.protectionPoints > 500 && getShieldState(player)) {
            event.setCanceled(true);
            event.getEntity().setHealth(10);
            return;
        }

        long[] charge = new long[summery.armorStacks.size()];
        long totalCharge = 0;
        for (int i = 0; i < summery.armorStacks.size(); i++) {
            if (!summery.armorStacks.get(i).isEmpty() && summery.armorStacks.get(i).getItem() instanceof IShieldProvider provider) {
                charge[i] = provider.getEnergyStored(summery.armorStacks.get(i));
                totalCharge += charge[i];
            }
        }

        if (totalCharge < MoreModulesConfig.config.lastStandEnergyRequirement.get()) {
            return;
        }

        for (int i = 0; i < summery.armorStacks.size(); i++) {
            if (!summery.armorStacks.get(i).isEmpty() && summery.armorStacks.get(i).getItem() instanceof IShieldProvider provider) {
                provider.modifyEnergy(summery.armorStacks.get(i), ((charge[i] / totalCharge) * 10000000), player);
            }
        }

        player.sendSystemMessage(MoreMekaSuitModulesUtils.ShieldModuleWarning());
        event.setCanceled(true);
        player.setHealth(1);
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        ArmorSummery summery = new ArmorSummery().getSummery(player);
        tickShield(summery, player);
    }

    public static void tickShield(ArmorSummery summery, Player player) {
        if (summery == null || (summery.maxProtectionPoints - summery.protectionPoints < 0.01 && summery.entropy == 0) || player.level().isClientSide()) {
            return;
        }
        double totalPointsToAdd = Math.min(summery.maxProtectionPoints - summery.protectionPoints, summery.maxProtectionPoints / 60F);
        totalPointsToAdd *= (1F - (summery.entropy / 100F));
        totalPointsToAdd = Math.min(totalPointsToAdd, summery.totalEnergyStored / 1000D);

        if (totalPointsToAdd < 0F) {
            totalPointsToAdd = 0F;
        }

        summery.entropy -= (summery.meanRecoveryPoints / 100F);
        if (summery.entropy < 0) {
            summery.entropy = 0;
        }


        for (int i = 0; i < summery.armorStacks.size(); i++) {
            ItemStack stack = summery.armorStacks.get(i);
            if (stack.isEmpty() || summery.totalEnergyStored <= 0) {
                continue;
            }
            if (!(stack.getItem() instanceof IShieldProvider provider)) {
                continue;
            }
            double maxForPiece = provider.getProtectionPoints(stack);
            int energyAmount = provider.getEnergyPerProtectionPoint();
            provider.modifyEnergy(stack, ((summery.energyAllocation[i] / summery.totalEnergyStored) * (long) (totalPointsToAdd * energyAmount)), player);
            double pointsForPiece = (summery.pointsDown[i] / Math.max(1, summery.maxProtectionPoints - summery.protectionPoints)) * totalPointsToAdd;
            summery.allocation[i] += pointsForPiece;
            if (summery.allocation[i] > maxForPiece || maxForPiece - summery.allocation[i] < 0.1F) {
                summery.allocation[i] = maxForPiece;
            }
            stack.set(MoreMekaSuitModulesDataComponents.PROTECTION_POINTS, summery.allocation[i]);
            //  ItemDataUtils.setDouble(stack, "ProtectionPoints", summery.allocation[i]);
            if (player.invulnerableTime <= 0) {//TODO Increase this delay (Store the delay in forge entity nbt)
                stack.set(MoreMekaSuitModulesDataComponents.SHIELD_ENTROPY, summery.entropy);
                //         ItemDataUtils.setDouble(stack, "ShieldEntropy", summery.entropy);
            }
        }
    }

    public float applyModDamageAdjustments(ArmorSummery summery, LivingIncomingDamageEvent event) {
        if (summery == null) {
            return event.getAmount();
        }
        Player attacker = event.getSource().getEntity() instanceof Player player ? player : null;

        if (attacker == null) {
            return event.getAmount();
        }
        if (event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)) {
            summery.entropy += 3;
            if (summery.entropy > 100) {
                summery.entropy = 100;
            }
            return event.getAmount() * 2;
        }
        return event.getAmount();
    }


    /**
     * @return true if the damage was blocked
     */
    private boolean applyArmorDamageBlocking(LivingIncomingDamageEvent event, ArmorSummery summery) {
        if (summery == null) {
            return false;
        }
        if ((event.getSource().is(DamageTypes.IN_WALL) || event.getSource().is(DamageTypes.DROWN)) && !summery.armorStacks.get(3).isEmpty()) {
            if (event.getAmount() <= 2f) {
                event.setCanceled(true);
            }
            return true;
        }
        return false;
    }

    public static class ArmorSummery {
        /*---- Shield ----*/
        /**
         * Max protection points from all equipped armor pieces.
         */
        public double maxProtectionPoints = 0F;
        /**
         * Total protection points from all equipped armor pieces.
         */
        public double protectionPoints = 0F;
        /**
         * Number of equipped armor pieces.
         */
        public int pieces = 0;
        /**
         * Point allocation, or the number of points on each piece.
         */
        public double[] allocation;
        /**
         * How many points have been drained from each armor piece.
         */
        public double[] pointsDown;
        /**
         * The armor pieces themselves. Index will contain ItemStack.EMPTY in the appropriate slot if piece is not present.
         */
        public NonNullList<ItemStack> armorStacks;
        /**
         * Mean fatigue of all armor pieces.
         */
        public double entropy = 0F;
        /**
         * Mean Recovery Points of all armor pieces.
         */
        public double meanRecoveryPoints = 0;
        /**
         * Total RF stored in all armor pieces.
         */
        public long totalEnergyStored = 0;
        /**
         * Total Max RF storage for all armor pieces.
         */
        public long maxTotalEnergyStorage = 0;
        /**
         * RF stored in each armor piece
         */
        public long[] energyAllocation;

        public ArmorSummery getSummery(Player player) {
            List<ItemStack> armorStacks = new ArrayList<>(player.getInventory().armor);
            double totalEntropy = 0;
            double totalRecoveryPoints = 0;
            allocation = new double[armorStacks.size()];
            this.armorStacks = NonNullList.withSize(armorStacks.size(), ItemStack.EMPTY);
            pointsDown = new double[armorStacks.size()];
            energyAllocation = new long[armorStacks.size()];
            for (int i = 0; i < armorStacks.size(); i++) {
                ItemStack stack = armorStacks.get(i);
                if (stack.isEmpty() || !(stack.getItem() instanceof IShieldProvider armor)) continue;
                pieces++;
                allocation[i] = stack.getOrDefault(MoreMekaSuitModulesDataComponents.PROTECTION_POINTS, 0D); //ItemDataUtils.getDouble(stack, "ProtectionPoints");
                protectionPoints += allocation[i];
                totalEntropy += stack.getOrDefault(MoreMekaSuitModulesDataComponents.SHIELD_ENTROPY, 0D);//itemDataUtils.getDouble(stack, "ShieldEntropy");
                this.armorStacks.set(i, stack);
                totalRecoveryPoints += armor.getRecoveryRate(stack); //UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.SHIELD_RECOVERY);
                double maxPoints = armor.getProtectionPoints(stack);
                pointsDown[i] = maxPoints - allocation[i];
                maxProtectionPoints += maxPoints;
                energyAllocation[i] = armor.getEnergyStored(stack);
                totalEnergyStored += energyAllocation[i];
                maxTotalEnergyStorage += armor.getMaxEnergyStored(stack);
            }
            if (pieces == 0) {
                return null;
            }
            entropy = totalEntropy / pieces;
            meanRecoveryPoints = totalRecoveryPoints / pieces;
            return this;
        }
    }

    public boolean getShieldState(LivingEntity entity) {
        return isInsulated(entity.getItemBySlot(EquipmentSlot.HEAD), entity);
    }

    private boolean isInsulated(ItemStack stack, LivingEntity entity) {
        return stack.getItem() instanceof IShieldProvider provider && provider.isEnableShield(stack, entity);
    }

}

