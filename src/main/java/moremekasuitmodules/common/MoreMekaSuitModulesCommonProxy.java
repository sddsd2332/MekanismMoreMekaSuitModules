package moremekasuitmodules.common;

import mekanism.common.base.IGuiProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class MoreMekaSuitModulesCommonProxy implements IGuiProvider {


    @Override
    public Container getServerGui(int i, EntityPlayer entityPlayer, World world, BlockPos blockPos) {
        return null;
    }

    @Override
    public Object getClientGui(int i, EntityPlayer entityPlayer, World world, BlockPos blockPos) {
        return null;
    }

    public void init() {

    }

    public void preInit() {
    }

    public void registerItemRenders() {
    }
}
