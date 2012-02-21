package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.api.BanData;
import com.dumptruckman.spamhammer.api.Config;
import com.dumptruckman.spamhammer.api.SpamHammer;
import com.dumptruckman.spamhammer.command.SpamHammerPluginCommand;
import com.dumptruckman.spamhammer.listeners.SpamHammerPlayerListener;
import com.dumptruckman.spamhammer.util.CommentedConfig;
import com.dumptruckman.spamhammer.util.Logging;
import com.dumptruckman.spamhammer.util.Perm;
import com.dumptruckman.spamhammer.util.PermHandler;
import com.dumptruckman.spamhammer.util.YamlBanData;
import com.dumptruckman.spamhammer.util.locale.Messager;
import com.dumptruckman.spamhammer.util.locale.SimpleMessager;
import com.pneumaticraft.commandhandler.CommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author dumptruckman
 */
public class SpamHammerPlugin extends JavaPlugin implements SpamHammer {

    private Config config;
    private BanData data;
    private CommandHandler commandHandler;
    private Messager messager = new SimpleMessager(this);
    private File serverFolder = new File(System.getProperty("user.dir"));

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

    public SpamHammerPlugin() {
        playerChatTimes = new HashMap<String, ArrayDeque<Long>>();
        playerChatHistory = new HashMap<String, ArrayDeque<String>>();
        mutedPlayers = new ArrayList<String>();
        beenMuted = new ArrayList<String>();
        beenKicked = new ArrayList<String>();
        actionTime = new HashMap<String, Long>();
    }

    final public void onDisable() {
        Logging.info("disabled.", true);
    }

    final public void onEnable() {
        Logging.init(this);
        Perm.register(this);

        this.commandHandler = new CommandHandler(this, new PermHandler());

        try {
            this.getMessager().setLocale(new Locale(this.getSettings().getLocale()));
        } catch (IllegalArgumentException e) {
            Logging.severe(e.getMessage());
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.reloadConfig();

        getServer().getPluginManager().registerEvents(new SpamHammerPlayerListener(this), this);

        // Register Commands
        this.registerCommands();

        // Display enable message/version info
        Logging.info("enabled.", true);


        // Register command executor for command
        getCommand("spamunban").setExecutor(new SpamHammerPluginCommand(this));
        getCommand("spamunmute").setExecutor(new SpamHammerPluginCommand(this));
        getCommand("spamreset").setExecutor(new SpamHammerPluginCommand(this));

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SpamHammerPlugin.this.checkTimes();
            }
        }, 0, 1000);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandHandler getCommandHandler() {
        return this.commandHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Config getSettings() {
        if (this.config == null) {
            // Loads the configuration
            try {
                this.config = new CommentedConfig(this);
                Logging.fine("Loaded config file!");
            } catch (Exception e) {  // Catch errors loading the config file and exit out if found.
                Logging.severe("Error loading config file!");
                Logging.severe(e.getMessage());
                Bukkit.getPluginManager().disablePlugin(this);
                return null;
            }
        }
        return this.config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BanData getData() {
        if (this.data == null) {
            // Loads the data
            try {
                this.data = new YamlBanData(this);
            } catch (IOException e) {  // Catch errors loading the language file and exit out if found.
                Logging.severe("Error loading data file!");
                Logging.severe(e.getMessage());
                Bukkit.getPluginManager().disablePlugin(this);
                return null;
            }
        }
        return this.data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Messager getMessager() {
        return messager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMessager(Messager messager) {
        if (messager == null)
            throw new IllegalArgumentException("The new messager can't be null!");

        this.messager = messager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getServerFolder() {
        return serverFolder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServerFolder(File newServerFolder) {
        if (!newServerFolder.isDirectory())
            throw new IllegalArgumentException("That's not a folder!");

        this.serverFolder = newServerFolder;
    }

    /**
     * Nulls the config object and reloads a new one.
     */
    @Override
    public void reloadConfig() {
        this.config = null;

        // Do any import first run stuff here.
        if (this.getSettings().isFirstRun()) {
            Logging.info("First run!");
        }
    }

    private void registerCommands() {
        this.commandHandler.registerCommand(new DebugCommand(this));
        this.commandHandler.registerCommand(new ReloadCommand(this));
    }

    final public void reload() {
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
