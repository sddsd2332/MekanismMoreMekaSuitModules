package moremekasuitmodules.common.content.gear;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleContainer;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.Mekanism;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.config.MoreModulesConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.IntFunction;

@ParametersAreNotNullByDefault
public record ModuleAutomaticAttackUnit(boolean attackPlayer, boolean attackHostile, boolean attackNeutral,
                                        boolean attackOther,
                                        Range range) implements ICustomModule<ModuleAutomaticAttackUnit> {

    public static final ResourceLocation ATTACK_PLAYER = MoreMekaSuitModules.rl("attack_player");
    public static final ResourceLocation ATTACK_HOSTILE = MoreMekaSuitModules.rl("attack_hostile");
    public static final ResourceLocation ATTACK_NEUTRAL = MoreMekaSuitModules.rl("attack_neutral");
    public static final ResourceLocation ATTACK_OTHER = MoreMekaSuitModules.rl("attack_other");
    public static final ResourceLocation RANGE = Mekanism.rl("range");


    public ModuleAutomaticAttackUnit(IModule<ModuleAutomaticAttackUnit> module) {
        this(module.getBooleanConfigOrFalse(ATTACK_PLAYER), module.getBooleanConfigOrFalse(ATTACK_HOSTILE), module.getBooleanConfigOrFalse(ATTACK_NEUTRAL), module.getBooleanConfigOrFalse(ATTACK_OTHER), module.<Range>getConfigOrThrow(RANGE).get());
    }


    @Override
    public void tickClient(IModule<ModuleAutomaticAttackUnit> module, IModuleContainer moduleContainer, ItemStack stack, Player player) {
        tickServer(module, moduleContainer, stack, player);
    }

    @Override
    public void tickServer(IModule<ModuleAutomaticAttackUnit> module, IModuleContainer moduleContainer, ItemStack stack, Player player) {
        if (range == Range.OFF) {
            return;
        }
        float size = range.getRange();
        long usage = MoreModulesConfig.config.mekaSuitEnergyUsageItemAttack.get() * range.getRange();
        boolean free = usage == 0L || player.isCreative();
        IEnergyContainer energyContainer = free ? null : module.getEnergyContainer(stack);
        if (free || (energyContainer != null && energyContainer.getEnergy() >= usage)) {
            List<LivingEntity> all = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(size));
            List<LivingEntity> canAttackList = new ArrayList<>();
            for (LivingEntity allEntity : all) {
                //排除是玩家
                if (!(allEntity instanceof Player)) {
                    //如果带有怪物标签,但不是中立生物
                    if (attackHostile && allEntity instanceof Enemy && !(allEntity instanceof NeutralMob)) {
                        canAttackList.add(allEntity);
                        //如果是中立生物
                    } else if (attackNeutral && allEntity instanceof NeutralMob) {
                        canAttackList.add(allEntity);
                        //剩余其他生物
                    } else if (attackOther) {
                        canAttackList.add(allEntity);
                    }
                    //如果是玩家，但不是假玩家
                } else if (attackPlayer && allEntity instanceof Player isplayer && !(isplayer instanceof FakePlayer)) {
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
                        } else if (module.useEnergy(player, energyContainer, usage, true) == 0L) {
                            //If we can't actually extract energy, exit
                            break;
                        } else {
                            attackEntityFrom(player, attackList);
                            if (energyContainer.getEnergy() < usage) {
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
    public enum Range implements IHasTextComponent, StringRepresentable {
        OFF(0),
        LOW(2),
        MED(4),
        HIGH(8),
        ULTRA(16);

        public static final Codec<Range> CODEC = StringRepresentable.fromEnum(Range::values);
        public static final IntFunction<Range> BY_ID = ByIdMap.continuous(Range::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, Range> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Range::ordinal);

        private final String serializedName;
        private final int range;
        private final Component label;

        Range(int boost) {
            this.serializedName = name().toLowerCase(Locale.ROOT);
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

        @Override
        public String getSerializedName() {
            return serializedName;
        }
    }
}

