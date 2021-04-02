package com.mrbysco.spelled.registry.keyword;

import com.mrbysco.spelled.api.keywords.BaseKeyword;
import com.mrbysco.spelled.api.keywords.IKeyword;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.Color;

public class ColorKeyword extends BaseKeyword {
    private final TextFormatting color;

    public ColorKeyword(String keyword, TextFormatting color, int level, int slots) {
        super(keyword, level, slots);
        this.color = color;
    }

    @Override
    public void cast(World worldIn, ServerPlayerEntity caster, SpellEntity spell, @Nullable IKeyword adjective) {
        if(spell != null) {
            if(adjective instanceof LiquidKeyword && color == TextFormatting.BLACK) {
                spell.setInky(true);
                spell.insertAction("ink");
            }
            if(color == TextFormatting.AQUA && adjective instanceof LiquidKeyword) {
                spell.setWater(true);
                Color waterColor = new Color(0.2F, 0.3F, 1.0F);
                Integer colorID = waterColor.getRGB();
                if(spell.hasColor()) {
                    int currentColor = spell.getColor().getAsInt();
                    int combinedColor = (int)((currentColor * 0.5F) + (colorID * 0.5F)); //Combine colors
                    spell.setColor(combinedColor);
                } else {
                    spell.setColor(colorID);
                }
                spell.insertAction("water");
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
    }
}
