package com.mrbysco.spelled.packets.message;

import com.mrbysco.spelled.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record SignSpellPayload(ItemStack book, boolean signing, int slot) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "sign_spell");

	public SignSpellPayload(FriendlyByteBuf buf) {
		this(buf.readItem(), buf.readBoolean(), buf.readVarInt());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeItem(this.book);
		buf.writeBoolean(this.signing);
		buf.writeVarInt(this.slot);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}
}