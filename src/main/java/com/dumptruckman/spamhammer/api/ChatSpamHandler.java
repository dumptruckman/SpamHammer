package com.dumptruckman.spamhammer.api;

import org.bukkit.event.player.PlayerEvent;

import java.io.IOException;

public abstract class ChatSpamHandler<T extends PlayerEvent> extends AbstractSpamHandler<T> {



    public ChatSpamHandler(SpamHammer plugin) throws IOException {
        super(plugin, "chat-spam", ChatSpamHandler.class);
    }
}
