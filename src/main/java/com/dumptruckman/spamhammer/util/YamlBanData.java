package com.dumptruckman.spamhammer.util;

import com.dumptruckman.spamhammer.api.BanData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Implementation of PlayerData.
 */
public class YamlBanData implements BanData {

    private static final String YML = ".yml";
    private File chestFolder = null;

    public YamlBanData(JavaPlugin plugin) throws IOException {
        // Make the data folders
        plugin.getDataFolder().mkdirs();

        // Check if the data file exists.  If not, create it.
        this.chestFolder = new File(plugin.getDataFolder(), "banlist.yml");
        if (!this.chestFolder.exists()) {
            if (!this.chestFolder.mkdirs()) {
                throw new IOException("Could not create banlist folder!");
            }
        }
    }

    private FileConfiguration getConfigHandle(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

    private File getFolder(String worldName) {
        File folder = new File(this.chestFolder, worldName);
        if (!folder.exists()) {
            folder.mkdirs();
            Logging.finer("Created chest folder for world: " + worldName);
        }
        Logging.finer("got data folder: " + folder.getPath());
        return folder;
    }
}

