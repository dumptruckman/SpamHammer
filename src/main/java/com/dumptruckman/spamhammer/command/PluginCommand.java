package com.dumptruckman.spamhammer.command;

import com.dumptruckman.simplecircuits.SimpleCircuitsPlugin;
import com.dumptruckman.simplecircuits.util.locale.Messager;
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
    protected SimpleCircuitsPlugin plugin;
    /**
     * The reference to {@link Messager}.
     */
    protected Messager messager;

    public PluginCommand(SimpleCircuitsPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.messager = this.plugin.getMessager();
    }

    @Override
    public abstract void runCommand(CommandSender sender, List<String> args);

}
