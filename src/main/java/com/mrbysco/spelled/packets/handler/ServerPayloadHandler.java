package com.mrbysco.spelled.packets.handler;

import com.mrbysco.spelled.item.SpellbookItem;
import com.mrbysco.spelled.packets.message.SignSpellPayload;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ServerPayloadHandler {
	public static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

	public static ServerPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleSignSpell(final SignSpellPayload data, final PlayPayloadContext context) {
		// Do something with the data, on the main thread
		context.workHandler().submitAsync(() -> {
					if (context.player().isPresent()) {
						Player player = context.player().get();
						ItemStack book = data.book();
						if (book.getItem() == SpelledRegistry.SPELL_BOOK.get()) {
							int slot = data.slot();
							CompoundTag tag = book.getTag();
							if (SpellbookItem.makeSureTagIsValid(player, tag)) {
								if (Inventory.isHotbarSlot(slot) || slot == 40) {
									boolean signing = data.signing();
									if (signing) {
										this.signBook(player, tag.getString("title"), tag.getString("spell"), slot);
									} else {
										this.updateBookContents(player, tag.getString("spell"), slot);
									}
								}
							}
						}
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.packetHandler().disconnect(Component.translatable("statues.networking.player_statue_sync.failed", e.getMessage()));
					return null;
				});
	}

	private void updateBookContents(Player player, String spell, int slot) {
		ItemStack stack = player.getInventory().getItem(slot);
		if (stack.getItem() == SpelledRegistry.SPELL_BOOK.get()) {
			stack.addTagElement("spell", StringTag.valueOf(spell));
		}
	}

	private void signBook(Player player, String title, String spell, int slot) {
		ItemStack itemstack = player.getInventory().getItem(slot);
		if (itemstack.getItem() == SpelledRegistry.SPELL_BOOK.get()) {
			ItemStack stack = new ItemStack(SpelledRegistry.SPELL_BOOK.get());
			CompoundTag tag = itemstack.getTag();
			if (tag != null) {
				stack.setTag(tag.copy());
			}

			stack.addTagElement("author", StringTag.valueOf(player.getName().getString()));
			stack.addTagElement("title", StringTag.valueOf(title));
			stack.addTagElement("spell", StringTag.valueOf(spell));
			stack.addTagElement("sealed", ByteTag.valueOf(true));
			player.getInventory().setItem(slot, stack);
		}
	}
}
