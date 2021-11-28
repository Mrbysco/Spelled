package com.mrbysco.spelled.chat;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.util.SpellUtil;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SpellCastHandler {
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.START)
            return;

        World world = event.player.level;
        if(!world.isClientSide && world.getGameTime() % 20 == 0) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.player;
            int cooldown = SpelledAPI.getCooldown(player);
            if(cooldown > 0) {
                SpelledAPI.setCooldown(player, cooldown - 1);
                SpelledAPI.syncCap(player);
            }
        }
    }

    @SubscribeEvent
    public void onChatEvent(ServerChatEvent event) {
        final String regExp = "^[a-zA-Z\\s]*$";
        String actualMessage = event.getMessage();
        if (!actualMessage.isEmpty() && actualMessage.matches(regExp)) {
            SpellUtil.castSpell(event);
        }
    }
}
