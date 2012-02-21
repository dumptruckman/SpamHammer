package com.dumptruckman.spamhammer.command;

import com.dumptruckman.spamhammer.SpamHammerPlugin;
import com.dumptruckman.spamhammer.util.Perm;
import com.dumptruckman.spamhammer.util.locale.Message;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Enables debug-information.
 */
public class ReloadCommand extends PluginCommand {

    public ReloadCommand(SpamHammerPlugin plugin) {
        super(plugin);
        this.setName("Reloads config file");
        this.setCommandUsage("/spam reload");
        this.setArgRange(0, 0);
        this.addKey("spam reload");
        this.addKey("spamreload");
        this.setPermission(Perm.COMMAND_RELOAD.getPermission());
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        plugin.reloadConfig();
        messager.normal(Message.RELOAD_COMPLETE, sender);
    }
}
