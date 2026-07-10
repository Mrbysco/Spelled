package com.mrbysco.spelled.generator.assets;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class SpelledSoundProvider extends SoundDefinitionsProvider {

	public SpelledSoundProvider(PackOutput output) {
		super(output, Reference.MOD_ID);
	}

	@Override
	public void registerSounds() {
		this.add(SpelledRegistry.SHOOT_SPELL, definition()
				.subtitle("spelled.subtitles.shoot.spell")
				.with(sound(Identifier.withDefaultNamespace("mob/ghast/fireball4"))));
	}
}
