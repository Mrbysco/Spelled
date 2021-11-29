package com.mrbysco.spelled.client;

import com.mrbysco.spelled.client.gui.book.AdjectiveEntry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class ClientHelper {
	public static void openSpellBookScreen(List<AdjectiveEntry> adjectives, InteractionHand hand, Player player) {
		net.minecraft.client.Minecraft.getInstance().setScreen(new com.mrbysco.spelled.client.gui.book.SpellBookScreen(adjectives, player, hand));
	}
}
