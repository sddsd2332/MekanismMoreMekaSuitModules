package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import mekanism.api.gear.config.ModuleEnumData;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.TextComponentGroup;
import mekanism.common.KeySync;
import mekanism.common.Mekanism;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.MoreMekaSuitModulesLang;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;

@ParametersAreNotNullByDefault
public class ModuleWallClingUnit implements ICustomModule<ModuleWallClingUnit> {

    private static final double WALL_CHECK_DISTANCE = 0.04D;
    private static final double CLING_ENERGY_USAGE = 60D;
    private static final double HOLD_ENERGY_USAGE = 80D;
    private static final double CLIMB_ENERGY_USAGE = 250D;
    private static final double PASSIVE_SLIDE_SPEED = -0.18D;
    private static final double SNEAK_HOLD_SPEED = 0D;

    private IModuleConfigItem<ClimbSpeed> climbSpeed;

    @Override
    public void init(IModule<ModuleWallClingUnit> module, ModuleConfigItemCreator configItemCreator) {
        climbSpeed = configItemCreator.createConfigItem("climb_speed", MoreMekaSuitModulesLang.MODULE_CLIMB_SPEED, new ModuleEnumData<>(ClimbSpeed.MED));
    }

    @Override
    public boolean canChangeModeWhenDisabled(IModule<ModuleWallClingUnit> module) {
        return true;
    }

    @Override
    public void changeMode(IModule<ModuleWallClingUnit> module, EntityPlayer player, ItemStack stack, int shift, boolean displayChangeMessage) {
        module.toggleEnabled(player, MoreMekaSuitModulesLang.MODULE_WALL_CLING.getTranslationKey());
    }

    @Override
    public void tickServer(IModule<ModuleWallClingUnit> module, EntityPlayer player) {
        tick(module, player, isJumping(player), true);
    }

    @Override
    public void tickClient(IModule<ModuleWallClingUnit> module, EntityPlayer player) {
        tick(module, player, isJumping(player), false);
    }

    private boolean isJumping(EntityPlayer player) {
        return Mekanism.keyMap.has(player.getUniqueID(), KeySync.ASCEND);
    }

    private void tick(IModule<ModuleWallClingUnit> module, EntityPlayer player, boolean jumping, boolean consumeEnergy) {
        if (!canFunction(player) || !isTouchingWall(player)) {
            return;
        }

        ClimbSpeed speed = climbSpeed.get();
        Action action = getAction(player, jumping);
        double usage = action.getEnergyUsage(speed);
        if (!module.canUseEnergy(player, usage, false)) {
            return;
        }

        applyAction(player, action, speed);
        if (consumeEnergy) {
            module.useEnergy(player, usage);
        }
    }

    private boolean canFunction(EntityPlayer player) {
        return player.isEntityAlive()
                && !player.onGround
                && !player.isSpectator()
                && !player.capabilities.isFlying
                && !player.isElytraFlying()
                && !player.isRiding()
                && !player.isOnLadder()
                && !player.isInWater()
                && !player.isInLava();
    }

    private boolean isTouchingWall(EntityPlayer player) {
        if (player.collidedHorizontally) {
            return true;
        }
        AxisAlignedBB box = player.getEntityBoundingBox();
        return hasHorizontalCollision(player, box.expand(WALL_CHECK_DISTANCE, 0, 0))
                || hasHorizontalCollision(player, box.expand(-WALL_CHECK_DISTANCE, 0, 0))
                || hasHorizontalCollision(player, box.expand(0, 0, WALL_CHECK_DISTANCE))
                || hasHorizontalCollision(player, box.expand(0, 0, -WALL_CHECK_DISTANCE));
    }

    private boolean hasHorizontalCollision(EntityPlayer player, AxisAlignedBB box) {
        return !player.world.getCollisionBoxes(player, box).isEmpty();
    }

    private Action getAction(EntityPlayer player, boolean jumping) {
        if (jumping && !player.isSneaking()) {
            return Action.CLIMB;
        }
        if (player.isSneaking()) {
            return Action.HOLD;
        }
        return Action.CLING;
    }

    private void applyAction(EntityPlayer player, Action action, ClimbSpeed speed) {
        switch (action) {
            case CLIMB:
                player.motionY = Math.max(player.motionY, speed.getSpeed());
                break;
            case HOLD:
                player.motionY = SNEAK_HOLD_SPEED;
                break;
            case CLING:
                if (player.motionY < PASSIVE_SLIDE_SPEED) {
                    player.motionY = PASSIVE_SLIDE_SPEED;
                }
                break;
        }
        player.fallDistance = 0;
        if (player instanceof EntityPlayerMP serverPlayer) {
            serverPlayer.connection.floatingTickCount = 0;
        }
    }

    private enum Action {
        CLIMB(CLIMB_ENERGY_USAGE),
        HOLD(HOLD_ENERGY_USAGE),
        CLING(CLING_ENERGY_USAGE);

        private final double energyUsage;

        Action(double energyUsage) {
            this.energyUsage = energyUsage;
        }

        private double getEnergyUsage(ClimbSpeed speed) {
            return energyUsage * speed.getEnergyMultiplier();
        }
    }

    @NothingNullByDefault
    public enum ClimbSpeed implements IHasTextComponent {
        LOW(0.14D, 1.0D),
        MED(0.20D, 1.5D),
        HIGH(0.28D, 2.25D);

        private final double speed;
        private final double energyMultiplier;
        private final ITextComponent label;

        ClimbSpeed(double speed, double energyMultiplier) {
            this.speed = speed;
            this.energyMultiplier = energyMultiplier;
            this.label = new TextComponentGroup().getString(Double.toString(speed));
        }

        @Override
        public ITextComponent getTextComponent() {
            return label;
        }

        public double getSpeed() {
            return speed;
        }

        public double getEnergyMultiplier() {
            return energyMultiplier;
        }
    }
}
