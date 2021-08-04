package com.mrbysco.spelled.chat;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.capability.ISpellData;
import com.mrbysco.spelled.api.capability.SpellDataCapability;
import com.mrbysco.spelled.api.keywords.IKeyword;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.config.SpelledConfig;
import com.mrbysco.spelled.entity.SpellEntity;
import com.mrbysco.spelled.registry.keyword.TypeKeyword;
import com.mrbysco.spelled.registry.keyword.TypeKeyword.Type;
import com.mrbysco.spelled.util.LevelHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

public class SpellCastHandler {
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.START)
            return;

        Level world = event.player.level;
        if(!world.isClientSide && world.getGameTime() % 20 == 0) {
            ServerPlayer player = (ServerPlayer) event.player;
            int cooldown = SpelledAPI.getCooldown(player);
            if(cooldown > 0) {
                SpelledAPI.setCooldown(player, cooldown - 1);
                SpelledAPI.syncCap(player);
            }
        }
    }


    @SubscribeEvent
    public void onChatEvent(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        final String regExp = "^[a-zA-Z\\s]*$";
        String actualMessage = event.getMessage();
        if(!actualMessage.isEmpty() && actualMessage.matches(regExp)) {
            SpelledAPI.getSpellDataCap(player).ifPresent(data -> {
                if(data.getLevel() > 0) {
                    KeywordRegistry registry = KeywordRegistry.instance();
                    String message = event.getMessage().toLowerCase(Locale.ROOT);
                        String[] words = message.split("\\s+");

                        if(words.length >= 2 && canCastSpell(player, words)) {
                            if(isOnCooldown(player)) {
                                event.setCanceled(true);
                                return;
                            }

                            //Do our stuff
                            IKeyword lastKeyword = registry.getKeywordFromName(words[words.length - 1]);
                            Level world = player.level;

                            if(lastKeyword instanceof TypeKeyword) {
                                TypeKeyword typeKeyword = (TypeKeyword) lastKeyword;
                                SpellEntity spell = constructEntity(player, typeKeyword.getType());

                                StringBuilder castText = new StringBuilder();
                                MutableComponent descriptionComponent = new TextComponent("");
                                int cooldown = -1;
                                for(int i = 0; i < (words.length - 1); i++) {
                                    IKeyword keyword = registry.getKeywordFromName(words[i]);
                                    if(keyword != null) {
                                        cooldown += keyword.getSlots();
                                        castText.append(keyword.getKeyword()).append(" ");
                                        descriptionComponent.append(keyword.getDescription()).append(new TextComponent(" "));
                                        int previous = i - 1;
                                        if(previous >= 0 && previous < (words.length - 1))
                                            keyword.cast(world, player, spell, registry.getKeywordFromName(words[previous]));
                                        else
                                            keyword.cast(world, player, spell, null);
                                    }
                                }
                                castText.append(lastKeyword.getKeyword());
                                TextComponent castComponent = new TextComponent(castText.toString());
                                descriptionComponent.append(typeKeyword.getDescription());
                                descriptionComponent.withStyle(ChatFormatting.GOLD);
                                castComponent.setStyle(event.getComponent().getStyle().withHoverEvent(
                                        new HoverEvent(Action.SHOW_TEXT, descriptionComponent))).withStyle(ChatFormatting.GOLD);

                                MutableComponent finalMessage = new TranslatableComponent("spelled.spell.cast", player.getDisplayName(), castComponent);
                                if(spell != null) {
                                    if(!player.abilities.instabuild) {
                                        SpelledAPI.setCooldown(player, cooldown);
                                        SpelledAPI.syncCap(player);
                                    }
                                    if(typeKeyword.getType() != Type.SELF) {
                                        shootSpell(player, spell);
                                        world.addFreshEntity(spell);
                                    } else {
                                        spell.handleEntityHit(player);
                                        spell.remove(false);
                                    }
                                }

                                if(SpelledConfig.COMMON.proximity.get() > 0) {
                                    event.setCanceled(true);
                                    List<? extends Player> playerEntities = world.players();
                                    for(Player nearbyPlayer : playerEntities) {
                                        if(nearbyPlayer.getUUID().equals(player.getUUID()) ||
                                                (nearbyPlayer.level.dimension() == world.dimension() && player.distanceToSqr(nearbyPlayer) <= SpelledConfig.COMMON.proximity.get())) {
                                            player.sendMessage(finalMessage, player.getUUID());
                                        }
                                    }
                                } else {
                                    event.setComponent(finalMessage);
                                }
                            }
                        }
                    }
            });
        }
    }

    public boolean canCastSpell(ServerPlayer player, String[] words) {
        ISpellData data = SpelledAPI.getSpellDataCap(player).orElse(new SpellDataCapability());
        final KeywordRegistry registry = KeywordRegistry.instance();

        int currentLevel = data.getLevel();

        if(currentLevel == 0)
            return false;

        //Check if every word matches a keyword
        for (String word : words) {
            //Unknown word. Probably not a spell
            if (!registry.containsKey(word))
                return false;
            //Doesn't know the word
            if(!data.knowsKeyword(word))
                return false;
        }

        //If creative just return true if the chat message was a valid spell
        if(player.abilities.instabuild)
            return true;

        int maxLevelWord = 0;
        for (String word : words) {
            IKeyword keyword = registry.getKeywordFromName(word);
            if(keyword != null && keyword.getLevel() > maxLevelWord)
                maxLevelWord = keyword.getLevel();
        }

        if(maxLevelWord > currentLevel)
            return false;

        int maxWordCount = LevelHelper.getAllowedWordCount(currentLevel);
        return maxWordCount > 0 && words.length <= maxWordCount;
    }

    public boolean isOnCooldown(ServerPlayer player) {
        ISpellData data = SpelledAPI.getSpellDataCap(player).orElse(new SpellDataCapability());
        //Check if player is on cooldown
        int cooldown = data.getCastCooldown();
        if(cooldown > 0) {
            MutableComponent finalMessage = new TranslatableComponent("spelled.spell.cooldown", player.getDisplayName(), cooldown);
            return true;
        }
        return false;
    }

    public SpellEntity constructEntity(ServerPlayer player, @Nonnull Type type) {
        SpellEntity spell = new SpellEntity(player, player.level);
        spell.setSpellType(type.getId());

        return spell;
    }

    public SpellEntity shootSpell(ServerPlayer player, SpellEntity spell) {
        spell.setOwner(player);
        spell.setPos(player.getX(), player.getEyeY() - (double)0.1F, player.getZ());
        switch(spell.getSpellType()) {
            default: //Ball (Self is handled elsewhere)
                spell.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 2.0F, 0.0F);
                break;
            case 1: //Projectile
                spell.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 4.0F, 0.0F);
                break;
        }

        return spell;
    }
}
