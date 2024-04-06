package com.mrbysco.spelled.packets.message;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.capability.ISpellData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record SpellDataSyncPayload(CompoundTag data, UUID playerUUID) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "spell_data_sync");

	public SpellDataSyncPayload(FriendlyByteBuf buf) {
		this(buf.readNbt(), buf.readUUID());
	}

	public SpellDataSyncPayload(ISpellData data, UUID playerUUID) {
		this(data.serializeNBT(), playerUUID);
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeNbt(data);
		buf.writeUUID(playerUUID);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}
}