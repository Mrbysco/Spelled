package com.mrbysco.spelled.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.config.SpelledConfig;
import com.mrbysco.spelled.menu.AltarMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Random;

public class AltarScreen extends AbstractContainerScreen<AltarMenu> {
	private static final ResourceLocation ALTAR_GUI_TEXTURE = new ResourceLocation(Reference.MOD_PREFIX + "textures/gui/container/leveling_altar.png");
	private static final ResourceLocation ALTAR_GUI_SLOTLESS_TEXTURE = new ResourceLocation(Reference.MOD_PREFIX + "textures/gui/container/leveling_altar_no_slot.png");

	private static final ResourceLocation ALTAR_BOOK_TEXTURE = new ResourceLocation(Reference.MOD_PREFIX + "textures/entity/altar_book.png");
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

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (bookHovered(mouseX, mouseY)) {
			Minecraft mc = this.minecraft;
			if (mc != null && mc.gameMode != null) {
				mc.gameMode.handleInventoryButtonClick((this.menu).containerId, 0);
			}
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	public boolean bookHovered(double mouseX, double mouseY) {
		int levelCost = (this.menu).getCurrentLevelCost();
		return this.isHovering(74, 20, 28, 22, (double) mouseX, (double) mouseY) && levelCost > 0;
	}

	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		Player player = this.minecraft != null ? this.minecraft.player : null;
		int levelCost = menu.getCurrentLevelCost();
		int itemAmountCost = (this.menu).getItemCostAmount();
		boolean itemFlag = SpelledConfig.COMMON.requireItems.get() && itemAmountCost > 0 && this.menu.getCostStackCount() < itemAmountCost;
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		ResourceLocation texture = SpelledConfig.COMMON.requireItems.get() ? ALTAR_GUI_TEXTURE : ALTAR_GUI_SLOTLESS_TEXTURE;
		guiGraphics.blit(texture, i, j, 0, 0, this.imageWidth, this.imageHeight);

		this.renderBook(guiGraphics, i, j, mouseX, mouseY, partialTicks, levelCost, itemFlag);

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

		guiGraphics.drawCenteredString(this.font, s, (k1 + 8), (j + 44), j2);
	}

	private void renderBook(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTick, int levelCost, boolean itemFlag) {
		Player player = this.minecraft != null ? this.minecraft.player : null;
		float f = Mth.lerp(partialTick, this.oOpen, this.open);
		float f1 = Mth.lerp(partialTick, this.oFlip, this.flip);
		Lighting.setupForEntityInInventory();
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate((float) x + 88.0F, (float) y + 28.0F, 100.0F);
		float f2 = 40.0F;
		guiGraphics.pose().scale(-f2, f2, f2);
		guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(25.0F));
		guiGraphics.pose().translate((1.0F - f) * 0.2F, (1.0F - f) * 0.1F, (1.0F - f) * 0.25F);
		float f3 = -(1.0F - f) * 90.0F - 90.0F;
		guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(f3));
		guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(180.0F));
		float f4 = Mth.clamp(Mth.frac(f1 + 0.25F) * 1.6F - 0.3F, 0.0F, 1.0F);
		float f5 = Mth.clamp(Mth.frac(f1 + 0.75F) * 1.6F - 0.3F, 0.0F, 1.0F);

		boolean flag = true;
		if (player != null) {
			flag = levelCost > 0 && ((itemFlag || player.experienceLevel < levelCost) && !player.getAbilities().instabuild);
		}

		this.bookModel.setupAnim(0.0F, f4, f5, f);
		float red = 1.0F;
		float green = 1.0F;
		float blue = 1.0F;
		VertexConsumer vertexconsumer = guiGraphics.bufferSource().getBuffer(this.bookModel.renderType(ALTAR_BOOK_TEXTURE));

		boolean bookHovered = bookHovered(mouseX, mouseY);
		if (bookHovered) {
			if (flag) {
				red = 0.6F;
				green = 0.4F;
			} else {
				red = 0.4F;
				green = 0.6F;
			}
			blue = 0.4F;
		} else {
			if (flag) {
				red = 0.6F;
				green = 0.6F;
				blue = 0.6F;
			}
		}
		this.bookModel.renderToBuffer(guiGraphics.pose(), vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);

		guiGraphics.flush();
		guiGraphics.pose().popPose();
		Lighting.setupFor3DItems();
	}

	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		Player player = this.minecraft != null ? this.minecraft.player : null;
		if (this.minecraft != null) {
			partialTicks = this.minecraft.getFrameTime();
		}
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
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
					iformattabletextcomponent1 = Component.translatable(Reference.MOD_PREFIX + "container.altar.item.requirement", itemAmountCost, itemCost.getDescription());
				} else {
					iformattabletextcomponent1 = Component.translatable(Reference.MOD_PREFIX + "container.altar.item", itemAmountCost, itemCost.getDescription());
				}

				list.add(iformattabletextcomponent1.withStyle(noItems ? ChatFormatting.RED : ChatFormatting.GREEN));
			}

			guiGraphics.renderComponentTooltip(font, list, mouseX, mouseY);
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