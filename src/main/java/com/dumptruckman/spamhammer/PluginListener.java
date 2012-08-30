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

public class PluginListener implements Listener {

    private SpamHammer plugin;

    public PluginListener(SpamHammer plugin) {
        this.plugin = plugin;
    }

    private Config getConfig() {
        return plugin.config();
    }

    private SpamHandler getHandler() {
        return plugin.getSpamHandler();
    }

    private Messager getMessager() {
        return plugin.getMessager();
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
        if (getHandler().isMuted(event.getPlayer()) && !Perms.BYPASS_MUTE.hasPermission(event.getPlayer())) {
            event.setCancelled(true);
            getMessager().bad(Language.MUTED, event.getPlayer());
            return;
        }
        if (getHandler().handleChat(event.getPlayer(), event.getMessage())
                && getConfig().get(Config.PREVENT_MESSAGES) && !Perms.BYPASS_REPEAT.hasPermission(event.getPlayer())) {
            event.setCancelled(true);
            getMessager().bad(Language.SPAMMING_MESSAGE, event.getPlayer());
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
        if (getHandler().isMuted(event.getPlayer()) && !Perms.BYPASS_MUTE.hasPermission(event.getPlayer())) {
            event.setCancelled(true);
            getMessager().bad(Language.MUTED, event.getPlayer());
            return;
        }
        if (!getConfig().get(Config.INCLUDE_COMMANDS).contains(event.getMessage().split("\\s")[0])) return;
        if (getHandler().handleChat(event.getPlayer(), event.getMessage())
                && getConfig().get(Config.PREVENT_MESSAGES) && !Perms.BYPASS_REPEAT.hasPermission(event.getPlayer())) {
            event.setCancelled(true);
            getMessager().bad(Language.SPAMMING_MESSAGE, event.getPlayer());
        }
    }
}
