package com.dumptruckman.spamhammer.api;

import com.dumptruckman.minecraft.pluginbase.config.AbstractYamlConfig;
import org.bukkit.event.player.PlayerEvent;

import java.io.File;
import java.io.IOException;

public abstract class AbstractSpam<T extends PlayerEvent> extends AbstractYamlConfig implements Spam<T> {


    public AbstractSpam(SpamHammer plugin, String name, Class configClass) throws IOException {
        super(plugin, false, true, new File(plugin.getDataFolder(), name + "-settings.yml"), configClass);
    }
}
