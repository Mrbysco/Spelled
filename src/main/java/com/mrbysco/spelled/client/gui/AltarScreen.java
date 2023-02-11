package com.mrbysco.spelled.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.config.SpelledConfig;
import com.mrbysco.spelled.menu.AltarMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
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
import org.joml.Matrix4f;

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

	protected void renderBg(PoseStack poseStack, float partialTicks, int x, int y) {
		Player player = this.minecraft != null ? this.minecraft.player : null;
		Lighting.setupForFlatItems();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, SpelledConfig.COMMON.requireItems.get() ? ALTAR_GUI_TEXTURE : ALTAR_GUI_SLOTLESS_TEXTURE);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(poseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		int k = (int) this.minecraft.getWindow().getGuiScale();
		RenderSystem.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
		Matrix4f matrix4f = (new Matrix4f()).translation(-0.34F, 0.23F, 0.0F).perspective(((float) Math.PI / 2F), 1.3333334F, 9.0F, 80.0F);
		RenderSystem.backupProjectionMatrix();
		RenderSystem.setProjectionMatrix(matrix4f);
		poseStack.pushPose();
		poseStack.setIdentity();
		poseStack.translate(0.0D, (double) 3.3F, 1984.0D);
		float f = 5.0F;
		poseStack.scale(5.0F, 5.0F, 5.0F);
		poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		poseStack.mulPose(Axis.XP.rotationDegrees(20.0F));
		float f1 = Mth.lerp(partialTicks, this.oOpen, this.open);
		poseStack.translate((double) ((1.0F - f1) * 0.2F), (double) ((1.0F - f1) * 0.1F), (double) ((1.0F - f1) * 0.25F));
		float f2 = -(1.0F - f1) * 90.0F - 90.0F;
		poseStack.mulPose(Axis.YP.rotationDegrees(f2));
		poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
		float f3 = Mth.lerp(partialTicks, this.oFlip, this.flip) + 0.25F;
		float f4 = Mth.lerp(partialTicks, this.oFlip, this.flip) + 0.75F;
		f3 = (f3 - (float) Mth.fastFloor((double) f3)) * 1.6F - 0.3F;
		f4 = (f4 - (float) Mth.fastFloor((double) f4)) * 1.6F - 0.3F;
		if (f3 < 0.0F) {
			f3 = 0.0F;
		}

		if (f4 < 0.0F) {
			f4 = 0.0F;
		}

		if (f3 > 1.0F) {
			f3 = 1.0F;
		}

		if (f4 > 1.0F) {
			f4 = 1.0F;
		}

		int levelCost = menu.getCurrentLevelCost();

		int itemAmountCost = (this.menu).getItemCostAmount();
		boolean itemFlag = SpelledConfig.COMMON.requireItems.get() && itemAmountCost > 0 && this.menu.getCostStackCount() < itemAmountCost;
		boolean flag = true;
		if (player != null) {
			flag = levelCost > 0 && ((itemFlag || player.experienceLevel < levelCost) && !player.getAbilities().instabuild);
		}
		boolean bookHovered = bookHovered(x, y);

		bookModel.setupAnim(0.0F, f3, f4, f1);
		MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		VertexConsumer vertexconsumer = bufferSource.getBuffer(this.bookModel.renderType(ALTAR_BOOK_TEXTURE));
		float red = 1.0F;
		float green = 1.0F;
		float blue = 1.0F;

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
				red = 0.4F;
				green = 0.4F;
				blue = 0.4F;
			}
		}
		bookModel.renderToBuffer(poseStack, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
		bufferSource.endBatch();
		poseStack.popPose();
		RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
		RenderSystem.restoreProjectionMatrix();
		Lighting.setupFor3DItems();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);


		int j1 = i + 60;
		int k1 = j1 + 20;
		this.setBlitOffset(0);
		RenderSystem.setShaderTexture(0, ALTAR_GUI_TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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

		drawCenteredString(poseStack, this.font, s, (k1 + 8), (j + 44), j2);
	}

	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		Player player = this.minecraft != null ? this.minecraft.player : null;
		if (this.minecraft != null) {
			partialTicks = this.minecraft.getFrameTime();
		}
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
		boolean flag = player != null && player.getAbilities().instabuild;

		if (bookHovered(mouseX, mouseY) && !flag) {
			final int levelCost = (this.menu).getCurrentLevelCost();

			List<Component> list = Lists.newArrayList();
			boolean noXP = levelCost > 0 && (player == null ? 0 : player.experienceLevel) < levelCost;

			MutableComponent iformattabletextcomponent;
			if (noXP) {
				iformattabletextcomponent = Component.translatable(Reference.MOD_PREFIX + "container.altar.level.requirement", levelCost);
			} else {
				if (levelCost == 1) {
					iformattabletextcomponent = Component.translatable(Reference.MOD_PREFIX + "container.altar.level.one");
				} else {
					iformattabletextcomponent = Component.translatable(Reference.MOD_PREFIX + "container.altar.level.many", levelCost);
				}
			}

			if (SpelledConfig.COMMON.requireItems.get()) {
				int stackCount = this.menu.getCostStackCount();
				final Item itemCost = (this.menu).getItemCost();
				final int itemAmountCost = (this.menu).getItemCostAmount();
				boolean noItems = itemAmountCost > 0 && itemCost != Items.AIR && stackCount <= itemAmountCost;

				list.add(iformattabletextcomponent.withStyle(noXP ? ChatFormatting.RED : ChatFormatting.GREEN));
				MutableComponent iformattabletextcomponent1;
				if (noItems) {
					iformattabletextcomponent1 = Component.translatable(Reference.MOD_PREFIX + "container.altar.item.requirement", itemAmountCost, itemCost.getDescription());
				} else {
					iformattabletextcomponent1 = Component.translatable(Reference.MOD_PREFIX + "container.altar.item", itemAmountCost, itemCost.getDescription());
				}

				list.add(iformattabletextcomponent1.withStyle(noItems ? ChatFormatting.RED : ChatFormatting.GREEN));
			}

			this.renderComponentTooltip(matrixStack, list, mouseX, mouseY);
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