/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.dumptruckman.spamhammer;

import com.dumptruckman.minecraft.pluginbase.config.BaseConfig;
import com.dumptruckman.spamhammer.PluginListener;
import com.dumptruckman.spamhammer.SpamHammerPlugin;
import com.dumptruckman.spamhammer.api.Config;
import com.dumptruckman.spamhammer.utils.TestInstanceCreator;
import junit.framework.Assert;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SpamHammerPlugin.class })
public class TestBasics {
    TestInstanceCreator creator;
    Server mockServer;
    CommandSender mockCommandSender;
    PluginListener listener;
    SpamHammerPlugin myPlugin;

    @Before
    public void setUp() throws Exception {
        creator = new TestInstanceCreator();
        assertTrue(creator.setUp());
        mockServer = creator.getServer();
        mockCommandSender = creator.getCommandSender();
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("SpamHammer");
        myPlugin = (SpamHammerPlugin) plugin;
        // Make sure plugin is not null
        assertNotNull(plugin);
        // Make sure plugin is enabled
        assertTrue(plugin.isEnabled());

        Field field = SpamHammerPlugin.class.getDeclaredField("listener");
        field.setAccessible(true);
        listener = (PluginListener) field.get(myPlugin);
    }

    @After
    public void tearDown() throws Exception {
        creator.tearDown();
    }

    @Test
    public void testEnableDebugMode() {

        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("spam");

        // Assert debug mode is off
        Assert.assertEquals(0, (int) myPlugin.config().get(BaseConfig.DEBUG_MODE));

        // Send the debug command.
        String[] cmdArgs = new String[] { "debug", "3" };
        myPlugin.onCommand(mockCommandSender, mockCommand, "", cmdArgs);

        cmdArgs = new String[] { "reload" };
        myPlugin.onCommand(mockCommandSender, mockCommand, "", cmdArgs);

        Assert.assertEquals(3, (int) myPlugin.config().get(BaseConfig.DEBUG_MODE));

        
        Assert.assertEquals(BaseConfig.LOCALE.getDefault().toString(), myPlugin.config().get(BaseConfig.LOCALE).toString());
        
        myPlugin.config().set(BaseConfig.LOCALE, Locale.CANADA);

        Assert.assertEquals(Locale.CANADA, myPlugin.config().get(BaseConfig.LOCALE));
        
        myPlugin.config().set(Config.MUTE_LENGTH, 3);
        myPlugin.config().save();
        
        Assert.assertEquals(myPlugin.config().get(Config.MUTE_LENGTH), Integer.valueOf(3));
        
        Player player = mockServer.getPlayer("dumptruckman");

        listener.onPlayerCommandPreprocess(new PlayerCommandPreprocessEvent(player, "/g hello"));
        listener.onPlayerChat(new PlayerChatEvent(player, "hello"));
        listener.onPlayerChat(new PlayerChatEvent(player, "hello"));
        listener.onPlayerCommandPreprocess(new PlayerCommandPreprocessEvent(player, "/g hello"));

        cmdArgs = new String[] { "unmute", "dumptruckman" };
        myPlugin.onCommand(mockCommandSender, mockCommand, "", cmdArgs);

        listener.onPlayerChat(new PlayerChatEvent(player, "hello"));
        sleep(1002);
        listener.onPlayerChat(new PlayerChatEvent(player, "hello"));
        sleep(1002);
        listener.onPlayerChat(new PlayerChatEvent(player, "hello"));
        sleep(1002);
        listener.onPlayerChat(new PlayerChatEvent(player, "hello"));

        sleep(3000);
        try {
            Field field = DefaultSpamHandler.class.getDeclaredField("config");
            field.setAccessible(true);
            field.set(myPlugin.getSpamHandler(), myPlugin.config());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        ((DefaultSpamHandler) myPlugin.getSpamHandler()).checkTimes();
        
        listener.onPlayerChat(new PlayerChatEvent(player, "hello"));
        listener.onPlayerChat(new PlayerChatEvent(player, "hello"));
        listener.onPlayerChat(new PlayerChatEvent(player, "hello"));
        listener.onPlayerChat(new PlayerChatEvent(player, "hello"));

        try { 
            Field field = DefaultSpamHandler.class.getDeclaredField("beenKicked");
            field.setAccessible(true);
            List<OfflinePlayer> kickedPlayers = (List<OfflinePlayer>) field.get(myPlugin.getSpamHandler());
            Assert.assertTrue(kickedPlayers.contains(player));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public void sleep(int millis) {
        Object lock = new Object();
        synchronized (lock) {
            try {
                lock.wait(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
