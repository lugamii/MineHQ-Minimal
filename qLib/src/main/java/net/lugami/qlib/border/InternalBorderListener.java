package net.lugami.qlib.border;

import net.lugami.qlib.border.event.BorderChangeEvent;
import net.lugami.qlib.border.event.PlayerEnterBorderEvent;
import net.lugami.qlib.border.event.PlayerExitBorderEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class InternalBorderListener implements Listener {

    @EventHandler
    public void onBorderChange(BorderChangeEvent event) {
        event.getBorder().getBorderConfiguration().getDefaultBorderChangeActions().forEach(c -> c.accept(event));
    }

    @EventHandler
    public void onBorderExit(PlayerExitBorderEvent event) {
        event.getBorder().getBorderConfiguration().getDefaultBorderExitActions().forEach(c -> c.accept(event));
    }

    @EventHandler
    public void onBorderEnter(PlayerEnterBorderEvent event) {
        event.getBorder().getBorderConfiguration().getDefaultBorderEnterActions().forEach(c -> c.accept(event));
    }
}

