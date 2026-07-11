package com.mrbysco.spelled.client.gui.book;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.packets.message.SignSpellPayload;
import com.mrbysco.spelled.registry.SpelledComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.StringUtils;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpellBookScreen extends Screen {
	private static final Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot");

	private static final Component EDIT_TITLE_LABEL = Component.translatable("spelled.book.editTitle");
	private static final Component FINALIZE_WARNING_LABEL = Component.translatable("spelled.book.finalizeWarning");
	private static final FormattedCharSequence GRAY_CURSOR = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.GRAY));
	private static final FormattedCharSequence WHITE_CURSOR = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.WHITE));

	private enum SortType {
		NORMAL,
		A_TO_Z,
		Z_TO_A;

		Button button;

		Component getButtonText() {
			return Component.translatable("spelled.screen.search." + name().toLowerCase(Locale.ROOT));
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

	private String title = "";
	private final TextFieldHelper titleEdit =
			new TextFieldHelper(() -> this.title,
					(message) -> this.title = message, this::getClipboard, this::setClipboard,
					(message) -> message.length() < 32);

	private final int numButtons = SortType.values().length;
	private String lastFilterText = "";

	private EditBox search;
	private boolean sorted = false;
	private SortType sortType = SortType.NORMAL;

	private final Player owner;
	private final InteractionHand hand;
	private final ItemStack stack;
	private boolean isSigning;
	private boolean isModified;
	private int frameTick;

	private final Component ownerText;

	public SpellBookScreen(List<AdjectiveEntry> entries, Player player, InteractionHand hand) {
		super(Component.translatable(Reference.MOD_ID + ".spell_book.screen"));
		this.hand = hand;
		this.owner = player;
		this.stack = player.getItemInHand(hand);

		List<AdjectiveEntry> sortedEntries = new ArrayList<>(entries);
		Collections.sort(sortedEntries);

		this.unsortedAdjectives = Collections.unmodifiableList(sortedEntries);
		this.adjectives = Collections.unmodifiableList(entries);

		this.ownerText = (Component.translatable("book.byAuthor", player.getName())).withStyle(ChatFormatting.GRAY);

		if (stack.has(SpelledComponents.SPELL)) {
			String currentSpell = stack.getOrDefault(SpelledComponents.SPELL, "");
			String[] words = currentSpell.split(" ");
			List<String> wordList = Arrays.asList(words);
			String type = wordList.getLast();
			wordList = wordList.subList(0, wordList.size() - 1);
			selectedAdjectives.addAll(wordList);
			typeWord = type;
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	protected void init() {
		int centerWidth = this.width / 2;
		int listWidth = 0;
		for (AdjectiveEntry adjectiveEntry : adjectives) {
			listWidth = Math.max(listWidth, getFont().width(adjectiveEntry.getAdjectiveName()) + 10);
		}
		listWidth = Math.clamp(listWidth, 200, width / 3);
		listWidth += listWidth % numButtons != 0 ? (numButtons - listWidth % numButtons) : 0;
		int structureWidth = this.width - listWidth - (PADDING * 3);
		int closeButtonWidth = Math.min(structureWidth, 200);
		int y = this.height - 20 - PADDING;
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
			if (this.isSigning) {
				this.isSigning = false;
			} else {
				this.onClose();
			}

			this.updateButtonVisibility();
		}).bounds(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20).build());

		y -= 18 + PADDING;
		this.addRenderableWidget(this.insertButton = Button.builder(Component.translatable("spelled.screen.selection.select"), (button) -> {
			if (focused != null) {
				if (focused.isType()) {
					typeWord = focused.getAdjectiveName();
				} else {
					selectedAdjectives.add(focused.getAdjectiveName());
				}
			}
		}).bounds(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20).build());

		y -= 18 + PADDING;
		this.addRenderableWidget(this.removeButton = Button.builder(Component.translatable("spelled.screen.selection.remove"), (button) -> {
			if (selectedAdjectives.size() == 1) {
				selectedAdjectives.clear();
			} else {
				if (!selectedAdjectives.isEmpty()) {
					selectedAdjectives.removeLast();
				}
			}
		}).bounds(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20).build());

		y -= 14 + PADDING;
		search = new EditBox(getFont(), centerWidth - listWidth / 2 + PADDING + 1, y, listWidth - 2, 14,
				Component.translatable("spelled.screen.search"));
		int fullButtonHeight = (PADDING * 2) + 20;

		y -= 30;

		this.adjectiveWidget = new AdjectiveListWidget(this, width, fullButtonHeight, y - getFont().lineHeight - PADDING);
		this.adjectiveWidget.setX(0);

		this.addWidget(search);
		this.addWidget(adjectiveWidget);
		setInitialFocus(search);

		final int width = listWidth / numButtons;
		int x = centerWidth + PADDING - width;
		int buttonMargin = 1;
		SortType.A_TO_Z.button = this.addRenderableWidget(Button.builder(SortType.A_TO_Z.getButtonText(), (button) -> {
			resortAdjectives(SortType.A_TO_Z);
		}).bounds(x, PADDING, width - buttonMargin, 20).build());
		x += width + buttonMargin;
		SortType.Z_TO_A.button = this.addRenderableWidget(Button.builder(SortType.Z_TO_A.getButtonText(), (button) -> {
			resortAdjectives(SortType.Z_TO_A);
		}).bounds(x, PADDING, width - buttonMargin, 20).build());

		this.addRenderableWidget(this.signButton = Button.builder(Component.translatable("spelled.book.finalizeButton"), (button) -> {
			this.isSigning = true;

			this.updateButtonVisibility();
		}).bounds(this.width - (60 + PADDING), PADDING, 60, 20).build());

		this.addRenderableWidget(this.finalizeButton = Button.builder(Component.translatable("book.signButton"), (button) -> {
			if (this.isSigning) {
				this.saveChanges(true);
				this.minecraft.setScreen(null);
			}
		}).bounds(centerWidth - (closeButtonWidth / 2) + PADDING, this.height - 50 - PADDING, closeButtonWidth, 20).build());

		this.updateButtonVisibility();
		resortAdjectives(SortType.A_TO_Z);
		updateCache();
	}

	private Component getFinalizeTooltip() {
		boolean flag = selectedAdjectives.isEmpty();
		boolean flag2 = typeWord.isEmpty();

		if (flag || flag2) {
			StringBuilder builder = new StringBuilder();
			if (flag) {
				builder.append(I18n.get("spelled.screen.missing_adjectives")).append(" ");
			}
			if (flag2) {
				builder.append(I18n.get("spelled.screen.missing_type"));
			}
			String errorMessage = builder.toString();
			return Component.literal(errorMessage);
		}
		return Component.empty();
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

	public void setFocused(AdjectiveListWidget.ListEntry previousEntry, AdjectiveListWidget.ListEntry entry) {
		if (this.focused == previousEntry) {
			this.focused = entry;
		} else {
			if (this.focused == null || entry != null) {
				this.focused = entry;
			}
		}
		updateCache();
	}


	@Override
	public void tick() {
		this.signButton.setTooltip(Tooltip.create(getFinalizeTooltip()));

		++this.frameTick;
		if (!isSigning) {
			this.signButton.active = !selectedAdjectives.isEmpty() && !typeWord.isEmpty();

			if (adjectiveWidget.getSelected() != focused) {
				adjectiveWidget.setSelected(focused);
			}

			if (!search.getValue().equals(lastFilterText)) {
				reloadAdjectives();
				sorted = false;
			}

			if (!sorted) {
				reloadAdjectives();
				if (sortType == SortType.A_TO_Z) {
					Collections.sort(adjectives);
				} else if (sortType == SortType.Z_TO_A) {
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
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
		if (isSigning) {
			int i = (this.width - 192) / 2;
			int j = this.height / 2 - 100;

			boolean flag = this.frameTick / 6 % 2 == 0;
			FormattedCharSequence formattedCharSequence = FormattedCharSequence.composite(FormattedCharSequence.forward(this.title, Style.EMPTY), flag ? GRAY_CURSOR : WHITE_CURSOR);
			int k = this.font.width(EDIT_TITLE_LABEL);
			graphics.text(font, EDIT_TITLE_LABEL, i + 36 + (114 - k) / 2, (int) (34.0F + j), ARGB.opaque(16777215), false);
			int l = this.font.width(formattedCharSequence);
			graphics.text(font, formattedCharSequence, i + 36 + (114 - l) / 2, (int) (50.0F + j), ARGB.opaque(16777215), false);
			int i1 = this.font.width(this.ownerText);
			graphics.text(font, this.ownerText, i + 36 + (114 - i1) / 2, (int) (60.0F + j), ARGB.opaque(16777215), false);
			graphics.textWithWordWrap(font, FINALIZE_WARNING_LABEL, i + 36, 82 + j, 114, ARGB.opaque(16777215));
		} else {
			this.adjectiveWidget.extractRenderState(graphics, mouseX, mouseY, partialTick);

			Component text = Component.translatable("spelled.screen.search");
			graphics.centeredText(font, text, this.width / 2 + PADDING,
					search.getY() - font.lineHeight - 2, ARGB.opaque(16777215));

			this.search.extractRenderState(graphics, mouseX, mouseY, partialTick);

			graphics.text(font, this.getTitle(), 5, 5, ARGB.opaque(16777215), false);

			int itemX = width / 2 - 2;
			int itemY = height - 130;

			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, itemX - 1, itemY - 1, 0, 18, 18);

			graphics.item(stack, itemX, itemY);
			graphics.itemDecorations(this.font, stack, itemX, itemY, null);

			if (isHovering(itemX - 16, itemY, itemX + 16, itemY + 24, mouseX, mouseY)) {
				boolean flag = selectedAdjectives.isEmpty();
				boolean flag2 = typeWord.isEmpty();

				StringBuilder builder = new StringBuilder();
				if (selectedAdjectives.isEmpty()) {
					builder.append(I18n.get("spelled.screen.missing_adjectives")).append(" ");
				} else {
					selectedAdjectives.forEach((adjective) -> builder.append(adjective).append(" "));
				}
				MutableComponent component = Component.literal(builder.toString());
				StringBuilder builder2 = new StringBuilder();
				if (typeWord.isEmpty()) {
					builder2.append(I18n.get("spelled.screen.missing_type"));
				} else {
					builder2.append(typeWord);
				}
				MutableComponent component2 = Component.literal(builder2.toString());

				if (flag) {
					component.withStyle(ChatFormatting.RED);
				}
				if (flag2) {
					component2.withStyle(ChatFormatting.RED);
				}

				final Component finalComponent = component.append(component2);
				graphics.setTooltipForNextFrame(font, finalComponent, mouseX, mouseY);
			}
		}
	}

	protected boolean isHovering(int x, int y, int x2, int y2, double mouseX, double mouseY) {
		return mouseX >= x && mouseX < x2 && mouseY >= y && mouseY <= y2;
	}

	public <T extends ObjectSelectionList.Entry<T>> void buildAdjectiveList(Consumer<T> ListViewConsumer, Function<AdjectiveEntry, T> newEntry) {
		adjectives.forEach(mod -> ListViewConsumer.accept(newEntry.apply(mod)));
	}

	private void reloadAdjectives() {
		lastFilterText = search.getValue();
		this.adjectives = this.unsortedAdjectives.stream().
				filter(word -> {
					boolean flag = StringUtils.toLowerCase(word.getAdjectiveName()).contains(StringUtils.toLowerCase(lastFilterText));
					boolean flag2 = StringUtils.toLowerCase(word.getAdjectiveDescription().getString()).contains(StringUtils.toLowerCase(lastFilterText));
					return flag || flag2;
				}).collect(Collectors.toList());
	}

	private void resortAdjectives(SortType newSort) {
		this.sortType = newSort;

		for (SortType sort : SortType.values()) {
			if (sort.button != null)
				sort.button.active = sortType != sort;
		}
		sorted = false;
	}

	private void updateCache() {
		this.insertButton.active = focused != null;
	}

	@Override
	public void resize(int newWidth, int newHeight) {
		super.resize(newWidth, newHeight);
		String s = this.search.getValue();
		SortType sort = this.sortType;
		AdjectiveListWidget.ListEntry focused = this.focused;
		this.init(newWidth, newHeight);
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
	public boolean keyPressed(KeyEvent event) {
		if (this.adjectiveWidget.keyPressed(event)) {
			return true;
		} else if (event.key() == 256) {
			this.minecraft.setScreen(null);
			return true;
		} else if (this.isSigning) {
			return this.titleKeyPressed(event);
		} else {
			return super.keyPressed(event);
		}
	}

	@Override
	public boolean charTyped(CharacterEvent event) {
		if (super.charTyped(event)) {
			return true;
		} else if (this.isSigning) {
			boolean flag = this.titleEdit.charTyped(event);
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

	private boolean titleKeyPressed(KeyEvent event) {
		switch (event.key()) {
			case 257:
			case 335:
				if (!this.title.isEmpty()) {
					this.saveChanges(true);
					this.minecraft.setScreen(null);
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
			if (!selectedAdjectives.isEmpty()) {
				selectedAdjectives.forEach((adjective) -> builder.append(adjective).append(" "));
			}
			builder.append(typeWord);
			String spell = builder.toString();
			this.stack.set(SpelledComponents.SPELL, spell);

			int i = this.hand == InteractionHand.MAIN_HAND ? this.owner.getInventory().getSelectedSlot() : 40;
			ClientPacketDistributor.sendToServer(new SignSpellPayload(this.stack, finalize, this.title.trim(), spell, i));
		}
	}

	private void setClipboard(String clipboard) {
		if (this.minecraft != null) {
			TextFieldHelper.setClipboardContents(this.minecraft, clipboard);
		}
	}

	private String getClipboard() {
		return this.minecraft != null ? TextFieldHelper.getClipboardContents(this.minecraft) : "";
	}

	@Override
	public void removed() {
		super.removed();
	}
}
