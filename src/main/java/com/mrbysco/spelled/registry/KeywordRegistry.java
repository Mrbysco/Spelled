package com.mrbysco.spelled.registry;

import com.google.common.collect.Maps;
import com.mrbysco.spelled.Spelled;
import com.mrbysco.spelled.registry.keyword.BreakingKeyword;
import com.mrbysco.spelled.registry.keyword.ColdKeyword;
import com.mrbysco.spelled.registry.keyword.ColorKeyword;
import com.mrbysco.spelled.registry.keyword.ExplodingKeyword;
import com.mrbysco.spelled.registry.keyword.FireKeyword;
import com.mrbysco.spelled.registry.keyword.HealingKeyword;
import com.mrbysco.spelled.registry.keyword.HurtingKeyword;
import com.mrbysco.spelled.registry.keyword.IKeyword;
import com.mrbysco.spelled.registry.keyword.LiquidKeyword;
import com.mrbysco.spelled.registry.keyword.ProtectKeyword;
import com.mrbysco.spelled.registry.keyword.PushingKeyword;
import com.mrbysco.spelled.registry.keyword.SizeKeyword;
import com.mrbysco.spelled.registry.keyword.SnowKeyword;
import com.mrbysco.spelled.registry.keyword.TypeKeyword;
import com.mrbysco.spelled.registry.keyword.TypeKeyword.Type;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class KeywordRegistry {
    private final Random random = new Random();
    private final Map<String, IKeyword> keywordMap = Maps.newHashMap();
    private final List<String> adjectiveList = new ArrayList<>();
    private final List<String> typeList = new ArrayList<>();

    private static KeywordRegistry INSTANCE;

    public static KeywordRegistry instance() {
        if (INSTANCE == null)
            INSTANCE = new KeywordRegistry();
        return INSTANCE;
    }

    public void initializeKeywords() {
        keywordMap.clear();
        typeList.clear();
        adjectiveList.clear();

        Spelled.LOGGER.info("Initializing keywords");

        //Colors
        registerKeyword(new ColorKeyword("ater", TextFormatting.BLACK, 1, 1));
        registerKeyword(new ColorKeyword("aureus", TextFormatting.GOLD, 1, 1));
        registerKeyword(new ColorKeyword("caeruleus", TextFormatting.BLUE, 1, 1));
        registerKeyword(new ColorKeyword("viridis", TextFormatting.GREEN, 1, 1));
        registerKeyword(new ColorKeyword("aqua", TextFormatting.AQUA, 1, 1));
        registerKeyword(new ColorKeyword("rubrum", TextFormatting.RED, 1, 1));
        registerKeyword(new ColorKeyword("roseus", TextFormatting.LIGHT_PURPLE, 1, 1));
        registerKeyword(new ColorKeyword("flavus", TextFormatting.YELLOW, 1, 1));
        registerKeyword(new ColorKeyword("albus", TextFormatting.WHITE, 1, 1));

        //Size
        registerKeyword(new SizeKeyword("parvus", 0.5F, 1, 1));
        registerKeyword(new SizeKeyword("magnum", 2.0F, 1, 2));
        registerKeyword(new SizeKeyword("grandis", 2.2F, 1, 2));
        registerKeyword(new SizeKeyword("immanis", 2.5F, 1, 3));

        //Informative
        registerKeyword(new LiquidKeyword("liquidus", 1, 1));
        registerKeyword(new SnowKeyword("nix", 1, 2));
        registerKeyword(new ColdKeyword("frigus", 1, 2));
        registerKeyword(new ExplodingKeyword("dissiliunt", 3, 2));
        registerKeyword(new HealingKeyword("sanitatem", 3, 2));
        registerKeyword(new HurtingKeyword("nocere", 1, 2));
        registerKeyword(new ProtectKeyword("praesidium", 1, 2));
        registerKeyword(new BreakingKeyword("fractionis", 1, 2));
        registerKeyword(new PushingKeyword("propellentibus", 1, 2));
        registerKeyword(new FireKeyword("ignis", 3, 2));

        //Projectile
        registerKeyword(new TypeKeyword("sphaera", Type.BALL, 2, 2));
        registerKeyword(new TypeKeyword("projectilis", Type.PROJECTILE, 2, 2));

        //Self
        registerKeyword(new TypeKeyword("sui", Type.SELF, 1, 1));
        registerKeyword(new TypeKeyword("sese", Type.SELF, 1, 1));
    }

    public void registerKeyword(IKeyword behavior) {
        String keyword = behavior.getKeyword().toLowerCase(Locale.ROOT);
        if(!containsKey(keyword)) {
            keywordMap.put(keyword, behavior);
            if(behavior instanceof TypeKeyword) {
                typeList.add(keyword);
            } else {
                adjectiveList.add(keyword);
            }
        }
    }

    public boolean containsKey(String keyword) {
        if(!keywordMap.isEmpty()) {
            return keywordMap.containsKey(keyword.toLowerCase(Locale.ROOT));
        }
        return false;
    }

    public HashMap<String, IKeyword> getKeywords() {
        return new HashMap<>(keywordMap);
    }

    public String getRandomAdjective() {
        ArrayList<String> list = new ArrayList<>(adjectiveList);
        return list.get(random.nextInt(list.size()));
    }

    public ArrayList<String> getTypes() {
        return new ArrayList<>(typeList);
    }

    @Nullable
    public IKeyword getKeywordFromName(String keywordName) {
        String name = keywordName.toLowerCase(Locale.ROOT);
        if(containsKey(name)) {
            return keywordMap.get(name);
        }
        return null;
    }
}
