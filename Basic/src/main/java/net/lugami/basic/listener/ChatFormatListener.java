package net.lugami.basic.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.metadata.MetadataValue;

public final class ChatFormatListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("RankPrefix")) {
            String prefix = (player.getMetadata("RankPrefix").get(0)).asString();
            event.setFormat(prefix + "%s: %s");
        } else {
            event.setFormat("%s: %s");
        }
    }
}

