package com.dumptruckman.spamhammer.util;

import com.dumptruckman.minecraft.pluginbase.permission.Perm;

public class Perms {
    
    public static final Perm BYPASS_MUTE = new Perm.Builder("bypass.punish.mute")
            .desc("Allows user to bypass mute punishments").build();
    public static final Perm BYPASS_KICK = new Perm.Builder("bypass.punish.kick")
            .desc("Allows user to bypass kick punishments").build();
    public static final Perm BYPASS_BAN = new Perm.Builder("bypass.punish.ban")
            .desc("Allows user to bypass ban punishments").build();

    public static final Perm BYPASS_PUNISH = new Perm.Builder("bypass.punish.*").child(BYPASS_KICK.getName(), true)
            .child(BYPASS_MUTE.getName(), true).child(BYPASS_BAN.getName(), true)
            .desc("Allows user to bypass ban punishments").build();
    
    public static final Perm BYPASS_REPEAT = new Perm.Builder("bypass.repeat")
            .desc("Allows user to bypass repeat message limit.").build();
    public static final Perm BYPASS = new Perm.Builder("bypass.*").child(BYPASS_PUNISH.getName(), true)
            .child(BYPASS_REPEAT.getName(), true).desc("Allows user to bypass ban punishments").build();
    
    public static final Perm CMD_UNMUTE = new Perm.Builder("cmd.unmute").desc("Allows use of unmute command.").build();
    public static final Perm CMD_RESET = new Perm.Builder("cmd.reset").desc("Allows use of reset command.").build();
    public static final Perm CMD = new Perm.Builder("cmd.*").child(CMD_UNMUTE.getName(), true)
            .child(CMD_RESET.getName(), true).desc("Allows user to use all SpamHammer commands.").build();

    public static final Perm ALL = new Perm.Builder("*").child(CMD.getName(), true)
            .child(BYPASS.getName(), true).desc("Gives user access to all SpamHammer permissions").build();
    
    private Perms() {
        throw new AssertionError();
    }
}
