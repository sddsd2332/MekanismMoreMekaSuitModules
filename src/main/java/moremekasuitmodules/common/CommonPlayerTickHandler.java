package moremekasuitmodules.common;

import blusunrize.immersiveengineering.common.util.IEDamageSources.ElectricDamageSource;
import com.brandon3055.draconicevolution.lib.DEDamageSources;
import com.google.common.collect.Sets;
import gregtech.api.damagesources.DamageSources;
import mekanism.api.gear.IModule;
import mekanism.api.gear.ModuleData;
import mekanism.api.text.TextComponentGroup;
import mekanism.common.MekanismModules;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.integration.MekanismHooks;
import micdoodle8.mods.galacticraft.api.event.oxygen.GCCoreOxygenSuffocationEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zmaster587.advancedRocketry.api.event.AtmosphereEvent;

import java.util.Set;

public class CommonPlayerTickHandler {

    private boolean ModuleInstallation(ItemStack stack, ModuleData<?> data) {
        if (stack.getItem() instanceof IModuleContainerItem item) {
            return item.isModuleEnabled(stack, data);
        }
        return false;
    }

    private boolean sealArmor(ItemStack stack) {
        return ModuleInstallation(stack, MekaSuitMoreModules.SEAL_UNIT);
    }

    private boolean sealHelmetArmor(ItemStack stack) {
        if (stack.getItem() instanceof IModuleContainerItem item) {
            return sealArmor(stack) && item.isModuleEnabled(stack, MekanismModules.INHALATION_PURIFICATION_UNIT);
        }
        return false;
    }

    @SubscribeEvent
    @Optional.Method(modid = MekanismHooks.AR_MOD_ID)
    public void canARBreathe(AtmosphereEvent.AtmosphereTickEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityLivingBase base) {
            boolean SealHelmet = sealHelmetArmor(base.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
            boolean SealChest = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
            boolean SealLegs = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
            boolean seaFeet = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.FEET));
            if (SealHelmet && SealChest && SealLegs && seaFeet) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = MekanismHooks.GC_MOD_ID)
    public void canGCBreathe(GCCoreOxygenSuffocationEvent.Pre event) {
        EntityLivingBase base = event.getEntityLiving();
        boolean SealHelmet = sealHelmetArmor(base.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
        boolean SealChest = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
        boolean SealLegs = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
        boolean seaFeet = sealArmor(base.getItemStackFromSlot(EntityEquipmentSlot.FEET));
        if (SealHelmet && SealChest && SealLegs && seaFeet) {
            event.setCanceled(true);
        }
    }

    //When the player dies
    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer player) {
            ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (head.getItem() instanceof IModuleContainerItem item) {
                if (item.isModuleEnabled(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT) || item.isModuleEnabled(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT)) {
                    event.setCanceled(true);
                    if (!item.isModuleEnabled(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT)) {
                        item.removeModule(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT);
                    }
                    player.setHealth(5F);
                    player.clearActivePotions();
                    player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 800, 2));
                    player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 900, 2));
                    player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 2));
                    player.setAir(300);
                    player.getFoodStats().addStats(20, 20);
                    if (item.isModuleEnabled(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT)) {
                        player.sendMessage(new TextComponentGroup(TextFormatting.GRAY).string("[", TextFormatting.RED).translation(MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT.getTranslationKey(), TextFormatting.RED).string("]", TextFormatting.RED).string(":").translation("module.emergency_rescue.use", TextFormatting.YELLOW));
                    } else if (item.isModuleEnabled(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT)) {
                        player.sendMessage(new TextComponentGroup(TextFormatting.GRAY).string("[", TextFormatting.RED).translation(MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT.getTranslationKey(), TextFormatting.RED).string("]", TextFormatting.RED).string(":").translation("module.emergency_rescue.use", TextFormatting.YELLOW));
                    }
                }
            }

        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        //If the player is affected by setHealth
        //What? Why do you want to go straight to setHealth?
        if (MekanismConfig.current().mekce.MekAsuitOverloadProtection.val()) {
            if (event.getEntityLiving() instanceof EntityPlayer player) {
                ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                if (!player.isEntityAlive()) {
                    if (head.getItem() instanceof IModuleContainerItem item) {
                        if (item.isModuleEnabled(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT) || item.isModuleEnabled(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT)) {
                            if (!item.isModuleEnabled(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT)) {
                                item.removeModule(head, MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT);
                            }
                            player.hurtResistantTime = 20;
                            player.deathTime = 0;
                            player.isDead = false;
                            player.setHealth(5F);
                            player.clearActivePotions();
                            player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 800, 2));
                            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 900, 2));
                            player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 2));
                            player.setAir(300);
                            player.getFoodStats().addStats(20, 20);
                        }
                    }
                }
            }
        }
    }

    private boolean isInsulated(ItemStack stack) {
        return ModuleInstallation(stack, MekaSuitMoreModules.INSULATED_UNIT);
    }

    @SubscribeEvent
    @Optional.Method(modid = "immersiveengineering")
    public void onIEElectricDamage(LivingAttackEvent event) {
        EntityLivingBase base = event.getEntityLiving();
        boolean helmet = isInsulated(base.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
        boolean chest = isInsulated(base.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
        boolean legs = isInsulated(base.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
        boolean feet = isInsulated(base.getItemStackFromSlot(EntityEquipmentSlot.FEET));
        if (helmet && chest && legs && feet) {
            if (event.getSource() instanceof ElectricDamageSource damageSource) {
                damageSource.dmg = 0;
                event.setCanceled(true);
            }
        }
    }


    public static final Set<String> CHAOS_DAMAGE_NAMES = Sets.newHashSet(
            "de.GuardianFireball", "de.GuardianEnergyBall", "de.GuardianChaosBall",
            "chaosImplosion", "damage.de.fusionExplode", "de.islandImplode");

    @SubscribeEvent //这个事件用于计算是否可以完全取消混沌伤害
    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public void onDEDamage(LivingAttackEvent event) {
        if (event.getSource() instanceof DEDamageSources.DamageSourceChaos || CHAOS_DAMAGE_NAMES.contains(event.getSource().damageType) || event.getSource().damageType.equals("chaos")) {
            EntityLivingBase base = event.getEntityLiving();
            int totalLevel = 0;
            for (ItemStack stack : base.getArmorInventoryList()) {
                if (stack.getItem() instanceof IModuleContainerItem item) {
                    IModule<?> module = item.getModule(stack, MekaSuitMoreModules.CHAOS_RESISTANCE_UNIT);
                    if (module != null && module.isEnabled()) {
                        totalLevel += module.getInstalledCount();
                    }
                }
            }
            if (totalLevel > 0) {
                float newDamage = event.getAmount() * (1F - (totalLevel * 0.01F));
                if (newDamage <= 0F) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent //这个事件用于计算是否可以取消混沌伤害 ，如果不能，则重新设置消减后的值
    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    public void onDEDamage(LivingHurtEvent event) {
        if (event.getSource() instanceof DEDamageSources.DamageSourceChaos || CHAOS_DAMAGE_NAMES.contains(event.getSource().damageType) || event.getSource().damageType.equals("chaos")) {
            EntityLivingBase base = event.getEntityLiving();
            int totalLevel = 0;
            for (ItemStack stack : base.getArmorInventoryList()) {
                if (stack.getItem() instanceof IModuleContainerItem item) {
                    IModule<?> module = item.getModule(stack, MekaSuitMoreModules.CHAOS_RESISTANCE_UNIT);
                    if (module != null && module.isEnabled()) {
                        totalLevel += module.getInstalledCount();
                    }
                }
            }
            if (totalLevel > 0) {
                float newDamage = event.getAmount() * (1F - (totalLevel * 0.01F));
                if (newDamage > 0F) {
                    event.setAmount(newDamage);
                } else {
                    event.setCanceled(true);
                }
            }
        }
    }


}
