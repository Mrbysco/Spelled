package com.mrbysco.spelled.item;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class CreativeTomeItem extends Item {
	public CreativeTomeItem(Properties builder) {
		super(builder);
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if(!worldIn.isClientSide) {
			ItemStack itemstack = playerIn.getItemInHand(handIn);
			playerIn.startUsingItem(handIn);
			List<String> adjectives = KeywordRegistry.instance().getAdjectives();
			for(String adjective : adjectives) {
				SpelledAPI.unlockKeyword((ServerPlayerEntity)playerIn, adjective);
			}
			SpelledAPI.syncCap((ServerPlayerEntity) playerIn);
			playerIn.displayClientMessage(new TranslationTextComponent("spelled.tome.success"), true);
			return ActionResult.consume(itemstack);
		}
		return super.use(worldIn, playerIn, handIn);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new TranslationTextComponent("spelled.creative_tome.description").withStyle(TextFormatting.DARK_PURPLE));
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
}
