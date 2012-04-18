package com.dumptruckman.spamhammer.api;

import com.dumptruckman.minecraft.pluginbase.config.BaseConfig;
import com.dumptruckman.minecraft.pluginbase.config.ConfigEntry;
import com.dumptruckman.minecraft.pluginbase.config.EntryBuilder;
import com.dumptruckman.minecraft.pluginbase.config.EntryValidator;
import com.dumptruckman.minecraft.pluginbase.config.ListConfigEntry;
import com.dumptruckman.minecraft.pluginbase.locale.Message;
import com.dumptruckman.minecraft.pluginbase.util.Null;
import com.dumptruckman.spamhammer.util.Language;

import java.util.Arrays;
import java.util.List;

public interface Config extends BaseConfig {

    ConfigEntry<Null> MESSAGE = new EntryBuilder<Null>(Null.class, "settings.message")
            .comment("# === [ Message Spam Settings ] ===").build();
    ConfigEntry<Null> MESSAGE_RATE = new EntryBuilder<Null>(Null.class, "settings.message.rate")
            .comment("# The message rate settings determine how many messages per time frame are allowed before they are "
                    + "considered spam.")
            .comment("# The default of limit: 3 and period: 1 means more than 3 messages per 1 second will be considered spam").build();

    class ZeroGreaterValidator implements EntryValidator {
        @Override
        public boolean isValid(Object o) {
            try {
                if (Integer.parseInt(o.toString()) > 0) {
                    return true;
                }
            } catch (NumberFormatException ignore) { }
            return false;
        }

        @Override
        public Message getInvalidMessage() {
            return Language.VALID_GREATER_ZERO;
        }
    }

    ConfigEntry<Integer> MESSAGE_LIMIT = new EntryBuilder<Integer>(Integer.class, "settings.message.rate.limit").def(3)
            .validator(new ZeroGreaterValidator()).build();
    ConfigEntry<Integer> TIME_PERIOD = new EntryBuilder<Integer>(Integer.class, "settings.message.rate.period").def(1)
            .validator(new ZeroGreaterValidator()).build();

    // TODO define this
    ConfigEntry<Boolean> PREVENT_MESSAGES = new EntryBuilder<Boolean>(Boolean.class, "settings.message.rate.prevent")
            .def(true).comment("# Prevents messages above the rate limit from displaying").build();

    ConfigEntry<Null> MESSAGE_REPEAT = new EntryBuilder<Null>(Null.class, "settings.message.repeat")
            .comment("# The repeat settings allow you to prevent users from repeating the same message in a row").build();
    ConfigEntry<Boolean> BLOCK_REPEATS = new EntryBuilder<Boolean>(Boolean.class, "settings.message.repeat.block")
            .def(true).comment("# If set to true, this will block repeat messages.").build();
    ConfigEntry<Integer> REPEAT_LIMIT = new EntryBuilder<Integer>(Integer.class, "settings.message.repeat.limit").def(2)
            .comment("# If S pamHammer is set to block repeat messages, this is how many messages before they are "
                    + "considered repeats.").validator(new ZeroGreaterValidator()).build();

    ListConfigEntry<String> INCLUDE_COMMANDS = new EntryBuilder<String>(String.class, "settings.commandlist.possiblespam")
            .defList(Arrays.asList("/g", "/general", "/yell"))
            .comment("# The commands listed here will be included in spam checking.").buildList();

    ConfigEntry<Null> PUNISHMENTS = new EntryBuilder<Null>(Null.class, "settings.punishments")
            .comment("# === [ Punishment Settings ] ===").build();
    ConfigEntry<Boolean> USE_MUTE = new EntryBuilder<Boolean>(Boolean.class, "settings.punishments.mute.use").def(true)
            .comment("# Setting this to true will mute players as the first level of punishment.").build();
    ConfigEntry<Integer> MUTE_LENGTH = new EntryBuilder<Integer>(Integer.class, "settings.punishments.mute.length").def(30)
            .comment("# If mute punishment is used, this is how long the player will be muted for.")
            .comment("# This time measured in seconds.").validator(new ZeroGreaterValidator()).build();

    ConfigEntry<Boolean> USE_KICK = new EntryBuilder<Boolean>(Boolean.class, "settings.punishments.kick.use").def(true)
            .comment("# Setting this to true will kick players as the second level of punishment.").build();
    ConfigEntry<Boolean> USE_BAN = new EntryBuilder<Boolean>(Boolean.class, "settings.punishments.ban.use").def(true)
            .comment("# Setting this to true will ban players as the final level of punishment.").build();
    /*
    ConfigEntry<Integer> BAN_LENGTH = new SimpleConfigEntry<Integer>("settings.punishments.ban.length", 60,
            "# If ban punishment is used, this is how long the player will be banned for.",
            "# This time measured in minutes.",
            "# Setting this to 0 or less will make bans permanent until unbanned manually");
    */
    ConfigEntry<Integer> COOL_OFF = new EntryBuilder<Integer>(Integer.class, "settings.cooloff.time").def(300)
            .comment("# This setting determines how long a player will be watched for additional spam before starting")
            .comment("# them at the lowest punishment level.").comment("# This time measured in seconds.")
            .validator(new ZeroGreaterValidator()).build();
}
