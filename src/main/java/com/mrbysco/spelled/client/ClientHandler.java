package com.mrbysco.spelled.client;

import com.mrbysco.spelled.api.behavior.BehaviorRegistry;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.client.gui.AltarScreen;
import com.mrbysco.spelled.client.renderer.SpellRenderer;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientHandler {
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		MenuScreens.register(SpelledRegistry.ALTAR_CONTAINER.get(), AltarScreen::new);
	}

	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(SpelledRegistry.SPELL.get(), SpellRenderer::new);
	}

	public static void loginEvent(LoggingIn event) {
		KeywordRegistry.instance().initializeKeywords();
		BehaviorRegistry.instance().initializeBehaviors();
	}
}
