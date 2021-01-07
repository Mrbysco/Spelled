package com.mrbysco.spelled.registry.keyword;

import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

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
    public void cast(World worldIn, ServerPlayerEntity caster, SpellEntity spell, @Nullable IKeyword adjective) {
        //Do stuff
    }

    @Override
    public String getKeyword() {
        return this.keyword;
    }

    @Override
    public ITextComponent getDescription() {
        return new TranslationTextComponent("spelled.keyword." + this.keyword + ".description");
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
