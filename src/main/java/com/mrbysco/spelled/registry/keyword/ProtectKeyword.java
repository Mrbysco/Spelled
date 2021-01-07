package com.mrbysco.spelled.registry.keyword;

import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ProtectKeyword extends BaseKeyword {
    public ProtectKeyword(String keyword, int level, int slots) {
        super(keyword, level, slots);
    }

    @Override
    public void cast(World worldIn, ServerPlayerEntity caster, SpellEntity spell, @Nullable IKeyword adjective) {
        if(spell != null) {
            if(spell.doesHurt()) {
                int currentDamage = spell.getDamage().getAsInt();
                spell.setDamage(currentDamage + 1);
            } else {
                spell.setDamage(1);
            }
        }
    }
}
