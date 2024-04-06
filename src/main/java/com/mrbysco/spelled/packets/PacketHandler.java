package com.mrbysco.spelled.packets;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.packets.handler.ClientPayloadHandler;
import com.mrbysco.spelled.packets.handler.ServerPayloadHandler;
import com.mrbysco.spelled.packets.message.SignSpellPayload;
import com.mrbysco.spelled.packets.message.SpellDataSyncPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class PacketHandler {

	public static void setupPackets(final RegisterPayloadHandlerEvent event) {
		final IPayloadRegistrar registrar = event.registrar(Reference.MOD_ID);

		registrar.play(SpellDataSyncPayload.ID, SpellDataSyncPayload::new, handler -> handler
				.client(ClientPayloadHandler.getInstance()::handleSync));
		registrar.play(SignSpellPayload.ID, SignSpellPayload::new, handler -> handler
				.server(ServerPayloadHandler.getInstance()::handleSignSpell));
	}
}
