package com.dumptruckman.spamhammer.listeners;

import com.dumptruckman.spamhammer.Config.ConfigPath;
import com.dumptruckman.spamhammer.SpamHammer;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * @author dumptruckman
 */
public class SpamHammerPlayerListener extends PlayerListener {

    private SpamHammer plugin;

    public SpamHammerPlayerListener(SpamHammer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.getPlayer().isOp()) return;
        if (plugin.isMuted(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(plugin.config.getString(ConfigPath.MUTED_MESSAGE.toString()));
            event.setCancelled(true);
            return;
        }
        
        plugin.addChatMessage(event.getPlayer().getName(), event.getMessage());
        plugin.checkSpam(event.getPlayer().getName());
    }

    public void onPlayerLogin(PlayerLoginEvent event) {
        if (plugin.isBanned(event.getPlayer().getName())) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    plugin.config.getString(ConfigPath.BAN_MESSAGE.toString()));
        }
    }
}
