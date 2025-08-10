package moremekasuitmodules.common.content.gear;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleBooleanData;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import mekanism.api.gear.config.ModuleEnumData;
import mekanism.api.math.FloatingLong;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.MekanismLang;
import moremekasuitmodules.common.MoreMekaSuitModulesLang;
import moremekasuitmodules.common.config.MoreModulesConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNotNullByDefault
public class ModuleAutomaticAttackUnit implements ICustomModule<ModuleAutomaticAttackUnit> {

    private IModuleConfigItem<Boolean> attackPlayer;
    private IModuleConfigItem<Boolean> attackHostile;
    private IModuleConfigItem<Boolean> attackNeutral;
    private IModuleConfigItem<Boolean> attackOther;
    private IModuleConfigItem<Range> range;

    @Override
    public void init(IModule<ModuleAutomaticAttackUnit> module, ModuleConfigItemCreator configItemCreator) {
        attackPlayer = configItemCreator.createConfigItem("attack_player", MoreMekaSuitModulesLang.MODULE_ATTACK_PLAYER, new ModuleBooleanData(false));
        attackHostile = configItemCreator.createConfigItem("attack_hostile", MoreMekaSuitModulesLang.MODULE_ATTACK_HOSTILE, new ModuleBooleanData());
        attackNeutral = configItemCreator.createConfigItem("attack_neutral", MoreMekaSuitModulesLang.MODULE_ATTACK_NEUTRAL, new ModuleBooleanData(false));
        attackOther = configItemCreator.createConfigItem("attack_other", MoreMekaSuitModulesLang.MODULE_ATTACK_OTHER, new ModuleBooleanData(false));
        range = configItemCreator.createConfigItem("range", MekanismLang.MODULE_RANGE, new ModuleEnumData<>(Range.LOW, module.getInstalledCount() + 1));
    }

    private int getRange() {
        return range.get().getRange();
    }

    @Override
    public void tickClient(IModule<ModuleAutomaticAttackUnit> module, Player player) {
        tickServer(module, player);
    }

    @Override
    public void tickServer(IModule<ModuleAutomaticAttackUnit> module, Player player) {
        if (range.get() == Range.OFF) {
            return;
        }
        float size = getRange();
        FloatingLong usage = MoreModulesConfig.config.mekaSuitEnergyUsageItemAttack.get().multiply(getRange());
        boolean free = usage.isZero() || player.isCreative();
        IEnergyContainer energyContainer = free ? null : module.getEnergyContainer();
        if (free || (energyContainer != null && energyContainer.getEnergy().greaterOrEqual(usage))) {
            List<LivingEntity> all = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(size));
            List<LivingEntity> canAttackList = new ArrayList<>();
            for (LivingEntity allEntity : all) {
                //排除是玩家
                if (!(allEntity instanceof Player)) {
                    //如果带有怪物标签,但不是中立生物
                    if (attackHostile.get() && allEntity instanceof Enemy && !(allEntity instanceof NeutralMob)) {
                        canAttackList.add(allEntity);
                        //如果是中立生物
                    } else if (attackNeutral.get() && allEntity instanceof NeutralMob) {
                        canAttackList.add(allEntity);
                        //剩余其他生物
                    } else if (attackOther.get()) {
                        canAttackList.add(allEntity);
                    }
                    //如果是玩家，但不是假玩家
                } else if (attackPlayer.get() && allEntity instanceof Player isplayer && !(isplayer instanceof FakePlayer)) {
                    //如果攻击列表中包含该模块的玩家，则跳过
                    if (!isplayer.equals(player)) {
                        canAttackList.add(isplayer);
                    }
                }
            }
            if (!canAttackList.isEmpty()) {
                for (LivingEntity attackList : canAttackList) {
                    if (attackList.isAlive()) {
                        if (free) {
                            attackEntityFrom(player, attackList);
                        } else if (module.useEnergy(player, energyContainer, usage, true).isZero()) {
                            //If we can't actually extract energy, exit
                            break;
                        } else {
                            attackEntityFrom(player, attackList);
                            if (energyContainer.getEnergy().smallerThan(usage)) {
                                //If after using energy, our energy is now smaller than how much we need to use, exit
                                break;
                            }
                        }
                    }
                }
            }
        }
    }


    private void attackEntityFrom(Player player, LivingEntity target) {
        //伤害源设置为玩家
        DamageSource source = player.damageSources().playerAttack(player);
        //获取玩家伤害属性
        float soureAmount = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        //攻击实体
        target.hurt(source, soureAmount);
    }

    @NothingNullByDefault
    public enum Range implements IHasTextComponent {
        OFF(0),
        LOW(2),
        MED(4),
        HIGH(8),
        ULTRA(16);

        private final int range;
        private final Component label;

        Range(int boost) {
            this.range = boost;
            this.label = TextComponentUtil.getString(Float.toString(boost));
        }

        @Override
        public Component getTextComponent() {
            return label;
        }

        public int getRange() {
            return range;
        }
    }
}
