package moremekasuitmodules.common;

import mekanism.api.providers.IModuleDataProvider;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.config.MoreModulesConfig;
import moremekasuitmodules.common.integration.ie.event.ieElectricDamage;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import moremekasuitmodules.common.util.MoreMekaSuitModulesUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class CommonPlayerTickHandler {

    private boolean ModuleInstallation(ItemStack stack, IModuleDataProvider<?> data) {
        if (stack.getItem() instanceof IModuleContainerItem item) {
            return item.isModuleEnabled(stack, data);
        }
        return false;
    }

    //When the player dies
    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (event.isCanceled()) {
            return;
        }
        if (event.getEntity() instanceof Player player) {
            ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
            if (head.getItem() instanceof IModuleContainerItem item) {
                boolean isInfiniteModule = item.hasModule(head, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
                if (item.isModuleEnabled(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT) || item.isModuleEnabled(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT) || isInfiniteModule) {
                    event.setCanceled(true);
                    if (!item.hasModule(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT)) {
                        item.removeModule(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT.getModuleData());
                    }
                    Death(player, isInfiniteModule);
                    sendMessage(player, isInfiniteModule, item, head);
                }
            }

        }
    }

    @SubscribeEvent
    public void onLivingUpdate(TickEvent.PlayerTickEvent event) {
        if (MoreModulesConfig.config.mekaSuitOverloadProtection.get()) {
            if (event.phase != TickEvent.Phase.START) {
                return;
            }
            //If the player is affected by setHealth
            //What? Why do you want to go straight to setHealth?
            Player player = event.player;
            if (player == null) {
                return;
            }
            ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
            if (head.getItem() instanceof IModuleContainerItem item) {
                boolean isInfiniteModule = item.hasModule(head, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
                if (!player.isAlive() || player.isDeadOrDying()) {
                    if (item.isModuleEnabled(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT) || item.isModuleEnabled(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT) || isInfiniteModule) {
                        if (!item.hasModule(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT)) {
                            item.removeModule(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT.getModuleData());
                        }
                        Death(player, isInfiniteModule);
                        //重新刷新玩家的位置 确保玩家在该位置
                        if (player instanceof ServerPlayer serverPlayer) {
                            if (player.level() instanceof ServerLevel level) {
                                serverPlayer.teleportTo(level, player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                            }
                            sendMessage(player, isInfiniteModule, item, head);
                        }
                    }
                }
            }
        }
    }

    private void Death(Player player, boolean isInfiniteModule) {
        player.revive();
        player.deathTime = 0;
        player.setHealth(isInfiniteModule ? player.getMaxHealth() : 5F);
        player.removeAllEffects();
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 2));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 2));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 2));
        player.setAirSupply(player.getMaxAirSupply());
        player.getFoodData().eat(20, 20);
    }


    private void sendMessage(Player player, boolean isInfiniteModule, IModuleContainerItem item, ItemStack head) {
        if (isInfiniteModule) {
            player.sendSystemMessage(MoreMekaSuitModulesUtils.moduleWarning(MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT));
        } else if (item.isModuleEnabled(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT)) {
            player.sendSystemMessage(MoreMekaSuitModulesUtils.moduleWarning(MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT));
        } else if (item.isModuleEnabled(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT)) {
            player.sendSystemMessage(MoreMekaSuitModulesUtils.moduleWarning(MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT));
        }
    }


    private boolean isInsulated(ItemStack stack) {
        return ModuleInstallation(stack, MekaSuitMoreModules.INSULATED_UNIT);
    }

    //为什么没有剔除接口了呢?
    @SubscribeEvent
    public void onIEElectricDamage(LivingAttackEvent event) {
        if (MoreMekaSuitModules.hooks.IELoaded) {
            LivingEntity base = event.getEntity();
            boolean helmet = isInsulated(base.getItemBySlot(EquipmentSlot.HEAD));
            boolean chest = isInsulated(base.getItemBySlot(EquipmentSlot.CHEST));
            boolean legs = isInsulated(base.getItemBySlot(EquipmentSlot.LEGS));
            boolean feet = isInsulated(base.getItemBySlot(EquipmentSlot.FEET));
            if (helmet && chest && legs && feet) {
                ieElectricDamage.onIEElectricDamage(event);
            }
        }
    }


    @SubscribeEvent
    public void isLightningDamage(LivingAttackEvent event) {
        LivingEntity base = event.getEntity();
        boolean helmet = isInsulated(base.getItemBySlot(EquipmentSlot.HEAD));
        boolean chest = isInsulated(base.getItemBySlot(EquipmentSlot.CHEST));
        boolean legs = isInsulated(base.getItemBySlot(EquipmentSlot.LEGS));
        boolean feet = isInsulated(base.getItemBySlot(EquipmentSlot.FEET));
        if (helmet && chest && legs && feet) {
            if (event.getSource().is(DamageTypeTags.IS_LIGHTNING)) {
                event.setCanceled(true);
            }
        }
    }


    /**
     * 无限拦截模块 开始
     */
    @SubscribeEvent //实体更新(玩家另外处理)
    public void isInfiniteModule(LivingEvent.LivingTickEvent event) {
        LivingEntity base = event.getEntity();
        if (!(base instanceof Player) && isInfiniteModule(base)) {
            if (base.getHealth() != base.getMaxHealth()) {
                base.setHealth(base.getMaxHealth());
            }
            if (base.isAlive()) {
                base.revive();
                base.deathTime = 0;
                base.removeAllEffects();
                base.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 2));
                base.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 2));
                base.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 2));
            }

        }
    }


    @SubscribeEvent  //取消所有伤害
    public void isInfiniteModule(LivingAttackEvent event) {
        LivingEntity base = event.getEntity();
        if (isInfiniteModule(base)) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent  //取消所有击退
    public void isInfiniteModule(LivingKnockBackEvent event) {
        LivingEntity base = event.getEntity();
        if (isInfiniteModule(base)) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent  //取消所有伤害2
    public void isInfiniteModule(LivingHurtEvent event) {
        LivingEntity base = event.getEntity();
        if (isInfiniteModule(base)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent  //取消所有伤害3
    public void isInfiniteModule(LivingDamageEvent event) {
        LivingEntity base = event.getEntity();
        if (isInfiniteModule(base)) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent  //取消所有死亡(玩家另外处理)
    public void isInfiniteModule(LivingDeathEvent event) {
        if (event.isCanceled()) {
            return;
        }
        LivingEntity base = event.getEntity();
        if (!(base instanceof Player) && isInfiniteModule(base)) {
            event.setCanceled(true);
            base.setHealth(base.getMaxHealth());
        }
    }


    @SubscribeEvent  //如果玩家攻击目标带有无限模块，取消本次攻击
    public void isInfiniteModule(AttackEntityEvent event) {
        if (event.getTarget() instanceof LivingEntity base) {
            if (isInfiniteModule(base)) {
                event.setCanceled(true);
            }

        }
    }

    @SubscribeEvent //弹射物伤害取消？
    public void isInfiniteModule(CriticalHitEvent event) {
        if (event.getTarget() instanceof LivingEntity base) {
            if (isInfiniteModule(base)) {
                event.setDamageModifier(0);
            }
        }
    }


    public boolean isInfiniteModule(Entity entity) {
        if (entity instanceof LivingEntity base) {
            ItemStack head = base.getItemBySlot(EquipmentSlot.HEAD);
            if (head.getItem() instanceof IModuleContainerItem item) {
                return item.hasModule(head, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
            }
        }
        return false;
    }


    @SubscribeEvent
    public void isInfiniteModule(LivingEvent.LivingVisibilityEvent event) {
        if (event.getLookingEntity() instanceof LivingEntity entity) {
            if (isInfiniteModule(entity)) {
                event.modifyVisibility(0);
            }
        }
    }


    @SubscribeEvent
    public void isInfiniteModuleStopTasks(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.tickCount % 5 != 0) {
            return;
        }
        if (!(entity instanceof Mob mob)) {
            return;
        }
        if (isInfiniteModule(mob.getTarget())) {
            mob.setTarget(null);
            if (mob.targetSelector != null) {
                mob.targetSelector.getRunningGoals().forEach(wrappedGoal -> {
                    if (wrappedGoal.getGoal() instanceof TargetGoal tg)
                        tg.stop();
                });
            }
        }

        if (entity instanceof NeutralMob nMob && entity.level() instanceof ServerLevel sl) {
            UUID uuid = nMob.getPersistentAngerTarget();
            if (uuid != null && isInfiniteModule(sl.getEntity(uuid))) {
                nMob.stopBeingAngry();
            }
        }

        if (isInfiniteModule(mob.getLastHurtByMob())) {
            mob.setLastHurtByMob(null);
            mob.setLastHurtByPlayer(null);
        }
    }


    @SubscribeEvent
    public void isInfiniteModule(LivingChangeTargetEvent event) {
        LivingEntity base = event.getOriginalTarget();
        if (isInfiniteModule(base)) {
            event.setCanceled(true);
        }
    }


}
