package net.lugami.qlib.event;

import java.beans.ConstructorProperties;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HourEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final int hour;

    public HandlerList getHandlers() {
        return handlerList;
    }

    @ConstructorProperties(value={"hour"})
    public HourEvent(int hour) {
        this.hour = hour;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public int getHour() {
        return this.hour;
    }
}

