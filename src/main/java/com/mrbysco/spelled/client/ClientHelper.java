package com.mrbysco.spelled.client;

import com.mrbysco.spelled.client.gui.book.AdjectiveEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import java.util.List;

public class ClientHelper {
	public static void openSpellBookScreen(List<AdjectiveEntry> adjectives, Hand hand, PlayerEntity player) {
		net.minecraft.client.Minecraft.getInstance().setScreen(new com.mrbysco.spelled.client.gui.book.SpellBookScreen(adjectives, player, hand));
	}
}
