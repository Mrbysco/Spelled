package com.mrbysco.spelled.packets;

import com.mrbysco.spelled.item.SpellbookItem;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SignSpellPacket {
	private final ItemStack book;
	private final boolean signing;
	private final int slot;

	public SignSpellPacket(ItemStack stack, boolean signing, int slot) {
		this.book = stack.copy();
		this.signing = signing;
		this.slot = slot;
	}

	private SignSpellPacket(FriendlyByteBuf buf) {
		this.book = buf.readItem();
		this.signing = buf.readBoolean();
		this.slot = buf.readVarInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeItem(this.book);
		buf.writeBoolean(this.signing);
		buf.writeVarInt(this.slot);
	}

	public static SignSpellPacket decode(final FriendlyByteBuf packetBuffer) {
		return new SignSpellPacket(packetBuffer);
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer()) {
				ServerPlayer player = ctx.getSender();
				if (book.getItem() == SpelledRegistry.SPELL_BOOK.get()) {
					CompoundTag compoundnbt = book.getTag();
					if (SpellbookItem.makeSureTagIsValid(player, compoundnbt)) {
						if (Inventory.isHotbarSlot(slot) || slot == 40) {
							if (signing) {
								this.signBook(player, compoundnbt.getString("title"), compoundnbt.getString("spell"), slot);
							} else {
								this.updateBookContents(player, compoundnbt.getString("spell"), slot);
							}
						}
					}
				}
			}
		});
		ctx.setPacketHandled(true);
	}

	private void updateBookContents(ServerPlayer player, String spell, int slot) {
		ItemStack stack = player.getInventory().getItem(slot);
		if (stack.getItem() == SpelledRegistry.SPELL_BOOK.get()) {
			stack.addTagElement("spell", StringTag.valueOf(spell));
		}
	}

	private void signBook(ServerPlayer player, String title, String spell, int slot) {
		ItemStack itemstack = player.getInventory().getItem(slot);
		if (itemstack.getItem() == SpelledRegistry.SPELL_BOOK.get()) {
			ItemStack stack = new ItemStack(SpelledRegistry.SPELL_BOOK.get());
			CompoundTag compoundnbt = itemstack.getTag();
			if (compoundnbt != null) {
				stack.setTag(compoundnbt.copy());
			}

			stack.addTagElement("author", StringTag.valueOf(player.getName().getString()));
			stack.addTagElement("title", StringTag.valueOf(title));
			stack.addTagElement("spell", StringTag.valueOf(spell));
			stack.addTagElement("sealed", ByteTag.valueOf(true));
			player.getInventory().setItem(slot, stack);
		}
	}
}