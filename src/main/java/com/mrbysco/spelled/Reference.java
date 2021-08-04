package com.mrbysco.spelled;

import com.mrbysco.spelled.entity.AbstractSpellEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.resources.ResourceLocation;

public class Reference {
    public static final String MOD_ID = "spelled";
    public static final String MOD_PREFIX = MOD_ID + ":";

    public static final ResourceLocation SPELL_DATA_CAP = new ResourceLocation(MOD_ID, "capability.spell_data");
    public final static String characterLevel = "CharacterLevel";
    public final static String characterUnlocks = "CharacterUnlocks";
    public final static String characterCooldown = "characterCooldown";
    public final static String tomeUnlock = MOD_PREFIX + "tomeUnlock";

    public static final String Phrase = "Yu Mo Gui Gwai Fai Di Zao";

    public static DamageSource causeMagicDamage(AbstractSpellEntity spell) {
        return new EntityDamageSource(MOD_PREFIX + "magic", spell);
    }
}
