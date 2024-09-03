package com.mrbysco.spelled.chat;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.util.SpellUtil;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class SpellCastHandler {
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent.Post event) {
		if (event.getEntity() instanceof ServerPlayer player && player.level().getGameTime() % 20 == 0) {
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
