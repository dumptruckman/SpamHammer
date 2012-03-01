package com.dumptruckman.spamhammer;

import com.dumptruckman.minecraft.config.Entries;
import com.dumptruckman.minecraft.plugin.AbstractBukkitPlugin;
import com.dumptruckman.minecraft.util.Logging;
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

/**
 * @author dumptruckman
 */
public class SpamHammerPlugin extends AbstractBukkitPlugin<Config> implements SpamHammer<Config> {

    private SpoutPlugin spoutPlugin = null;
    
    private SpamHandler spamHandler = null;
    final public void onDisable() {
        super.onDisable();
    }

    public void preEnable() {
        Language.init();
        Entries.registerConfig(Config.class);
        Entries.registerConfig(YamlConfig.class);
    }

    public void postEnable() {
        Plugin plugin = getServer().getPluginManager().getPlugin("SpoutPlugin");
        if (plugin != null) {
            Logging.info("Hooked SpoutPlugin!");
            this.spoutPlugin = (SpoutPlugin) plugin;
        }

        registerCommands();
    }

    private void registerCommands() {
        getCommandHandler().registerCommand(new SpamReset(this));
        getCommandHandler().registerCommand(new SpamUnmute(this));
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
