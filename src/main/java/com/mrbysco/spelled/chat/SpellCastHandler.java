package com.mrbysco.spelled.chat;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.capability.ISpellData;
import com.mrbysco.spelled.capability.SpellDataCapability;
import com.mrbysco.spelled.entity.SpellEntity;
import com.mrbysco.spelled.registry.KeywordRegistry;
import com.mrbysco.spelled.registry.SpelledRegistry;
import com.mrbysco.spelled.registry.keyword.IKeyword;
import com.mrbysco.spelled.registry.keyword.TypeKeyword;
import com.mrbysco.spelled.registry.keyword.TypeKeyword.Type;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraft.world.World;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.Locale;

public class SpellCastHandler {
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.START)
            return;

        World world = event.player.world;
        if(!world.isRemote && world.getGameTime() % 20 == 0) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.player;
            int cooldown = SpelledAPI.getCooldown(player);
            if(cooldown > 0) {
                SpelledAPI.setCooldown(player, cooldown--);
                SpelledAPI.syncCap(player);
            }
        }
    }


    @SubscribeEvent
    public void onChatEvent(ServerChatEvent event) {
        ServerPlayerEntity player = event.getPlayer();
        SpelledAPI.getSpellDataCap(player).ifPresent(data -> {
            if(data.getLevel() > 0) {
                KeywordRegistry registry = KeywordRegistry.instance();
                String message = event.getMessage().toLowerCase(Locale.ROOT);
                String[] words = message.split("\\s+");

                if(words.length >= 2 && canCastSpell(player, words)) {
                    //Do our stuff
                    IKeyword lastKeyword = registry.getKeywordFromName(words[words.length - 1]);
                    World world = player.world;

                    if(lastKeyword instanceof TypeKeyword) {
                        TypeKeyword typeKeyword = (TypeKeyword) lastKeyword;
                        SpellEntity spell = constructEntity(player, typeKeyword.getType());

                        StringBuilder castText = new StringBuilder("'");
                        IFormattableTextComponent descriptionComponent = new StringTextComponent("");
                        int cooldown = -1;
                        for(int i = 0; i < (words.length - 1); i++) {
                            IKeyword keyword = registry.getKeywordFromName(words[i]);
                            cooldown += keyword.getSlots();
                            if(keyword != null) {
                                castText.append(keyword.getKeyword()).append(" ");
                                descriptionComponent.append(keyword.getDescription()).append(new StringTextComponent(" "));
                                int previous = i - 1;
                                if(previous >= 0 && previous < (words.length - 1))
                                    keyword.cast(world, player, spell, registry.getKeywordFromName(words[previous]));
                                else
                                    keyword.cast(world, player, spell, null);
                            }
                        }
                        castText.append(lastKeyword.getKeyword()).append("'");
                        StringTextComponent castComponent = new StringTextComponent(castText.toString());
                        descriptionComponent.append(typeKeyword.getDescription());
                        descriptionComponent.mergeStyle(TextFormatting.GOLD);
                        castComponent.setStyle(event.getComponent().getStyle().setHoverEvent(new HoverEvent(Action.SHOW_TEXT, descriptionComponent)));
                        castComponent.mergeStyle(TextFormatting.GOLD);

                        StringTextComponent messageComponent = new StringTextComponent("has cast ");
                        messageComponent.append(castComponent);

                        IFormattableTextComponent finalMessage = new TranslationTextComponent("chat.type.emote", player.getDisplayName(), messageComponent);
                        event.setComponent(finalMessage);

                        if(spell != null) {
                            if(!player.abilities.isCreativeMode) {
                                SpelledAPI.setCooldown(player, cooldown);
                                SpelledAPI.syncCap(player);
                            }
                            System.out.println(spell);
                            world.addEntity(spell);
                        }
                    }
                }
            }
        });
    }

    public boolean canCastSpell(ServerPlayerEntity player, String[] words) {
        ISpellData data = SpelledAPI.getSpellDataCap(player).orElse(new SpellDataCapability());
        final KeywordRegistry registry = KeywordRegistry.instance();

        //Check if every word matches a keyword
        for (String word : words) {
            if (!registry.containsKey(word)) {
                //Unknown word. Probably not a spell
                return false;
            }
            if(!data.knowsKeyword(word)) {
                //Doesn't know the word
                return false;
            }
        }

        //If creative just return true if the chat message was a valid spell
        if(player.abilities.isCreativeMode)
            return true;

        //Check if player is on cooldown
        if(data.getCastCooldown() > 0)
            return false;

        int maxLevelWord = 0;
        for (String word : words) {
            IKeyword keyword = registry.getKeywordFromName(word);
            if(keyword != null && keyword.getLevel() > maxLevelWord)
                maxLevelWord = keyword.getLevel();
        }
        return maxLevelWord <= data.getLevel();
    }

    public SpellEntity constructEntity(ServerPlayerEntity player, @Nonnull Type type) {
        switch(type) {
            default:
                SpellEntity spell = SpelledRegistry.BALL_SPELL.get().create(player.world);
                if(spell != null) {
                    spell.shootSpell(player.getLookVec());
                    spell.rotationYaw = player.rotationYaw % 360.0F;
                    spell.rotationPitch = player.rotationPitch % 360.0F;
                    spell.setLocationAndAngles(player.getPosX(), player.getPosYEye() - (double) 0.2F, player.getPosZ(), player.rotationYaw, player.rotationPitch);
                    spell.setShooter(player);
                }
                return spell;
            case PROJECTILE:
            case SELF:
                return null;
        }
    }
}
