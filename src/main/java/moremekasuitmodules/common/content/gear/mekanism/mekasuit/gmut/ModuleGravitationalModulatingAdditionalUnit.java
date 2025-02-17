package moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut;

import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleBooleanData;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import mekanism.api.gear.config.ModuleEnumData;
import mekanism.common.CommonPlayerTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ModuleGravitationalModulatingAdditionalUnit implements ICustomModule<ModuleGravitationalModulatingAdditionalUnit> {
    private IModuleConfigItem<Boolean> flyAlways;
    private IModuleConfigItem<Boolean> stopImmediately;
    private IModuleConfigItem<Boolean> fixFOV;
    private IModuleConfigItem<VerticalSpeed> verticalSpeed;

    @Override
    public void init(IModule<ModuleGravitationalModulatingAdditionalUnit> module, ModuleConfigItemCreator configItemCreator) {
        this.flyAlways = configItemCreator.createConfigItem("fly_always", GMUTLang.MODULE_FLY_ALWAYS, new ModuleBooleanData(false));
        this.stopImmediately = configItemCreator.createConfigItem("stop_immediately", GMUTLang.MODULE_STOP_IMMEDIATELY, new ModuleBooleanData(true));
        this.fixFOV = configItemCreator.createConfigItem("fix_fov", GMUTLang.MODULE_FIX_FOV, new ModuleBooleanData(false));
        this.verticalSpeed = configItemCreator.createConfigItem("vertical_speed", GMUTLang.MODULE_VERTICAL_SPEED, new ModuleEnumData<>(VerticalSpeed.class, VerticalSpeed.OFF));
    }

    @Override
    public void tickServer(IModule<ModuleGravitationalModulatingAdditionalUnit> module, EntityPlayer player) {
        boolean hasGravitationalModulator = CommonPlayerTickHandler.isGravitationalModulationReady(player);
        if (hasGravitationalModulator) {
            if (this.flyAlways.get()) {
                if (!player.isSneaking() && !player.capabilities.isFlying) {
                    player.capabilities.isFlying = true;
                    player.sendPlayerAbilities();
                }
            }
        }
    }

    @Override
    public void tickClient(IModule<ModuleGravitationalModulatingAdditionalUnit> module, EntityPlayer player) {
        this.tickServer(module, player);
        boolean hasGravitationalModulator = CommonPlayerTickHandler.isGravitationalModulationReady(player);
        if (hasGravitationalModulator) {
            if (this.stopImmediately.get()) {
                if (player.capabilities.isFlying && player.moveForward == 0.0F && player.moveStrafing == 0.0F) {
                    player.motionY *= 1.0D;
                }
            }
            if (player instanceof EntityPlayerSP clientPlayer) {
                if (clientPlayer.capabilities.isFlying && Minecraft.getMinecraft().player == clientPlayer) {
                    float j = 0.0F;
                    if (clientPlayer.movementInput.sneak) {
                        j--;
                    }
                    if (clientPlayer.movementInput.jump) {
                        j++;
                    }
                    if (j != 0) {
                        j *= (this.getVerticalSpeed().get().getSpeed() - 1.0F);
                        Vec3d deltaMovement = new Vec3d(clientPlayer.motionX, clientPlayer.motionY, clientPlayer.motionZ);
                        Vec3d in = deltaMovement.add(0.0D, j * clientPlayer.capabilities.getFlySpeed() * 3.0F, 0.0D);
                        clientPlayer.motionX = in.x;
                        clientPlayer.motionY = in.y;
                        clientPlayer.motionZ = in.z;
                    }
                }
            }
        }
    }

    @Override
    public void changeMode(IModule<ModuleGravitationalModulatingAdditionalUnit> module, EntityPlayer player, ItemStack stack, int shift, boolean displayChangeMessage) {
        if (module.isEnabled()) {
            VerticalSpeed prevSpeed = this.getVerticalSpeed().get();
            VerticalSpeed nextSpeed = prevSpeed.adjust(shift);
            if (prevSpeed != nextSpeed) {
                this.getVerticalSpeed().set(nextSpeed);
                if (displayChangeMessage) {
                    module.displayModeChange(player, GMUTLang.MODULE_VERTICAL_SPEED.getTranslationKey(), nextSpeed);
                }
            }
        }
    }

    public IModuleConfigItem<Boolean> getFlyAlways() {
        return this.flyAlways;
    }

    public IModuleConfigItem<Boolean> getStopImmediately() {
        return this.stopImmediately;
    }

    public IModuleConfigItem<Boolean> getFixFOV() {
        return this.fixFOV;
    }

    public IModuleConfigItem<VerticalSpeed> getVerticalSpeed() {
        return this.verticalSpeed;
    }

}
