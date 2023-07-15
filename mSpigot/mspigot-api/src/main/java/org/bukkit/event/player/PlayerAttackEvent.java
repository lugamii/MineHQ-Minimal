package org.bukkit.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class PlayerAttackEvent extends PlayerEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    @Getter private final Entity target;

    public PlayerAttackEvent(Player player, Entity target) {
        super(player);

        this.target = target;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
