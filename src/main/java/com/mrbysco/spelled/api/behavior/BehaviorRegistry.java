package com.mrbysco.spelled.api.behavior;

import com.google.common.collect.Maps;
import com.mrbysco.spelled.Spelled;
import com.mrbysco.spelled.registry.behavior.ColdBehavior;
import com.mrbysco.spelled.registry.behavior.FireBehavior;
import com.mrbysco.spelled.registry.behavior.HarvestBehavior;
import com.mrbysco.spelled.registry.behavior.HealBehavior;
import com.mrbysco.spelled.registry.behavior.InkBehavior;
import com.mrbysco.spelled.registry.behavior.KnockbackBehavior;
import com.mrbysco.spelled.registry.behavior.LavaBehavior;
import com.mrbysco.spelled.registry.behavior.SmokeBehavior;
import com.mrbysco.spelled.registry.behavior.SnowBehavior;
import com.mrbysco.spelled.registry.behavior.WaterBehavior;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BehaviorRegistry {
    private final Map<String, ISpellBehavior> behaviorMap = Maps.newHashMap();

    private static BehaviorRegistry INSTANCE;

    public static BehaviorRegistry instance() {
        if (INSTANCE == null)
            INSTANCE = new BehaviorRegistry();
        return INSTANCE;
    }

    public void initializeBehaviors() {
        behaviorMap.clear();

        Spelled.LOGGER.info("Initializing spell behavior");

        registerBehavior(new ColdBehavior());
        registerBehavior(new FireBehavior());
        registerBehavior(new HarvestBehavior());
        registerBehavior(new HealBehavior());
        registerBehavior(new InkBehavior());
        registerBehavior(new KnockbackBehavior());
        registerBehavior(new LavaBehavior());
        registerBehavior(new SmokeBehavior());
        registerBehavior(new SnowBehavior());
        registerBehavior(new WaterBehavior());
    }

    public void registerBehavior(ISpellBehavior behavior) {
        String keyword = behavior.getName().toLowerCase(Locale.ROOT);
        if(!containsKey(keyword)) {
            behaviorMap.put(keyword, behavior);
        }
    }

    public boolean containsKey(String keyword) {
        if(!behaviorMap.isEmpty()) {
            return behaviorMap.containsKey(keyword.toLowerCase(Locale.ROOT));
        }
        return false;
    }

    public HashMap<String, ISpellBehavior> getBehaviors() {
        return new HashMap<>(behaviorMap);
    }

    @Nullable
    public ISpellBehavior getBehaviorByName(String behavior) {
        String name = behavior.toLowerCase(Locale.ROOT);
        if(containsKey(name)) {
            return behaviorMap.get(name);
        }
        return null;
    }
}
