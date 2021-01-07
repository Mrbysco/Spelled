package com.mrbysco.spelled.registry.keyword;

import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SizeKeyword extends BaseKeyword {
    private final float sizeMultiplier;

    public SizeKeyword(String keyword, float sizeMultiplier, int level, int slots) {
        super(keyword, level, slots);
        this.sizeMultiplier = sizeMultiplier;
    }

    @Override
    public void cast(World worldIn, ServerPlayerEntity caster, SpellEntity spell, @Nullable IKeyword adjective) {
        if(spell != null) {
            spell.setSizeMultiplier(spell.getSizeMultiplier() * sizeMultiplier);
        }
    }
}
