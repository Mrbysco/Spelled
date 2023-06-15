package com.mrbysco.spelled;

import com.mrbysco.spelled.entity.AbstractSpellEntity;
import com.mrbysco.spelled.registry.SpelledDamageTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;

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
		return spell.damageSources().source(SpelledDamageTypes.MAGIC, spell.getEffectSource());
	}
}
