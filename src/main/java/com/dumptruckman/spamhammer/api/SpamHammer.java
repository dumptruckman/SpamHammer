package com.dumptruckman.spamhammer.api;

import com.dumptruckman.tools.config.BaseConfig;
import com.dumptruckman.tools.plugin.PluginBase;
import org.getspout.spoutapi.plugin.SpoutPlugin;

public interface SpamHammer<C extends BaseConfig> extends PluginBase<C> {

    boolean isUsingSpout();
    
    SpoutPlugin getSpout();
    
    SpamHandler getSpamHandler();
}
