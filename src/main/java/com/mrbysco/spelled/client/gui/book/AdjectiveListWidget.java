package com.mrbysco.spelled.client.gui.book;

import com.mrbysco.spelled.client.gui.book.AdjectiveListWidget.ListEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class AdjectiveListWidget extends ObjectSelectionList<ListEntry> {
	private final int listWidth;

	private final SpellBookScreen parent;

	public AdjectiveListWidget(SpellBookScreen parent, int listWidth, int top, int bottom) {
		super(parent.getMinecraft(), listWidth, bottom - top, top, parent.getFont().lineHeight * 2 + 8);
		this.parent = parent;
		this.listWidth = listWidth;
		this.refreshList();
	}

	@Override
	protected int scrollBarX() {
		return this.listWidth - 6;
	}

	@Override
	public int getRowWidth() {
		return this.listWidth;
	}

	public void refreshList() {
		this.clearEntries();
		parent.buildAdjectiveList(this::addEntry, mod -> new ListEntry(mod, this.parent));
	}

	public class ListEntry extends ObjectSelectionList.Entry<ListEntry> {
		private final AdjectiveEntry adjective;
		private final SpellBookScreen parent;

		ListEntry(AdjectiveEntry adjective, SpellBookScreen parent) {
			this.adjective = adjective;
			this.parent = parent;
		}

		@Override
		public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
			int top = getContentY();
			Component name = Component.literal(getAdjectiveName());
			Font font = this.parent.getFont();
			int color = isType() ? 16351261 : 0xFFFFFF;
			graphics.text(font, Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(name, listWidth))),
					(this.parent.width / 2) - (font.width(name) / 2) + 3, top + 6, color, false);

			if (hovered) {
				graphics.setTooltipForNextFrame(font, getDescription(), mouseX, mouseY);
			}
		}

		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
			parent.setFocused(this);
			AdjectiveListWidget.this.setSelected(this);
			return false;
		}

		public String getAdjectiveName() {
			return adjective.getAdjectiveName();
		}

		public Component getDescription() {
			if (isType()) {
				return Component.literal("Type: ").withStyle(ChatFormatting.GOLD).append(adjective.getAdjectiveDescription());
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
