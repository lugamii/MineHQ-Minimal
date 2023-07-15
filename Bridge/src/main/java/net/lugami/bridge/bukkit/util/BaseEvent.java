package net.lugami.bridge.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BaseEvent extends Event
{
    private static final HandlerList handlers;

    public HandlerList getHandlers() {
        return BaseEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return BaseEvent.handlers;
    }

    public void call() {
        Bukkit.getServer().getPluginManager().callEvent(this);
    }

    static {
        handlers = new HandlerList();
    }
}
