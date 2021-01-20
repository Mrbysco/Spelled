package com.mrbysco.spelled.item;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.capability.ISpellData;
import com.mrbysco.spelled.config.ConfigCache;
import com.mrbysco.spelled.registry.KeywordRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;

public class TomeItem extends Item {
    public TomeItem(Properties builder) {
        super(builder);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(!worldIn.isRemote) {
            ItemStack itemstack = playerIn.getHeldItem(handIn);

            if(itemstack.hasTag() && itemstack.getTag() != null && itemstack.getTag().contains(Reference.tomeUnlock)) {
                CompoundNBT tag = itemstack.getTag();
                LazyOptional<ISpellData> cap = SpelledAPI.getSpellDataCap(playerIn);
                ISpellData data = cap.orElseGet(null);
                if(cap.isPresent()) {
                    String word = tag.getString(Reference.tomeUnlock);
                    if(!data.knowsKeyword(word)) {
                        playerIn.setActiveHand(handIn);
                        SpelledAPI.unlockKeyword((ServerPlayerEntity)playerIn, word);
                        SpelledAPI.syncCap((ServerPlayerEntity) playerIn);
                        playerIn.sendStatusMessage(new TranslationTextComponent("spelled.tome.success"), true);
                        return ActionResult.resultConsume(itemstack);
                    } else {
                        playerIn.sendStatusMessage(new TranslationTextComponent("spelled.tome.fail"), true);
                        return ActionResult.resultFail(itemstack);
                    }
                }
            } else {
                CompoundNBT tag = new CompoundNBT();
                tag.putString(Reference.tomeUnlock, KeywordRegistry.instance().getRandomAdjective());
                itemstack.setTag(tag);
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(!ConfigCache.hideKnowledgeTomeInfo && stack.hasTag() && stack.getTag().contains(Reference.tomeUnlock)) {
            CompoundNBT tag = stack.getTag();
            tooltip.add(new TranslationTextComponent("spelled.tome.description", new StringTextComponent(tag.getString(Reference.tomeUnlock)).mergeStyle(TextFormatting.GOLD)).mergeStyle(TextFormatting.YELLOW));
        } else {
            tooltip.add(new TranslationTextComponent("spelled.tome.description.invalid").mergeStyle(TextFormatting.RED));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
