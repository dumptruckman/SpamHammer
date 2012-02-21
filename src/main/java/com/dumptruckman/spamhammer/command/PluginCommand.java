package com.dumptruckman.spamhammer.command;

import com.dumptruckman.spamhammer.SpamHammerPlugin;
import com.dumptruckman.spamhammer.util.locale.Messager;
import com.pneumaticraft.commandhandler.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * A generic Multiverse-command.
 */
public abstract class PluginCommand extends Command {

    /**
     * The reference to the core.
     */
    protected SpamHammerPlugin plugin;
    /**
     * The reference to {@link Messager}.
     */
    protected Messager messager;

    public PluginCommand(SpamHammerPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.messager = this.plugin.getMessager();
    }

    @Override
    public abstract void runCommand(CommandSender sender, List<String> args);

}
