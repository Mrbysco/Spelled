package com.mrbysco.spelled.menu;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.util.LevelHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AltarSlot extends Slot {
	private final Player player;

	public AltarSlot(Player player, Container inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		this.player = player;
	}

	public boolean mayPlace(ItemStack stack) {
		int currentLevel = !player.level.isClientSide ? SpelledAPI.getLevel((ServerPlayer) player) : 0;
		return stack.getItem() == LevelHelper.getItemCost(currentLevel + 1).getItem();
	}
}
