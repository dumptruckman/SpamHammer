package com.dumptruckman.spamhammer.config;

/**
 * @author dumptruckman
 */
public enum ConfigPath {
    MESSAGE_LIMIT("Message Limit", "message.limit", 3),
    TIME_PERIOD("Time Period", "message.period", 1),
    USE_MUTE ("Use Mute", "punishments.mute", true),
    MUTE_LENGTH ("Mute Length", "mute.length", 30),
    MUTE_MESSAGE ("Mute Message", "mute.message.mute", "You will be muted for %t second(s) for spamming.  Keep it up and you'll be kicked."),
    UNMUTE_MESSAGE ("Un-Mute Message", "mute.message.unmute", "You are no longer muted."),
    MUTED_MESSAGE ("Muted Message", "mute.message.muted", "You are muted!"),
    USE_KICK("Use Kick", "punishments.kick", true),
    //KICK_LIMIT_TIME ("Kick Limit Time Period", "kick.limittime", 1),
    KICK_MESSAGE ("Kick Message", "kick.message", "You have been kicked for spamming.  Keep it up and you'll be banned."),
    USE_BAN("Use Ban", "punishments.ban", true),
    //BAN_LIMIT_TIME ("Ban Limit Time Period", "ban.limittime", 1),
    BAN_MESSAGE ("Ban Message", "ban.message", "You have been banned for spamming."),
    COOL_OFF("Cool Off Time", "cooloff.time", 300),
    COOL_OFF_MESSAGE("Cool Off Message", "cooloff.message", "Spamming punishment reset.  Be nice!"),
    UNMUTE_COMMAND_MESSAGE_SUCCESS ("Unmute Command Success Message", "commands.unmute.success", "Player has been unmuted."),
    UNMUTE_COMMAND_MESSAGE_FAILURE ("Unmute Command Failure Message", "commands.unmute.failure", "Player is not muted."),
    UNBAN_COMMAND_MESSAGE_SUCCESS ("Unban Command Success Message", "commands.unban.success", "Player has been unbanned"),
    UNBAN_COMMAND_MESSAGE_FAILURE ("Unban Command Failure Message", "commands.unban.failure", "Player is not banned by SpamHammer."),
    RESET_COMMAND_MESSAGE_SUCCESS ("Reset Command Success Message", "commands.reset.success", "Player's punishment level reset.");

    private String name;
    private Object def;
    private String path;
    ConfigPath(String name, String path, Object d) {
        this.name = name;
        this.path = path;
        def = d;
    }
    public Object getDefault() {
        return def;
    }
    public String getName() {
        return name;
    }
    public String toString() {
        return path;
    }
}
