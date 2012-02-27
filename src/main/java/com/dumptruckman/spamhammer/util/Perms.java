package com.dumptruckman.spamhammer.util;

import com.dumptruckman.tools.permission.Perm;

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
    
    private Perms() {
        throw new AssertionError();
    }
}
