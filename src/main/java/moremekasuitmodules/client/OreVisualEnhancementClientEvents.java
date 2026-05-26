package moremekasuitmodules.client;

import moremekasuitmodules.common.network.to_client.PacketOreVisualScan;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class OreVisualEnhancementClientEvents {

    private int oreValidationTicks;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        oreValidationTicks++;
        if (oreValidationTicks < 20) {
            return;
        }
        oreValidationTicks = 0;
        List<BlockPos> removed = new ArrayList<>();
        for (PacketOreVisualScan.OreEntry entry : OreVisualScanClientCache.getEntries()) {
            if (isRemovedClientSide(entry)) {
                removed.add(entry.getPos());
            }
        }
        OreVisualScanClientCache.removeAll(removed);
    }

    public static boolean isRemovedClientSide(PacketOreVisualScan.OreEntry entry) {
        Minecraft minecraft = Minecraft.getMinecraft();
        BlockPos pos = entry.getPos();
        if (minecraft.world == null || !minecraft.world.isBlockLoaded(pos)) {
            return false;
        }
        IBlockState state = minecraft.world.getBlockState(pos);
        Block block = state.getBlock();
        if (block == Blocks.AIR || block.isAir(state, minecraft.world, pos) || state.getMaterial() == Material.LAVA || state.getMaterial() == Material.WATER) {
            return true;
        }

        boolean currentIsOre = false;
        try {
            ItemStack stack = block.getPickBlock(state, null, minecraft.world, pos, null);
            if (stack.isEmpty()) {
                stack = new ItemStack(block, 1, block.damageDropped(state));
            }
            if (stack.isEmpty()) {
                return false;
            }
            for (int oreId : OreDictionary.getOreIDs(stack)) {
                String name = OreDictionary.getOreName(oreId);
                if (name.startsWith("ore")) {
                    currentIsOre = true;
                    if (name.equals(entry.getOreName())) {
                        return false;
                    }
                }
            }
        } catch (RuntimeException ignored) {
            return false;
        }
        return currentIsOre;
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote) {
            OreVisualScanClientCache.clear();
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        OreVisualScanClientCache.clear();
    }
}
