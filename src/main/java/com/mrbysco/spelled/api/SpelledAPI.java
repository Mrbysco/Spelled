package com.mrbysco.spelled.api;

import com.mrbysco.spelled.Spelled;
import com.mrbysco.spelled.api.capability.ISpellData;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.packets.SpellDataSyncMessage;
import com.mrbysco.spelled.util.AdvancementHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SpelledAPI {
    @CapabilityInject(ISpellData.class)
    public static final Capability<ISpellData> SPELL_DATA_CAP = null;

    public static LazyOptional<ISpellData> getSpellDataCap(@Nonnull final PlayerEntity player) {
        return player.getCapability(SpelledAPI.SPELL_DATA_CAP);
    }

    public static void forceLevelUp(PlayerEntity player) {
        SpelledAPI.getSpellDataCap(player).ifPresent(cap -> cap.setLevel(cap.getLevel() + 1));
    }

    public static void forceLevelDown(PlayerEntity player) {
        SpelledAPI.getSpellDataCap(player).ifPresent(cap -> {
            if(cap.getLevel() > 1) {
                cap.setLevel(cap.getLevel() - 1);
            }
        });
    }

    public static void forceSetLevel(PlayerEntity player, int level) {
        SpelledAPI.getSpellDataCap(player).ifPresent(cap -> cap.setLevel(level));
    }

    public static int getLevel(PlayerEntity player) {
        LazyOptional<ISpellData> cap = SpelledAPI.getSpellDataCap(player);
        if(cap.isPresent()) {
            ISpellData data = cap.orElse(null);
            return data.getLevel();
        }
        return -1;
    }

    public static void resetUnlocks(PlayerEntity player) {
        SpelledAPI.getSpellDataCap(player).ifPresent(ISpellData::resetUnlocks);
        if(!player.world.isRemote) {
            AdvancementHelper.removeAllAdjectiveAdvancements((ServerPlayerEntity) player);
        }
    }

    public static List<String> getUnlocks(PlayerEntity player) {
        LazyOptional<ISpellData> cap = SpelledAPI.getSpellDataCap(player);
        if(cap.isPresent()) {
            ISpellData data = cap.orElse(null);
            List<String> unlocks = new ArrayList<>(data.getUnlocked().keySet());
            unlocks.removeAll(KeywordRegistry.instance().getTypes());
            return unlocks;
        }
        return new ArrayList<>();
    }

    public static void unlockKeyword(PlayerEntity player, String keyword) {
        SpelledAPI.getSpellDataCap(player).ifPresent(cap -> cap.unlockKeyword(keyword));
        if(!player.world.isRemote) {
            AdvancementHelper.unlockAdjectiveAdvancement((ServerPlayerEntity) player, keyword);
        }
    }

    public static void lockKeyword(PlayerEntity player, String keyword) {
        SpelledAPI.getSpellDataCap(player).ifPresent(cap -> cap.lockKeyword(keyword));
        if(!player.world.isRemote) {
            AdvancementHelper.lockAdjectiveAdvancement((ServerPlayerEntity) player, keyword);
        }
    }

    public static int getCooldown(PlayerEntity player) {
        LazyOptional<ISpellData> cap = SpelledAPI.getSpellDataCap(player);
        if(cap.isPresent()) {
            ISpellData data = cap.orElse(null);
            return data.getCastCooldown();
        }
        return 0;
    }

    public static void setCooldown(PlayerEntity player, int amount) {
        SpelledAPI.getSpellDataCap(player).ifPresent(cap -> cap.setCastCooldown(amount));
    }

    public static void clearCooldown(PlayerEntity player) {
        SpelledAPI.getSpellDataCap(player).ifPresent(cap -> cap.setCastCooldown(0));
    }

    public static void syncCap(ServerPlayerEntity player) {
        SpelledAPI.getSpellDataCap(player).ifPresent(cap -> Spelled.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SpellDataSyncMessage(cap, player.getGameProfile().getId())));
    }
}
