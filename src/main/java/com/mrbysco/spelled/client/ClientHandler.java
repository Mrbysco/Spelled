package com.mrbysco.spelled.client;

import com.mrbysco.spelled.client.gui.AltarScreen;
import com.mrbysco.spelled.client.renderer.SpellRenderer;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientHandler {

    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(KeyBindings.KEY_USE);

        RenderTypeLookup.setRenderLayer(SpelledRegistry.LEVELING_ALTAR.get(), RenderType.getCutout());
        ScreenManager.registerFactory(SpelledRegistry.ALTAR_CONTAINER.get(), AltarScreen::new);

        RenderingRegistry.registerEntityRenderingHandler(SpelledRegistry.BALL_SPELL.get(), renderManager -> new SpellRenderer(renderManager));
    }

//    static final Minecraft mc = Minecraft.getInstance();

//    public static boolean lastUsePressed = false;
//
//    public static void clientTickEvent(TickEvent.ClientTickEvent event) {
//        final ClientPlayerEntity player = Minecraft.getInstance().player;
//        if (player == null) return;
//
//        boolean usePressed;
//
//        if (event.phase == TickEvent.Phase.START) {
//            usePressed = mc.isGameFocused() && KeyBindings.KEY_USE.isKeyDown();
//            if(lastUsePressed != usePressed) {
//                lastUsePressed = usePressed;
//                Spelled.CHANNEL.send(PacketDistributor.SERVER.noArg(), new KeyboardSyncMessage(usePressed));
//            }
//        }
//    }
}
