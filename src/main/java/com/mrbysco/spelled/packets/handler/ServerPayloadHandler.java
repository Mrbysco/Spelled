package com.mrbysco.spelled.packets.handler;

import com.mrbysco.spelled.packets.message.SignSpellPayload;
import com.mrbysco.spelled.registry.SpelledComponents;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public class ServerPayloadHandler {
	public static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

	public static ServerPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleSignSpell(final SignSpellPayload data, final IPayloadContext context) {
		// Do something with the data, on the main thread
		context.enqueueWork(() -> {
					if (context.player() instanceof ServerPlayer player) {
						ItemStack book = data.book();
						if (book.getItem() == SpelledRegistry.SPELL_BOOK.get()) {
							int slot = data.slot();

							if (Inventory.isHotbarSlot(slot) || slot == 40) {
								boolean signing = data.signing();
								if (signing) {
									this.signBook(player, data.title(), data.spell(), slot);
								} else {
									this.updateBookContents(player, data.spell(), slot);
								}
							}
						}
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("statues.networking.player_statue_sync.failed", e.getMessage()));
					return null;
				});
	}

	private void updateBookContents(Player player, String spell, int slot) {
		ItemStack stack = player.getInventory().getItem(slot);
		if (stack.getItem() == SpelledRegistry.SPELL_BOOK.get()) {
			stack.set(SpelledComponents.SPELL, spell);
		}
	}

	private void signBook(Player player, String title, String spell, int index) {
		ItemStack itemstack = player.getInventory().getItem(index);
		if (itemstack.getItem() == SpelledRegistry.SPELL_BOOK.get()) {
			ItemStack stack = itemstack.transmuteCopy(SpelledRegistry.SPELL_BOOK.get());
			stack.remove(DataComponents.WRITABLE_BOOK_CONTENT);
			List<Filterable<Component>> list = new ArrayList<>();
			stack.set(
					DataComponents.WRITTEN_BOOK_CONTENT,
					new WrittenBookContent(filterableFromOutgoing(player, FilteredText.passThrough(title)), player.getName().getString(), 0, list, true)
			);
			stack.set(SpelledComponents.SPELL, spell);
			stack.set(SpelledComponents.SEALED, true);
			player.getInventory().setItem(index, stack);
		}
	}

	private Filterable<String> filterableFromOutgoing(Player player, FilteredText filteredText) {
		return player.isTextFilteringEnabled() ? Filterable.passThrough(filteredText.filteredOrEmpty()) : Filterable.from(filteredText);
	}
}
