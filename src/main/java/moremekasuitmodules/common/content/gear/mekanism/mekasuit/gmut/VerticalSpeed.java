package moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut;

import javax.annotation.Nonnull;

import mekanism.api.IIncrementalEnum;
import mekanism.api.math.MathUtils;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.TextComponentGroup;
import net.minecraft.util.text.ITextComponent;

public enum VerticalSpeed implements IHasTextComponent, IIncrementalEnum<VerticalSpeed>
{
    OFF(1.0F),
    LOW(1.5F),
    MEDIUM(2.0F),
    HIGH(2.5F),
    ULTRA(3.0F);

    private static final VerticalSpeed[] MODES = values();

    private final float speed;
    private final ITextComponent label;

    VerticalSpeed(float speed)
    {
        this.speed = speed;
        this.label = new TextComponentGroup().getString(Float.toString(speed));
    }

    @Nonnull
    @Override
    public VerticalSpeed byIndex(int index)
    {
        return MathUtils.getByIndexMod(MODES, index);
    }

    @Override
    public ITextComponent getTextComponent()
    {
        return this.label;
    }

    public float getSpeed()
    {
        return this.speed;
    }

}
