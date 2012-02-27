package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.api.BanData;
import com.dumptruckman.spamhammer.api.Config;
import com.dumptruckman.spamhammer.api.SpamHammer;
import com.dumptruckman.spamhammer.api.SpamHandler;
import com.dumptruckman.spamhammer.util.Language;
import com.dumptruckman.spamhammer.util.YamlBanData;
import com.dumptruckman.spamhammer.util.YamlConfig;
import com.dumptruckman.tools.plugin.AbstractPluginBase;
import com.dumptruckman.tools.util.Logging;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.plugin.SpoutPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dumptruckman
 */
public class SpamHammerPlugin extends AbstractPluginBase<Config> implements SpamHammer<Config> {

    private SpoutPlugin spoutPlugin = null;
    
    private SpamHandler spamHandler = null;

    private BanData data;

    final public void onDisable() {
        super.onDisable();
    }

    public void preEnable() {
        Language.init();
    }

    public void postEnable() {
        Plugin plugin = getServer().getPluginManager().getPlugin("SpoutPlugin");
        if (plugin != null) {
            Logging.info("Hooked SpoutPlugin!");
            this.spoutPlugin = (SpoutPlugin) plugin;
        }
    }

    /*
    final public void onEnable() {

        // Register command executor for command
        getCommand("spamunban").setExecutor(new SpamHammerPluginCommand(this));
        getCommand("spamunmute").setExecutor(new SpamHammerPluginCommand(this));
        getCommand("spamreset").setExecutor(new SpamHammerPluginCommand(this));

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SpamHammerPlugin.this.checkTimes();
            }
        }, 0, 1000);

    }*/

    /**
     * {@inheritDoc}
     */
    @Override
    public BanData getData() {
        if (this.data == null) {
            // Loads the data
            try {
                this.data = new YamlBanData(this);
            } catch (IOException e) {  // Catch errors loading the language file and exit out if found.
                Logging.severe("Error loading data file!");
                Logging.severe(e.getMessage());
                Bukkit.getPluginManager().disablePlugin(this);
                return null;
            }
        }
        return this.data;
    }

    @Override
    public String getCommandPrefix() {
        return "spam";
    }

    @Override
    protected Config newConfigInstance() throws IOException {
        return new YamlConfig(this);
    }

    @Override
    public boolean isUsingSpout() {
        return spoutPlugin != null;
    }

    @Override
    public SpoutPlugin getSpout() {
        return spoutPlugin;
    }

    @Override
    public SpamHandler getSpamHandler() {
        if (this.spamHandler == null) {
            this.spamHandler = new DefaultSpamHandler(this);
        }
        return this.spamHandler;
    }
}
