package com.mrbysco.spelled.api.keywords;

import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class BaseKeyword implements IKeyword {
	private final String keyword;
	private int level;
	private int slots;

	public BaseKeyword(String keyword, int level, int slots) {
		this.keyword = keyword;
		this.level = level;
		this.slots = slots;
	}

	@Override
	public void cast(Level level, ServerPlayer caster, SpellEntity spell, @Nullable IKeyword adjective) {
		//Do stuff
	}

	@Override
	public String getKeyword() {
		return this.keyword;
	}

	@Override
	public Component getDescription() {
		return Component.translatable("spelled.keyword." + this.keyword + ".description");
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
	public int getSlots() {
		return this.slots;
	}

	@Override
	public void setSlots(int slots) {
		this.slots = slots;
	}
}
