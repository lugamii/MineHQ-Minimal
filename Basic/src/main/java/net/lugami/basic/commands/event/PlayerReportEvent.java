package net.lugami.basic.commands.event;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class PlayerReportEvent extends PlayerEvent {

    private static HandlerList handlerList = new HandlerList();
    private Player target;

    public PlayerReportEvent(Player player, Player target) {
        super(player);
        this.target = Preconditions.checkNotNull(target);
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Player getTarget() {
        return this.target;
    }
}

