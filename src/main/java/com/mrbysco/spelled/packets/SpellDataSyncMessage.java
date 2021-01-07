package com.mrbysco.spelled.packets;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.capability.ISpellData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.UUID;
import java.util.function.Supplier;

public class SpellDataSyncMessage {
    private CompoundNBT data;
    private UUID playerUUID;

    public SpellDataSyncMessage(ISpellData data, UUID playerUUID) {
        this.data = (CompoundNBT) SpelledAPI.SPELL_DATA_CAP.writeNBT(data, null);
        this.playerUUID = playerUUID;
    }

    private SpellDataSyncMessage(PacketBuffer buf) {
        this.data = buf.readCompoundTag();
        this.playerUUID = buf.readUniqueId();
    }

    public void encode(PacketBuffer buf) {
        buf.writeCompoundTag(data);
        buf.writeUniqueId(playerUUID);
    }

    public static SpellDataSyncMessage decode(final PacketBuffer packetBuffer) {
        return new SpellDataSyncMessage(packetBuffer);
    }

    public void handle(Supplier<Context> context) {
        Context ctx = context.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isClient()) {
                PlayerEntity player = Minecraft.getInstance().world.getPlayerByUuid(this.playerUUID);
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