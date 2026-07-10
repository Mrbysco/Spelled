package com.mrbysco.spelled.client.gui;

import com.google.common.collect.Lists;
import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.config.SpelledConfig;
import com.mrbysco.spelled.menu.AltarMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Random;

public class AltarScreen extends AbstractContainerScreen<AltarMenu> {
	private static final Identifier ALTAR_GUI_TEXTURE = Reference.modLoc("textures/gui/container/leveling_altar.png");
	private static final Identifier ALTAR_GUI_SLOTLESS_TEXTURE = Reference.modLoc("textures/gui/container/leveling_altar_no_slot.png");

	private static final Identifier ALTAR_BOOK_TEXTURE = Reference.modLoc("textures/entity/altar_book.png");
	private BookModel bookModel;
	/**
	 * A Random instance for use with the Altar gui
	 */
	private final Random random = new Random();
	public int ticks;
	public float flip;
	public float oFlip;
	public float flipT;
	public float flipA;
	public float open;
	public float oOpen;
	private ItemStack last = ItemStack.EMPTY;

	public AltarScreen(AltarMenu container, Inventory playerInventory, Component textComponent) {
		super(container, playerInventory, textComponent);
	}

	@Override
	protected void init() {
		super.init();
		this.bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		this.tickBook();
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (bookHovered(event.x(), event.y())) {
			Minecraft mc = this.minecraft;
			if (mc != null && mc.gameMode != null) {
				mc.gameMode.handleInventoryButtonClick((this.menu).containerId, 0);
			}
			return true;
		}

		return super.mouseClicked(event, doubleClick);
	}

	public boolean bookHovered(double mouseX, double mouseY) {
		int levelCost = (this.menu).getCurrentLevelCost();
		return this.isHovering(74, 20, 28, 22, (double) mouseX, (double) mouseY) && levelCost > 0;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);

		Player player = this.minecraft != null ? this.minecraft.player : null;
		int levelCost = menu.getCurrentLevelCost();
		int itemAmountCost = (this.menu).getItemCostAmount();
		boolean itemFlag = SpelledConfig.COMMON.requireItems.get() && itemAmountCost > 0 && this.menu.getCostStackCount() < itemAmountCost;
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		Identifier texture = SpelledConfig.COMMON.requireItems.get() ? ALTAR_GUI_TEXTURE : ALTAR_GUI_SLOTLESS_TEXTURE;
		graphics.blit(texture, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

		this.extractBook(graphics, i, j, mouseX, mouseY, levelCost, itemFlag);

		int j1 = i + 60;
		int k1 = j1 + 20;
		String s = "";

		if (levelCost > 0) {
			s += levelCost;
		} else {
			s += "∞";
		}
		int j2;
		if (player != null && ((itemFlag || player.experienceLevel < levelCost) && !player.getAbilities().instabuild)) {
			j2 = 4226832;
		} else {
			j2 = 8453920;
		}
		if (levelCost == -1) {
			j2 = 16755200;
		}

		graphics.centeredText(this.font, s, (k1 + 8), (j + 44), j2);
	}

	private void extractBook(GuiGraphicsExtractor graphics, int left, int top, int mouseX, int mouseY, int levelCost, boolean itemFlag) {
		float a = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
		float open = Mth.lerp(a, this.oOpen, this.open);
		float flip = Mth.lerp(a, this.oFlip, this.flip);
		int x0 = left + 14;
		int y0 = top + 14;
		int x1 = x0 + 38;
		int y1 = y0 + 31;

		//TODO: Figure out if I can still color the book model
//		Player player = this.minecraft != null ? this.minecraft.player : null;
//		boolean flag = true;
//		if (player != null) {
//			flag = levelCost > 0 && ((itemFlag || player.experienceLevel < levelCost) && !player.getAbilities().instabuild);
//		}
//
//		float red = 1.0F;
//		float green = 1.0F;
//		float blue = 1.0F;
//
//		boolean bookHovered = bookHovered(mouseX, mouseY);
//		if (bookHovered) {
//			if (flag) {
//				red = 0.6F;
//				green = 0.4F;
//			} else {
//				red = 0.4F;
//				green = 0.6F;
//			}
//			blue = 0.4F;
//		} else {
//			if (flag) {
//				red = 0.6F;
//				green = 0.6F;
//				blue = 0.6F;
//			}
//		}

		graphics.book(this.bookModel, ALTAR_BOOK_TEXTURE, 40.0F, open, flip, x0, y0, x1, y1);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		Player player = this.minecraft != null ? this.minecraft.player : null;
		if (this.minecraft != null) {
			partialTicks = this.minecraft.getFrameTimeNs();
		}

		super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
		this.extractTooltip(graphics, mouseX, mouseY);
		boolean flag = player != null && player.getAbilities().instabuild;

		if (bookHovered(mouseX, mouseY) && !flag) {
			final int levelCost = (this.menu).getCurrentLevelCost();

			List<Component> list = Lists.newArrayList();
			boolean noXP = levelCost > 0 && (player == null ? 0 : player.experienceLevel) < levelCost;

			MutableComponent mutableComponent;
			if (noXP) {
				mutableComponent = Component.translatable(Reference.MOD_PREFIX + "container.altar.level.requirement", levelCost);
			} else {
				if (levelCost == 1) {
					mutableComponent = Component.translatable(Reference.MOD_PREFIX + "container.altar.level.one");
				} else {
					mutableComponent = Component.translatable(Reference.MOD_PREFIX + "container.altar.level.many", levelCost);
				}
			}

			if (SpelledConfig.COMMON.requireItems.get()) {
				int stackCount = this.menu.getCostStackCount();
				final Item itemCost = (this.menu).getItemCost();
				final int itemAmountCost = (this.menu).getItemCostAmount();
				boolean noItems = itemAmountCost > 0 && itemCost != Items.AIR && stackCount <= itemAmountCost;

				list.add(mutableComponent.withStyle(noXP ? ChatFormatting.RED : ChatFormatting.GREEN));
				MutableComponent iformattabletextcomponent1;
				if (noItems) {
					iformattabletextcomponent1 = Component.translatable(Reference.MOD_PREFIX + "container.altar.item.requirement",
							itemAmountCost, itemCost.getDefaultInstance().getDisplayName());
				} else {
					iformattabletextcomponent1 = Component.translatable(Reference.MOD_PREFIX + "container.altar.item",
							itemAmountCost, itemCost.getDefaultInstance().getDisplayName());
				}

				list.add(iformattabletextcomponent1.withStyle(noItems ? ChatFormatting.RED : ChatFormatting.GREEN));
			}

			graphics.setComponentTooltipForNextFrame(font, list, mouseX, mouseY);
		}

	}

	public void tickBook() {
		if (SpelledConfig.COMMON.requireItems.get()) {
			ItemStack itemstack = this.menu.getSlot(0).getItem();

			if (!ItemStack.matches(itemstack, this.last)) {
				this.last = itemstack;

				do {
					this.flipT += (float) (this.random.nextInt(4) - this.random.nextInt(4));
				} while (this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
			}
		}

		++this.ticks;
		this.oFlip = this.flip;
		this.oOpen = this.open;
		boolean flag = false;

		int currentLevel = (this.menu).getCurrentLevel();
		if ((this.menu).levelCosts.length > currentLevel && (this.menu).getCurrentLevelCost() > 0) {
			flag = true;
		}

		if (flag) {
			this.open += 0.2F;
		} else {
			this.open -= 0.2F;
		}

		this.open = Mth.clamp(this.open, 0.0F, 1.0F);
		float f1 = (this.flipT - this.flip) * 0.4F;
		float f = 0.2F;
		f1 = Mth.clamp(f1, -0.2F, 0.2F);
		this.flipA += (f1 - this.flipA) * 0.9F;
		this.flip += this.flipA;
	}
}