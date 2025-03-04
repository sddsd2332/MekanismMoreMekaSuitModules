package moremekasuitmodules.mixin.appliedenergistics2;

import appeng.api.implementations.IUpgradeableCellContainer;
import appeng.api.storage.ITerminalHost;
import appeng.container.implementations.ContainerPatternEncoder;
import appeng.container.implementations.ContainerWirelessPatternTerminal;
import appeng.container.interfaces.IInventorySlotAware;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.Platform;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ContainerWirelessPatternTerminal.class)
public abstract class MixinContainerWirelessPatternTerminal extends ContainerPatternEncoder implements IUpgradeableCellContainer, IInventorySlotAware {


    @Shadow(remap = false)
    protected AppEngInternalInventory output;

    @Shadow(remap = false)
    protected AppEngInternalInventory pattern;

    @Shadow(remap = false)
    protected AppEngInternalInventory upgrades;

    @Shadow(remap = false)
    @Final
    private WirelessTerminalGuiObject wirelessTerminalGUIObject;

    protected MixinContainerWirelessPatternTerminal(InventoryPlayer ip, ITerminalHost monitorable, boolean bindInventory) {
        super(ip, monitorable, bindInventory);
    }


    /**
     * @author sddsd2332
     * @reason 修复样板和合成终端同一个nbt
     */
    @Override
    public void saveChanges() {
        if (Platform.isServer()) {
            NBTTagCompound tag = new NBTTagCompound();
            ((AppEngInternalInventory) this.crafting).writeToNBT(tag, "craftingGridPattern");
            this.output.writeToNBT(tag, "output");
            this.pattern.writeToNBT(tag, "patterns");
            this.upgrades.writeToNBT(tag, "upgrades");
            this.wirelessTerminalGUIObject.saveChanges(tag);
        }
    }

    /**
     * @author sddsd2332
     * @reason 修复样板和合成终端同一个nbt
     */
    @Overwrite(remap = false)
    private void loadFromNBT() {
        NBTTagCompound data = wirelessTerminalGUIObject.getItemStack().getTagCompound();
        if (data != null) {
            ((AppEngInternalInventory) crafting).readFromNBT(data, "craftingGridPattern");
            this.output.readFromNBT(data, "output");
            this.pattern.readFromNBT(data, "patterns");
            upgrades.readFromNBT(wirelessTerminalGUIObject.getItemStack().getTagCompound().getCompoundTag("upgrades"));
        }
    }

}
