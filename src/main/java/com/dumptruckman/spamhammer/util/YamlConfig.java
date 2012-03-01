package com.dumptruckman.spamhammer.util;

import com.dumptruckman.minecraft.config.AbstractYamlConfig;
import com.dumptruckman.minecraft.config.ConfigEntry;
import com.dumptruckman.minecraft.config.SimpleConfigEntry;
import com.dumptruckman.minecraft.plugin.BukkitPlugin;
import com.dumptruckman.spamhammer.api.Config;

import java.io.IOException;

/**
 * Commented Yaml implementation of Config.
 */
public class YamlConfig extends AbstractYamlConfig implements Config {

    private static final ConfigEntry SETTINGS = new SimpleConfigEntry("settings", null, "# === [ SpamHammer Settings ] ===");

    public YamlConfig(BukkitPlugin plugin) throws IOException {
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
