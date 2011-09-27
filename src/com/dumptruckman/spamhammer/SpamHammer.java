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
    private int messagelimit;
    private long timeperiod;
    private int repeatMessageLimit;
    private long repeatTimePeriod;
    private boolean useMute;
    private boolean useBan;
    private boolean useKick;
    private Map<String, ArrayDeque<Long>> playerChatTimes;
    private Map<String, PlayerChatRepetition> playerRepeatedMsgs;
    private List<String> mutedPlayers;
    private Map<String, Long> actionTime;
    public List<String> beenMuted;
    public List<String> beenKicked;
    private Timer timer;

    public SpamHammer() {
        playerChatTimes = new HashMap<String, ArrayDeque<Long>>();
        playerRepeatedMsgs = new HashMap<String, PlayerChatRepetition>();
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

        messagelimit = config.getInt(MESSAGE_LIMIT.toString(), 3);
        timeperiod = config.getInt(TIME_PERIOD.toString(), 1) * 1000;
        repeatMessageLimit = config.getInt(REPEAT_MESSAGE_LIMIT.toString(), 8);
        repeatTimePeriod =  config.getInt(REPEAT_TIME_PERIOD.toString(), 40) * 1000;
        useBan = Boolean.parseBoolean(config.getString(USE_BAN.toString()));
        useKick = Boolean.parseBoolean(config.getString(USE_KICK.toString()));
        useMute = Boolean.parseBoolean(config.getString(USE_MUTE.toString()));
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
        if (config.getString(REPEAT_MESSAGE_LIMIT.toString()) == null) {
            config.setProperty(REPEAT_MESSAGE_LIMIT.toString(), REPEAT_MESSAGE_LIMIT.getDefault());
        } else {
            try {
                int temp = Integer.parseInt(config.getString(REPEAT_MESSAGE_LIMIT.toString()));
                if (temp < 0) {
                    throw new NumberFormatException("negative");
                }
            } catch (NumberFormatException nfe) {
                log.warning("[" + PLUGIN_NAME + "] Invalid setting for '" + REPEAT_MESSAGE_LIMIT.getName() + "'."
                        + "  Setting to default: " + REPEAT_MESSAGE_LIMIT.getDefault());
                config.setProperty(REPEAT_MESSAGE_LIMIT.toString(), REPEAT_MESSAGE_LIMIT.getDefault());
            }
        }
        if (config.getString(REPEAT_TIME_PERIOD.toString()) == null) {
            config.setProperty(REPEAT_TIME_PERIOD.toString(), REPEAT_TIME_PERIOD.getDefault());
        } else {
            try {
                int temp = Integer.parseInt(config.getString(REPEAT_TIME_PERIOD.toString()));
                if (temp < 0) {
                    throw new NumberFormatException("negative");
                }
            } catch (NumberFormatException nfe) {
                log.warning("[" + PLUGIN_NAME + "] Invalid setting for '" + REPEAT_TIME_PERIOD.getName() + "'."
                        + "  Setting to default: " + REPEAT_TIME_PERIOD.getDefault());
                config.setProperty(REPEAT_TIME_PERIOD.toString(), REPEAT_TIME_PERIOD.getDefault());
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
        
    	// handle fast messages
        ArrayDeque<Long> times = getPlayerChatTimes(name);
        long curtime = System.currentTimeMillis();
        times.add(curtime);
        while (times.getLast() - times.getFirst() > timeperiod) {
        	times.removeFirst();
        }
        if (times.size() >= messagelimit) {
            playerIsSpamming(name);
            isSpamming = true;
        }

        // handle repetition, if not obviously spamming already
        if (!isSpamming) {
            PlayerChatRepetition rep = getPlayerChatRepetition(name);
        	rep.addMessage(message, repeatMessageLimit, repeatTimePeriod);
        	if (rep.isSpamming()) {
        		playerIsSpamming(name);
        		isSpamming = true;
        	}
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
    
    protected ArrayDeque<Long> getPlayerChatTimes(String name) {
    	// get repetition log by player name
    	ArrayDeque<Long> times = playerChatTimes.get(name);
        
        // if it doesn't exist, create and store it
        if (times == null) {
        	times = new ArrayDeque<Long>();
        	playerChatTimes.put(name, times);
        }
        
        // return it in either case 
        return times;
    }
    
    protected PlayerChatRepetition getPlayerChatRepetition(String name) {
    	// get repetition log by player name
        PlayerChatRepetition rep = playerRepeatedMsgs.get(name);
        
        // if it doesn't exist, create and store it
        if (rep == null) {
        	rep = new PlayerChatRepetition();
        	playerRepeatedMsgs.put(name, rep);
        }
        
        // return it in either case 
        return rep;
    }
}
