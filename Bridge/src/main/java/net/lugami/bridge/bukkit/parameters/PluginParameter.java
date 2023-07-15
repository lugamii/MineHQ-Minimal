package net.lugami.bridge.bukkit.parameters;

import net.lugami.qlib.command.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PluginParameter implements ParameterType<Plugin> {

    @Override
    public Plugin transform(CommandSender commandSender, String s) {
        Plugin plugin = Arrays.stream(Bukkit.getPluginManager().getPlugins()).filter(pluginOne -> pluginOne.getName().equalsIgnoreCase(s)).findAny().orElse(null);

        if(plugin == null) commandSender.sendMessage(ChatColor.RED + "There is no such plugin by the name \"" + s + "\".");
        return plugin;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        return Arrays.stream(Bukkit.getPluginManager().getPlugins()).filter(plugin -> StringUtils.startsWithIgnoreCase(plugin.getName(), source)).map(Plugin::getName).collect(Collectors.toList());
    }
}
