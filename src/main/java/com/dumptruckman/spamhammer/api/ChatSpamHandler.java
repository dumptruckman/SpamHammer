package com.dumptruckman.spamhammer.api;

import com.dumptruckman.minecraft.pluginbase.config.EntryBuilder;
import com.dumptruckman.minecraft.pluginbase.config.ListConfigEntry;
import com.dumptruckman.minecraft.pluginbase.config.SimpleConfigEntry;
import com.dumptruckman.minecraft.pluginbase.util.Null;
import com.dumptruckman.minecraft.pluginbase.util.time.Duration;
import com.dumptruckman.spamhammer.api.Config.ZeroGreaterValidator;
import org.bukkit.event.player.PlayerEvent;

import java.util.Arrays;

public interface ChatSpamHandler<E extends PlayerEvent> extends SpamHandler<E> {

    SimpleConfigEntry<Boolean> ENABLED = new EntryBuilder<Boolean>(Boolean.class, "enabled")
            .def(true)
            .comment("Whether or not to handle chat spam at all")
            .build();

    SimpleConfigEntry<Null> MESSAGE_RATE = new EntryBuilder<Null>(Null.class, "rate")
            .comment("The message rate settings determine how many messages per time frame are allowed before they are considered spam.")
            .comment("The default of limit: 3 and period: 1 means more than 3 messages per 1 second will be considered spam")
            .build();
    SimpleConfigEntry<Boolean> RATE_ENABLED = new EntryBuilder<Boolean>(Boolean.class, "rate.enabled")
            .def(true)
            .comment("Whether or not to handle excessive rate spam")
            .build();
    SimpleConfigEntry<Integer> MESSAGE_LIMIT = new EntryBuilder<Integer>(Integer.class, "rate.limit")
            .def(3)
            .validator(new Config.ZeroGreaterValidator()).build();
    SimpleConfigEntry<Integer> TIME_PERIOD = new EntryBuilder<Integer>(Integer.class, "rate.period")
            .def(1)
            .validator(new Config.ZeroGreaterValidator()).build();
    SimpleConfigEntry<Boolean> BLOCK_RATE_MESSAGES = new EntryBuilder<Boolean>(Boolean.class, "rate.prevent")
            .def(true)
            .comment("Prevents messages above the rate limit from displaying")
            .build();

    SimpleConfigEntry<Null> MESSAGE_REPEAT = new EntryBuilder<Null>(Null.class, "repeat")
            .comment("The repeat settings allow you to prevent users from repeating the same message in a row")
            .build();
    SimpleConfigEntry<Boolean> REPEAT_ENABLED = new EntryBuilder<Boolean>(Boolean.class, "repeat.enabled")
            .def(true)
            .comment("Whether or not to handle repeat message spam")
            .build();
    SimpleConfigEntry<Integer> REPEAT_LIMIT = new EntryBuilder<Integer>(Integer.class, "repeat.limit")
            .def(2)
            .comment("If SpamHammer is set to block repeat messages, this is how many messages before they are considered repeats.")
            .validator(new ZeroGreaterValidator())
            .build();
    SimpleConfigEntry<Duration> REPEAT_TIME_LIMIT = new EntryBuilder<Duration>(Duration.class, "repeat.time_limit")
            .def(Duration.valueOf("30s"))
            .comment("If SpamHammer is set to block repeat messages, this is how long before the next message will not be considered as a duplicate.")
            .comment("Set this to 0 to disable this feature and always consider the next message for duplicates.")
            .build();
    SimpleConfigEntry<Boolean> BLOCK_REPEAT_MESSAGES = new EntryBuilder<Boolean>(Boolean.class, "repeat.prevent")
            .def(true)
            .comment("Prevents duplicate messages above the limit from displaying")
            .build();

    SimpleConfigEntry<Null> IP_ADDRESS = new EntryBuilder<Null>(Null.class, "ip_address")
            .comment("The IP address settings allow you to block and punish users for putting IP addresses in their messages.")
            .build();
    SimpleConfigEntry<Boolean> IP_ENABLED = new EntryBuilder<Boolean>(Boolean.class, "ip_address.enabled")
            .def(true)
            .comment("Whether or not to handle people listing ip addresses")
            .build();
    SimpleConfigEntry<Boolean> BLOCK_IP_MESSAGES = new EntryBuilder<Boolean>(Boolean.class, "ip_address.prevent")
            .def(true)
            .comment("Completely prevents messages containing ip addresses from being displayed")
            .build();

    ListConfigEntry<String> INCLUDE_COMMANDS = new EntryBuilder<String>(String.class, "commands.treated_as_chat")
            .defList(Arrays.asList("/g", "/general", "/yell"))
            .comment("The commands listed here will be treated as if they are chat and thus follow the above rules.").buildList();
    SimpleConfigEntry<Boolean> ONLY_CONSIDER_CMD_ARGS = new EntryBuilder<Boolean>(Boolean.class, "commands.only_consider_arguments")
            .def(true)
            .comment("This will cause the base commands above to be ignored and treat all text after it as the chat.")
            .comment("For example, if you have \"/yell\" in the list above \"/yell help me!\" would be the same as the player chatting \"help me!\"")
            .build();

}
