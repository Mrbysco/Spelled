package com.mrbysco.spelled.handler;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.attachment.SpellData;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class CapabilityHandler {

	@SubscribeEvent
	public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
		Player player = event.getEntity();
		if (!player.level().isClientSide()) {
			SpelledAPI.syncCap((ServerPlayer) player);
		}
	}

	@SubscribeEvent
	public void onDeath(PlayerEvent.Clone event) {
		Player newPlayer = event.getEntity();
		if (event.isWasDeath() && !newPlayer.level().isClientSide()) {
			Player original = event.getOriginal();

			SpellData data = original.getData(SpelledRegistry.SPELL_DATA_ATTACHMENT);
			newPlayer.setData(SpelledRegistry.SPELL_DATA_ATTACHMENT, data);
		}
		if (!newPlayer.level().isClientSide()) {
			SpelledAPI.syncCap((ServerPlayer) newPlayer);
		}
	}
}
