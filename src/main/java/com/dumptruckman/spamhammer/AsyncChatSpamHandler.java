package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.api.ChatSpam;
import com.dumptruckman.spamhammer.api.SpamHammer;
import com.dumptruckman.spamhammer.api.SpamHistory;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;
import java.util.Map;

class AsyncChatSpamHandler extends ChatSpamHandler<AsyncPlayerChatEvent> {

    public AsyncChatSpamHandler(SpamHammer plugin, Map<String, SpamHistory<ChatSpam>> chatSpam) throws IOException {
        super(plugin, chatSpam);
    }

    @Override
    public void handleSpam(AsyncPlayerChatEvent event) {
        this.handleChat(event.getPlayer().getName(), event.getMessage());
    }
}
