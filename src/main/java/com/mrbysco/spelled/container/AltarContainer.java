package com.mrbysco.spelled.container;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.config.ConfigCache.ItemCost;
import com.mrbysco.spelled.config.SpelledConfig;
import com.mrbysco.spelled.registry.SpelledRegistry;
import com.mrbysco.spelled.util.LevelHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class AltarContainer extends Container {

    private final IInventory tableInventory = new Inventory(1) {
        /**
         * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think
         * it hasn't changed and skip it.
         */
        public void setChanged() {
            super.setChanged();
            AltarContainer.this.slotsChanged(this);
        }
    };
    private final IWorldPosCallable worldPosCallable;

    public final int[] currentLevel = new int[1];
    public final int[] levelCosts = new int[SpelledConfig.COMMON.maxLevel.get()];
    public final int[] itemAmountCosts = new int[SpelledConfig.COMMON.maxLevel.get()];
    public final Item[] itemCosts = new Item[SpelledConfig.COMMON.maxLevel.get()];

    public AltarContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, IWorldPosCallable.NULL, 0);
    }

    public AltarContainer(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable, int currentLevel) {
        super(SpelledRegistry.ALTAR_CONTAINER.get(), id);
        this.worldPosCallable = worldPosCallable;

        this.currentLevel[0] = currentLevel;
        this.addDataSlot(IntReferenceHolder.shared(this.currentLevel, 0));

        for(int level = 0; level < SpelledConfig.COMMON.maxLevel.get(); level++) {
            levelCosts[level] = LevelHelper.getXPCost(level + 1);
            ItemCost cost = LevelHelper.getItemCost(level + 1);
            itemAmountCosts[level] = cost.getCost();
            itemCosts[level] = cost.getItem();
            this.addDataSlot(IntReferenceHolder.shared(this.levelCosts, level));
            this.addDataSlot(IntReferenceHolder.shared(this.itemAmountCosts, level));
        }

        if(SpelledConfig.COMMON.requireItems.get()) {
            this.addSlot(new AltarSlot(playerInventory.player, this.tableInventory, 0, 80, 56));
        }

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

    }

    public int getCostStackCount() {
        ItemStack itemstack = this.tableInventory.getItem(0);
        return itemstack.isEmpty() ? 0 : itemstack.getCount();
    }

    private boolean hasXP(PlayerEntity playerIn, int level) {
        int XPCost = this.levelCosts[level];
        return SpelledConfig.COMMON.individualLevels.get() ? playerIn.experienceLevel >= XPCost : playerIn.totalExperience >= XPCost;
    }

    public int getCurrentLevel() {
        return currentLevel[0];
    }

    public int getCurrentLevelCost() {
        int currentLevel = getCurrentLevel();
        if(currentLevel < levelCosts.length) {
            return levelCosts[currentLevel];
        }
        return -1;
    }

    public int getItemCostAmount() {
        int currentLevel = getCurrentLevel();
        if(currentLevel < itemAmountCosts.length) {
            return itemAmountCosts[currentLevel];
        }
        return -1;
    }

    public Item getItemCost() {
        int currentLevel = getCurrentLevel();
        if(currentLevel < itemCosts.length) {
            return itemCosts[currentLevel];
        }
        return Items.AIR;
    }

    /**
     * Handles the button-click for leveling (Name is due to it in the past only being used by the enchanting table)
     */
    public boolean clickMenuButton(PlayerEntity playerIn, int id) {
        int level = this.currentLevel[0];
        final int newLevel = level + 1;
        final int XPCost = this.levelCosts[level];

        if(level >= SpelledConfig.COMMON.maxLevel.get() || (!hasXP(playerIn, level)  && !playerIn.abilities.instabuild)) {
            if(!hasXP(playerIn, level)) {
                LevelHelper.levelUpFailXP(playerIn);
            }
            return false;
        } else {
            if(SpelledConfig.COMMON.requireItems.get()) {
                ItemStack stack = this.tableInventory.getItem(0);
                ItemCost itemCost = LevelHelper.getItemCost(newLevel);
                if(stack.isEmpty() || stack.getItem() != itemCost.getItem() && stack.getCount() < itemCost.getCost()) {
                    LevelHelper.levelUpFailItems(playerIn);
                    return false;
                } else {
                    this.worldPosCallable.execute((p_217003_6_, p_217003_7_) -> {
                        this.useLevels(playerIn, XPCost);

                        if (!playerIn.abilities.instabuild) {
                            stack.shrink(itemCost.getCost());
                            if (stack.isEmpty()) {
                                this.tableInventory.setItem(0, ItemStack.EMPTY);
                            }
                        }

                        this.tableInventory.setChanged();
                        this.slotsChanged(this.tableInventory);
                        p_217003_6_.playSound((PlayerEntity)null, p_217003_7_, SoundEvents.ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, p_217003_6_.random.nextFloat() * 0.1F + 0.9F);

                        SpelledAPI.forceSetLevel((ServerPlayerEntity) playerIn, newLevel);
                        SpelledAPI.syncCap((ServerPlayerEntity) playerIn);
                        this.currentLevel[0] = newLevel;
                    });
                    return true;
                }
            } else {
                this.useLevels(playerIn, XPCost);
                SpelledAPI.forceSetLevel((ServerPlayerEntity) playerIn, newLevel);
                SpelledAPI.syncCap((ServerPlayerEntity) playerIn);
                this.currentLevel[0] = newLevel;
                return true;
            }
        }
    }

    public void useLevels(PlayerEntity playerIn, int XPCost) {
        if (SpelledConfig.COMMON.individualLevels.get())
            playerIn.giveExperienceLevels(-XPCost);
        else
            playerIn.giveExperiencePoints(-XPCost);

        if (playerIn.experienceLevel < 0) {
            playerIn.experienceLevel = 0;
            playerIn.experienceProgress = 0.0F;
            playerIn.totalExperience = 0;
        }
    }

    /**
     * Called when the container is closed.
     */
    public void removed(PlayerEntity playerIn) {
        super.removed(playerIn);
        this.worldPosCallable.execute((p_217004_2_, p_217004_3_) -> {
            this.clearContainer(playerIn, playerIn.level, this.tableInventory);
        });
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean stillValid(PlayerEntity playerIn) {
        return stillValid(this.worldPosCallable, playerIn, SpelledRegistry.LEVELING_ALTAR.get());
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 2, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == 1) {
                if (!this.moveItemStackTo(itemstack1, 2, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, 1, true)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }
}
