package com.mrbysco.spelled.client.gui.book;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.spelled.client.gui.book.AdjectiveListWidget.ListEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;

public class AdjectiveListWidget extends ObjectSelectionList<ListEntry> {
	private final int listWidth;

	private final SpellBookScreen parent;

	public AdjectiveListWidget(SpellBookScreen parent, int listWidth, int top, int bottom) {
		super(parent.getMinecraft(), listWidth, parent.height, top, bottom, parent.getFont().lineHeight * 2 + 8);
		this.parent = parent;
		this.listWidth = listWidth;
		this.refreshList();
	}

	@Override
	protected int getScrollbarPosition() {
		return this.listWidth;
	}

	@Override
	public int getRowWidth() {
		return this.listWidth;
	}

	public void refreshList() {
		this.clearEntries();
		parent.buildAdjectiveList(this::addEntry, mod -> new ListEntry(mod, this.parent));
	}

	@Override
	protected void renderBackground(PoseStack poseSTack) {
		this.parent.renderBackground(poseSTack);
	}

	public class ListEntry extends ObjectSelectionList.Entry<ListEntry> {
		private final AdjectiveEntry adjective;
		private final SpellBookScreen parent;

		ListEntry(AdjectiveEntry adjective, SpellBookScreen parent) {
			this.adjective = adjective;
			this.parent = parent;
		}

		@Override
		public void render(PoseStack poseStack, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
			Component name = new TextComponent(getAdjectiveName());
			Font font = this.parent.getFont();

			font.draw(poseStack, Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(name, listWidth))),
					(this.parent.width / 2) - (font.width(name) / 2) + 3, top + 6, 0xFFFFFF);

			if (isMouseOver(mouseX, mouseY)) {
				parent.renderTooltip(poseStack, getDescription(), mouseX, mouseY);
			}
		}

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			return mouseX >= (width / 2) - 40 && mouseX <= (width / 2) + 40 && super.isMouseOver(mouseX, mouseY);
		}

		@Override
		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
			parent.setFocused(this);
			AdjectiveListWidget.this.setSelected(this);
			return false;
		}

		public String getAdjectiveName() {
			return adjective.getAdjectiveName();
		}

		public Component getDescription() {
			if (isType()) {
				return new TextComponent("Type: ").withStyle(ChatFormatting.GOLD).append(adjective.getAdjectiveDescription());
			}
			return adjective.getAdjectiveDescription();
		}

		public boolean isType() {
			return adjective.isType();
		}

		public int getSlots() {
			return adjective.getSlots();
		}

		@Override
		public Component getNarration() {
			return getDescription();
		}
	}
}
