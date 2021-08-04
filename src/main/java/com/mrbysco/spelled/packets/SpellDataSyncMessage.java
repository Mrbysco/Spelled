package com.mrbysco.spelled.packets;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.capability.ISpellData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.UUID;
import java.util.function.Supplier;

public class SpellDataSyncMessage {
    private CompoundTag data;
    private UUID playerUUID;

    public SpellDataSyncMessage(ISpellData data, UUID playerUUID) {
        this.data = (CompoundTag) SpelledAPI.SPELL_DATA_CAP.writeNBT(data, null);
        this.playerUUID = playerUUID;
    }

    private SpellDataSyncMessage(FriendlyByteBuf buf) {
        this.data = buf.readNbt();
        this.playerUUID = buf.readUUID();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(data);
        buf.writeUUID(playerUUID);
    }

    public static SpellDataSyncMessage decode(final FriendlyByteBuf packetBuffer) {
        return new SpellDataSyncMessage(packetBuffer);
    }

    public void handle(Supplier<Context> context) {
        Context ctx = context.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isClient()) {
                Player player = Minecraft.getInstance().level.getPlayerByUUID(this.playerUUID);
                if(player != null) {
                    player.getCapability(SpelledAPI.SPELL_DATA_CAP).ifPresent(sanityCap -> {
                        SpelledAPI.SPELL_DATA_CAP.readNBT(sanityCap, null, data);
                    });
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}