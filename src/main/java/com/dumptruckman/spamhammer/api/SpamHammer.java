package com.dumptruckman.spamhammer.api;

import com.dumptruckman.minecraft.config.BaseConfig;
import com.dumptruckman.minecraft.plugin.BukkitPlugin;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.plugin.SpoutPlugin;

public interface SpamHammer<C extends BaseConfig> extends BukkitPlugin<C>, Plugin {

    boolean isUsingSpout();
    
    SpoutPlugin getSpout();
    
    SpamHandler getSpamHandler();
}
