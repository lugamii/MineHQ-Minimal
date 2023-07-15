package net.lugami.qlib.util;

import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.EntityType;

import java.util.*;

@NoArgsConstructor
public final class ChatUtils {

    private static final List<ChatColor> chatColor = new ArrayList<>();
    private static int currentFakeEntityId = -1;


    public static ChatColor randomChatColor() {
        return chatColor.get(new Random().nextInt(chatColor.size()));

    }


    static {
        chatColor.add(ChatColor.BLUE);
        chatColor.add(ChatColor.AQUA);
        chatColor.add(ChatColor.DARK_AQUA);
        chatColor.add(ChatColor.WHITE);
        chatColor.add(ChatColor.LIGHT_PURPLE);
        chatColor.add(ChatColor.DARK_PURPLE);
        chatColor.add(ChatColor.GRAY);
        chatColor.add(ChatColor.DARK_GREEN);
        chatColor.add(ChatColor.DARK_RED);
        chatColor.add(ChatColor.RED);
        chatColor.add(ChatColor.GREEN);
        chatColor.add(ChatColor.YELLOW);
    }
}

