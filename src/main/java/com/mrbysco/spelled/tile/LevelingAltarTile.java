package com.mrbysco.spelled.tile;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.Nameable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nullable;

public class LevelingAltarTile extends BlockEntity implements Nameable {
    private Component customName;

    public LevelingAltarTile() {
        super(SpelledRegistry.LEVELING_ALTAR_TILE.get());
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        if (this.hasCustomName())
            compound.putString("CustomName", Component.Serializer.toJson(this.customName));

        return compound;
    }

    @Override
    public void load(BlockState state, CompoundTag nbt) {
        super.load(state, nbt);
        if (nbt.contains("CustomName", 8))
            this.customName = Component.Serializer.fromJson(nbt.getString("CustomName"));
    }

    @Override
    public Component getName() {
        return (Component)(this.customName != null ? this.customName : new TranslatableComponent(Reference.MOD_PREFIX+ "container.altar"));
    }

    public void setCustomName(@Nullable Component name) {
        this.customName = name;
    }

    @Nullable
    public Component getCustomName() {
        return this.customName;
    }
}
