package com.dumptruckman.spamhammer.api;

import com.dumptruckman.minecraft.pluginbase.plugin.BukkitPlugin;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.plugin.SpoutPlugin;

public interface SpamHammer extends BukkitPlugin<Config>, Plugin {

    boolean isUsingSpout();
    
    SpoutPlugin getSpout();
    
    LegacySpamHandler getSpamHandler();

    //ChatSpamHandler getChatSpamHandler();
}
