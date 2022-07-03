package com.mrbysco.spelled.client.gui.book;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.packets.PacketHandler;
import com.mrbysco.spelled.packets.SignSpellPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
	private Button cancelButton;

	private String title = "";
	private final TextFieldHelper titleEdit =
			new TextFieldHelper(() -> this.title,
					(message) -> this.title = message, this::getClipboard, this::setClipboard,
					(message) -> message.length() < 32);

	private final int buttonMargin = 1;
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

		if (stack.hasTag()) {
			String currentSpell = stack.getTag().getString("spell");
			String[] words = currentSpell.split(" ");
			List<String> wordList = Arrays.asList(words);
			String type = wordList.get(wordList.size() - 1);
			wordList = wordList.subList(0, wordList.size() - 1);
			selectedAdjectives.addAll(wordList);
			typeWord = type;
		}
	}

	public Font getFont() {
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
		listWidth = Math.max(Math.min(listWidth, width / 3), 200);
		listWidth += listWidth % numButtons != 0 ? (numButtons - listWidth % numButtons) : 0;
		int structureWidth = this.width - listWidth - (PADDING * 3);
		int closeButtonWidth = Math.min(structureWidth, 200);
		int y = this.height - 20 - PADDING;
		this.addRenderableWidget(this.cancelButton = new Button(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20,
				Component.translatable("gui.cancel"), b -> {
			if (this.isSigning) {
				this.isSigning = false;
			} else {
				this.onClose();
			}

			this.updateButtonVisibility();
		}));

		y -= 18 + PADDING;
		this.addRenderableWidget(this.insertButton = new Button(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20,
				Component.translatable("spelled.screen.selection.select"), b -> {
			if (focused != null) {
				if (focused.isType()) {
					typeWord = focused.getAdjectiveName();
				} else {
					selectedAdjectives.add(focused.getAdjectiveName());
				}
			}
		}));

		y -= 18 + PADDING;
		this.addRenderableWidget(this.removeButton = new Button(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20,
				Component.translatable("spelled.screen.selection.remove"), b -> {
			if (selectedAdjectives.size() == 1) {
				selectedAdjectives.clear();
			} else {
				if (!selectedAdjectives.isEmpty()) {
					selectedAdjectives.remove(selectedAdjectives.size() - 1);
				}
			}
		}));

		y -= 14 + PADDING;
		search = new EditBox(getFont(), centerWidth - listWidth / 2 + PADDING + 1, y, listWidth - 2, 14,
				Component.translatable("spelled.screen.search"));
		int fullButtonHeight = (PADDING * 2) + 20;

		y -= 30;

		this.adjectiveWidget = new AdjectiveListWidget(this, width, fullButtonHeight, y - getFont().lineHeight - PADDING);
		this.adjectiveWidget.setLeftPos(0);

		this.addWidget(search);
		this.addWidget(adjectiveWidget);
		setInitialFocus(search);

		final int width = listWidth / numButtons;
		int x = centerWidth + PADDING - width;
		this.addRenderableWidget(SortType.A_TO_Z.button = new Button(x, PADDING, width - buttonMargin, 20, SortType.A_TO_Z.getButtonText(), b -> resortAdjectives(SortType.A_TO_Z)));
		x += width + buttonMargin;
		this.addRenderableWidget(SortType.Z_TO_A.button = new Button(x, PADDING, width - buttonMargin, 20, SortType.Z_TO_A.getButtonText(), b -> resortAdjectives(SortType.Z_TO_A)));

		this.addRenderableWidget(this.signButton = new Button(this.width - (60 + PADDING), PADDING, 60, 20, Component.translatable("book.signButton"), b -> {
			this.isSigning = true;
			this.updateButtonVisibility();
		}, (button, poseStack, mouseX, mouseY) -> {
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
				if (!errorMessage.isEmpty()) {
					renderTooltip(poseStack, Component.literal(errorMessage).withStyle(ChatFormatting.RED), mouseX, mouseY);
				}
			}
		}));
		this.addRenderableWidget(this.finalizeButton = new Button(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20,
				Component.translatable("spelled.book.finalizeButton"), (button) -> {
			if (this.isSigning) {
				this.saveChanges(true);
				this.minecraft.setScreen((Screen) null);
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
		if (!isSigning) {
			this.signButton.active = !selectedAdjectives.isEmpty() && !typeWord.isEmpty();
			search.tick();
			adjectiveWidget.setSelected(focused);

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
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		if (isSigning) {
			int i = (this.width - 192) / 2;
			int j = this.height / 2 - 100;

			boolean flag = this.frameTick / 6 % 2 == 0;
			FormattedCharSequence ireorderingprocessor = FormattedCharSequence.composite(FormattedCharSequence.forward(this.title, Style.EMPTY), flag ? GRAY_CURSOR : WHITE_CURSOR);
			int k = this.font.width(EDIT_TITLE_LABEL);
			this.font.draw(poseStack, EDIT_TITLE_LABEL, (float) (i + 36 + (114 - k) / 2), 34.0F + j, 16777215);
			int l = this.font.width(ireorderingprocessor);
			this.font.draw(poseStack, ireorderingprocessor, (float) (i + 36 + (114 - l) / 2), 50.0F + j, 16777215);
			int i1 = this.font.width(this.ownerText);
			this.font.draw(poseStack, this.ownerText, (float) (i + 36 + (114 - i1) / 2), 60.0F + j, 16777215);
			this.font.drawWordWrap(FINALIZE_WARNING_LABEL, i + 36, 82 + j, 114, 16777215);
		} else {
			this.adjectiveWidget.render(poseStack, mouseX, mouseY, partialTicks);

			Component text = Component.translatable("spelled.screen.search");
			drawCenteredString(poseStack, font, text, this.width / 2 + PADDING,
					search.y - font.lineHeight - 2, 16777215);

			this.search.render(poseStack, mouseX, mouseY, partialTicks);

			this.font.draw(poseStack, this.getTitle(), 5, 5, 16777215);

			poseStack.pushPose();
			this.itemRenderer.blitOffset = 100.0F;

			RenderSystem.enableDepthTest();
			int itemX = width / 2 - 2;
			int itemY = height - 130;

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, STATS_ICON_LOCATION);
			blit(poseStack, itemX - 1, itemY - 1, 0, 0, 18, 18, 128, 128);

			this.itemRenderer.renderAndDecorateItem(stack, itemX, itemY);
			this.itemRenderer.renderGuiItemDecorations(this.font, stack, itemX, itemY, null);
			RenderSystem.disableDepthTest();
			poseStack.popPose();

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
				renderTooltip(poseStack, finalComponent, mouseX, mouseY);
			}
		}
		super.render(poseStack, mouseX, mouseY, partialTicks);
	}

	protected boolean isHovering(int x, int y, int x2, int y2, double mouseX, double mouseY) {
		return mouseX >= x && mouseX < x2 && mouseY >= y && mouseY <= y2;
	}

	public <T extends ObjectSelectionList.Entry<T>> void buildAdjectiveList(Consumer<T> ListViewConsumer, Function<AdjectiveEntry, T> newEntry) {
		adjectives.forEach(mod -> ListViewConsumer.accept(newEntry.apply(mod)));
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
			this.minecraft.setScreen((Screen) null);
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
		switch (keyCode) {
			case 257:
			case 335:
				if (!this.title.isEmpty()) {
					this.saveChanges(true);
					this.minecraft.setScreen((Screen) null);
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
			this.stack.addTagElement("spell", StringTag.valueOf(builder.toString()));

			if (finalize) {
				this.stack.addTagElement("author", StringTag.valueOf(this.owner.getGameProfile().getName()));
				this.stack.addTagElement("title", StringTag.valueOf(this.title.trim()));
			}

			int i = this.hand == InteractionHand.MAIN_HAND ? this.owner.getInventory().selected : 40;
			PacketHandler.CHANNEL.sendToServer(new SignSpellPacket(this.stack, finalize, i));
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
		this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
		super.removed();
	}
}
