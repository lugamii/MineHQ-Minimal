package net.lugami.bridge.bukkit.util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

public class Chat {

    public static String LINE = "--------------------------------";
    public static ChatColor ITALIC = ChatColor.ITALIC;
    public static ChatColor BOLD = ChatColor.BOLD;
    public static ChatColor LIGHT_GREEN = ChatColor.GREEN;
    public static ChatColor LIGHT_BLUE = ChatColor.AQUA;
    public static ChatColor LIGHT_RED = ChatColor.RED;
    public static ChatColor PINK = ChatColor.LIGHT_PURPLE;
    public static ChatColor YELLOW = ChatColor.YELLOW;
    public static ChatColor WHITE = ChatColor.WHITE;
    public static ChatColor RESET = ChatColor.RESET;
    public static ChatColor OBFUSCATION = ChatColor.MAGIC;
    public static ChatColor STRIKETHROUGH = ChatColor.STRIKETHROUGH;
    public static ChatColor UNDERLINE = ChatColor.UNDERLINE;
    public static ChatColor DARK_BLUE = ChatColor.DARK_BLUE;
    public static ChatColor DARK_GREEN = ChatColor.DARK_GREEN;
    public static ChatColor CYAN = ChatColor.DARK_AQUA;
    public static ChatColor DARK_RED = ChatColor.DARK_RED;
    public static ChatColor PURPLE = ChatColor.DARK_PURPLE;
    public static ChatColor ORANGE = ChatColor.GOLD;
    public static ChatColor LIGHT_GRAY = ChatColor.GRAY;
    public static ChatColor DARK_GRAY = ChatColor.DARK_GRAY;
    public static ChatColor INDIGO = ChatColor.BLUE;

    public static String format(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String unformat(String message){
        return ChatColor.stripColor(message);
    }

    public static String repeat(String str, int length){
        return StringUtils.repeat(str, length);
    }

}
