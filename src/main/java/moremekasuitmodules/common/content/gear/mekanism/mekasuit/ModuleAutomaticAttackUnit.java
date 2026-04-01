package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.energy.IEnergizedItem;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleBooleanData;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import mekanism.api.gear.config.ModuleEnumData;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.TextComponentGroup;
import mekanism.common.MekanismDamageSource;
import mekanism.common.MekanismLang;
import moremekasuitmodules.common.config.MoreModulesConfig;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.MoreMekaSuitModulesLang;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.FakePlayer;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@ParametersAreNotNullByDefault
public class ModuleAutomaticAttackUnit implements ICustomModule<ModuleAutomaticAttackUnit> {

    private IModuleConfigItem<Boolean> attackPlayer;
    private IModuleConfigItem<Boolean> attackHostile;
    private IModuleConfigItem<Boolean> attackFriendly;
    private IModuleConfigItem<Boolean> attackOther;
    private IModuleConfigItem<Range> range;
    private IModuleConfigItem<TickAttack> tickAttack;

    @Override
    public void init(IModule<ModuleAutomaticAttackUnit> module, ModuleConfigItemCreator configItemCreator) {
        attackPlayer = configItemCreator.createConfigItem("attack_player", MoreMekaSuitModulesLang.MODULE_ATTACK_PLAYER, new ModuleBooleanData(false));
        attackHostile = configItemCreator.createConfigItem("attack_hostile", MoreMekaSuitModulesLang.MODULE_ATTACK_HOSTILE, new ModuleBooleanData());
        attackFriendly = configItemCreator.createConfigItem("attack_neutral", MoreMekaSuitModulesLang.MODULE_ATTACK_FRIENDLY, new ModuleBooleanData(false));
        attackOther = configItemCreator.createConfigItem("attack_other", MoreMekaSuitModulesLang.MODULE_ATTACK_OTHER, new ModuleBooleanData(false));
        range = configItemCreator.createConfigItem("range", MekanismLang.MODULE_RANGE, new ModuleEnumData<>(Range.LOW, module.getInstalledCount() + 1));
        tickAttack = configItemCreator.createConfigItem("tick", MoreMekaSuitModulesLang.MODULE_ATTACK_TICK, new ModuleEnumData<>(TickAttack.LOW, module.getInstalledCount() + 1));
    }

    private int getRange() {
        return range.get().getRange();
    }

    private int getTick() {
        return tickAttack.get().getTick();
    }

    @Override
    public void tickServer(IModule<ModuleAutomaticAttackUnit> module, EntityPlayer player) {
        if (range.get() == Range.OFF || tickAttack.get() == TickAttack.OFF) {
            return;
        }
        if (player.ticksExisted % getTick() != 0) {
            return;
        }

        float size = getRange();
        double usage = MoreModulesConfig.current().config.mekaSuitEnergyUsageItemAttack.val() * getRange();
        boolean free = usage == 0 || player.isCreative();
        IEnergizedItem energyContainer = free ? null : module.getEnergyContainer();

        if (!free && (energyContainer == null || energyContainer.getEnergy(module.getContainer()) < usage)) {
            return;
        }

        List<EntityLivingBase> targets = player.world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                player.getEntityBoundingBox().grow(size),
                target -> isValidTarget(player, target)
        );
        if (targets.isEmpty()) {
            return;
        }

        targets.sort(Comparator.comparingDouble(player::getDistanceSq));
        int maxTargetsPerTick = Math.max(1, MoreModulesConfig.current().config.MekAsuitAutoAttackMax.val());
        int targetCount = targets.size();
        int limit = Math.min(targetCount, maxTargetsPerTick);
        int start = ThreadLocalRandom.current().nextInt(targetCount);
        for (int i = 0; i < limit; i++) {
            EntityLivingBase target = targets.get((start + i) % targetCount);
            if (free) {
                attackEntityFrom(player, target);
            } else if (module.useEnergy(player, energyContainer, usage, true) == 0) {
                // If we can't actually extract energy, exit.
                break;
            } else {
                attackEntityFrom(player, target);
                if (energyContainer.getEnergy(module.getContainer()) < usage) {
                    // If after using energy we don't have enough for next hit, exit.
                    break;
                }
            }
        }
    }

    private boolean isValidTarget(EntityPlayer player, EntityLivingBase target) {
        if (!target.isEntityAlive()) {
            return false;
        }

        if (target instanceof EntityPlayer) {
            return attackPlayer.get() && !(target instanceof FakePlayer) && !target.equals(player);
        }
        if (attackHostile.get() && target instanceof IMob) {
            return true;
        }
        if (attackFriendly.get() && target instanceof IAnimals) {
            return true;
        }
        return attackOther.get();
    }

    private void attackEntityFrom(EntityPlayer player, EntityLivingBase target) {
        MekanismDamageSource source = MekanismDamageSource.SMARTATTACKDAMAGE.setTrueSource(player);
        float sourceAmount = (float) player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        target.attackEntityFrom(source, sourceAmount);
        player.swingArm(EnumHand.MAIN_HAND);
    }

    public enum Range implements IHasTextComponent {
        OFF(0),
        LOW(2),
        MED(4),
        HIGH(8),
        ULTRA(16);

        private final int range;
        private final ITextComponent label;

        Range(int boost) {
            this.range = boost;
            this.label = new TextComponentGroup().getString(Float.toString(boost));
        }

        @Override
        public ITextComponent getTextComponent() {
            return label;
        }

        public int getRange() {
            return range;
        }
    }

    public enum TickAttack implements IHasTextComponent {
        OFF(1000),
        LOW(100),
        MED(80),
        HIGH(60),
        ULTRA(40),
        MAX(20);

        private final int tick;
        private final ITextComponent label;

        TickAttack(int boost) {
            this.tick = boost;
            this.label = new TextComponentGroup().getString(Float.toString(boost));
        }

        @Override
        public ITextComponent getTextComponent() {
            return label;
        }

        public int getTick() {
            return tick;
        }
    }
}
