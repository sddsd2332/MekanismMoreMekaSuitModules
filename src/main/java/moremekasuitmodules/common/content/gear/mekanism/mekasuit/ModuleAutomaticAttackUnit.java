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
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.GMUTLang;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.FakePlayer;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNotNullByDefault
public class ModuleAutomaticAttackUnit implements ICustomModule<ModuleAutomaticAttackUnit> {

    private IModuleConfigItem<Boolean> attackPlayer;
    private IModuleConfigItem<Boolean> attackHostile;
    private IModuleConfigItem<Boolean> attackFriendly;
    private IModuleConfigItem<Range> range;

    @Override
    public void init(IModule<ModuleAutomaticAttackUnit> module, ModuleConfigItemCreator configItemCreator) {
        attackPlayer = configItemCreator.createConfigItem("attack_player", GMUTLang.MODULE_ATTACK_PLAYER, new ModuleBooleanData(false));
        attackHostile = configItemCreator.createConfigItem("attack_hostile", GMUTLang.MODULE_ATTACK_HOSTILE, new ModuleBooleanData());
        attackFriendly = configItemCreator.createConfigItem("attack_friendly", GMUTLang.MODULE_ATTACK_FRIENDLY, new ModuleBooleanData(false));
        range = configItemCreator.createConfigItem("range", MekanismLang.MODULE_RANGE, new ModuleEnumData<>(Range.LOW, module.getInstalledCount() + 1));
    }

    private int getRange() {
        return range.get().getRange();
    }

    @Override
    public void tickClient(IModule<ModuleAutomaticAttackUnit> module, EntityPlayer player) {
        tickServer(module, player);
    }

    @Override
    public void tickServer(IModule<ModuleAutomaticAttackUnit> module, EntityPlayer player) {
        if (range.get() == Range.OFF) {
            return;
        }
        float size = getRange();
        double usage = MoreModulesConfig.current().config.mekaSuitEnergyUsageItemAttack.val() * getRange();
        boolean free = usage == 0 || player.isCreative();
        IEnergizedItem energyContainer = free ? null : module.getEnergyContainer();
        if (free || (energyContainer != null && energyContainer.getEnergy(module.getContainer()) >= (usage))) {
            List<EntityLivingBase> all = player.world.getEntitiesWithinAABB(EntityLivingBase.class, player.getEntityBoundingBox().grow(size));
            List<EntityLivingBase> canAttackList = new ArrayList<>();
            for (EntityLivingBase allEntity : all) {
                //排除是玩家
                if (!(allEntity instanceof EntityPlayer)) {
                    //如果带有怪物标签
                    if (attackHostile.get() && allEntity instanceof IMob) {
                        canAttackList.add(allEntity);
                    } else if (attackFriendly.get()) {
                        //添加到攻击列表
                        canAttackList.add(allEntity);
                    }
                    //如果是玩家，但不是假玩家
                } else if (attackPlayer.get() && allEntity instanceof EntityPlayer isplayer && !(isplayer instanceof FakePlayer)) {
                    //如果攻击列表中包含该模块的玩家，则跳过
                    if (!isplayer.equals(player)) {
                        canAttackList.add(isplayer);
                    }
                }
            }
            if (!canAttackList.isEmpty()) {
                for (EntityLivingBase attackList : canAttackList) {
                    if (attackList.isEntityAlive()) {
                        if (free) {
                            attackEntityFrom(player, attackList);
                        } else if (module.useEnergy(player, energyContainer, usage, true) == 0) {
                            //If we can't actually extract energy, exit
                            break;
                        } else {
                            attackEntityFrom(player, attackList);
                            if (energyContainer.getEnergy(module.getContainer()) < usage) {
                                //If after using energy, our energy is now smaller than how much we need to use, exit
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void attackEntityFrom(EntityPlayer player, EntityLivingBase target) {
        //伤害源
        MekanismDamageSource source = MekanismDamageSource.SMARTATTACKDAMAGE.setTrueSource(player);
        //获取玩家伤害属性
        float soureAmount = (float) player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        //攻击实体
        target.attackEntityFrom(source, soureAmount);
        //播放主手动画
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

}
