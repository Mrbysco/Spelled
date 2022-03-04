package com.mrbysco.spelled.api.keywords;

import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface IKeyword {

	void cast(Level worldIn, ServerPlayer caster, SpellEntity spell, @Nullable IKeyword adjective);

	/*
	 * @return keyword
	 */
	String getKeyword();

	/*
	 * @return description of keyword
	 */
	Component getDescription();

	/*
	 * @return level required for keyword
	 */
	int getLevel();

	/*
	 * Set level required for keyword
	 * @param level: the level required for the keyword
	 */
	void setLevel(int level);

	/*
	 * @return slots used by keyword
	 */
	int getSlots();

	/*
	 * Set slots used by the keyword
	 * @param slots: the slots used by the keyword
	 */
	void setSlots(int slots);
}
