package com.dumptruckman.spamhammer.util;

import com.dumptruckman.minecraft.pluginbase.permission.Perm;

public class Perms {
    
    public static final Perm BYPASS_MUTE = new Perm.Builder("bypass.punish.mute")
            .desc("Allows user to bypass mute punishments").usePluginName().build();
    public static final Perm BYPASS_KICK = new Perm.Builder("bypass.punish.kick")
            .desc("Allows user to bypass kick punishments").usePluginName().build();
    public static final Perm BYPASS_BAN = new Perm.Builder("bypass.punish.ban")
            .desc("Allows user to bypass ban punishments").usePluginName().build();

    public static final Perm BYPASS_PUNISH = new Perm.Builder("bypass.punish.*").child(BYPASS_KICK, true)
            .child(BYPASS_MUTE, true).child(BYPASS_BAN, true).usePluginName()
            .desc("Allows user to bypass ban punishments").build();
    
    public static final Perm BYPASS_REPEAT = new Perm.Builder("bypass.repeat").usePluginName()
            .desc("Allows user to bypass repeat message limit.").build();
    public static final Perm BYPASS = new Perm.Builder("bypass.*").child(BYPASS_PUNISH, true).usePluginName()
            .child(BYPASS_REPEAT, true).desc("Allows user to bypass ban punishments").addToAll().build();
    
    public static final Perm CMD_UNMUTE = new Perm.Builder("cmd.unmute").desc("Allows use of unmute command.")
            .usePluginName().commandPermission().build();
    public static final Perm CMD_RESET = new Perm.Builder("cmd.reset").desc("Allows use of reset command.")
            .usePluginName().commandPermission().build();
    
    private Perms() {
        throw new AssertionError();
    }
}
