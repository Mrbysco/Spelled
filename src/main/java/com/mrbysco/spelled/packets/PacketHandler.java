package com.mrbysco.spelled.packets;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.packets.handler.ClientPayloadHandler;
import com.mrbysco.spelled.packets.handler.ServerPayloadHandler;
import com.mrbysco.spelled.packets.message.SignSpellPayload;
import com.mrbysco.spelled.packets.message.SpellDataSyncPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {

	public static void setupPackets(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(Reference.MOD_ID);

		registrar.playToClient(SpellDataSyncPayload.ID, SpellDataSyncPayload.CODEC, ClientPayloadHandler.getInstance()::handleSync);
		registrar.playToServer(SignSpellPayload.ID, SignSpellPayload.CODEC, ServerPayloadHandler.getInstance()::handleSignSpell);
	}
}
