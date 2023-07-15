package com.lunarclient.bukkitapi.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called whenever a player registers the LC plugin channel
 */
public final class LCPlayerRegisterEvent extends PlayerEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public LCPlayerRegisterEvent(Player player) {
        super(player);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}