package net.lugami.bridge.global.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Msg {

    public static void logConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
