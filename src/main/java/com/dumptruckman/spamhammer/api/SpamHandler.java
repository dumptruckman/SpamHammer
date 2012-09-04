package com.dumptruckman.spamhammer.api;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public interface SpamHandler<T extends Event> extends Listener, com.dumptruckman.minecraft.pluginbase.config.Config {

    @EventHandler
    void handleSpam(T event);
}
