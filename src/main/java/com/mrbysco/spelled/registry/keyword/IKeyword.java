package com.mrbysco.spelled.registry.keyword;

import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IKeyword {

    void cast(World worldIn, ServerPlayerEntity caster, SpellEntity spell, @Nullable IKeyword adjective);

    /*
     * @return keyword
     */
    String getKeyword();

    /*
     * @return description of keyword
     */
    ITextComponent getDescription();

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
