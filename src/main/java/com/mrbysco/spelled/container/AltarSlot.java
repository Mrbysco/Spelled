package com.mrbysco.spelled.container;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.util.LevelHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class AltarSlot extends Slot {
    private final PlayerEntity player;

    public AltarSlot(PlayerEntity player, IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.player = player;
    }

    public boolean isItemValid(ItemStack stack) {
        int currentLevel = !player.world.isRemote ? SpelledAPI.getLevel((ServerPlayerEntity) player) : 0;
        return stack.getItem() == LevelHelper.getItemCost(currentLevel + 1).getItem();
    }
}
