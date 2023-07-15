package com.lunarclient.bukkitapi.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called whenever a player unregisters the LC plugin channel
 */
public final class LCPlayerUnregisterEvent extends PlayerEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public LCPlayerUnregisterEvent(Player player) {
        super(player);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}