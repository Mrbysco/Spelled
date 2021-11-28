package com.mrbysco.spelled.client.gui.book;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.packets.PacketHandler;
import com.mrbysco.spelled.packets.SignSpellPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.TextInputUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.loading.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpellBookScreen extends Screen {
	private static final ITextComponent EDIT_TITLE_LABEL = new TranslationTextComponent("spelled.book.editTitle");
	private static final ITextComponent FINALIZE_WARNING_LABEL = new TranslationTextComponent("spelled.book.finalizeWarning");
	private static final IReorderingProcessor GRAY_CURSOR = IReorderingProcessor.forward("_", Style.EMPTY.withColor(TextFormatting.GRAY));
	private static final IReorderingProcessor WHITE_CURSOR = IReorderingProcessor.forward("_", Style.EMPTY.withColor(TextFormatting.WHITE));

	private enum SortType {
		NORMAL,
		A_TO_Z,
		Z_TO_A;

		Button button;

		ITextComponent getButtonText() {
			return new TranslationTextComponent("spelled.screen.search." + name().toLowerCase(Locale.ROOT));
		}
	}
	private static final int PADDING = 6;

	private final List<String> selectedAdjectives = new ArrayList<>();
	private String typeWord = "";

	private AdjectiveListWidget adjectiveWidget;
	private AdjectiveListWidget.ListEntry focused = null;

	private final List<AdjectiveEntry> unsortedAdjectives;
	private List<AdjectiveEntry> adjectives;
	private Button insertButton;
	private Button removeButton;
	private Button signButton;
	private Button finalizeButton;
	private Button cancelButton;

	private String title = "";
	private final TextInputUtil titleEdit =
			new TextInputUtil(() -> this.title,
					(message) -> this.title = message, this::getClipboard, this::setClipboard,
					(message) -> message.length() < 32);

	private final int buttonMargin = 1;
	private final int numButtons = SortType.values().length;
	private String lastFilterText = "";

	private TextFieldWidget search;
	private boolean sorted = false;
	private SortType sortType = SortType.NORMAL;

	private final PlayerEntity owner;
	private final Hand hand;
	private final ItemStack stack;
	private boolean isSigning;
	private boolean isModified;
	private int frameTick;

	private final ITextComponent ownerText;

	public SpellBookScreen(List<AdjectiveEntry> entries, PlayerEntity player, Hand hand) {
		super(new TranslationTextComponent(Reference.MOD_ID + ".spell_book.screen"));
		this.hand = hand;
		this.owner = player;
		this.stack = player.getItemInHand(hand);

		List<AdjectiveEntry> sortedEntries = new ArrayList<>(entries);
		Collections.sort(sortedEntries);

		this.unsortedAdjectives = Collections.unmodifiableList(sortedEntries);
		this.adjectives = Collections.unmodifiableList(entries);

		this.ownerText = (new TranslationTextComponent("book.byAuthor", player.getName())).withStyle(TextFormatting.GRAY);

		if(stack.hasTag()) {
			String currentSpell = stack.getTag().getString("spell");
			String[] words = currentSpell.split(" ");
			List<String> wordList = Arrays.asList(words);
			String type = wordList.get(wordList.size() - 1);
			wordList = wordList.subList(0, wordList.size() - 1);
			selectedAdjectives.addAll(wordList);
			typeWord = type;
		}
	}

	public FontRenderer getFont() {
		return font;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	protected void init() {
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		int centerWidth = this.width / 2;
		int listWidth = 0;
		for (AdjectiveEntry adjectiveEntry : adjectives) {
			listWidth = Math.max(listWidth, getFont().width(adjectiveEntry.getAdjectiveName()) + 10);
		}
		listWidth = Math.max(Math.min(listWidth, width/3), 200);
		listWidth += listWidth % numButtons != 0 ? (numButtons - listWidth % numButtons) : 0;
		int structureWidth = this.width - listWidth - (PADDING * 3);
		int closeButtonWidth = Math.min(structureWidth, 200);
		int y = this.height - 20 - PADDING;
		this.addButton(this.cancelButton = new Button(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20,
				new TranslationTextComponent("gui.cancel"), b -> {
			if (this.isSigning) {
				this.isSigning = false;
			} else {
				this.onClose();
			}

			this.updateButtonVisibility();
		}));

		y -= 18 + PADDING;
		this.addButton(this.insertButton = new Button(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20,
				new TranslationTextComponent("spelled.screen.selection.select"), b -> {
			if(focused != null) {
				if(focused.isType()) {
					typeWord = focused.getAdjectiveName();
				} else {
					selectedAdjectives.add(focused.getAdjectiveName());
				}
			}
		}));

		y -= 18 + PADDING;
		this.addButton(this.removeButton = new Button(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20,
				new TranslationTextComponent("spelled.screen.selection.remove"), b -> {
			if(selectedAdjectives.size() == 1) {
				selectedAdjectives.clear();
			} else {
				if(!selectedAdjectives.isEmpty()) {
					selectedAdjectives.remove(selectedAdjectives.size() - 1);
				}
			}
		}));

		y -= 14 + PADDING;
		search = new TextFieldWidget(getFont(), centerWidth - listWidth / 2 + PADDING + 1, y, listWidth - 2, 14,
				new TranslationTextComponent("spelled.screen.search"));
		int fullButtonHeight = (PADDING * 2) + 20;

		y -= 30;

		this.adjectiveWidget = new AdjectiveListWidget(this, width, fullButtonHeight, y - getFont().lineHeight - PADDING);
		this.adjectiveWidget.setLeftPos(0);

		children.add(search);
		children.add(adjectiveWidget);
		setInitialFocus(search);

		final int width = listWidth / numButtons;
		int x = centerWidth + PADDING - width;
		addButton(SortType.A_TO_Z.button = new Button(x, PADDING, width - buttonMargin, 20, SortType.A_TO_Z.getButtonText(), b -> resortAdjectives(SortType.A_TO_Z)));
		x += width + buttonMargin;
		addButton(SortType.Z_TO_A.button = new Button(x, PADDING, width - buttonMargin, 20, SortType.Z_TO_A.getButtonText(), b -> resortAdjectives(SortType.Z_TO_A)));

		addButton(this.signButton = new Button(this.width - (60 + PADDING), PADDING, 60, 20, new TranslationTextComponent("book.signButton"), b -> {
			this.isSigning = true;
			this.updateButtonVisibility();
		}, (button, poseStack, mouseX, mouseY) -> {
			boolean flag = selectedAdjectives.isEmpty();
			boolean flag2 = typeWord.isEmpty();

			if(flag || flag2) {
				StringBuilder builder = new StringBuilder();
				if(flag) {
					builder.append(I18n.get("spelled.screen.missing_adjectives") + " ");
				}
				if(flag2) {
					builder.append(I18n.get("spelled.screen.missing_type"));
				}
				String errorMessage = builder.toString();
				if(!errorMessage.isEmpty()) {
					renderTooltip(poseStack, new StringTextComponent(errorMessage).withStyle(TextFormatting.RED), mouseX, mouseY);
				}
			}
		}));
		this.addButton(this.finalizeButton = new Button(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20,
				new TranslationTextComponent("spelled.book.finalizeButton"), (button) -> {
			if (this.isSigning) {
				this.saveChanges(true);
				this.minecraft.setScreen((Screen)null);
			}
		}));

		this.updateButtonVisibility();
		resortAdjectives(SortType.A_TO_Z);
		updateCache();
	}

	private void updateButtonVisibility() {
		this.signButton.visible = !this.isSigning;
		this.insertButton.visible = !this.isSigning;
		this.removeButton.visible = !this.isSigning;
		this.finalizeButton.visible = this.isSigning;
		this.finalizeButton.active = !this.title.trim().isEmpty();
		SortType.A_TO_Z.button.visible = !this.isSigning;
		SortType.Z_TO_A.button.visible = !this.isSigning;
	}

	@Override
	public void tick() {
		super.tick();
		++this.frameTick;
		if(!isSigning) {
			this.signButton.active = !selectedAdjectives.isEmpty() && !typeWord.isEmpty();
			search.tick();
			adjectiveWidget.setSelected(focused);

			if (!search.getValue().equals(lastFilterText)) {
				reloadAdjectives();
				sorted = false;
			}

			if (!sorted) {
				reloadAdjectives();
				if(sortType == SortType.A_TO_Z) {
					Collections.sort(adjectives);
				} else if(sortType == SortType.Z_TO_A) {
					adjectives.sort(Collections.reverseOrder());
				}
				adjectiveWidget.refreshList();
				if (focused != null) {
					focused = adjectiveWidget.children().stream().filter(e -> e == focused).findFirst().orElse(null);
					updateCache();
				}
				sorted = true;
			}
		}
	}

	@Override
	public void init(Minecraft minecraft, int width, int height) {
		super.init(minecraft, width, height);
	}

	@Override
	public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(isSigning) {
			int i = (this.width - 192) / 2;
			int j = this.height / 2 - 100;

			boolean flag = this.frameTick / 6 % 2 == 0;
			IReorderingProcessor ireorderingprocessor = IReorderingProcessor.composite(IReorderingProcessor.forward(this.title, Style.EMPTY), flag ? GRAY_CURSOR : WHITE_CURSOR);
			int k = this.font.width(EDIT_TITLE_LABEL);
			this.font.draw(poseStack, EDIT_TITLE_LABEL, (float)(i + 36 + (114 - k) / 2), 34.0F + j, 16777215);
			int l = this.font.width(ireorderingprocessor);
			this.font.draw(poseStack, ireorderingprocessor, (float)(i + 36 + (114 - l) / 2), 50.0F + j, 16777215);
			int i1 = this.font.width(this.ownerText);
			this.font.draw(poseStack, this.ownerText, (float)(i + 36 + (114 - i1) / 2), 60.0F + j, 16777215);
			this.font.drawWordWrap(FINALIZE_WARNING_LABEL, i + 36, 82 + j, 114, 16777215);
		} else {
			this.adjectiveWidget.render(poseStack, mouseX, mouseY, partialTicks);

			ITextComponent text = new TranslationTextComponent("spelled.screen.search");
			drawCenteredString(poseStack, font, text, this.width / 2 + PADDING,
					search.y - font.lineHeight - 2, 16777215);

			this.search.render(poseStack, mouseX , mouseY, partialTicks);

			this.font.draw(poseStack, this.getTitle(), 5, 5, 16777215);

			poseStack.pushPose();
			RenderSystem.pushMatrix();
			this.itemRenderer.blitOffset = 100.0F;

			RenderSystem.enableDepthTest();
			int itemX = width / 2 - 2;
			int itemY = height - 130;

			this.minecraft.getTextureManager().bind(STATS_ICON_LOCATION);
			blit(poseStack, itemX - 1, itemY - 1, 0, 0, 18, 18, 128, 128);

			this.itemRenderer.renderAndDecorateItem(this.minecraft.player, stack, itemX, itemY);
			this.itemRenderer.renderGuiItemDecorations(this.font, stack, itemX, itemY, null);
			RenderSystem.popMatrix();
			poseStack.popPose();

			if(isHovering(itemX - 16, itemY, itemX + 16, itemY + 24, mouseX, mouseY)) {
				boolean flag = selectedAdjectives.isEmpty();
				boolean flag2 = typeWord.isEmpty();

				StringBuilder builder = new StringBuilder();
				if(selectedAdjectives.isEmpty()) {
					builder.append(I18n.get("spelled.screen.missing_adjectives") + " ");
				} else {
					selectedAdjectives.forEach((adjective) -> builder.append(adjective).append(" "));
				}
				TextComponent component = new StringTextComponent(builder.toString());
				StringBuilder builder2 = new StringBuilder();
				if(typeWord.isEmpty()) {
					builder2.append(I18n.get("spelled.screen.missing_type"));
				} else {
					builder2.append(typeWord);
				}
				TextComponent component2 = new StringTextComponent(builder2.toString());

				if(flag) {
					component.withStyle(TextFormatting.RED);
				}
				if(flag2) {
					component2.withStyle(TextFormatting.RED);
				}

				final ITextComponent finalComponent = component.append(component2);
				renderTooltip(poseStack, finalComponent, mouseX, mouseY);
			}
		}
		super.render(poseStack, mouseX, mouseY, partialTicks);
	}

	protected boolean isHovering(int x, int y, int x2, int y2, double mouseX, double mouseY) {
		return mouseX >= x && mouseX < x2 && mouseY >= y && mouseY <= y2;
	}

	public <T extends ExtendedList.AbstractListEntry<T>> void buildAdjectiveList(Consumer<T> ListViewConsumer, Function<AdjectiveEntry, T> newEntry) {
		adjectives.forEach(mod->ListViewConsumer.accept(newEntry.apply(mod)));
	}

	private void reloadAdjectives() {
		this.adjectives = this.unsortedAdjectives.stream().
				filter(struc -> StringUtils.toLowerCase(struc.toString()).contains(StringUtils.toLowerCase(search.getValue()))).collect(Collectors.toList());
		lastFilterText = search.getValue();
	}

	private void resortAdjectives(SortType newSort) {
		this.sortType = newSort;

		for (SortType sort : SortType.values()) {
			if (sort.button != null)
				sort.button.active = sortType != sort;
		}
		sorted = false;
	}

	public void setFocused(AdjectiveListWidget.ListEntry entry) {
		this.focused = entry == this.focused ? null : entry;
		updateCache();
	}

	private void updateCache() {
		this.insertButton.active = focused != null;
	}

	@Override
	public void resize(Minecraft mc, int newWidth, int newHeight) {
		super.resize(mc, newHeight, newHeight);
		String s = this.search.getValue();
		SortType sort = this.sortType;
		AdjectiveListWidget.ListEntry focused = this.focused;
		this.init(mc, newWidth, newHeight);
		this.search.setValue(s);
		this.focused = focused;
		if (!this.search.getValue().isEmpty())
			reloadAdjectives();
		if (sort != SortType.NORMAL)
			resortAdjectives(sort);
		updateCache();
	}

	@Override
	public void onClose() {
		super.onClose();
		this.minecraft.setScreen(null);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.minecraft.setScreen((Screen)null);
			return true;
		} else if (this.isSigning) {
			return this.titleKeyPressed(keyCode, scanCode, modifiers);
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public boolean charTyped(char keyCode, int modifiers) {
		if (super.charTyped(keyCode, modifiers)) {
			return true;
		} else if (this.isSigning) {
			boolean flag = this.titleEdit.charTyped(keyCode);
			if (flag) {
				this.updateButtonVisibility();
				this.isModified = true;
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean titleKeyPressed(int keyCode, int scanCode, int modifiers) {
		switch(keyCode) {
			case 257:
			case 335:
				if (!this.title.isEmpty()) {
					this.saveChanges(true);
					this.minecraft.setScreen((Screen)null);
				}

				return true;
			case 259:
				this.titleEdit.removeCharsFromCursor(-1);
				this.updateButtonVisibility();
				this.isModified = true;
				return true;
			default:
				return false;
		}
	}

	private void saveChanges(boolean finalize) {
		if (this.isModified) {
			StringBuilder builder = new StringBuilder();
			if(!selectedAdjectives.isEmpty()) {
				selectedAdjectives.forEach((adjective) -> builder.append(adjective).append(" "));
			}
			builder.append(typeWord);
			this.stack.addTagElement("spell", StringNBT.valueOf(builder.toString()));

			if (finalize) {
				this.stack.addTagElement("author", StringNBT.valueOf(this.owner.getGameProfile().getName()));
				this.stack.addTagElement("title", StringNBT.valueOf(this.title.trim()));
			}

			int i = this.hand == Hand.MAIN_HAND ? this.owner.inventory.selected : 40;
			PacketHandler.CHANNEL.sendToServer(new SignSpellPacket(this.stack, finalize, i));
		}
	}

	private void setClipboard(String clipboard) {
		if (this.minecraft != null) {
			TextInputUtil.setClipboardContents(this.minecraft, clipboard);
		}
	}

	private String getClipboard() {
		return this.minecraft != null ? TextInputUtil.getClipboardContents(this.minecraft) : "";
	}

	@Override
	public void removed() {
		this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
		super.removed();
	}
}
