package com.dumptruckman.spamhammer.api;

import com.dumptruckman.spamhammer.util.locale.Messaging;
import com.pneumaticraft.commandhandler.CommandHandler;
import org.bukkit.plugin.Plugin;

import java.io.File;

public interface SpamHammer extends Plugin, Messaging {

    /**
     * @return the CommandHandler instance for this plugin.
     */
    CommandHandler getCommandHandler();

    /**
     * @return the Config object which contains settings for this plugin.
     */
    public Config getSettings();

    /**
     * @return the BanData object which contains data for this plugin.
     */
    public BanData getData();

    /**
     * Gets the server's root-folder as {@link java.io.File}.
     *
     * @return The server's root-folder
     */
    public File getServerFolder();

    /**
     * Sets this server's root-folder.
     *
     * @param newServerFolder The new server-root
     */
    public void setServerFolder(File newServerFolder);
}
