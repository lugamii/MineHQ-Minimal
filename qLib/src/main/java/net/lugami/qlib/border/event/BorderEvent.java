package net.lugami.qlib.border.event;

import net.lugami.qlib.border.Border;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BorderEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final Border border;

    public BorderEvent(Border border) {
        this.border = border;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public Border getBorder() {
        return this.border;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

