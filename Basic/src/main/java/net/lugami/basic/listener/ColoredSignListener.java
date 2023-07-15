package net.lugami.basic.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class ColoredSignListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("basic.coloredsigns")) {
            return;
        }
        for (int i = 0; i < event.getLines().length; ++i) {
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLines()[i]).trim());
        }
    }
}

