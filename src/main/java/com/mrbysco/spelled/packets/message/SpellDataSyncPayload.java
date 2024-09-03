package com.mrbysco.spelled.packets.message;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.capability.ISpellData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record SpellDataSyncPayload(CompoundTag data, UUID playerUUID) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, SpellDataSyncPayload> CODEC = CustomPacketPayload.codec(
			SpellDataSyncPayload::write,
			SpellDataSyncPayload::new);
	public static final Type<SpellDataSyncPayload> ID = new Type<>(Reference.modLoc("spell_data_sync"));

	public SpellDataSyncPayload(FriendlyByteBuf buf) {
		this(buf.readNbt(), buf.readUUID());
	}

	public SpellDataSyncPayload(ISpellData data, UUID playerUUID) {
		this(data.serializeNBT(RegistryAccess.EMPTY), playerUUID);
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeNbt(data);
		buf.writeUUID(playerUUID);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}