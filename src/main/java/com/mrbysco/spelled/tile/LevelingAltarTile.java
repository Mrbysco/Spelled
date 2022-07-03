package com.mrbysco.spelled.tile;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class LevelingAltarTile extends BlockEntity implements Nameable {
	private Component customName;

	public LevelingAltarTile(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
		super(entityType, pos, state);
	}

	public LevelingAltarTile(BlockPos pos, BlockState state) {
		this(SpelledRegistry.LEVELING_ALTAR_TILE.get(), pos, state);
	}

	@Override
	protected void saveAdditional(CompoundTag compoundTag) {
		super.saveAdditional(compoundTag);

		if (this.hasCustomName())
			compoundTag.putString("CustomName", Component.Serializer.toJson(this.customName));
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (nbt.contains("CustomName", 8))
			this.customName = Component.Serializer.fromJson(nbt.getString("CustomName"));
	}

	@Override
	public Component getName() {
		return this.customName != null ? this.customName : Component.translatable(Reference.MOD_PREFIX + "container.altar");
	}

	public void setCustomName(@Nullable Component name) {
		this.customName = name;
	}

	@Nullable
	public Component getCustomName() {
		return this.customName;
	}
}
