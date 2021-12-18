package com.mrbysco.spelled.client;

import com.mrbysco.spelled.api.behavior.BehaviorRegistry;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.client.gui.AltarScreen;
import com.mrbysco.spelled.client.renderer.SpellRenderer;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedInEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientHandler {

    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(SpelledRegistry.LEVELING_ALTAR.get(), RenderType.cutout());
        ScreenManager.register(SpelledRegistry.ALTAR_CONTAINER.get(), AltarScreen::new);

        RenderingRegistry.registerEntityRenderingHandler(SpelledRegistry.SPELL.get(), renderManager -> new SpellRenderer(renderManager));
    }

    public static void loginEvent(LoggedInEvent event) {
        KeywordRegistry.instance().initializeKeywords();
        BehaviorRegistry.instance().initializeBehaviors();
    }
}
