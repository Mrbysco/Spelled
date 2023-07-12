package com.mrbysco.spelled.registry.keyword;

import com.mrbysco.spelled.api.keywords.BaseKeyword;
import com.mrbysco.spelled.api.keywords.IKeyword;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.awt.*;

public class ColorKeyword extends BaseKeyword {
	private final ChatFormatting color;

	public ColorKeyword(String keyword, ChatFormatting color, int level, int slots) {
		super(keyword, level, slots);
		this.color = color;
	}

	@Override
	public void cast(Level level, ServerPlayer caster, SpellEntity spell, @Nullable IKeyword adjective) {
		if (spell != null) {
			if ((adjective == null || adjective instanceof LiquidKeyword) && color == ChatFormatting.BLACK) {
				spell.setInky(true);
				spell.insertAction("ink");
			}
			if (color == ChatFormatting.AQUA && adjective instanceof LiquidKeyword) {
				spell.setWater(true);
				Color waterColor = new Color(0.2F, 0.3F, 1.0F);
				int colorID = waterColor.getRGB();
				if (spell.hasColor()) {
					int currentColor = spell.getColor().getAsInt();
					int combinedColor = (int) ((currentColor * 0.5F) + (colorID * 0.5F)); //Combine colors
					spell.setColor(combinedColor);
				} else {
					spell.setColor(colorID);
				}
				spell.insertAction("water");
			} else {
				Integer colorID = color.getColor();
				if (colorID != null) {
					if (spell.hasColor()) {
						int currentColor = spell.getColor().getAsInt();
						int combinedColor = (int) ((currentColor * 0.5F) + (colorID * 0.5F)); //Combine colors
						spell.setColor(combinedColor);
					} else {
						spell.setColor(colorID);
					}
				}

				if (adjective == null && color == ChatFormatting.WHITE) {
					spell.insertAction("glow");
				}
			}
		}
	}
}
