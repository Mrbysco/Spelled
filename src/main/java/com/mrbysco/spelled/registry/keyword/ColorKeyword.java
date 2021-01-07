package com.mrbysco.spelled.registry.keyword;

import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ColorKeyword extends BaseKeyword {
    private final TextFormatting color;

    public ColorKeyword(String keyword, TextFormatting color, int level, int slots) {
        super(keyword, level, slots);
        this.color = color;
    }

    @Override
    public void cast(World worldIn, ServerPlayerEntity caster, SpellEntity spell, @Nullable IKeyword adjective) {
        if(spell != null) {
            if(color == TextFormatting.AQUA && adjective != null && adjective instanceof LiquidKeyword) {
                spell.setWater(true);
            } else {
                Integer colorID = color.getColor();
                if(colorID != null) {
                    if(spell.hasColor()) {
                        int currentColor = spell.getColor().getAsInt();
                        int combinedColor = (int)((currentColor * 0.5F) + (colorID * 0.5F)); //Combine colors
                        spell.setColor(combinedColor);
                    } else {
                        spell.setColor(colorID);
                    }
                }
            }
        }

        //TODO: Do the colors combine?
    }
}
