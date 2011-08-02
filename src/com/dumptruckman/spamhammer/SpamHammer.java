package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.commands.SpamHammerPluginCommand;
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

import static com.dumptruckman.spamhammer.config.ConfigPath.*;

/**
 * @author dumptruckman
 */
public class SpamHammer extends JavaPlugin {

    private final static String PLUGIN_NAME = "SpamHammer";
    public static final Logger log = Logger.getLogger("Minecraft.SpamHammer");

    public final SpamHammerPlayerListener playerListener = new SpamHammerPlayerListener(this);

    public Configuration config;
    public Configuration banList;
    private int messageLimit;
    private long timePeriod;
    private int repeatLimit;
    private boolean useMute;
    private boolean useBan;
    private boolean useKick;
    public boolean useRepeatLimit;
    public boolean preventMessages;
    private Map<String, ArrayDeque<Long>> playerChatTimes;
    private Map<String, ArrayDeque<String>> playerChatHistory;
    private List<String> mutedPlayers;
    private Map<String, Long> actionTime;
    public List<String> beenMuted;
    public List<String> beenKicked;
    public List<Object> spamCommands;
    public boolean usePerms;
    private Timer timer;

    public SpamHammer() {
        playerChatTimes = new HashMap<String, ArrayDeque<Long>>();
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
        pm.registerEvent(Type.PLAYER_CHAT, playerListener, Event.Priority.High, this);
        pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Event.Priority.High, this);
        pm.registerEvent(Type.PLAYER_LOGIN, playerListener, Event.Priority.Normal, this);

        reload();

        // Register command executor for commands
        getCommand("spamunban").setExecutor(new SpamHammerPluginCommand(this));
        getCommand("spamunmute").setExecutor(new SpamHammerPluginCommand(this));
        getCommand("spamreset").setExecutor(new SpamHammerPluginCommand(this));

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

        messageLimit = config.getInt(MESSAGE_LIMIT.toString(), (Integer)MESSAGE_LIMIT.getDefault());
        repeatLimit = config.getInt(REPEAT_LIMIT.toString(), (Integer)REPEAT_LIMIT.getDefault());
        timePeriod = config.getInt(TIME_PERIOD.toString(), (Integer)TIME_PERIOD.getDefault()) * 1000;
        useRepeatLimit = Boolean.parseBoolean(config.getString(BLOCK_REPEATS.toString()));
        preventMessages = Boolean.parseBoolean(config.getString(PREVENT_MESSAGES.toString()));
        spamCommands = config.getList(INCLUDE_COMMANDS.toString());
        //for (int i = 0; i < spamCommands.size(); i++) {
        //    spamCommands.set(i, spamCommands.get(i).toString().split("\\s"));
        //}
        useBan = Boolean.parseBoolean(config.getString(USE_BAN.toString()));
        useKick = Boolean.parseBoolean(config.getString(USE_KICK.toString()));
        useMute = Boolean.parseBoolean(config.getString(USE_MUTE.toString()));
        usePerms = config.getBoolean(USE_PERMS.toString(), (Boolean)USE_PERMS.getDefault());
        config.save();
    }

    final public void setDefaults() {
        // Set/Verifies defaults
        if (config.getString(MESSAGE_LIMIT.toString()) == null) {
            config.setProperty(MESSAGE_LIMIT.toString(), MESSAGE_LIMIT.getDefault());
        } else {
            try {
                int temp = Integer.parseInt(config.getString(MESSAGE_LIMIT.toString()));
                if (temp < 0) {
                    throw new NumberFormatException("negative");
                }
            } catch (NumberFormatException nfe) {
                log.warning("[" + PLUGIN_NAME + "] Invalid setting for '" + MESSAGE_LIMIT.getName() + "'."
                        + "  Setting to default: " + MESSAGE_LIMIT.getDefault());
                config.setProperty(MESSAGE_LIMIT.toString(), MESSAGE_LIMIT.getDefault());
            }
        }
        if (config.getString(REPEAT_LIMIT.toString()) == null) {
            config.setProperty(REPEAT_LIMIT.toString(), REPEAT_LIMIT.getDefault());
        } else {
            try {
                int temp = Integer.parseInt(config.getString(REPEAT_LIMIT.toString()));
                if (temp < 0) {
                    throw new NumberFormatException("negative");
                }
            } catch (NumberFormatException nfe) {
                log.warning("[" + PLUGIN_NAME + "] Invalid setting for '" + REPEAT_LIMIT.getName() + "'."
                        + "  Setting to default: " + REPEAT_LIMIT.getDefault());
                config.setProperty(REPEAT_LIMIT.toString(), REPEAT_LIMIT.getDefault());
            }
        }
        if (config.getString(TIME_PERIOD.toString()) == null) {
            config.setProperty(TIME_PERIOD.toString(), TIME_PERIOD.getDefault());
        } else {
            try {
                int temp = Integer.parseInt(config.getString(TIME_PERIOD.toString()));
                if (temp < 0) {
                    throw new NumberFormatException("negative");
                }
            } catch (NumberFormatException nfe) {
                log.warning("[" + PLUGIN_NAME + "] Invalid setting for '" + TIME_PERIOD.getName() + "'."
                        + "  Setting to default: " + TIME_PERIOD.getDefault());
                config.setProperty(TIME_PERIOD.toString(), TIME_PERIOD.getDefault());
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
        if (config.getString(BLOCK_REPEATS.toString()) == null) {
            config.setProperty(BLOCK_REPEATS.toString(), BLOCK_REPEATS.getDefault());
        }
        if (config.getList(INCLUDE_COMMANDS.toString()) == null) {
            config.setProperty(INCLUDE_COMMANDS.toString(), INCLUDE_COMMANDS.getDefault());
        }
        if (config.getString(USE_MUTE.toString()) == null) {
            config.setProperty(USE_MUTE.toString(), USE_MUTE.getDefault());
        }
        if (config.getString(USE_KICK.toString()) == null) {
            config.setProperty(USE_KICK.toString(), USE_KICK.getDefault());
        }
        if (config.getString(USE_BAN.toString()) == null) {
            config.setProperty(USE_BAN.toString(), USE_BAN.getDefault());
        }
        if (config.getString(PREVENT_MESSAGES.toString()) == null) {
            config.setProperty(PREVENT_MESSAGES.toString(), PREVENT_MESSAGES.getDefault());
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
        if (config.getString(COOL_OFF_MESSAGE.toString()) == null) {
            config.setProperty(COOL_OFF_MESSAGE.toString(), COOL_OFF_MESSAGE.getDefault());
        }
        if (config.getString(UNMUTE_COMMAND_MESSAGE_SUCCESS.toString()) == null) {
            config.setProperty(UNMUTE_COMMAND_MESSAGE_SUCCESS.toString(), UNMUTE_COMMAND_MESSAGE_SUCCESS.getDefault());
        }
        if (config.getString(UNMUTE_COMMAND_MESSAGE_FAILURE.toString()) == null) {
            config.setProperty(UNMUTE_COMMAND_MESSAGE_FAILURE.toString(), UNMUTE_COMMAND_MESSAGE_FAILURE.getDefault());
        }
        if (config.getString(UNBAN_COMMAND_MESSAGE_SUCCESS.toString()) == null) {
            config.setProperty(UNBAN_COMMAND_MESSAGE_SUCCESS.toString(), UNBAN_COMMAND_MESSAGE_SUCCESS.getDefault());
        }
        if (config.getString(UNBAN_COMMAND_MESSAGE_FAILURE.toString()) == null) {
            config.setProperty(UNBAN_COMMAND_MESSAGE_FAILURE.toString(), UNBAN_COMMAND_MESSAGE_FAILURE.getDefault());
        }
        if (config.getString(RESET_COMMAND_MESSAGE_SUCCESS.toString()) == null) {
            config.setProperty(RESET_COMMAND_MESSAGE_SUCCESS.toString(), RESET_COMMAND_MESSAGE_SUCCESS.getDefault());
        }
        if (config.getString(RATE_LIMIT_MESSAGE.toString()) == null) {
            config.setProperty(RATE_LIMIT_MESSAGE.toString(), RATE_LIMIT_MESSAGE.getDefault());
        }
    }

    public boolean addChatMessage(String name, String message) {
        boolean isSpamming = false;

        // Detect rate limited messages
        ArrayDeque<Long> times = playerChatTimes.get(name);
        if (times == null) times = new ArrayDeque<Long>();
        long curtime = System.currentTimeMillis();
        times.add(curtime);
        if (times.size() > messageLimit) {
            times.remove();
        }
        long timediff = times.getLast() - times.getFirst();
        if (timediff > timePeriod) {
            times.clear();
            times.add(curtime);
        }
        if (times.size() == messageLimit) {
            isSpamming = true;
        }
        playerChatTimes.put(name, times);

        // Detect duplicate messages
        if (useRepeatLimit && !isSpamming) {
            ArrayDeque<String> player = playerChatHistory.get(name);
            if (player == null) player = new ArrayDeque<String>();
            player.add(message);
            if (player.size() > (repeatLimit + 1)) {
                player.remove();
            }
            playerChatHistory.put(name, player);
            isSpamming = hasDuplicateMessages(name);
        }
        
        if (isSpamming) {
            playerIsSpamming(name);
        }
        return isSpamming;
    }

    public boolean hasDuplicateMessages(String name) {
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
            isSpamming = (samecount > repeatLimit);
        }
        return isSpamming;
    }

    public void playerIsSpamming(String name) {
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

    public void mutePlayer(String name) {
        mutedPlayers.add(name);
        beenMuted.add(name);
        actionTime.put(name, System.currentTimeMillis() / 1000);
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
        actionTime.put(name, System.currentTimeMillis() / 1000);
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

    public void unBanPlayer(String name) {
        List<Object> bannedplayers = banList.getList("banned");
        if (bannedplayers == null) return;
        bannedplayers.remove(name);
        banList.setProperty("banned", bannedplayers);
        banList.save();
    }

    public void checkTimes() {
        Long time = System.currentTimeMillis() / 1000;
        for (String player : actionTime.keySet()) {
            if (isMuted(player)) {
                if (time > (actionTime.get(player) + config.getInt(MUTE_LENGTH.toString(), 0))) {
                    unMutePlayer(player);
                }
            }
            if ((time > (actionTime.get(player) + config.getInt(COOL_OFF.toString(), 300)))
                    && (config.getInt(COOL_OFF.toString(), 300) != 0)) {
                if (beenKicked(player)) beenKicked.remove(player);
                if (beenMuted(player)) {
                    if (getServer().getPlayer(player) != null) {
                        getServer().getPlayer(player).sendMessage(config.getString(COOL_OFF_MESSAGE.toString()));
                    }
                    beenMuted.remove(player);
                }
            }
        }
    }
}
