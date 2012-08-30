package com.dumptruckman.spamhammer;

import com.dumptruckman.minecraft.pluginbase.plugin.AbstractBukkitPlugin;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import com.dumptruckman.spamhammer.api.Config;
import com.dumptruckman.spamhammer.api.SpamHammer;
import com.dumptruckman.spamhammer.api.SpamHandler;
import com.dumptruckman.spamhammer.command.SpamReset;
import com.dumptruckman.spamhammer.command.SpamUnmute;
import com.dumptruckman.spamhammer.util.Language;
import com.dumptruckman.spamhammer.util.YamlConfig;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.plugin.SpoutPlugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author dumptruckman
 */
public class SpamHammerPlugin extends AbstractBukkitPlugin<Config> implements SpamHammer {

    private final List<String> prefixes = Arrays.asList("spam");
    private SpoutPlugin spoutPlugin = null;

    private PluginListener listener;
    
    private SpamHandler spamHandler = null;

    public void preEnable() {
        Language.init();
    }

    public void postEnable() {
        Plugin plugin = getServer().getPluginManager().getPlugin("SpoutPlugin");
        if (plugin != null) {
            Logging.info("Hooked SpoutPlugin!");
            this.spoutPlugin = (SpoutPlugin) plugin;
        }
        listener = new PluginListener(this);
        getServer().getPluginManager().registerEvents(listener, this);
        registerCommands();
    }

    private void registerCommands() {
        getCommandHandler().registerCommand(new SpamReset(this));
        getCommandHandler().registerCommand(new SpamUnmute(this));
    }

    @Override
    public List<String> getCommandPrefixes() {
        return prefixes;
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
