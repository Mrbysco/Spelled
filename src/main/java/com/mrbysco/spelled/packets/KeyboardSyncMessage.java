package com.mrbysco.spelled.packets;

import com.mrbysco.spelled.registry.KeyboardSync;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class KeyboardSyncMessage {
    public boolean keyDown;

    public KeyboardSyncMessage(boolean keyDown) {
        this.keyDown = keyDown;
    }

    private KeyboardSyncMessage(PacketBuffer buf) {
        this.keyDown = buf.readBoolean();
    }

    public void encode(PacketBuffer buf) {
        buf.writeBoolean(keyDown);
    }

    public static KeyboardSyncMessage decode(final PacketBuffer packetBuffer) {
        return new KeyboardSyncMessage(packetBuffer);
    }

    public void handle(Supplier<Context> context) {
        Context ctx = context.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isServer()) {
                ServerPlayerEntity serverPlayer = ctx.getSender();
                if(serverPlayer != null)
                    KeyboardSync.putUseKeyDown(serverPlayer.getUniqueID(), keyDown);
            }
        });
        ctx.setPacketHandled(true);
    }
}