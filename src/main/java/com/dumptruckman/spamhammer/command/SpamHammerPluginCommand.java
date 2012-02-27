package com.dumptruckman.spamhammer.command;

import com.dumptruckman.spamhammer.SpamHammerPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SpamHammerPluginCommand implements CommandExecutor {

    SpamHammerPlugin plugin;

    public SpamHammerPluginCommand(SpamHammerPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*if (command.getName().equals("spamunban")) {
            if (sender.isOp() || (plugin.usePerms && !sender.hasPermission("spamhammer.unban"))) {
                sender.sendMessage("You do not have permission to access this command.");
                return true;
            }
            if (args.length != 1) {
                return false;
            }
            if (plugin.isBanned(args[0])) {
                plugin.unBanPlayer(args[0]);
                sender.sendMessage(plugin.config.getString(UNBAN_COMMAND_MESSAGE_SUCCESS.toString()));
            } else {
                sender.sendMessage(plugin.config.getString(UNBAN_COMMAND_MESSAGE_FAILURE.toString()));
            }
            return true;
        }
        */
        return true;
    }
}
