package com.mrbysco.spelled.registry.keyword;

import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PushingKeyword extends BaseKeyword {
    public PushingKeyword(String keyword, int level, int slots) {
        super(keyword, level, slots);
    }

    @Override
    public void cast(World worldIn, ServerPlayerEntity caster, SpellEntity spell, @Nullable IKeyword adjective) {
        if(spell != null) {
            spell.insertAction("knockback");
        }
    }
}
