package com.mrbysco.spelled.packets.handler;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.packets.message.SpellDataSyncPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueInput;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
	private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

	public static ClientPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleSync(final SpellDataSyncPayload data, final IPayloadContext context) {
		context.enqueueWork(() -> {
					Player player = context.player().level().getPlayerByUUID(data.playerUUID());
					if (player != null) {
						SpelledAPI.getSpellDataCap(player).ifPresent(sanityCap -> {
							sanityCap.deserialize(TagValueInput.create(ProblemReporter.DISCARDING, player.registryAccess(), data.data()));
						});
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("spelled.networking.spell_data_sync.failed", e.getMessage()));
					return null;
				});
	}
}
