package moremekasuitmodules.common;

import mekanism.api.gear.ModuleData;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.ModuleContainer;
import mekanism.common.content.gear.ModuleHelper;
import moremekasuitmodules.common.config.MoreModulesConfig;
import moremekasuitmodules.common.integration.ie.event.ieElectricDamage;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import moremekasuitmodules.common.util.MoreMekaSuitModulesUtils;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.UUID;

public class CommonPlayerTickHandler {


    private boolean ModuleInstallation(ItemStack stack, Holder<ModuleData<?>> data) {
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
                        ModuleContainer container = ModuleHelper.get().getModuleContainer(head);
                        if (container != null) {
                            container.removeModule(player.registryAccess(), head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT, 1);
                        }
                    }
                    Death(player, isInfiniteModule);
                    sendMessage(player, isInfiniteModule, item, head);
                }
            }

        }
    }

    @SubscribeEvent
    public void onLivingUpdate(PlayerTickEvent.Pre event) {
        if (MoreModulesConfig.config.isLoaded() && MoreModulesConfig.config.mekaSuitOverloadProtection.get()) {
            //If the player is affected by setHealth
            //What? Why do you want to go straight to setHealth?
            Player player = event.getEntity();
            if (player == null) {
                return;
            }
            ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
            if (head.getItem() instanceof IModuleContainerItem item) {
                boolean isInfiniteModule = item.hasModule(head, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT);
                if (!player.isAlive() || player.isDeadOrDying()) {
                    if (item.isModuleEnabled(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT) || item.isModuleEnabled(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT) || isInfiniteModule) {
                        if (!item.hasModule(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT)) {
                            ModuleContainer container = ModuleHelper.get().getModuleContainer(head);
                            if (container != null) {
                                container.removeModule(player.registryAccess(), head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT, 1);
                            }
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
    public void onIEElectricDamage(LivingIncomingDamageEvent event) {
        if (MoreMekaSuitModules.hooks.IELoaded.isLoaded()) {
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
    public void isLightningDamage(LivingIncomingDamageEvent event) {
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
    public void isInfiniteModule(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof LivingEntity base) {
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
    }

    //取消所有伤害流程1
    //设置无懈可击
    @SubscribeEvent
    public void isInfiniteModule(EntityInvulnerabilityCheckEvent event) {
        if (event.getEntity() instanceof LivingEntity base) {
            if (isInfiniteModule(base)) {
                event.setInvulnerable(true);
            }
        }
    }

    //取消所有伤害流程2
    @SubscribeEvent
    public void isInfiniteModule(LivingIncomingDamageEvent event) {
        LivingEntity base = event.getEntity();
        if (isInfiniteModule(base)) {
            event.setCanceled(true);
        }
    }

    //取消所有伤害流程3
    @SubscribeEvent
    public void isInfiniteModule(LivingShieldBlockEvent event) {
        LivingEntity base = event.getEntity();
        if (isInfiniteModule(base)) {
            event.setBlocked(true);
            event.setBlockedDamage(event.getOriginalBlockedDamage());
            event.setShieldDamage(0);
        }
    }

    //取消所有伤害4
    @SubscribeEvent
    public void isInfiniteModule(LivingDamageEvent.Pre event) {
        LivingEntity base = event.getEntity();
        if (isInfiniteModule(base)) {
            event.setNewDamage(0);
        }
    }


    @SubscribeEvent  //取消所有击退
    public void isInfiniteModule(LivingKnockBackEvent event) {
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
                event.setDamageMultiplier(0);
                event.setCriticalHit(false);
                event.setDisableSweep(false);
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
    public void isInfiniteModuleStopTasks(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            if (entity.tickCount % 5 != 0) {
                return;
            }
            if (!(entity instanceof Mob mob)) {
                return;
            }
            if (isInfiniteModule(mob.getTarget())) {
                mob.setTarget(null);
                if (mob.targetSelector != null) {
                    mob.targetSelector.getAvailableGoals().stream().filter(WrappedGoal::isRunning).forEach(wrappedGoal -> {
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
    }


    @SubscribeEvent
    public void isInfiniteModule(LivingChangeTargetEvent event) {
        LivingEntity base = event.getOriginalAboutToBeSetTarget();
        if (isInfiniteModule(base)) {
            event.setCanceled(true);
        }
    }
}
