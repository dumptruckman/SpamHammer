package com.dumptruckman.spamhammer.util;

import com.dumptruckman.spamhammer.api.Config;
import com.dumptruckman.tools.config.AbstractYamlConfig;
import com.dumptruckman.tools.config.ConfigEntry;
import com.dumptruckman.tools.config.SimpleConfigEntry;
import com.dumptruckman.tools.plugin.PluginBase;

import java.io.IOException;

/**
 * Commented Yaml implementation of Config.
 */
public class YamlConfig extends AbstractYamlConfig implements Config {

    private static final ConfigEntry SETTINGS = new SimpleConfigEntry("settings", null, "# === [ SpamHammer Settings ] ===");

    public YamlConfig(PluginBase plugin) throws IOException {
        super(plugin);
    }

    @Override
    protected ConfigEntry getSettingsEntry() {
        return SETTINGS;
    }
    
    protected String getHeader() {
        return "# === [ SpamHammer Config ] ===";
    }
}
