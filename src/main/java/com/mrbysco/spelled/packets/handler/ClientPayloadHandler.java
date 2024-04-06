package com.mrbysco.spelled.packets.handler;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.packets.message.SpellDataSyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ClientPayloadHandler {
	private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

	public static ClientPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleSync(final SpellDataSyncPayload data, final PlayPayloadContext context) {
		context.workHandler().submitAsync(() -> {
					Player player = Minecraft.getInstance().level.getPlayerByUUID(data.playerUUID());
					if (player != null) {
						SpelledAPI.getSpellDataCap(player).ifPresent(sanityCap -> {
							sanityCap.deserializeNBT(data.data());
						});
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.packetHandler().disconnect(Component.translatable("spelled.networking.spell_data_sync.failed", e.getMessage()));
					return null;
				});
	}
}
