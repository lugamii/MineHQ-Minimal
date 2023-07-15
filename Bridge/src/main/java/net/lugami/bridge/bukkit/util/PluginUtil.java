package net.lugami.bridge.bukkit.util;

import com.google.common.base.Joiner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.lugami.bridge.bukkit.Bridge;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;

public class PluginUtil {

    public static void enable(Plugin plugin) {
        if (plugin != null && !plugin.isEnabled()) {
            Bukkit.getPluginManager().enablePlugin(plugin);
        }
    }

    public static void enableAll() {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (!isIgnored(plugin)) {
                enable(plugin);
            }
        }
    }

    public static void disable(Plugin plugin) {
        if (plugin != null && plugin.isEnabled()) {
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public static void disableAll() {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (!isIgnored(plugin)) {
                disable(plugin);
            }
        }
    }

    public static String getFormattedName(Plugin plugin) {
        return getFormattedName(plugin, false);
    }

    public static String getFormattedName(Plugin plugin, boolean includeVersions) {
        ChatColor color = plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED;
        String pluginName = color + plugin.getName();
        if (includeVersions) {
            pluginName += " (" + plugin.getDescription().getVersion() + ")";
        }
        return pluginName;
    }

    public static Plugin getPluginByName(String[] args, int start) {
        return getPluginByName(StringUtils.join(args, ' ', start, args.length));
    }

    public static Plugin getPluginByName(String name) {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (name.equalsIgnoreCase(plugin.getName())) {
                return plugin;
            }
        }
        return null;
    }

    public static List<String> getPluginNames(boolean fullName) {
        List<String> plugins = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            plugins.add(fullName ? plugin.getDescription().getFullName() : plugin.getName());
        }
        return plugins;
    }

    public static String getPluginVersion(String name) {
        Plugin plugin = getPluginByName(name);
        if (plugin != null && plugin.getDescription() != null) {
            return plugin.getDescription().getVersion();
        }
        return null;
    }

    public static String getUsages(Plugin plugin) {

        List<String> parsedCommands = new ArrayList<>();

        Map commands = plugin.getDescription().getCommands();

        if (commands != null) {
            Iterator commandsIt = commands.entrySet().iterator();
            while (commandsIt.hasNext()) {
                Map.Entry thisEntry = (Map.Entry) commandsIt.next();
                if (thisEntry != null) {
                    parsedCommands.add((String) thisEntry.getKey());
                }
            }
        }

        if (parsedCommands.isEmpty())
            return "No commands registered.";

        return Joiner.on(", ").join(parsedCommands);

    }

    public static List<String> findByCommand(String command) {

        List<String> plugins = new ArrayList<>();

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {

            // Map of commands and their attributes.
            Map<String, Map<String, Object>> commands = plugin.getDescription().getCommands();

            if (commands != null) {

                // Iterator for all the plugin's commands.
                Iterator<Map.Entry<String, Map<String, Object>>> commandIterator = commands.entrySet().iterator();

                while (commandIterator.hasNext()) {

                    // Current value.
                    Map.Entry<String, Map<String, Object>> commandNext = commandIterator.next();

                    // Plugin name matches - return.
                    if (commandNext.getKey().equalsIgnoreCase(command)) {
                        plugins.add(plugin.getName());
                        continue;
                    }

                    // No match - let's iterate over the attributes and see if
                    // it has aliases.
                    Iterator<Map.Entry<String, Object>> attributeIterator = commandNext.getValue().entrySet().iterator();

                    while (attributeIterator.hasNext()) {

                        // Current value.
                        Map.Entry<String, Object> attributeNext = attributeIterator.next();

                        // Has an alias attribute.
                        if (attributeNext.getKey().equals("aliases")) {

                            Object aliases = attributeNext.getValue();

                            if (aliases instanceof String) {
                                if (((String) aliases).equalsIgnoreCase(command)) {
                                    plugins.add(plugin.getName());
                                    continue;
                                }
                            } else {

                                // Cast to a List of Strings.
                                List<String> array = (List<String>) aliases;

                                // Check for matches here.
                                for (String str : array) {
                                    if (str.equalsIgnoreCase(command)) {
                                        plugins.add(plugin.getName());
                                        continue;
                                    }
                                }

                            }

                        }

                    }
                }

            }

        }

        // No matches.
        return plugins;

    }

    public static boolean isIgnored(Plugin plugin) {
        return isIgnored(plugin.getName());
    }

    public static boolean isIgnored(String plugin) {
        return false;
    }

    private static String load(Plugin plugin) {
        return load(plugin.getName());
    }

    public static String load(String name) {

        Plugin target = null;

        File pluginDir = new File("plugins");

        File pluginFile = new File(pluginDir, name + (!name.endsWith(".jar") ? ".jar" : ""));

        if (!pluginFile.isFile()) {
            for (File f : pluginDir.listFiles()) {
                if (f.getName().endsWith(".jar")) {
                    try {
                        PluginDescriptionFile desc = Bridge.getInstance().getPluginLoader().getPluginDescription(f);
                        if (desc.getName().equalsIgnoreCase(name)) {
                            pluginFile = f;
                            break;
                        }
                    } catch (InvalidDescriptionException e) {
                        return ChatColor.RED + "There is no such file by the name \"" + name + "\".";
                    }
                }
            }
        }

        try {
            target = Bukkit.getPluginManager().loadPlugin(pluginFile);
        } catch (InvalidDescriptionException e) {
            e.printStackTrace();
            return ChatColor.RED + "The plugin \"" + name + "\" has an invalid description, so we cannot load.";
        } catch (InvalidPluginException e) {
            e.printStackTrace();
            return ChatColor.RED + "The plugin \"" + name + "\" is an invalid plugin, so we cannot load.";
        }

        target.onLoad();
        Bukkit.getPluginManager().enablePlugin(target);

        return ChatColor.GREEN + "Successfully loaded the plugin \"" + target.getName() + "\".";

    }

    public static void reload(Plugin plugin) {
        if (plugin != null) {
            unload(plugin);
            load(plugin);
        }
    }

    public static void reloadAll() {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (!isIgnored(plugin)) {
                reload(plugin);
            }
        }
    }

    public static String unload(Plugin plugin) {

        String name = plugin.getName();

        PluginManager pluginManager = Bukkit.getPluginManager();

        SimpleCommandMap commandMap = null;

        List<Plugin> plugins = null;

        Map<String, Plugin> names = null;
        Map<String, Command> commands = null;
        Map<Event, SortedSet<RegisteredListener>> listeners = null;

        boolean reloadlisteners = true;

        if (pluginManager != null) {

            pluginManager.disablePlugin(plugin);

            try {

                Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
                pluginsField.setAccessible(true);
                plugins = (List<Plugin>) pluginsField.get(pluginManager);

                Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
                lookupNamesField.setAccessible(true);
                names = (Map<String, Plugin>) lookupNamesField.get(pluginManager);

                try {
                    Field listenersField = Bukkit.getPluginManager().getClass().getDeclaredField("listener");
                    listenersField.setAccessible(true);
                    listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(pluginManager);
                } catch (Exception e) {
                    reloadlisteners = false;
                }

                Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);

                Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommandsField.setAccessible(true);
                commands = (Map<String, Command>) knownCommandsField.get(commandMap);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return ChatColor.RED + "Failed to unload the plugin \"" + name + "\".";
            }

        }

        pluginManager.disablePlugin(plugin);

        if (plugins != null)
            plugins.remove(plugin);

        if (names != null)
            names.remove(name);

        if (listeners != null && reloadlisteners) {
            for (SortedSet<RegisteredListener> set : listeners.values()) {
                set.removeIf(value -> value.getPlugin() == plugin);
            }
        }

        if (commandMap != null) {
            for (Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Command> entry = it.next();
                if (entry.getValue() instanceof PluginCommand) {
                    PluginCommand c = (PluginCommand) entry.getValue();
                    if (c.getPlugin() == plugin) {
                        c.unregister(commandMap);
                        it.remove();
                    }
                }
            }
        }

        // Attempt to close the classloader to unlock any handles on the plugin's jar file.
        ClassLoader cl = plugin.getClass().getClassLoader();

        if (cl instanceof URLClassLoader) {

            try {

                Field pluginField = cl.getClass().getDeclaredField("plugin");
                pluginField.setAccessible(true);
                pluginField.set(cl, null);

                Field pluginInitField = cl.getClass().getDeclaredField("pluginInit");
                pluginInitField.setAccessible(true);
                pluginInitField.set(cl, null);

            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(PluginUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {

                ((URLClassLoader) cl).close();
            } catch (IOException ex) {
                Logger.getLogger(PluginUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        // Will not work on processes started with the -XX:+DisableExplicitGC flag, but lets try it anyway.
        // This tries to get around the issue where Windows refuses to unlock jar files that were previously loaded into the JVM.
        System.gc();

        return ChatColor.GREEN + "Successfully unloaded the plugin \"" + name + "\".";

    }

}