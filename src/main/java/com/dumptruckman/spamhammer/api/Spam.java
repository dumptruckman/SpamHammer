package com.dumptruckman.spamhammer.api;

public interface Spam {

    long getTime();

    String getPlayerName();

    boolean isDuplicate(Spam spam);
}
