package com.dumptruckman.spamhammer.config;

import java.util.Arrays;

/**
 * @author dumptruckman
 */
public enum ConfigPath {

    MUTE("mute.message.mute", "You will be muted for %t second(s) for spamming.  Keep it up and you'll be kicked."),
    UNMUTE("mute.message.unmute", "You are no longer muted."),
    MUTED("mute.message.muted", "You are muted!"),
    KICK_MESSAGE ("kick.message", "You have been kicked for spamming.  Keep it up and you'll be banned."),
    BAN_MESSAGE("ban.message", "You have been banned for spamming."),
    COOL_OFF_MESSAGE("cooloff.message", "Spamming punishment reset.  Be nice!"),
    PREVENT_MESSAGES("message.preventabovelimit", true),
    RATE_LIMIT_MESSAGE("abovelimit.message", "You are typing too fast!"),
    UNMUTE_COMMAND_MESSAGE_SUCCESS("command.unmute.success", "Player has been unmuted."),
    UNMUTE_COMMAND_MESSAGE_FAILURE("command.unmute.failure", "Player is not muted."),
    UNBAN_COMMAND_MESSAGE_SUCCESS("command.unban.success", "Player has been unbanned"),
    UNBAN_COMMAND_MESSAGE_FAILURE("command.unban.failure", "Player is not banned by SpamHammer."),
    RESET_COMMAND_MESSAGE_SUCCESS("command.reset.success", "Player's punishment level reset.");

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
