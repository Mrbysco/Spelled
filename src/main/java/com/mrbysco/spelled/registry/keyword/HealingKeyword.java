package com.mrbysco.spelled.registry.keyword;

import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class HealingKeyword extends BaseKeyword {
    public HealingKeyword(String keyword, int level, int slots) {
        super(keyword, level, slots);
    }

    @Override
    public void cast(World worldIn, ServerPlayerEntity caster, SpellEntity spell, @Nullable IKeyword adjective) {
        if(spell != null) {
            if(spell.isHealing()) {
                int currentFactor = spell.getHealingFactor().getAsInt();
                spell.setHealing(currentFactor + 1);
            } else {
                spell.setHealing(1);
            }
        }
    }
}
