package com.mrbysco.spelled.packets;

import com.mrbysco.spelled.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Reference.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static int i = 0;

    public static void registerPackets() {
        PacketHandler.CHANNEL.registerMessage(i++, SpellDataSyncMessage.class, SpellDataSyncMessage::encode, SpellDataSyncMessage::decode, SpellDataSyncMessage::handle);
        PacketHandler.CHANNEL.registerMessage(i++, SignSpellPacket.class, SignSpellPacket::encode, SignSpellPacket::decode, SignSpellPacket::handle);
    }
}
