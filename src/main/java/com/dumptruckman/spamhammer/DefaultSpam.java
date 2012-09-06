package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.api.Spam;

abstract class DefaultSpam implements Spam {

    private final String playerName;
    private final long time;

    public DefaultSpam(String playerName) {
        this.playerName = playerName;
        this.time = System.currentTimeMillis();
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public boolean isDuplicate(Spam spam) {
        return spam.getPlayerName().equals(getPlayerName());
    }
}
