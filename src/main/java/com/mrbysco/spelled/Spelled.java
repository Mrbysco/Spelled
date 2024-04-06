package com.mrbysco.spelled;

import com.mojang.logging.LogUtils;
import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.behavior.BehaviorRegistry;
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
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(Reference.MOD_ID)
public class Spelled {
	public static final Logger LOGGER = LogUtils.getLogger();

	public Spelled(IEventBus eventBus) {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SpelledConfig.commonSpec);
		eventBus.register(SpelledConfig.class);

		SpelledRegistry.BLOCKS.register(eventBus);
		SpelledRegistry.BLOCK_ENTITY_TYPES.register(eventBus);
		SpelledRegistry.MENU_TYPES.register(eventBus);
		SpelledRegistry.ITEMS.register(eventBus);
		SpelledRegistry.CREATIVE_MODE_TABS.register(eventBus);
		SpelledRegistry.ENTITY_TYPES.register(eventBus);
		SpelledRegistry.SOUND_EVENTS.register(eventBus);
		SpelledRegistry.ATTACHMENT_TYPES.register(eventBus);

		eventBus.addListener(PacketHandler::setupPackets);
		eventBus.addListener(this::onCapabilityRegister);

		NeoForge.EVENT_BUS.register(new ReloadManager());
		NeoForge.EVENT_BUS.register(new CapabilityHandler());
		NeoForge.EVENT_BUS.register(new SpellCastHandler());
		NeoForge.EVENT_BUS.register(new LootHandler());
		NeoForge.EVENT_BUS.register(new SpellHandler());

		NeoForge.EVENT_BUS.addListener(this::onCommandRegister);
		NeoForge.EVENT_BUS.addListener(this::serverStart);

		if (FMLEnvironment.dist.isClient()) {
			eventBus.addListener(ClientHandler::onClientSetupEvent);
			eventBus.addListener(ClientHandler::registerEntityRenders);
			NeoForge.EVENT_BUS.addListener(ClientHandler::loginEvent);
		}
	}

	public void onCapabilityRegister(RegisterCapabilitiesEvent event) {
		event.registerEntity(SpelledAPI.SPELL_DATA_CAP, EntityType.PLAYER, (player, ctx) -> player.getData(SpelledRegistry.SPELL_DATA_ATTACHMENT.get()));
	}

	public void onCommandRegister(RegisterCommandsEvent event) {
		SpelledCommands.initializeCommands(event.getDispatcher());
	}

	public void serverStart(ServerStartingEvent event) {
		KeywordRegistry.instance().initializeKeywords();
		BehaviorRegistry.instance().initializeBehaviors();
	}
}
