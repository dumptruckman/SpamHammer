package com.dumptruckman.spamhammer.api;

import com.dumptruckman.minecraft.pluginbase.config.AbstractYamlConfig;
import org.bukkit.event.player.PlayerEvent;

import java.io.File;
import java.io.IOException;

public abstract class AbstractSpamHandler<T extends PlayerEvent> extends AbstractYamlConfig implements SpamHandler<T> {


    public AbstractSpamHandler(SpamHammer plugin, String name, Class configClass) throws IOException {
        super(plugin, true, true, new File(plugin.getDataFolder(), name + "-settings.yml"), configClass);
    }
}
