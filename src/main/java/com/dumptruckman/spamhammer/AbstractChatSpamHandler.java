package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.api.AbstractSpamHandler;
import com.dumptruckman.spamhammer.api.ChatSpam;
import com.dumptruckman.spamhammer.api.ChatSpamHandler;
import com.dumptruckman.spamhammer.api.SpamHammer;
import com.dumptruckman.spamhammer.api.SpamHistory;
import org.bukkit.event.player.PlayerEvent;

import java.io.IOException;
import java.util.Map;

abstract class AbstractChatSpamHandler<E extends PlayerEvent> extends AbstractSpamHandler<E> implements ChatSpamHandler<E> {

    protected final Map<String, SpamHistory<ChatSpam>> chatSpam;

    public AbstractChatSpamHandler(SpamHammer plugin, Map<String, SpamHistory<ChatSpam>> chatSpam) throws IOException {
        super(plugin, "chat-spam", ChatSpamHandler.class);
        this.chatSpam = chatSpam;
    }

    private SpamHistory<ChatSpam> getPlayerSpam(String name) {
        SpamHistory<ChatSpam> spam = chatSpam.get(name);
        if (spam == null) {
            spam = new DefaultSpamHistory<ChatSpam>(name);
            chatSpam.put(name, spam);
        }
        return spam;
    }

    protected boolean handleChat(String playerName, String message) {
        boolean cancel = false;
        boolean punish = false;

        SpamHistory<ChatSpam> playerSpam = getPlayerSpam(playerName);
        playerSpam.add(SpamFactory.newChatSpam(playerName, message));

        if (get(REPEAT_ENABLED)) {
            final int repeatLimit = get(REPEAT_LIMIT);
            if (playerSpam.countSequentialDuplicates(repeatLimit + 1, get(REPEAT_TIME_LIMIT).asMilliseconds()) >= repeatLimit) {
                punish = true;
                if (get(BLOCK_REPEAT_MESSAGES)) {
                    cancel = true;
                }
            }
        }



        if (punish) {
            // TODO punish player
        }
        return cancel;
    }

    @Override
    protected String getHeader() {
        return "# === [ Chat Spam Settings ] ===";
    }
}
