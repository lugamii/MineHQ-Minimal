package net.lugami.basic.listener;

import net.lugami.basic.commands.GlintCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class GlintListener implements Listener {

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        GlintCommand.load(event.getUniqueId());
    }
}

