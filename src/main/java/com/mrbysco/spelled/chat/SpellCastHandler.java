package com.mrbysco.spelled.chat;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.entity.AbstractSpellEntity;
import com.mrbysco.spelled.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;

public class SpellCastHandler {

    public static HashMap<BlockPos, AbstractSpellEntity> collectableBlocks = new HashMap<>();

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent.Post event) {
		if (event.getEntity() instanceof ServerPlayer player && player.level().getGameTime() % 20 == 0) {
			int cooldown = SpelledAPI.getCooldown(player);
			if (cooldown > 0) {
				SpelledAPI.setCooldown(player, cooldown - 1);
				SpelledAPI.syncCap(player);
			}
		}
	}

	@SubscribeEvent
	public void onChatEvent(ServerChatEvent event) {
		final String regExp = "^[a-zA-Z\\s]*$";
		String actualMessage = event.getRawText();
		if (!actualMessage.isEmpty() && actualMessage.matches(regExp)) {
			SpellUtil.castSpell(event);
		}
	}

    @SubscribeEvent
    public void onBlockDrops(BlockDropsEvent event) {
        if (event.isCanceled()) return;
        AbstractSpellEntity spell = collectableBlocks.get(event.getPos());
        if (spell == null) return;
        if (spell.level() != event.getLevel()) return;

        event.getDrops().forEach(item -> {
            item.setPos(spell.getOwner().getX(), spell.getOwner().getY(), spell.getOwner().getZ());
            item.setNoPickUpDelay();
        });
    }
}
