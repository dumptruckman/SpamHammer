package com.dumptruckman.spamhammer.api;

import java.util.concurrent.BlockingDeque;

public interface SpamHistory<S extends Spam> extends BlockingDeque<S> {

    String getPlayerName();

    int countSequentialDuplicates(int spamLimit, long timeLimit);
}
