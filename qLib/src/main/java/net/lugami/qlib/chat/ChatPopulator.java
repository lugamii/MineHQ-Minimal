package net.lugami.qlib.chat;

import net.lugami.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.beans.ConstructorProperties;
import java.util.HashSet;

public abstract class ChatPopulator {


    private final JavaPlugin javaPlugin;
    private final String name;
    private final int order;

    public abstract String getCommandParam();
    public abstract char getChatChar();

    public abstract String layout(Player player, String message);
    public abstract void handleChat(Player player, String message);

    @ConstructorProperties(value={"name"})
    public ChatPopulator(JavaPlugin javaPlugin, String name, int order) {
        this.javaPlugin = javaPlugin;
        this.name = name;
        this.order = order;
    }

    public JavaPlugin getJavaPlugin() {
        return this.javaPlugin;
    }

    public String getName() {
        return this.name;
    }

    public int getOrder() {
        return this.order;
    }

    public String checkMessage(Player player, String message, boolean allowColor) {
        ChatPlayer chatPlayer = ChatHandler.getChatPlayer(player.getUniqueId());
        Bukkit.getPluginManager().callEvent(new AsyncPlayerChatEvent(false, player, message, new HashSet<>()));

        String msg = message;
        if(message.startsWith("" + getChatChar())) msg = message.substring(1).trim();
        msg = (!allowColor ? ChatColor.stripColor(msg) : msg);
        if(msg.isEmpty() || msg.equals("")) {
            player.sendMessage(ChatColor.RED + "Please supply a message.");
            return null;
        }

        if(chatPlayer.isChatCancelled()) {
            player.sendMessage(ChatColor.RED + "You can't send a message right now.");
            chatPlayer.setChatCancelled(false);
            return null;
        }
        if(msg != null) Bukkit.getConsoleSender().sendMessage(layout(player, message)); // Log the messages to console, ty!
        return msg;
    }

    public String checkMessage(Player player, String message) {
        return checkMessage(player, message, true);
    }

    public static class PublicChatProvider extends ChatPopulator {

        public PublicChatProvider() {
            super(qLib.getInstance(), "Public", 0);
        }

        public PublicChatProvider(JavaPlugin javaPlugin, String name, int order) {
            super(javaPlugin, name, order);
        }

        @Override
        public String getCommandParam() {
            return "/pc [message]";
        }

        @Override
        public char getChatChar() {
            return '!';
        }

        @Override
        public String layout(Player player, String message) {
            String rankPrefix = player.hasMetadata("RankPrefix") ? player.getMetadata("RankPrefix").get(0).asString() : "";
            String prefix = player.hasMetadata("prefix") ? player.getMetadata("prefix").get(0).asString() : "";
            return prefix + rankPrefix + player.getDisplayName() + ChatColor.WHITE + ": " + (player.hasPermission("basic.staff") ? ChatColor.translateAlternateColorCodes('&', message) : message);
        }

        @Override
        public void handleChat(Player player, String message) {
            String msg = checkMessage(player, message);
            if(msg == null) return;
            Bukkit.getOnlinePlayers().forEach(player1 -> player1.sendMessage(layout(player, msg)));
        }

    }

}
