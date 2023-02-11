package com.mrbysco.spelled.chat;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.util.SpellUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SpellCastHandler {
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START)
			return;

		Level world = event.player.level;
		if (!world.isClientSide && world.getGameTime() % 20 == 0) {
			ServerPlayer player = (ServerPlayer) event.player;
			int cooldown = SpelledAPI.getCooldown(player);
			if (cooldown > 0) {
				SpelledAPI.setCooldown(player, cooldown - 1);
				SpelledAPI.syncCap(player);
			}
		}
	}

	@SubscribeEvent
	public void onChatEvent(ServerChatEvent event) {
		final String regExp = "^[a-zA-Z\\s]*$";
		String actualMessage = event.getRawText();
		if (!actualMessage.isEmpty() && actualMessage.matches(regExp)) {
			SpellUtil.castSpell(event);
		}
	}
}
