package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.api.Config;
import com.dumptruckman.spamhammer.api.SpamHammer;
import com.dumptruckman.spamhammer.api.SpamHandler;
import com.dumptruckman.spamhammer.util.Language;
import com.dumptruckman.spamhammer.util.Perms;
import com.dumptruckman.tools.locale.Messager;
import com.dumptruckman.tools.permission.Perm;
import com.dumptruckman.tools.util.MinecraftTools;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DefaultSpamHandler implements SpamHandler {

    Config config;
    Messager messager;

    private Map<OfflinePlayer, ArrayDeque<Long>> playerChatTimes = new HashMap<OfflinePlayer, ArrayDeque<Long>>();
    private Map<OfflinePlayer, ArrayDeque<String>> playerChatHistory = new HashMap<OfflinePlayer, ArrayDeque<String>>();
    private Map<OfflinePlayer, Long> actionTime = new HashMap<OfflinePlayer, Long>();

    private List<OfflinePlayer> mutedPlayers = new ArrayList<OfflinePlayer>();
    public List<OfflinePlayer> beenMuted = new ArrayList<OfflinePlayer>();
    public List<OfflinePlayer> beenKicked = new ArrayList<OfflinePlayer>();

    public DefaultSpamHandler(SpamHammer<Config> plugin) {
        this.config = plugin.config();
        this.messager = plugin.getMessager();
        plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                checkTimes();
            }
        }, 0, MinecraftTools.convertSecondsToTicks(1));
    }

    @Override
    public boolean handleChat(OfflinePlayer player, String message) {
        boolean isSpamming = false;

        // Detect rate limited messages
        ArrayDeque<Long> times = playerChatTimes.get(player);
        if (times == null) times = new ArrayDeque<Long>();
        long curtime = System.nanoTime() / 1000000;
        times.add(curtime);
        if (times.size() > config.get(Config.MESSAGE_LIMIT)) {
            times.remove();
        }
        long timediff = times.getLast() - times.getFirst();
        if (timediff > config.get(Config.TIME_PERIOD)) {
            times.clear();
            times.add(curtime);
        }
        if (times.size() == config.get(Config.MESSAGE_LIMIT)) {
            isSpamming = true;
        }
        playerChatTimes.put(player, times);

        // Detect duplicate messages
        if (config.get(Config.BLOCK_REPEATS) && !isSpamming) {
            ArrayDeque<String> playerChat = playerChatHistory.get(player);
            if (playerChat == null) playerChat = new ArrayDeque<String>();
            playerChat.add(message);
            if (playerChat.size() > (config.get(Config.REPEAT_LIMIT) + 1)) {
                playerChat.remove();
            }
            playerChatHistory.put(player, playerChat);
            isSpamming = hasDuplicateMessages(player);
        }

        if (isSpamming) {
            playerIsSpamming(player);
        }
        return isSpamming;
    }

    public boolean hasDuplicateMessages(OfflinePlayer name) {
        boolean isSpamming = false;
        int samecount = 1;
        String lastMessage = null;
        for (Object m : playerChatHistory.get(name).toArray()) {
            String message = m.toString();
            if (lastMessage == null) {
                lastMessage = message;
                continue;
            }
            if (message.equals(lastMessage)) {
                samecount++;
            } else {
                playerChatHistory.get(name).clear();
                playerChatHistory.get(name).add(message);
                break;
            }
            isSpamming = (samecount > config.get(Config.REPEAT_LIMIT));
        }
        return isSpamming;
    }

    public void playerIsSpamming(OfflinePlayer name) {
        boolean useMute = config.get(Config.USE_MUTE);
        boolean useKick = config.get(Config.USE_KICK);
        boolean useBan = config.get(Config.USE_BAN);
        if(useMute && (!beenMuted(name) || (!useKick && !useBan))) {
            mutePlayer(name);
            return;
        }
        if (useKick && (!beenKicked(name) || !useBan)) {
            kickPlayer(name);
            return;
        }
        if (useBan) {
            banPlayer(name);
        }
    }

    public boolean isMuted(OfflinePlayer name) {
        return mutedPlayers.contains(name);
    }

    public boolean beenMuted(OfflinePlayer name) {
        return beenMuted.contains(name);
    }

    public boolean beenKicked(OfflinePlayer name) {
        return beenKicked.contains(name);
    }

    public void mutePlayer(OfflinePlayer player) {
        mutedPlayers.add(player);
        beenMuted.add(player);
        actionTime.put(player, System.nanoTime() / 1000000);

        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            messager.normal(Language.MUTE, onlinePlayer, config.get(Config.MUTE_LENGTH));
        }
    }
    
    public void removeKickHistory(OfflinePlayer player) {
        beenKicked.remove(player);
    }
    
    public void removeMuteHistory(OfflinePlayer player) {
        beenMuted.remove(player);
    }

    public void unMutePlayer(OfflinePlayer player) {
        mutedPlayers.remove(player);
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            messager.normal(Language.UNMUTE, onlinePlayer);
        }
    }

    public void kickPlayer(OfflinePlayer player) {
        beenKicked.add(player);
        actionTime.put(player, System.currentTimeMillis() / 1000);
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            if (!Perms.BYPASS_KICK.hasPermission(onlinePlayer)) {
                onlinePlayer.kickPlayer(messager.getMessage(Language.KICK_MESSAGE));
            }
        }
    }

    public void banPlayer(OfflinePlayer player) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            if (!Perms.BYPASS_BAN.hasPermission(onlinePlayer)) {
                player.setBanned(true);
                onlinePlayer.kickPlayer(messager.getMessage(Language.BAN_MESSAGE));
            }
        }
    }

    public void unBanPlayer(OfflinePlayer player) {
        player.setBanned(false);
    }

    public synchronized void checkTimes() {
        Long time = System.currentTimeMillis() / 1000;
        for (OfflinePlayer player : actionTime.keySet()) {
            if (isMuted(player)) {
                if (time > (actionTime.get(player) + config.get(Config.MUTE_LENGTH))) {
                    unMutePlayer(player);
                }
            }
            if ((time > (actionTime.get(player) + config.get(Config.COOL_OFF)))
                    && (config.get(Config.COOL_OFF) != 0)) {
                if (beenKicked(player)) beenKicked.remove(player);
                if (beenMuted(player)) {
                    Player onlinePlayer = Bukkit.getPlayer(player.getName());
                    if (onlinePlayer != null) {
                        messager.good(Language.COOL_OFF_MESSAGE, onlinePlayer);
                    }
                    beenMuted.remove(player);
                }
            }
        }
    }

    @Override
    public void handleCommand(OfflinePlayer player, String command) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
