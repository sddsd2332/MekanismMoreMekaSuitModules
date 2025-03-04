package moremekasuitmodules.mixin.appliedenergistics2;

import appeng.api.AEApi;
import appeng.api.definitions.IDefinitions;
import appeng.api.definitions.IItems;
import appeng.api.definitions.IMaterials;
import appeng.api.features.INetworkEncodable;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.implementations.items.IBiometricCard;
import appeng.api.implementations.items.ISpatialStorageCell;
import appeng.api.implementations.items.IStorageComponent;
import appeng.api.implementations.items.IUpgradeModule;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.ICellWorkbenchItem;
import appeng.container.slot.AppEngSlot;
import appeng.container.slot.SlotRestrictedInput;
import appeng.util.Platform;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SlotRestrictedInput.class)
public abstract class MixinSlotRestrictedInput extends AppEngSlot {

    @Shadow(remap = false)
    protected abstract boolean isAllowEdit();

    @Shadow(remap = false)
    @Final
    private SlotRestrictedInput.PlacableItemType which;

    @Shadow(remap = false)
    public static boolean isMetalIngot(ItemStack i) {
        return false;
    }

    @Shadow(remap = false)
    @Final
    private InventoryPlayer p;

    public MixinSlotRestrictedInput(IItemHandler inv, int idx, int x, int y) {
        super(inv, idx, x, y);
    }


    /**
     * @author sddsd2332
     * @reason 修改ae的有效物品
     */
    @Override
    public boolean isItemValid(final ItemStack i) {
        if (!this.getContainer().isValidForSlot(this, i)) {
            return false;
        }

        if (i.isEmpty()) {
            return false;
        }

        if (i.getItem() == Items.AIR) {
            return false;
        }

        if (!super.isItemValid(i)) {
            return false;
        }

        if (!this.isAllowEdit()) {
            return false;
        }

        final IDefinitions definitions = AEApi.instance().definitions();
        final IMaterials materials = definitions.materials();
        final IItems items = definitions.items();

        switch (this.which) {
            case ENCODED_CRAFTING_PATTERN:
                if (i.getItem() instanceof ICraftingPatternItem b) {
                    final ICraftingPatternDetails de = b.getPatternForItem(i, this.p.player.world);
                    if (de != null) {
                        return de.isCraftable();
                    }
                }
                return false;
            case VALID_ENCODED_PATTERN_W_OUTPUT:
            case ENCODED_PATTERN_W_OUTPUT:
            case ENCODED_PATTERN: {
                return i.getItem() instanceof ICraftingPatternItem;
            }
            case BLANK_PATTERN:
                return materials.blankPattern().isSameAs(i);
            case PATTERN:
                if (i.getItem() instanceof ICraftingPatternItem) {
                    return true;
                }
                return materials.blankPattern().isSameAs(i);
            case INSCRIBER_PLATE:
                if (materials.namePress().isSameAs(i)) {
                    return true;
                }
                for (final ItemStack optional : AEApi.instance().registries().inscriber().getOptionals()) {
                    if (Platform.itemComparisons().isSameItem(i, optional)) {
                        return true;
                    }
                }
                return false;
            case INSCRIBER_INPUT:
                return true;
            case METAL_INGOTS:
                return isMetalIngot(i);
            case VIEW_CELL:
                return items.viewCell().isSameAs(i);
            case ORE:
                return appeng.api.AEApi.instance().registries().grinder().getRecipeForInput(i) != null;
            case FUEL:
                return TileEntityFurnace.getItemBurnTime(i) > 0;
            case POWERED_TOOL:
                return Platform.isChargeable(i);
            case QE_SINGULARITY:
                return materials.qESingularity().isSameAs(i);

            case RANGE_BOOSTER:
                return materials.wirelessBooster().isSameAs(i);

            case SPATIAL_STORAGE_CELLS:
                return i.getItem() instanceof ISpatialStorageCell && ((ISpatialStorageCell) i.getItem()).isSpatialStorage(i);
            case STORAGE_CELLS:
                return AEApi.instance().registries().cell().isCellHandled(i);
            case WORKBENCH_CELL:
                return i.getItem() instanceof ICellWorkbenchItem && ((ICellWorkbenchItem) i.getItem()).isEditable(i);
            case STORAGE_COMPONENT:
                return i.getItem() instanceof IStorageComponent && ((IStorageComponent) i.getItem()).isStorageComponent(i);
            case TRASH:
                if (AEApi.instance().registries().cell().isCellHandled(i)) {
                    return false;
                }
                return !(i.getItem() instanceof IStorageComponent && ((IStorageComponent) i.getItem()).isStorageComponent(i));
            case ENCODABLE_ITEM:
                return i.getItem() instanceof INetworkEncodable && AEApi.instance().registries().wireless().isWirelessTerminal(i) && AEApi.instance().registries().wireless().getWirelessTerminalHandler(i).canHandle(i); //修改此处，只有可以添加的时候输入
            case BIOMETRIC_CARD:
                return i.getItem() instanceof IBiometricCard;
            case UPGRADES:
                return i.getItem() instanceof IUpgradeModule && ((IUpgradeModule) i.getItem()).getType(i) != null;
            case CARD_QUANTUM:
                if (AEApi.instance().definitions().materials().cardQuantumLink().maybeItem().isPresent()) {
                    return AEApi.instance().definitions().materials().cardQuantumLink().maybeItem().get() == i.getItem();
                }
                return false;
            default:
                break;
        }
        return false;
    }

}
