package net.lugami.practice.chat.listener;

import net.lugami.qlib.util.TaskUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.lugami.practice.chat.ChatManager;

public class ChatListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        TaskUtil.run(() -> {
            ChatManager.refresh(player);
        } );

    }

}
