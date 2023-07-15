package net.lugami.basic.commands.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class PlayerRequestReportEvent extends PlayerEvent implements Cancellable {

    private static HandlerList handlerList = new HandlerList();
    private boolean cancelled;
    private String cancelledMessage;

    public PlayerRequestReportEvent(Player player) {
        super(player);
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getCancelledMessage() {
        return this.cancelledMessage;
    }

    public void setCancelledMessage(String cancelledMessage) {
        this.cancelledMessage = cancelledMessage;
    }
}

