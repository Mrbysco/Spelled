package com.mrbysco.spelled.tile;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class LevelingAltarTile extends TileEntity implements INameable {
    private ITextComponent customName;

    public LevelingAltarTile() {
        super(SpelledRegistry.LEVELING_ALTAR_TILE.get());
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (this.hasCustomName())
            compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));

        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        if (nbt.contains("CustomName", 8))
            this.customName = ITextComponent.Serializer.getComponentFromJson(nbt.getString("CustomName"));
    }

    @Override
    public ITextComponent getName() {
        return (ITextComponent)(this.customName != null ? this.customName : new TranslationTextComponent(Reference.MOD_PREFIX+ "container.altar"));
    }

    public void setCustomName(@Nullable ITextComponent name) {
        this.customName = name;
    }

    @Nullable
    public ITextComponent getCustomName() {
        return this.customName;
    }
}
