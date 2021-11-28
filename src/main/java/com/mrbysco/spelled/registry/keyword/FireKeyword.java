package com.mrbysco.spelled.registry.keyword;

import com.mrbysco.spelled.api.keywords.BaseKeyword;
import com.mrbysco.spelled.api.keywords.IKeyword;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class FireKeyword extends BaseKeyword {
    public FireKeyword(String keyword, int level, int slots) {
        super(keyword, level, slots);
    }

    @Override
    public void cast(World worldIn, ServerPlayerEntity caster, SpellEntity spell, @Nullable IKeyword adjective) {
        if(spell != null) {
            if(adjective instanceof LiquidKeyword) {
                spell.setLava(true);
                spell.insertAction("lava");
            } else {
                spell.setFiery(true);
                spell.insertAction("fire");
            }
        }
    }
}
