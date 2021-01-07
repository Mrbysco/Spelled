package com.mrbysco.spelled.packets;

import com.mrbysco.spelled.Spelled;

public class PacketHandler {

    public static void registerPackets() {
        Spelled.CHANNEL.registerMessage(0, SpellDataSyncMessage.class, SpellDataSyncMessage::encode, SpellDataSyncMessage::decode, SpellDataSyncMessage::handle);
        Spelled.CHANNEL.registerMessage(1, KeyboardSyncMessage.class, KeyboardSyncMessage::encode, KeyboardSyncMessage::decode, KeyboardSyncMessage::handle);
        Spelled.CHANNEL.registerMessage(2, DisableUseMessage.class, DisableUseMessage::encode, DisableUseMessage::decode, DisableUseMessage::handle);
    }
}
