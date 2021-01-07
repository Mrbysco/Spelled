package com.mrbysco.spelled.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class DisableUseMessage {

    public DisableUseMessage() {

    }

    private DisableUseMessage(PacketBuffer buf) {

    }

    public void encode(PacketBuffer buf) {

    }

    public static DisableUseMessage decode(final PacketBuffer packetBuffer) {
        return new DisableUseMessage(packetBuffer);
    }

    public void handle(Supplier<Context> context) {
        Context ctx = context.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isClient()) {
                com.mrbysco.spelled.client.KeyBindings.KEY_USE.setPressed(false);
            }
        });
        ctx.setPacketHandled(true);
    }
}