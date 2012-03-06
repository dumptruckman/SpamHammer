package com.dumptruckman.spamhammer.command;

import com.dumptruckman.minecraft.pluginbase.plugin.command.PluginCommand;
import com.dumptruckman.spamhammer.SpamHammerPlugin;
import com.dumptruckman.spamhammer.util.Language;
import com.dumptruckman.spamhammer.util.Perms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SpamReset extends PluginCommand<SpamHammerPlugin> {

    public SpamReset(SpamHammerPlugin plugin) {
        super(plugin);
        this.setName("Resets a player's history with SpamHammer");
        this.setCommandUsage("/" + plugin.getCommandPrefixes().get(0) + " reset" + ChatColor.GOLD + " <player>");
        this.setArgRange(1, 1);
        for (String prefix : plugin.getCommandPrefixes()) {
            this.addKey(prefix + " reset");
            this.addKey(prefix + "reset");
        }
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " reset " + ChatColor.GOLD + "dumptruckman");
        this.setPermission(Perms.CMD_UNMUTE.getPermission());
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(args.get(0));
        plugin.getSpamHandler().removeMuteHistory(player);
        plugin.getSpamHandler().removeKickHistory(player);
        messager.good(Language.RESET_COMMAND_MESSAGE_SUCCESS, sender, args.get(0));
    }
}
