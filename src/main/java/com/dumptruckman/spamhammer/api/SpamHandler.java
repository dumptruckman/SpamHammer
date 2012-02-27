package com.dumptruckman.spamhammer.api;

import org.bukkit.OfflinePlayer;

public interface SpamHandler {

    boolean handleChat(OfflinePlayer player, String message);

    void handleCommand(OfflinePlayer player, String command);
    
    boolean isMuted(OfflinePlayer player);
}
