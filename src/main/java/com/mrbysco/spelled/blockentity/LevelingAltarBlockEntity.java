package com.mrbysco.spelled.blockentity;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;

public class LevelingAltarBlockEntity extends BlockEntity implements Nameable {
	@Nullable
	private Component customName;

	public LevelingAltarBlockEntity(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
		super(entityType, pos, state);
	}

	public LevelingAltarBlockEntity(BlockPos pos, BlockState state) {
		this(SpelledRegistry.LEVELING_ALTAR_TILE.get(), pos, state);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);

		if (this.hasCustomName())
			output.putString("CustomName", this.customName.getString());
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);

		String name = input.getString("CustomName").orElse("");
		this.customName = name.isBlank() ? null : Component.literal(name);
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

	@Override
	protected void applyImplicitComponents(DataComponentGetter getter) {
		super.applyImplicitComponents(getter);
		this.customName = getter.get(DataComponents.CUSTOM_NAME);
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);
		components.set(DataComponents.CUSTOM_NAME, this.customName);
	}

	@Override
	public void removeComponentsFromTag(ValueOutput output) {
		output.discard("CustomName");
	}
}
