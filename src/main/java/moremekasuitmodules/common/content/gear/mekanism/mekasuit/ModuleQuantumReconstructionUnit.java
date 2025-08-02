package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.common.config.MekanismConfig;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.GMUTLang;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ModuleQuantumReconstructionUnit implements ICustomModule<ModuleQuantumReconstructionUnit> {

    private static final ModuleDamageAbsorbInfo INHALATION_ABSORB_INFO = new ModuleDamageAbsorbInfo(MekanismConfig.current().meka.mekaSuitMagicDamageRatio, MekanismConfig.current().meka.mekaSuitEnergyUsageMagicReduce.val());


    @Override
    public void tickClient(IModule<ModuleQuantumReconstructionUnit> module, EntityPlayer player) {
        this.tickServer(module, player);
    }

    @Override
    public void tickServer(IModule<ModuleQuantumReconstructionUnit> module, EntityPlayer player) {
        player.noClip = module.isEnabled() && player.isEntityAlive() && !player.onGround;
    }

    @Nullable
    @Override
    public ModuleDamageAbsorbInfo getDamageAbsorbInfo(IModule<ModuleQuantumReconstructionUnit> module, DamageSource damageSource) {
        return damageSource.equals(DamageSource.IN_WALL) ? INHALATION_ABSORB_INFO : null;
    }

    @Override
    public boolean canChangeModeWhenDisabled(IModule<ModuleQuantumReconstructionUnit> module) {
        return true;
    }

    @Override
    public void changeMode(IModule<ModuleQuantumReconstructionUnit> module, EntityPlayer player, ItemStack stack, int g, boolean displayChangeMessage) {
        module.toggleEnabled(player, GMUTLang.MODULE_PHASE_THROUGH_BLOCKS.getTranslationKey());
    }

}
