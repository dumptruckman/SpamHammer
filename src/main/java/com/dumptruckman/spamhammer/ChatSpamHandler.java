package com.dumptruckman.spamhammer;

import com.dumptruckman.minecraft.pluginbase.config.EntryBuilder;
import com.dumptruckman.minecraft.pluginbase.config.ListConfigEntry;
import com.dumptruckman.minecraft.pluginbase.config.SimpleConfigEntry;
import com.dumptruckman.minecraft.pluginbase.util.Null;
import com.dumptruckman.minecraft.pluginbase.util.time.Duration;
import com.dumptruckman.spamhammer.api.AbstractSpamHandler;
import com.dumptruckman.spamhammer.api.ChatSpam;
import com.dumptruckman.spamhammer.api.Config;
import com.dumptruckman.spamhammer.api.Config.ZeroGreaterValidator;
import com.dumptruckman.spamhammer.api.SpamHammer;
import com.dumptruckman.spamhammer.api.SpamHistory;
import org.bukkit.event.player.PlayerEvent;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

abstract class ChatSpamHandler<T extends PlayerEvent> extends AbstractSpamHandler<T> {

    static final SimpleConfigEntry<Boolean> ENABLED = new EntryBuilder<Boolean>(Boolean.class, "enabled")
            .def(true)
            .comment("Whether or not to handle chat spam")
            .build();

    static final SimpleConfigEntry<Null> MESSAGE_RATE = new EntryBuilder<Null>(Null.class, "message.rate")
            .comment("The message rate settings determine how many messages per time frame are allowed before they are considered spam.")
            .comment("The default of limit: 3 and period: 1 means more than 3 messages per 1 second will be considered spam")
            .build();
    static final SimpleConfigEntry<Integer> MESSAGE_LIMIT = new EntryBuilder<Integer>(Integer.class, "message.rate.limit")
            .def(3)
            .validator(new Config.ZeroGreaterValidator()).build();
    static final SimpleConfigEntry<Integer> TIME_PERIOD = new EntryBuilder<Integer>(Integer.class, "message.rate.period")
            .def(1)
            .validator(new Config.ZeroGreaterValidator()).build();
    static final SimpleConfigEntry<Boolean> PREVENT_MESSAGES = new EntryBuilder<Boolean>(Boolean.class, "message.rate.prevent")
            .def(true)
            .comment("Prevents messages above the rate limit from displaying")
            .build();

    static final SimpleConfigEntry<Null> MESSAGE_REPEAT = new EntryBuilder<Null>(Null.class, "message.repeat")
            .comment("The repeat settings allow you to prevent users from repeating the same message in a row")
            .build();
    static final SimpleConfigEntry<Boolean> BLOCK_REPEATS = new EntryBuilder<Boolean>(Boolean.class, "message.repeat.block")
            .def(true)
            .comment("If set to true, this will block repeat messages.")
            .build();
    static final SimpleConfigEntry<Integer> REPEAT_LIMIT = new EntryBuilder<Integer>(Integer.class, "message.repeat.limit")
            .def(2)
            .comment("If SpamHammer is set to block repeat messages, this is how many messages before they are considered repeats.")
            .validator(new ZeroGreaterValidator())
            .build();
    static final SimpleConfigEntry<Duration> REPEAT_TIME_LIMIT = new EntryBuilder<Duration>(Duration.class, "message.repeat.time_limit")
            .def(Duration.valueOf("30s"))
            .comment("If SpamHammer is set to block repeat messages, this is how long before the next message will not be considered as a duplicate.")
            .comment("Set this to 0 to disable this feature and always consider the next message for duplicates.")
            .build();

    static final ListConfigEntry<String> INCLUDE_COMMANDS = new EntryBuilder<String>(String.class, "commandlist.possiblespam")
            .defList(Arrays.asList("/g", "/general", "/yell"))
            .comment("The commands listed here will be treated as if they are chat and thus follow the above rules.").buildList();

    protected final Map<String, SpamHistory<ChatSpam>> chatSpam;

    public ChatSpamHandler(SpamHammer plugin, Map<String, SpamHistory<ChatSpam>> chatSpam) throws IOException {
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
        boolean isSpamming = false;

        SpamHistory<ChatSpam> playerSpam = getPlayerSpam(playerName);
        playerSpam.add(SpamFactory.newChatSpam(playerName, message));

        return isSpamming;
    }

    @Override
    protected String getHeader() {
        return "# === [ Chat Spam Settings ] ===";
    }
}
