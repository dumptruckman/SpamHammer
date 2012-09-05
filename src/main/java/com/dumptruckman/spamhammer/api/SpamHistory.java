package com.dumptruckman.spamhammer.api;

import java.util.concurrent.BlockingDeque;

public interface SpamHistory<S extends Spam> extends BlockingDeque<S> {

    String getPlayerName();

    // TODO Fix this method, it's not gonna work.
    boolean areDuplicates(int history);
}
