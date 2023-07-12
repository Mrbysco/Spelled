package com.mrbysco.spelled.handler;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.capability.SpellDataCapability;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityHandler {
	@SubscribeEvent
	public void attachCapabilityEntity(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Player) {
			event.addCapability(Reference.SPELL_DATA_CAP, new SpellDataCapability());
		}
	}

	@SubscribeEvent
	public void playerLoggedInEvent(PlayerLoggedInEvent event) {
		Player player = event.getEntity();
		if (!player.level().isClientSide) {
			SpelledAPI.syncCap((ServerPlayer) player);
		}
	}

	@SubscribeEvent
	public void onDeath(PlayerEvent.Clone event) {
		Player newPlayer = event.getEntity();
		if (event.isWasDeath() && !newPlayer.level().isClientSide) {
			Player original = event.getOriginal();
			original.reviveCaps();

			newPlayer.getCapability(SpelledAPI.SPELL_DATA_CAP).ifPresent(cap ->
					original.getCapability(SpelledAPI.SPELL_DATA_CAP).ifPresent(oldCap -> {
						cap.deserializeNBT(oldCap.serializeNBT());
					}));
			original.invalidateCaps();
		}
		if (!newPlayer.level().isClientSide) {
			SpelledAPI.syncCap((ServerPlayer) newPlayer);
		}
	}
}
