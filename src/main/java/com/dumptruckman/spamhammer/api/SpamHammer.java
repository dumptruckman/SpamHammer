package com.dumptruckman.spamhammer.api;

import com.dumptruckman.tools.config.ConfigBase;
import com.dumptruckman.tools.plugin.PluginBase;

public interface SpamHammer<C extends ConfigBase> extends PluginBase<C> {
    
    BanData getData();

}
