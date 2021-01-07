package com.mrbysco.spelled.registry;

import net.minecraft.resources.IResourceManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import java.util.function.Predicate;

public class ReloadManager implements ISelectiveResourceReloadListener {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        KeywordRegistry.instance().initializeKeywords();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(this);
    }
}
