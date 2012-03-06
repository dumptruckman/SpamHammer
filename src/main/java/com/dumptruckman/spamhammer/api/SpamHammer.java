package com.dumptruckman.spamhammer.api;

import com.dumptruckman.minecraft.pluginbase.config.BaseConfig;
import com.dumptruckman.minecraft.pluginbase.plugin.BukkitPlugin;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.plugin.SpoutPlugin;

public interface SpamHammer<C extends BaseConfig> extends BukkitPlugin<C>, Plugin {

    boolean isUsingSpout();
    
    SpoutPlugin getSpout();
    
    SpamHandler getSpamHandler();
}
