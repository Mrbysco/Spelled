package com.mrbysco.spelled.api.capability;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public class SpellDataCapability implements ISpellData {
	private int level;
	private CompoundTag unlockedKeywords;
	private int castCooldown;

	public SpellDataCapability() {
		this.level = 0;
		this.unlockedKeywords = getDefaultUnlocks();
		this.castCooldown = 0;
	}

	@Override
	public int getLevel() {
		return this.level;
	}

	@Override
	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public CompoundTag getUnlocked() {
		return this.unlockedKeywords;
	}

	@Override
	public void setUnlocked(CompoundTag nbt) {
		this.unlockedKeywords = nbt;
	}

	@Override
	public boolean knowsKeyword(String keyword) {
		return this.unlockedKeywords.contains(keyword);
	}

	@Override
	public void unlockKeyword(String keyword) {
		this.unlockedKeywords.putBoolean(keyword.toLowerCase(Locale.ROOT), true);
	}

	@Override
	public void lockKeyword(String keyword) {
		KeywordRegistry registry = KeywordRegistry.instance();
		if (!registry.getTypes().contains(keyword)) {
			this.unlockedKeywords.remove(keyword.toLowerCase(Locale.ROOT));
		}
	}

	@Override
	public int getCastCooldown() {
		return this.castCooldown;
	}

	@Override
	public void setCastCooldown(int cooldown) {
		this.castCooldown = cooldown;
	}

	@Override
	public void resetUnlocks() {
		this.unlockedKeywords = getDefaultUnlocks();
	}

	private CompoundTag getDefaultUnlocks() {
		KeywordRegistry registry = KeywordRegistry.instance();
		CompoundTag tag = new CompoundTag();
		registry.getTypes().forEach(type -> tag.putBoolean(type, true));
		return tag;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putInt(Reference.characterLevel, getLevel());
		tag.put(Reference.characterUnlocks, getUnlocked());
		tag.putInt(Reference.characterCooldown, getCastCooldown());

		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		int level = tag.getInt(Reference.characterLevel);
		CompoundTag characterUnlocks = tag.getCompound(Reference.characterUnlocks);
		int castCooldown = tag.getInt(Reference.characterCooldown);

		setLevel(level);
		setUnlocked(characterUnlocks);
		setCastCooldown(castCooldown);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return SpelledAPI.SPELL_DATA_CAP.orEmpty(cap, LazyOptional.of(() -> this));
	}
}
