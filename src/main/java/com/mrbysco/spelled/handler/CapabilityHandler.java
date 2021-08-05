package com.mrbysco.spelled.handler;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.capability.ISpellData;
import com.mrbysco.spelled.api.capability.SpelledCapProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
    public void playerLoggedInEvent(PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if(!player.world.isRemote) {
            SpelledAPI.syncCap((ServerPlayerEntity) player);
        }
    }

    @SubscribeEvent
    public void onDeath(PlayerEvent.Clone event) {
        // If not dead, player is returning from the End
        if (!event.isWasDeath()) return;

        PlayerEntity original = event.getOriginal();
        PlayerEntity clone = event.getPlayer();

        final Capability<ISpellData> capability = SpelledAPI.SPELL_DATA_CAP;
        original.getCapability(capability).ifPresent(dataOriginal ->
            clone.getCapability(capability).ifPresent(dataClone -> {
                INBT nbt = capability.getStorage().writeNBT(capability, dataOriginal, null);
                capability.getStorage().readNBT(capability, dataClone, null, nbt);
            })
        );
    }
}
