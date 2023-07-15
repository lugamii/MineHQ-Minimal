package net.lugami.qlib.protocol.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerLaggedOutEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    public ServerLaggedOutEvent(int averagePing) {
        super(true);
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

