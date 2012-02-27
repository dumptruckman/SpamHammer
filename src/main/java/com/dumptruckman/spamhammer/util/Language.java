package com.dumptruckman.spamhammer.util;

import com.dumptruckman.tools.locale.Message;

public class Language {

    public static final Message MUTE = new Message("mute.message.mute",
            "You will be muted for %1 second(s) for spamming.  Keep it up and you'll be kicked.");
    public static final Message UNMUTE = new Message("mute.message.unmute", "You are no longer muted.");
    public static final Message MUTED = new Message("mute.message.muted", "You are muted!");
    public static final Message KICK_MESSAGE = new Message("kick.message",
            "You have been kicked for spamming.  Keep it up and you'll be banned.");
    public static final Message BAN_MESSAGE = new Message("ban.message", "You have been banned for spamming.");
    public static final Message COOL_OFF_MESSAGE = new Message("cooloff.message",
            "Spamming punishment reset.  Be nice!");

    public static final Message RATE_LIMIT_MESSAGE = new Message("abovelimit.message", "You are typing too fast!");
    public static final Message UNMUTE_COMMAND_MESSAGE_SUCCESS = new Message("command.unmute.success",
            "%1 has been unmuted.");
    public static final Message UNMUTE_COMMAND_MESSAGE_FAILURE = new Message("command.unmute.failure",
            "%1 is not muted.");
    public static final Message UNBAN_COMMAND_MESSAGE_SUCCESS = new Message("command.unban.success",
            "%1 has been unbanned");
    public static final Message UNBAN_COMMAND_MESSAGE_FAILURE = new Message("command.unban.failure",
            "%1 is not banned by SpamHammer.");
    public static final Message RESET_COMMAND_MESSAGE_SUCCESS = new Message("command.reset.success",
            "%1's punishment level reset.");
    
    public static final Message VALID_GREATER_ZERO = new Message("validation.greater_than_zero",
            "Must be a number greater than zero!");
    
    public static void init() { }
}
