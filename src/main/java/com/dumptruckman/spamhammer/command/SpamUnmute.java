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

public class SpamUnmute extends PluginCommand<SpamHammerPlugin> {

    public SpamUnmute(SpamHammerPlugin plugin) {
        super(plugin);
        this.setName("Unmute someone muted by SpamHammer");
        this.setCommandUsage("/" + plugin.getCommandPrefixes().get(0) + " unmute" + ChatColor.GOLD + " <player>");
        this.setArgRange(1, 1);
        for (String prefix : plugin.getCommandPrefixes()) {
            this.addKey(prefix + " unmute");
            this.addKey(prefix + "unmute");
        }
        this.addCommandExample("/" + plugin.getCommandPrefixes().get(0) + " unmute " + ChatColor.GOLD + "dumptruckman");
        this.setPermission(Perms.CMD_UNMUTE.getPermission());
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(args.get(0));
        if (plugin.getSpamHandler().isMuted(player)) {
            plugin.getSpamHandler().unMutePlayer(player);
            plugin.getSpamHandler().removeMuteHistory(player);
            messager.good(Language.UNMUTE_COMMAND_MESSAGE_SUCCESS, sender, args.get(0));
        } else {
            messager.good(Language.UNMUTE_COMMAND_MESSAGE_FAILURE, sender, args.get(0));
        }
    }
}
