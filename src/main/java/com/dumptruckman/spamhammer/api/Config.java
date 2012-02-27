package com.dumptruckman.spamhammer.api;

import com.dumptruckman.spamhammer.util.Language;
import com.dumptruckman.tools.config.BaseConfig;
import com.dumptruckman.tools.config.ConfigEntry;
import com.dumptruckman.tools.config.Entries;
import com.dumptruckman.tools.config.Null;
import com.dumptruckman.tools.config.SimpleConfigEntry;
import com.dumptruckman.tools.locale.Message;

import java.util.Arrays;
import java.util.List;

public interface Config extends BaseConfig {

    static final ConfigEntry<Null> MESSAGE = new SimpleConfigEntry<Null>("settings.message", null,
            "# === [ Message Spam Settings ] ===");
    static final ConfigEntry<Null> MESSAGE_RATE = new SimpleConfigEntry<Null>("settings.message.rate", null,
            "# The message rate settings determine how many messages per time frame are allowed before they are "
                    + "considered spam.",
            "# The default of limit: 3 and period: 1 means more than 3 messages per 1 second will be considered spam");
    static final ConfigEntry<Integer> MESSAGE_LIMIT = new SimpleConfigEntry<Integer>("settings.message.rate.limit", 3) {
        @Override
        public boolean isValid(Object obj) {
            try {
                if (Integer.parseInt(obj.toString()) > 0) {
                    return true;
                }
            } catch (NumberFormatException ignore) { }
            return false;
        }

        @Override
        public Message getInvalidMessage() {
            return Language.VALID_GREATER_ZERO;
        }
    };
    static final ConfigEntry<Integer> TIME_PERIOD = new SimpleConfigEntry<Integer>("settings.message.rate.period", 1) {
        @Override
        public boolean isValid(Object obj) {
            try {
                if (Integer.parseInt(obj.toString()) > 0) {
                    return true;
                }
            } catch (NumberFormatException ignore) { }
            return false;
        }

        @Override
        public Message getInvalidMessage() {
            return Language.VALID_GREATER_ZERO;
        }
    };

    // TODO define this
    public static final ConfigEntry<Boolean> PREVENT_MESSAGES = new SimpleConfigEntry<Boolean>(
            "message.preventabovelimit", true);

    static final ConfigEntry<Null> MESSAGE_REPEAT = new SimpleConfigEntry<Null>("settings.message.repeat", null,
            "# The repeat settings allow you to prevent users from repeating the same message in a row");
    static final ConfigEntry<Boolean> BLOCK_REPEATS = new SimpleConfigEntry<Boolean>("settings.message.repeat.block",
            true, "# If set to true, this will block repeat messages.");
    static final ConfigEntry<Integer> REPEAT_LIMIT = new SimpleConfigEntry<Integer>("settings.message.repeat.limit", 2,
            "# If SpamHammer is set to block repeat messages, this is how many messages before they are "
                    + "considered repeats.") {
        @Override
        public boolean isValid(Object obj) {
            try {
                if (Integer.parseInt(obj.toString()) > 0) {
                    return true;
                }
            } catch (NumberFormatException ignore) { }
            return false;
        }

        @Override
        public Message getInvalidMessage() {
            return Language.VALID_GREATER_ZERO;
        }
    };

    static final ConfigEntry<List<String>> INCLUDE_COMMANDS = new SimpleConfigEntry<List<String>>(
            "settings.commandlist.possiblespam", Arrays.asList("/g", "/general", "/yell"),
            "# The commands listed here will be included in spam checking.");

    static final ConfigEntry<Null> PUNISHMENTS = new SimpleConfigEntry<Null>("settings.punishments", null,
            "# === [ Punishment Settings ] ===");
    static final ConfigEntry<Boolean> USE_MUTE = new SimpleConfigEntry<Boolean>("settings.punishments.mute.use", true,
            "# Setting this to true will mute players as the first level of punishment.");
    static final ConfigEntry<Integer> MUTE_LENGTH = new SimpleConfigEntry<Integer>("settings.punishments.mute.length",
            30, "# If mute punishment is used, this is how long the player will be muted for.",
            "# This time measured in seconds.") {
        @Override
        public boolean isValid(Object obj) {
            try {
                if (Integer.parseInt(obj.toString()) > 0) {
                    return true;
                }
            } catch (NumberFormatException ignore) { }
            return false;
        }

        @Override
        public Message getInvalidMessage() {
            return Language.VALID_GREATER_ZERO;
        }
    };
    static final ConfigEntry<Boolean> USE_KICK = new SimpleConfigEntry<Boolean>("settings.punishments.kick.use", true,
            "# Setting this to true will kick players as the second level of punishment.");
    static final ConfigEntry<Boolean> USE_BAN = new SimpleConfigEntry<Boolean>("settings.punishments.ban.use", true,
            "# Setting this to true will ban players as the final level of punishment.");
    static final ConfigEntry<Integer> BAN_LENGTH = new SimpleConfigEntry<Integer>("settings.punishments.ban.length", 60,
            "# If ban punishment is used, this is how long the player will be banned for.",
            "# This time measured in minutes.",
            "# Setting this to 0 or less will make bans permanent until unbanned manually");
    static final ConfigEntry<Integer> COOL_OFF = new SimpleConfigEntry<Integer>("settings.cooloff.time", 300,
            "# This setting determines how long a player will be watched for additional spam before starting",
            "# them at the lowest punishment level.", "# This time measured in seconds.") {
        @Override
        public boolean isValid(Object obj) {
            try {
                if (Integer.parseInt(obj.toString()) > 0) {
                    return true;
                }
            } catch (NumberFormatException ignore) { }
            return false;
        }

        @Override
        public Message getInvalidMessage() {
            return Language.VALID_GREATER_ZERO;
        }
    };

    static class Initializer {
        public static void init() {
            Entries.registerConfig(Config.class);
        }
    }
}
