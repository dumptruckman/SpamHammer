package com.dumptruckman.spamhammer;

import com.dumptruckman.minecraft.pluginbase.config.EntryBuilder;
import com.dumptruckman.minecraft.pluginbase.config.SimpleConfigEntry;
import com.dumptruckman.minecraft.pluginbase.util.Null;
import com.dumptruckman.spamhammer.api.AbstractSpamHandler;
import com.dumptruckman.spamhammer.api.ChatSpam;
import com.dumptruckman.spamhammer.api.Config;
import com.dumptruckman.spamhammer.api.Spam;
import com.dumptruckman.spamhammer.api.SpamHammer;
import org.bukkit.event.player.PlayerEvent;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

abstract class ChatSpamHandler<T extends PlayerEvent> extends AbstractSpamHandler<T> {

    static final SimpleConfigEntry<Boolean> ENABLED = new EntryBuilder<Boolean>(Boolean.class, "enabled").def(true)
            .comment("Whether or not to handle chat spam").defaultIfMissing().build();

    static final SimpleConfigEntry<Null> MESSAGE_RATE = new EntryBuilder<Null>(Null.class, "message.rate")
            .comment("# The message rate settings determine how many messages per time frame are allowed before they are "
                    + "considered spam.")
            .comment("# The default of limit: 3 and period: 1 means more than 3 messages per 1 second will be considered spam").build();

    static final SimpleConfigEntry<Integer> MESSAGE_LIMIT = new EntryBuilder<Integer>(Integer.class, "message.rate.limit").def(3)
            .validator(new Config.ZeroGreaterValidator()).build();
    static final SimpleConfigEntry<Integer> TIME_PERIOD = new EntryBuilder<Integer>(Integer.class, "message.rate.period").def(1)
            .validator(new Config.ZeroGreaterValidator()).build();

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

        BlockingDeque<ChatSpam> playerSpam = getPlayerSpam(playerName);
        playerSpam.add(SpamFactory.newChatSpam(playerName, message));

        return false;
    }

    @Override
    protected String getHeader() {
        return "=== [ Chat Spam Settings ] ===";
    }
}
