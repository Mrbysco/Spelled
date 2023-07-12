package com.mrbysco.spelled.item;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.capability.ISpellData;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.config.SpelledConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;

public class TomeItem extends Item {
	public TomeItem(Properties builder) {
		super(builder);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
		if (!level.isClientSide) {
			ItemStack itemstack = playerIn.getItemInHand(handIn);

			if (itemstack.hasTag() && itemstack.getTag() != null && itemstack.getTag().contains(Reference.tomeUnlock)) {
				CompoundTag tag = itemstack.getTag();
				LazyOptional<ISpellData> cap = SpelledAPI.getSpellDataCap(playerIn);
				ISpellData data = cap.orElseGet(null);
				if (cap.isPresent()) {
					String word = tag.getString(Reference.tomeUnlock);
					if (!data.knowsKeyword(word)) {
						playerIn.startUsingItem(handIn);
						SpelledAPI.unlockKeyword((ServerPlayer) playerIn, word);
						SpelledAPI.syncCap((ServerPlayer) playerIn);
						playerIn.displayClientMessage(Component.translatable("spelled.tome.success"), true);
						return InteractionResultHolder.consume(itemstack);
					} else {
						playerIn.displayClientMessage(Component.translatable("spelled.tome.fail"), true);
						return InteractionResultHolder.fail(itemstack);
					}
				}
			} else {
				CompoundTag tag = new CompoundTag();
				String adjective = KeywordRegistry.instance().getRandomAdjective();
				if (!adjective.isEmpty()) {
					tag.putString(Reference.tomeUnlock, KeywordRegistry.instance().getRandomAdjective());
					itemstack.setTag(tag);
				}
			}
		}
		return super.use(level, playerIn, handIn);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		if (!SpelledConfig.COMMON.hideKnowledgeTomeInfo.get()) {
			if (stack.hasTag() && stack.getTag().contains(Reference.tomeUnlock)) {
				CompoundTag tag = stack.getTag();
				tooltip.add(Component.translatable("spelled.tome.description", Component.literal(tag.getString(Reference.tomeUnlock)).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.YELLOW));
			} else {
				tooltip.add(Component.translatable("spelled.tome.description.invalid").withStyle(ChatFormatting.RED));
			}
		}
		super.appendHoverText(stack, level, tooltip, flagIn);
	}
}
