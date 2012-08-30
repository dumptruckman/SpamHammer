package com.dumptruckman.spamhammer.api;

import org.bukkit.event.player.PlayerEvent;

import java.io.IOException;

public abstract class ChatSpam<T extends PlayerEvent> extends AbstractSpam<T> {



    public ChatSpam(SpamHammer plugin) throws IOException {
        super(plugin, "chat-spam", ChatSpam.class);
    }
}
