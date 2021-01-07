package com.mrbysco.spelled.capability;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.item.TomeItem;
import com.mrbysco.spelled.registry.KeywordRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class CapabilityHandler {
    @SubscribeEvent
    public void attachCapabilityEntity(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof PlayerEntity) {
            event.addCapability(Reference.SPELL_DATA_CAP, new SpelledCapProvider());
        }
    }

    @SubscribeEvent
    public void attachKeywordToStack(AttachCapabilitiesEvent<ItemStack> event) {
        if(event.getObject().getItem() instanceof TomeItem) {
            ItemStack stack = event.getObject();
            CompoundNBT tag = stack.hasTag() && stack.getTag() != null ? stack.getTag() : new CompoundNBT();
            if(!tag.contains(Reference.tomeUnlock)) {
                tag.putString(Reference.tomeUnlock, KeywordRegistry.instance().getRandomAdjective());
                stack.setTag(tag);
            }
        }
    }

    @SubscribeEvent
    public void playerLoggedInEvent(PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if(!player.world.isRemote) {
            SpelledAPI.syncCap((ServerPlayerEntity) player);
        }
    }
}
