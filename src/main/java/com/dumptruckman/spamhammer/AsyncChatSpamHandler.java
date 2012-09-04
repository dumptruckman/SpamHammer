package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.api.ChatSpamHandler;
import com.dumptruckman.spamhammer.api.SpamHammer;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;

class AsyncChatSpamHandler extends ChatSpamHandler<AsyncPlayerChatEvent> {



    public AsyncChatSpamHandler(SpamHammer plugin) throws IOException {
        super(plugin);
    }

    @Override
    public void handleSpam(AsyncPlayerChatEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
