package moremekasuitmodules.common.content.gear;

import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleContainer;
import mekanism.common.config.MekanismConfig;
import moremekasuitmodules.common.MoreMekaSuitModulesLang;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ModuleQuantumReconstructionUnit implements ICustomModule<ModuleQuantumReconstructionUnit> {

    private static final ModuleDamageAbsorbInfo INHALATION_ABSORB_INFO = new ModuleDamageAbsorbInfo(MekanismConfig.gear.mekaSuitMagicDamageRatio, MekanismConfig.gear.mekaSuitEnergyUsageMagicReduce);

    @Override
    public void tickClient(IModule<ModuleQuantumReconstructionUnit> module, IModuleContainer moduleContainer, ItemStack stack, Player player) {
        this.tickServer(module, moduleContainer, stack, player);
    }

    @Override
    public void tickServer(IModule<ModuleQuantumReconstructionUnit> module, IModuleContainer moduleContainer, ItemStack stack, Player player) {
        player.noPhysics = module.isEnabled() && player.isAlive() && !player.onGround();
    }

    @Nullable
    @Override
    public ModuleDamageAbsorbInfo getDamageAbsorbInfo(IModule<ModuleQuantumReconstructionUnit> module, DamageSource damageSource) {
        return damageSource.is(DamageTypes.IN_WALL) ? INHALATION_ABSORB_INFO : null;
    }

    @Override
    public boolean canChangeModeWhenDisabled(IModule<ModuleQuantumReconstructionUnit> module) {
        return true;
    }

    @Override
    public void changeMode(IModule<ModuleQuantumReconstructionUnit> module,Player player, IModuleContainer moduleContainer, ItemStack stack, int shift, boolean displayChangeMessage) {
        module.toggleEnabled(moduleContainer, stack, player, MoreMekaSuitModulesLang.MODULE_PHASE_THROUGH_BLOCKS.translate());
    }

}
