package moremekasuitmodules.common.content.gear.integration.astralsorcery;

import hellfirepvp.astralsorcery.common.base.FluidRarityRegistry;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktPlayLiquidSpring;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

@ParametersAreNotNullByDefault
public class ModuleIchosicResonatorUnit implements ICustomModule<ModuleIchosicResonatorUnit> {

    @Override
    public void tickServer(IModule<ModuleIchosicResonatorUnit> module, EntityPlayer player) {
        if (Loader.isModLoaded("astralsorcery")) {
            module.useEnergy(player, 100);
            AsFluidEffect(player);
        }
    }

    @Optional.Method(modid = "astralsorcery")
    private void AsFluidEffect(EntityPlayer player) {
        double dstr = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(player.world);
        if (dstr <= 1E-4) return;
        if (player.rand.nextFloat() < dstr && player.rand.nextInt(15) == 0) {
            int oX = player.rand.nextInt(30) * (player.rand.nextBoolean() ? 1 : -1);
            int oZ = player.rand.nextInt(30) * (player.rand.nextBoolean() ? 1 : -1);

            BlockPos pos = new BlockPos(player.getPosition()).add(oX, 0, oZ);
            pos = player.world.getTopSolidOrLiquidBlock(pos);
            if (pos.getDistance(MathHelper.floor(player.posX), MathHelper.floor(player.posY), MathHelper.floor(player.posZ)) > 75) {
                return;
            }
            FluidRarityRegistry.ChunkFluidEntry at = FluidRarityRegistry.getChunkEntry(player.world.getChunk(pos));
            FluidStack display = at == null ? new FluidStack(FluidRegistry.WATER, 1) : at.tryDrain(1, false);
            if (display == null || display.getFluid() == null) display = new FluidStack(FluidRegistry.WATER, 1);
            PktPlayLiquidSpring pkt = new PktPlayLiquidSpring(display, new Vector3(pos).add(player.rand.nextFloat(), 0, player.rand.nextFloat()));
            PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(player.world, player.getPosition(), 32));
        }
    }
}
