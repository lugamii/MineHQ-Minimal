package net.lugami.bridge.bungee.listener;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;

public class GeneralBungeeListener {

    public static void logMessages(String msg) {
        BungeeCord.getInstance().getLogger().info(ChatColor.translateAlternateColorCodes('&', msg));
    }


}
