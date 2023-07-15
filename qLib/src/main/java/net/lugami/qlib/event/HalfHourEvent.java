package net.lugami.qlib.event;

import java.beans.ConstructorProperties;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HalfHourEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final int hour;
    private final int minute;

    public HandlerList getHandlers() {
        return handlerList;
    }

    @ConstructorProperties(value={"hour", "minute"})
    public HalfHourEvent(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
    }
}

