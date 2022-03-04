package com.mrbysco.spelled.handler;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.capability.ISpellData;
import com.mrbysco.spelled.api.capability.SpellDataCapability;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
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
		Player player = event.getPlayer();
		if (!player.level.isClientSide) {
			SpelledAPI.syncCap((ServerPlayer) player);
		}
	}

	@SubscribeEvent
	public void onDeath(PlayerEvent.Clone event) {
		Player original = event.getOriginal();
		Player newPlayer = event.getPlayer();

		final Capability<ISpellData> capability = SpelledAPI.SPELL_DATA_CAP;
		original.getCapability(capability).ifPresent(originalData -> {
			newPlayer.getCapability(capability).ifPresent(futureData -> {
				futureData.deserializeNBT(originalData.serializeNBT());
			});
		});

		if (!newPlayer.level.isClientSide) {
			SpelledAPI.syncCap((ServerPlayer) newPlayer);
		}
	}
}
