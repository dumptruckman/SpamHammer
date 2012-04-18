package com.dumptruckman.spamhammer.util;

import com.dumptruckman.minecraft.pluginbase.config.AbstractYamlConfig;
import com.dumptruckman.minecraft.pluginbase.config.ConfigEntry;
import com.dumptruckman.minecraft.pluginbase.config.EntryBuilder;
import com.dumptruckman.minecraft.pluginbase.plugin.BukkitPlugin;
import com.dumptruckman.minecraft.pluginbase.util.Null;
import com.dumptruckman.spamhammer.api.Config;

import java.io.File;
import java.io.IOException;

/**
 * Commented Yaml implementation of Config.
 */
public class YamlConfig extends AbstractYamlConfig<Config> implements Config {

    private static final ConfigEntry<Null> SETTINGS = new EntryBuilder<Null>(Null.class, "settings").comment("# === [ SpamHammer Settings ] ===").build();

    public YamlConfig(BukkitPlugin plugin) throws IOException {
        super(plugin, true, new File(plugin.getDataFolder(), "config.yml"), Config.class);
    }
    
    protected String getHeader() {
        return "# === [ SpamHammer Config ] ===";
    }
}
