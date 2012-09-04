package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.api.ChatSpam;

class DefaultChatSpam extends DefaultSpam implements ChatSpam {

    private final String message;

    public DefaultChatSpam(String playerName, String message) {
        super(playerName);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
