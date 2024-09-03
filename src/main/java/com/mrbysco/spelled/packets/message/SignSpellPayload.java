package com.mrbysco.spelled.packets.message;

import com.mrbysco.spelled.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

public record SignSpellPayload(ItemStack book, boolean signing, String title, String spell, int slot) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, SignSpellPayload> CODEC = CustomPacketPayload.codec(
			SignSpellPayload::write,
			SignSpellPayload::new);
	public static final Type<SignSpellPayload> ID = new Type<>(Reference.modLoc("sign_spell"));

	public SignSpellPayload(RegistryFriendlyByteBuf buf) {
		this(ItemStack.STREAM_CODEC.decode(buf), buf.readBoolean(), buf.readUtf(), buf.readUtf(), buf.readVarInt());
	}

	public void write(RegistryFriendlyByteBuf buf) {
		ItemStack.STREAM_CODEC.encode(buf, this.book);
		buf.writeBoolean(this.signing);
		buf.writeUtf(this.title);
		buf.writeUtf(this.spell);
		buf.writeVarInt(this.slot);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}