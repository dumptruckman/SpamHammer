package com.dumptruckman.spamhammer.Config;

/**
 * @author dumptruckman
 */
public enum ConfigPath {
    MUTE_LIMIT ("Mute Limit", "mute.limit", 3),
    MUTE_LENGTH ("Mute Length", "mute.length", 30),
    MUTE_MESSAGE ("Mute Message", "mute.message.mute", "You will be muted for %t second(s) for spamming.  Keep it up and you'll be kicked."),
    UNMUTE_MESSAGE ("Un-Mute Message", "mute.message.unmute", "You are no longer muted."),
    MUTED_MESSAGE ("Muted Message", "mute.message.muted", "You are muted!"),
    KICK_LIMIT ("Kick Limit", "kick.limit", 3),
    KICK_MESSAGE ("Kick Message", "kick.message", "You have been kicked for spamming.  Keep it up and you'll be banned."),
    BAN_LIMIT ("Ban Limit", "ban.limit", 3),
    BAN_MESSAGE ("Ban Message", "ban.message", "You have been banned for spamming."),
    COOL_OFF("Cool Off Time", "coolofftime", 300);

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
