package com.mrbysco.spelled;

import com.mrbysco.spelled.api.behavior.BehaviorRegistry;
import com.mrbysco.spelled.api.capability.ISpellData;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.chat.SpellCastHandler;
import com.mrbysco.spelled.client.ClientHandler;
import com.mrbysco.spelled.commands.SpelledCommands;
import com.mrbysco.spelled.config.SpelledConfig;
import com.mrbysco.spelled.handler.CapabilityHandler;
import com.mrbysco.spelled.handler.LootHandler;
import com.mrbysco.spelled.handler.SpellHandler;
import com.mrbysco.spelled.packets.PacketHandler;
import com.mrbysco.spelled.registry.ReloadManager;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MOD_ID)
public class Spelled {
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    public Spelled() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(Type.COMMON, SpelledConfig.commonSpec);
        eventBus.register(SpelledConfig.class);

        SpelledRegistry.BLOCKS.register(eventBus);
        SpelledRegistry.BLOCK_ENTITIES.register(eventBus);
        SpelledRegistry.CONTAINERS.register(eventBus);
        SpelledRegistry.ITEMS.register(eventBus);
        SpelledRegistry.ENTITIES.register(eventBus);
        SpelledRegistry.SOUND_EVENTS.register(eventBus);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::onCapabilityRegister);

        MinecraftForge.EVENT_BUS.register(new ReloadManager());
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
        MinecraftForge.EVENT_BUS.register(new SpellCastHandler());
        MinecraftForge.EVENT_BUS.register(new LootHandler());
        MinecraftForge.EVENT_BUS.register(new SpellHandler());

        MinecraftForge.EVENT_BUS.addListener(this::onCommandRegister);
        MinecraftForge.EVENT_BUS.addListener(this::serverStart);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            eventBus.addListener(ClientHandler::onClientSetupEvent);
            eventBus.addListener(ClientHandler::registerEntityRenders);
            MinecraftForge.EVENT_BUS.addListener(ClientHandler::loginEvent);
        });
    }

    private void setup(final FMLCommonSetupEvent event) {
        PacketHandler.registerPackets();
    }

    public void onCapabilityRegister(RegisterCapabilitiesEvent event) {
        event.register(ISpellData.class);
    }

    public void onCommandRegister(RegisterCommandsEvent event) {
        SpelledCommands.initializeCommands(event.getDispatcher());
    }

    public void serverStart(ServerStartingEvent event) {
        KeywordRegistry.instance().initializeKeywords();
        BehaviorRegistry.instance().initializeBehaviors();
    }
}
