package com.mrbysco.spelled.api.capability;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface ISpellData extends INBTSerializable<CompoundTag> {
	CompoundTag getUnlocked();

	void setUnlocked(CompoundTag nbt);

	boolean knowsKeyword(String keyword);

	void unlockKeyword(String keyword);

	void lockKeyword(String keyword);

	void resetUnlocks();

	int getLevel();

	void setLevel(int level);

	int getCastCooldown();

	void setCastCooldown(int cooldown);
}
