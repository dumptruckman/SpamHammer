package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.api.AbstractSpamHandler;
import com.dumptruckman.spamhammer.api.ChatSpam;
import com.dumptruckman.spamhammer.api.Spam;
import com.dumptruckman.spamhammer.api.SpamHammer;
import org.bukkit.event.player.PlayerEvent;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public abstract class ChatSpamHandler<T extends PlayerEvent> extends AbstractSpamHandler<T> {

    protected final Map<String, BlockingDeque<ChatSpam>> chatSpam;

    public ChatSpamHandler(SpamHammer plugin, Map<String, BlockingDeque<ChatSpam>> chatSpam) throws IOException {
        super(plugin, "chat-spam", ChatSpamHandler.class);
        this.chatSpam = chatSpam;
    }

    private BlockingDeque<ChatSpam> getPlayerSpam(String name) {
        BlockingDeque<ChatSpam> spam = chatSpam.get(name);
        if (spam == null) {
            spam = new LinkedBlockingDeque<ChatSpam>();
            chatSpam.put(name, spam);
        }
        return spam;
    }

    protected boolean handleChat(String playerName, String message) {
        boolean isSpamming = false;

        // Detect rate limited messages
        BlockingDeque<ChatSpam> playerSpam = getPlayerSpam(playerName);
        playerSpam.add(SpamFactory.newChatSpam(playerName, message));

        return false;
    }
}
