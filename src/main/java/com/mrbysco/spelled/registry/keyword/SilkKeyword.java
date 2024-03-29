package com.mrbysco.spelled.registry.keyword;

import com.mrbysco.spelled.api.keywords.BaseKeyword;
import com.mrbysco.spelled.api.keywords.IKeyword;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class SilkKeyword extends BaseKeyword {
	public SilkKeyword(String keyword, int level, int slots) {
		super(keyword, level, slots);
	}

	@Override
	public void cast(Level worldIn, ServerPlayer caster, SpellEntity spell, @Nullable IKeyword adjective) {
		if (spell != null) {
			spell.setSilky(true);
			spell.insertAction("silk");
		}
	}
}
