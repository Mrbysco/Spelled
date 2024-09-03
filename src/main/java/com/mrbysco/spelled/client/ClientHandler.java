package com.mrbysco.spelled.client;

import com.mrbysco.spelled.api.behavior.BehaviorRegistry;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.client.gui.AltarScreen;
import com.mrbysco.spelled.client.renderer.SpellRenderer;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientHandler {
	public static void onMenuRegister(RegisterMenuScreensEvent event) {
		event.register(SpelledRegistry.ALTAR_CONTAINER.get(), AltarScreen::new);
	}

	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(SpelledRegistry.SPELL.get(), SpellRenderer::new);
	}

	public static void loginEvent(ClientPlayerNetworkEvent.LoggingIn event) {
		KeywordRegistry.instance().initializeKeywords();
		BehaviorRegistry.instance().initializeBehaviors();
	}
}
