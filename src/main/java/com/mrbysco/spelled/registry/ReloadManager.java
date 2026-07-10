package com.mrbysco.spelled.registry;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.behavior.BehaviorRegistry;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

public class ReloadManager implements ResourceManagerReloadListener {
	private static final Identifier ID = Reference.modLoc("reload_manager");

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		KeywordRegistry.instance().reloadKeywords();
		BehaviorRegistry.instance().reloadBehaviors();
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onAddReloadListeners(AddServerReloadListenersEvent event) {
		event.addListener(ID, this);
	}
}
