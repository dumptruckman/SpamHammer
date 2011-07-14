package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.listeners.SpamHammerPlayerListener;
import com.dumptruckman.util.io.ConfigIO;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

import static com.dumptruckman.spamhammer.Config.ConfigPath.*;

/**
 * @author dumptruckman
 */
public class SpamHammer extends JavaPlugin {

    private final static String PLUGIN_NAME = "SpamHammer";
    public static final Logger log = Logger.getLogger("Minecraft.SpamHammer");


    public Configuration config;
    public Configuration banList;
    private int limit;
    private Map<String, ArrayDeque<String>> playerChatHistory;
    private List<String> mutedPlayers;
    private Map<String, Long> actionTime;
    private List<String> beenMuted;
    private List<String> beenKicked;
    private Timer timer;

    public SpamHammer() {
        playerChatHistory = new HashMap<String, ArrayDeque<String>>();
        mutedPlayers = new ArrayList<String>();
        beenMuted = new ArrayList<String>();
        beenKicked = new ArrayList<String>();
        actionTime = new HashMap<String, Long>();
    }

    final public void onEnable() {
        // Make the data folders that dChest uses
        getDataFolder().mkdirs();

        // Grab the PluginManager
        final PluginManager pm = getServer().getPluginManager();

        // Register event
        pm.registerEvent(Type.PLAYER_CHAT, new SpamHammerPlayerListener(this), Event.Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_LOGIN, new SpamHammerPlayerListener(this), Event.Priority.Normal, this);

        reload();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SpamHammer.this.checkTimes();
            }
        }, 0, 1000);

        // Display enable message/version info
        log.info(PLUGIN_NAME + " " + getDescription().getVersion() + " enabled.");
    }

    final public void onDisable() {
        log.info(PLUGIN_NAME + " " + getDescription().getVersion() + " disabled.");
    }

    final public void reload() {
        config = new ConfigIO(new File(this.getDataFolder(), "config.yml")).load();
        banList = new ConfigIO(new File(this.getDataFolder(), "banlist.yml")).load();
        setDefaults();

        limit = config.getInt(MUTE_LIMIT.toString(), 0);
        if (limit < config.getInt(KICK_LIMIT.toString(), 0)) {
            limit = config.getInt(KICK_LIMIT.toString(), 0);
        }
        if (limit < config.getInt(BAN_LIMIT.toString(), 0)) {
            limit = config.getInt(BAN_LIMIT.toString(), 0);
        }
        config.save();
    }

    final public void setDefaults() {
        // Set/Verifies defaults
        if (config.getString(MUTE_LIMIT.toString()) == null) {
            config.setProperty(MUTE_LIMIT.toString(), MUTE_LIMIT.getDefault());
        } else {
            try {
                int temp = Integer.parseInt(config.getString(MUTE_LIMIT.toString()));
                if (temp < 0) {
                    throw new NumberFormatException("negative");
                }
            } catch (NumberFormatException nfe) {
                log.warning("[" + PLUGIN_NAME + "] Invalid setting for '" + MUTE_LIMIT.getName() + "'."
                        + "  Setting to default: " + MUTE_LIMIT.getDefault());
                config.setProperty(MUTE_LIMIT.toString(), MUTE_LIMIT.getDefault());
            }
        }
        if (config.getString(MUTE_LENGTH.toString()) == null) {
            config.setProperty(MUTE_LENGTH.toString(), MUTE_LENGTH.getDefault());
        } else {
            try {
                int temp = Integer.parseInt(config.getString(MUTE_LENGTH.toString()));
                if (temp < 0) {
                    throw new NumberFormatException("negative");
                }
            } catch (NumberFormatException nfe) {
                log.warning("[" + PLUGIN_NAME + "] Invalid setting for '" + MUTE_LENGTH.getName() + "'."
                        + "  Setting to default: " + MUTE_LENGTH.getDefault());
                config.setProperty(MUTE_LENGTH.toString(), MUTE_LENGTH.getDefault());
            }
        }
        if (config.getString(KICK_LIMIT.toString()) == null) {
            config.setProperty(KICK_LIMIT.toString(), KICK_LIMIT.getDefault());
        } else {
            try {
                int temp = Integer.parseInt(config.getString(KICK_LIMIT.toString()));
                if (temp < 0) {
                    throw new NumberFormatException("negative");
                }
            } catch (NumberFormatException nfe) {
                log.warning("[" + PLUGIN_NAME + "] Invalid setting for '" + KICK_LIMIT.getName() + "'."
                        + "  Setting to default: " + KICK_LIMIT.getDefault());
                config.setProperty(KICK_LIMIT.toString(), KICK_LIMIT.getDefault());
            }
        }
        if (config.getString(BAN_LIMIT.toString()) == null) {
            config.setProperty(BAN_LIMIT.toString(), BAN_LIMIT.getDefault());
        } else {
            try {
                int temp = Integer.parseInt(config.getString(BAN_LIMIT.toString()));
                if (temp < 0) {
                    throw new NumberFormatException("negative");
                }
            } catch (NumberFormatException nfe) {
                log.warning("[" + PLUGIN_NAME + "] Invalid setting for '" + BAN_LIMIT.getName() + "'."
                        + "  Setting to default: " + BAN_LIMIT.getDefault());
                config.setProperty(BAN_LIMIT.toString(), BAN_LIMIT.getDefault());
            }
        }
        if (config.getString(COOL_OFF.toString()) == null) {
            config.setProperty(COOL_OFF.toString(), COOL_OFF.getDefault());
        } else {
            try {
                int temp = Integer.parseInt(config.getString(COOL_OFF.toString()));
                if (temp < 0) {
                    throw new NumberFormatException("negative");
                }
            } catch (NumberFormatException nfe) {
                log.warning("[" + PLUGIN_NAME + "] Invalid setting for '" + COOL_OFF.getName() + "'."
                        + "  Setting to default: " + COOL_OFF.getDefault());
                config.setProperty(COOL_OFF.toString(), COOL_OFF.getDefault());
            }
        }
        if (config.getString(MUTE_MESSAGE.toString()) == null) {
            config.setProperty(MUTE_MESSAGE.toString(), MUTE_MESSAGE.getDefault());
        }
        if (config.getString(UNMUTE_MESSAGE.toString()) == null) {
            config.setProperty(UNMUTE_MESSAGE.toString(), UNMUTE_MESSAGE.getDefault());
        }
        if (config.getString(MUTED_MESSAGE.toString()) == null) {
            config.setProperty(MUTED_MESSAGE.toString(), MUTED_MESSAGE.getDefault());
        }
        if (config.getString(KICK_MESSAGE.toString()) == null) {
            config.setProperty(KICK_MESSAGE.toString(), KICK_MESSAGE.getDefault());
        }
        if (config.getString(BAN_MESSAGE.toString()) == null) {
            config.setProperty(BAN_MESSAGE.toString(), BAN_MESSAGE.getDefault());
        }
    }

    public void addChatMessage(String name, String message) {
        ArrayDeque<String> player = playerChatHistory.get(name);
        if (player == null) player = new ArrayDeque<String>();
        player.add(message);
        if (player.size() > limit) {
            player.remove();
        }
        playerChatHistory.put(name, player);
    }

    public boolean isMuted(String name) {
        return mutedPlayers.contains(name);
    }

    public boolean isBanned(String name) {
        List<Object> bannedplayers = banList.getList("banned");
        if (bannedplayers == null) return false;
        return bannedplayers.contains(name);
    }

    public boolean beenMuted(String name) {
        return beenMuted.contains(name);
    }

    public boolean beenKicked(String name) {
        return beenKicked.contains(name);
    }

    public void checkSpam(String name) {
        int samecount = 1;
        int mute = config.getInt(MUTE_LIMIT.toString(), 0);
        int kick = config.getInt(KICK_LIMIT.toString(), 0);
        int ban = config.getInt(BAN_LIMIT.toString(), 0);
        String lastMessage = null;
        for (Object m : playerChatHistory.get(name).toArray()) {
            String message = m.toString();
            if (lastMessage == null) {
                lastMessage = message;
            } else if (message.equals(lastMessage)) {
                samecount++;
            } else {
                playerChatHistory.get(name).clear();
                addChatMessage(name, message);
                break;
            }
            if (mute != 0 && !beenMuted(name) && samecount >= mute) {
                mutePlayer(name);
                playerChatHistory.get(name).clear();
                addChatMessage(name, message);
                break;
            } else if (mute != 0 && !beenMuted(name)) {
                break;
            }
            if (kick != 0 && !beenKicked(name) && samecount >= kick) {
                kickPlayer(name);
                playerChatHistory.get(name).clear();
                addChatMessage(name, message);
                break;
            } else if (kick != 0 && !beenKicked(name)) {
                break;
            }
            if (ban != 0 && samecount >= ban) {
                banPlayer(name);
                playerChatHistory.get(name).clear();
                addChatMessage(name, message);
                break;
            }
        }
    }

    public void mutePlayer(String name) {
        mutedPlayers.add(name);
        beenMuted.add(name);
        actionTime.put(name, new Date().getTime() / 1000);
        String message = config.getString(MUTE_MESSAGE.toString());
        message = message.replace("%t", config.getString(MUTE_LENGTH.toString()));
        getServer().getPlayer(name).sendMessage(message);
    }

    public void unMutePlayer(String name) {
        mutedPlayers.remove(name);
        if (getServer().getPlayer(name) != null) {
            getServer().getPlayer(name).sendMessage(config.getString(UNMUTE_MESSAGE.toString()));
        }
    }

    public void kickPlayer(String name) {
        beenKicked.add(name);
        actionTime.put(name, new Date().getTime() / 1000);
        if (getServer().getPlayer(name) != null) {
            getServer().getPlayer(name).kickPlayer(config.getString(KICK_MESSAGE.toString()));
        }
    }

    public void banPlayer(String name) {
        if (getServer().getPlayer(name) != null) {
            getServer().getPlayer(name).kickPlayer(config.getString(BAN_MESSAGE.toString()));
        }
        List<Object> bannedplayers = banList.getList("banned");
        if (bannedplayers == null) bannedplayers = new ArrayList<Object>();
        bannedplayers.add(name);
        banList.setProperty("banned", bannedplayers);
        banList.save();
    }

    public void checkTimes() {
        Long time = new Date().getTime() / 1000;
        for (String player : actionTime.keySet()) {
            if (isMuted(player)) {
                if (time > (actionTime.get(player) + config.getInt(MUTE_LENGTH.toString(), 0))) {
                    unMutePlayer(player);
                }
            }
            if ((time > (actionTime.get(player) + config.getInt(COOL_OFF.toString(), 300)))
                    && (config.getInt(COOL_OFF.toString(), 300) != 0)) {
                unMutePlayer(player);
            }
        }
    }
}
