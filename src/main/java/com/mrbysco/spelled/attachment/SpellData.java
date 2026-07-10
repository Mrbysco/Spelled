package com.mrbysco.spelled.attachment;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.capability.ISpellData;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.util.Locale;

public class SpellData implements ISpellData {
	private int level;
	private CompoundTag unlockedKeywords;
	private int castCooldown;

	public SpellData() {
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
	public void serialize(ValueOutput output) {
		output.putInt(Reference.characterLevel, getLevel());
		output.store(Reference.characterUnlocks, CompoundTag.CODEC, getUnlocked());
		output.putInt(Reference.characterCooldown, getCastCooldown());
	}

	@Override
	public void deserialize(ValueInput input) {
		int level = input.getIntOr(Reference.characterLevel, 0);
		CompoundTag characterUnlocks = input.read(Reference.characterUnlocks, CompoundTag.CODEC).orElse(new CompoundTag());
		int castCooldown = input.getIntOr(Reference.characterCooldown, 0);

		setLevel(level);
		setUnlocked(characterUnlocks);
		setCastCooldown(castCooldown);
	}
}
