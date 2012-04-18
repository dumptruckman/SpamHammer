package com.dumptruckman.spamhammer;

import com.dumptruckman.minecraft.pluginbase.locale.Messager;
import com.dumptruckman.spamhammer.api.Config;
import com.dumptruckman.spamhammer.api.SpamHammer;
import com.dumptruckman.spamhammer.api.SpamHandler;
import com.dumptruckman.spamhammer.util.Language;
import com.dumptruckman.spamhammer.util.Perms;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author dumptruckman
 */
public class PluginListener implements Listener {

    private SpamHammer plugin;
    private SpamHandler handler;
    private Config config;
    private Messager messager;

    public PluginListener(SpamHammer<Config> plugin) {
        this.plugin = plugin;
        this.handler = plugin.getSpamHandler();
        this.config = plugin.config();
        this.messager = plugin.getMessager();
    }

    @EventHandler()
    public void onPlayerChat(PlayerChatEvent event) {
        // TODO change this to detect long messages and see if they're different. could then assume chat mod in use.
        if (plugin.isUsingSpout()) {
            if (event.getPlayer() instanceof SpoutPlayer) {
                SpoutPlayer plyr = (SpoutPlayer)event.getPlayer();
                if (plyr.isSpoutCraftEnabled()) {
                    // This plugin may incorrectly punish SpoutCraft players. Therefore it doesn't work on them.
                    return;
                }
            }
        }
        if (handler.isMuted(event.getPlayer()) && !Perms.BYPASS_MUTE.hasPermission(event.getPlayer())) {
            event.setCancelled(true);
            messager.bad(Language.MUTED, event.getPlayer());
            return;
        }
        if (handler.handleChat(event.getPlayer(), event.getMessage())
                && config.get(Config.PREVENT_MESSAGES) && !Perms.BYPASS_REPEAT.hasPermission(event.getPlayer())) {
            event.setCancelled(true);
            messager.bad(Language.SPAMMING_MESSAGE, event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getPlayer().isBanned()) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, plugin.getMessager().getMessage(Language.BAN_MESSAGE));
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (handler.isMuted(event.getPlayer()) && !Perms.BYPASS_MUTE.hasPermission(event.getPlayer())) {
            event.setCancelled(true);
            messager.bad(Language.MUTED, event.getPlayer());
            return;
        }
        if (!config.getList(Config.INCLUDE_COMMANDS).contains(event.getMessage().split("\\s")[0])) return;
        if (handler.handleChat(event.getPlayer(), event.getMessage())
                && config.get(Config.PREVENT_MESSAGES) && !Perms.BYPASS_REPEAT.hasPermission(event.getPlayer())) {
            event.setCancelled(true);
            messager.bad(Language.SPAMMING_MESSAGE, event.getPlayer());
        }
    }
}
