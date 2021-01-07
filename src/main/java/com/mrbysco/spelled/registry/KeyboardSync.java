package com.mrbysco.spelled.registry;

import com.mrbysco.spelled.Spelled;
import com.mrbysco.spelled.packets.DisableUseMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KeyboardSync {
    private static final Map<UUID, Boolean> useKeyPressed = new HashMap<>();

    public static boolean isUseKeyDown(@Nonnull UUID player) {
        return useKeyPressed.containsKey(player) && useKeyPressed.get(player);
    }

    public static void putUseKeyDown(@Nonnull UUID player, boolean keyDown) {
        useKeyPressed.put(player, keyDown);
    }

    public static void disableUseKey(@Nonnull PlayerEntity player) {
        if(player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            Spelled.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new DisableUseMessage());
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        if(!event.getPlayer().world.isRemote)
            useKeyPressed.remove(event.getPlayer().getUniqueID());
    }

    @SubscribeEvent
    public void onDimChanged(PlayerChangedDimensionEvent event) {
        if(!event.getPlayer().world.isRemote)
            useKeyPressed.remove(event.getPlayer().getUniqueID());
    }
}
