package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.api.ChatSpam;
import com.dumptruckman.spamhammer.api.Spam;

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

    @Override
    public boolean isDuplicate(Spam spam) {
        return spam instanceof ChatSpam && ((ChatSpam) spam).getMessage().equals(getMessage());
    }
}
