package com.mrbysco.spelled.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.config.ConfigCache;
import com.mrbysco.spelled.container.AltarContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;
import java.util.Random;

public class AltarScreen extends ContainerScreen<AltarContainer> {
    private static final ResourceLocation ALTAR_GUI_TEXTURE = new ResourceLocation(Reference.MOD_PREFIX + "textures/gui/container/leveling_altar.png");
    private static final ResourceLocation ALTAR_GUI_SLOTLESS_TEXTURE = new ResourceLocation(Reference.MOD_PREFIX + "textures/gui/container/leveling_altar_no_slot.png");

    private static final ResourceLocation ALTAR_BOOK_TEXTURE = new ResourceLocation(Reference.MOD_PREFIX + "textures/entity/altar_book.png");
    private static final BookModel MODEL_BOOK = new BookModel();
    /** A Random instance for use with the Altar gui */
    private final Random random = new Random();
    public int ticks;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    private ItemStack last = ItemStack.EMPTY;

    public AltarScreen(AltarContainer container, PlayerInventory playerInventory, ITextComponent textComponent) {
        super(container, playerInventory, textComponent);
    }

    public void tick() {
        super.tick();
        this.tickBook();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (bookHovered(mouseX, mouseY)) {
            this.minecraft.playerController.sendEnchantPacket((this.container).windowId, 0);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean bookHovered(double mouseX, double mouseY) {
        int currentLevel = (this.container).currentLevel[0];
        int levelCost = (this.container).levelCosts[currentLevel];
        return this.isPointInRegion(74, 20, 28, 22, (double) mouseX, (double) mouseY) && levelCost > 0;
    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderHelper.setupGuiFlatDiffuseLighting();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(ConfigCache.requireItems ? ALTAR_GUI_TEXTURE : ALTAR_GUI_SLOTLESS_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        int k = (int)this.minecraft.getMainWindow().getGuiScaleFactor();
        RenderSystem.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
        RenderSystem.translatef(0, 0.23F, 0.0F);
        RenderSystem.multMatrix(Matrix4f.perspective(90.0D, 1.3333334F, 9.0F, 80.0F));
        RenderSystem.matrixMode(5888);
        matrixStack.push();
        MatrixStack.Entry matrixstack$entry = matrixStack.getLast();
        matrixstack$entry.getMatrix().setIdentity();
        matrixstack$entry.getNormal().setIdentity();
        matrixStack.translate(0.0D, (double)3.3F, 1984.0D);
        float f = 5.0F;
        matrixStack.scale(5.0F, 5.0F, 5.0F);
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(20.0F));
        float f1 = MathHelper.lerp(partialTicks, this.oOpen, this.open);
        matrixStack.translate((double)((1.0F - f1) * 0.2F), (double)((1.0F - f1) * 0.1F), (double)((1.0F - f1) * 0.25F));
        float f2 = -(1.0F - f1) * 90.0F - 90.0F;
        matrixStack.rotate(Vector3f.YP.rotationDegrees(f2));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(180.0F));
        float f3 = MathHelper.lerp(partialTicks, this.oFlip, this.flip) + 0.25F;
        float f4 = MathHelper.lerp(partialTicks, this.oFlip, this.flip) + 0.75F;
        f3 = (f3 - (float)MathHelper.fastFloor((double)f3)) * 1.6F - 0.3F;
        f4 = (f4 - (float)MathHelper.fastFloor((double)f4)) * 1.6F - 0.3F;
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

        int currentLevel = (this.container).currentLevel[0];
        int l1 = (this.container).levelCosts[currentLevel];

        int itemAmountCost = (this.container).itemAmountCosts[currentLevel];
        boolean itemFlag = ConfigCache.requireItems ? this.container.getCostStackCount() < itemAmountCost : false;
        boolean flag = ((itemFlag || this.minecraft.player.experienceLevel < l1) && !this.minecraft.player.abilities.isCreativeMode);
        boolean bookHovered = bookHovered(x, y);

        RenderSystem.enableRescaleNormal();
        MODEL_BOOK.setBookState(0.0F, f3, f4, f1);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        IVertexBuilder ivertexbuilder = irendertypebuffer$impl.getBuffer(MODEL_BOOK.getRenderType(ALTAR_BOOK_TEXTURE));
        float red = 1.0F;
        float green = 1.0F;
        float blue = 1.0F;

        if(bookHovered) {
            if(flag) {
                red = 0.6F;
                green = 0.4F;
                blue = 0.4F;
            } else {
                red = 0.4F;
                green = 0.6F;
                blue = 0.4F;
            }
        } else {
            if(flag) {
                red = 0.4F;
                green = 0.4F;
                blue = 0.4F;
            }
        }
        MODEL_BOOK.render(matrixStack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);

        irendertypebuffer$impl.finish();
        matrixStack.pop();
        RenderSystem.matrixMode(5889);
        RenderSystem.viewport(0, 0, this.minecraft.getMainWindow().getFramebufferWidth(), this.minecraft.getMainWindow().getFramebufferHeight());
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        RenderHelper.setupGui3DDiffuseLighting();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);


        int j1 = i + 60;
        int k1 = j1 + 20;
        this.setBlitOffset(0);
        this.minecraft.getTextureManager().bindTexture(ALTAR_GUI_TEXTURE);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        String s = "" + l1;
        int j2;
        if (((itemFlag || this.minecraft.player.experienceLevel < l1) && !this.minecraft.player.abilities.isCreativeMode)) {
            j2 = 4226832;
        } else {
            j2 = 8453920;
        }

        drawCenteredString(matrixStack, this.font, s, (k1 + 8), (j + 44), j2 );
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        partialTicks = this.minecraft.getRenderPartialTicks();
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
        boolean flag = this.minecraft.player.abilities.isCreativeMode;

        if (bookHovered(mouseX, mouseY) && !flag) {
            final int currentLevel = (this.container).currentLevel[0];
            final int levelCost = (this.container).levelCosts[currentLevel];

            List<ITextComponent> list = Lists.newArrayList();
            boolean noXP = this.minecraft.player.experienceLevel < levelCost;

            IFormattableTextComponent iformattabletextcomponent;
            if(noXP) {
                iformattabletextcomponent = new TranslationTextComponent(Reference.MOD_PREFIX + "container.altar.level.requirement", levelCost);
            } else {
                if (levelCost == 1) {
                    iformattabletextcomponent = new TranslationTextComponent(Reference.MOD_PREFIX + "container.altar.level.one");
                } else {
                    iformattabletextcomponent = new TranslationTextComponent(Reference.MOD_PREFIX + "container.altar.level.many", levelCost);
                }
            }

            if(ConfigCache.requireItems) {
                int stackCount = this.container.getCostStackCount();
                final Item itemCost = (this.container).itemCosts[currentLevel];
                final int itemAmountCost = (this.container).itemAmountCosts[currentLevel];
                boolean noItems = stackCount <= itemAmountCost;

                list.add(iformattabletextcomponent.mergeStyle(noXP ? TextFormatting.RED :  TextFormatting.GREEN));
                IFormattableTextComponent iformattabletextcomponent1;
                if(noItems) {
                    iformattabletextcomponent1 = new TranslationTextComponent(Reference.MOD_PREFIX + "container.altar.item.requirement", itemAmountCost, itemCost.getName());
                } else {
                    iformattabletextcomponent1 = new TranslationTextComponent(Reference.MOD_PREFIX + "container.altar.item", itemAmountCost, itemCost.getName());
                }

                list.add(iformattabletextcomponent1.mergeStyle(noItems ? TextFormatting.RED : TextFormatting.GREEN));
            }

            this.func_243308_b(matrixStack, list, mouseX, mouseY);
        }

    }

    public void tickBook() {
        if(ConfigCache.requireItems) {
            ItemStack itemstack = this.container.getSlot(0).getStack();

            if (!ItemStack.areItemStacksEqual(itemstack, this.last)) {
                this.last = itemstack;

                do {
                    this.flipT += (float)(this.random.nextInt(4) - this.random.nextInt(4));
                } while(this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
            }
        }

        ++this.ticks;
        this.oFlip = this.flip;
        this.oOpen = this.open;
        boolean flag = false;

        int currentLevel = (this.container).currentLevel[0];
        if ((this.container).levelCosts[currentLevel] != 0) {
            flag = true;
        }

        if (flag) {
            this.open += 0.2F;
        } else {
            this.open -= 0.2F;
        }

        this.open = MathHelper.clamp(this.open, 0.0F, 1.0F);
        float f1 = (this.flipT - this.flip) * 0.4F;
        float f = 0.2F;
        f1 = MathHelper.clamp(f1, -0.2F, 0.2F);
        this.flipA += (f1 - this.flipA) * 0.9F;
        this.flip += this.flipA;
    }
}