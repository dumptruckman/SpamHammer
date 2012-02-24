package com.dumptruckman.spamhammer.util;

import com.dumptruckman.spamhammer.api.Config;
import com.dumptruckman.tools.config.AbstractYamlConfig;
import com.dumptruckman.tools.config.ConfigEntry;
import com.dumptruckman.tools.plugin.PluginBase;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Commented Yaml implementation of Config.
 */
public class YamlConfig extends AbstractYamlConfig implements Config {

    private static final ConfigEntry SETTINGS = new ConfigEntry("settings", null, "# ===[ SpamHammer Config ]===");

    private static final ConfigEntry MESSAGE = new ConfigEntry("settings.message", null,
            "# ===[ Message Spam Settings ]===");
    private static final ConfigEntry MESSAGE_RATE = new ConfigEntry("settings.message.rate", null,
            "# The message rate settings determine how messages per time frame",
            "# are allowed before they are considered spam.",
            "# The default of limit: 3 and period: 1 means more than 3 messages per 1 second will be considered spam");
    private static final ConfigEntry MESSAGE_LIMIT = new ConfigEntry("settings.message.rate.limit", 3);
    private static final ConfigEntry TIME_PERIOD = new ConfigEntry("settings.message.rate.period", 1);

    private static final ConfigEntry MESSAGE_REPEAT = new ConfigEntry("settings.message.repeat", null,
            "# The repeat settings allow you to prevent users from repeating the same message in a row");
    private static final ConfigEntry BLOCK_REPEATS = new ConfigEntry("settings.message.repeat.block", true,
            "# If set to true, this will block repeat messages.");
    private static final ConfigEntry REPEAT_LIMIT = new ConfigEntry("settings.message.repeat.limit", 2,
            "# If SpamHammer is set to block repeat messages, this is how many messages before they are "
                    + "considered repeats.");

    private static final ConfigEntry INCLUDE_COMMANDS = new ConfigEntry("settings.commandlist.possiblespam",
            Arrays.asList("/g", "/general", "/yell"), "# The commands listed here will be included in spam checking.");

    private static final ConfigEntry PUNISHMENTS = new ConfigEntry("settings.punishments", null,
            "# ===[ Punishment Settings ]===");
    private static final ConfigEntry USE_MUTE = new ConfigEntry("settings.punishments.mute.use", true,
            "# Setting this to true will mute players as the first level of punishment.");
    private static final ConfigEntry MUTE_LENGTH = new ConfigEntry("settings.punishments.mute.length", 30,
            "# If mute punishment is used, this is how long the player will be muted for.",
            "# This time measured in seconds.");
    private static final ConfigEntry USE_KICK = new ConfigEntry("settings.punishments.kick.use", true,
            "# Setting this to true will kick players as the second level of punishment.");
    private static final ConfigEntry USE_BAN = new ConfigEntry("settings.punishments.ban.use", true,
            "# Setting this to true will ban players as the final level of punishment.");
    private static final ConfigEntry BAN_LENGTH = new ConfigEntry("settings.punishments.mute.length", 60,
            "# If ban punishment is used, this is how long the player will be banned for.",
            "# This time measured in minutes.", "# Setting this to 0 will make bans permanent until unbanned manually");
    private static final ConfigEntry COOL_OFF = new ConfigEntry("settings.cooloff.time", 300,
            "# This setting determines how long a player will be watched for additional spam before starting",
            "# them at the lowest punishment level.", "# This time measured in seconds.");

    public YamlConfig(PluginBase plugin) throws IOException {
        super(plugin);
    }

    @Override
    protected ConfigEntry getSettingsHeader() {
        return SETTINGS;
    }
}
