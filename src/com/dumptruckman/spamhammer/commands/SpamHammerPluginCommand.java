package com.dumptruckman.spamhammer.commands;

import com.dumptruckman.spamhammer.SpamHammer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static com.dumptruckman.spamhammer.config.ConfigPath.*;

/**
 * @author dumptruckman
 */
public class SpamHammerPluginCommand implements CommandExecutor {

    SpamHammer plugin;

    public SpamHammerPluginCommand(SpamHammer plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("spamunban")) {
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
        } else if (command.getName().equals("spamunmute")) {
            if (sender.isOp() || (plugin.usePerms && !sender.hasPermission("spamhammer.unmute"))) {
                sender.sendMessage("You do not have permission to access this command.");
                return true;
            }
            if (args.length != 1) {
                return false;
            }
            if (plugin.isMuted(args[0])) {
                plugin.unMutePlayer(args[0]);
                plugin.beenMuted.remove(args[0]);
                sender.sendMessage(plugin.config.getString(UNMUTE_COMMAND_MESSAGE_SUCCESS.toString()));
            } else {
                sender.sendMessage(plugin.config.getString(UNMUTE_COMMAND_MESSAGE_FAILURE.toString()));
            }
            return true;
        } else if (command.getName().equals("spamreset")) {
            if (sender.isOp() || (plugin.usePerms && !sender.hasPermission("spamhammer.reset"))) {
                sender.sendMessage("You do not have permission to access this command.");
                return true;
            }
            if (args.length != 1) {
                return false;
            }
            if (plugin.beenMuted(args[0])) {
                plugin.beenMuted.remove(args[0]);
            }
            if (plugin.beenKicked(args[0])) {
                plugin.beenKicked.remove(args[0]);
            }
            sender.sendMessage(plugin.config.getString(RESET_COMMAND_MESSAGE_SUCCESS.toString()));
            return true;
        } else {
            return false;
        }
    }
}
