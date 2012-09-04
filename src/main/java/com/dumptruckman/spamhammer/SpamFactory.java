package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.api.ChatSpam;
import com.dumptruckman.spamhammer.api.Spam;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class SpamFactory {

    private static final Map<String, BlockingDeque<Spam>> totalSpam = new HashMap<String, BlockingDeque<Spam>>();

    private static BlockingDeque<Spam> getPlayerSpam(String name) {
        BlockingDeque<Spam> playerSpam = totalSpam.get(name);
        if (playerSpam == null) {
            playerSpam = new LinkedBlockingDeque<Spam>();
            totalSpam.put(name, playerSpam);
        }
        return playerSpam;
    }
    public static ChatSpam newChatSpam(String playerName, String message) {
        ChatSpam spam = new DefaultChatSpam(playerName, message);
        getPlayerSpam(playerName).add(spam);
        return spam;
    }
}
