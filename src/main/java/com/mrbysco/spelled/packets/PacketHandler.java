package com.mrbysco.spelled.packets;

import com.mrbysco.spelled.Spelled;

public class PacketHandler {

    public static void registerPackets() {
        Spelled.CHANNEL.registerMessage(0, SpellDataSyncMessage.class, SpellDataSyncMessage::encode, SpellDataSyncMessage::decode, SpellDataSyncMessage::handle);
    }
}
