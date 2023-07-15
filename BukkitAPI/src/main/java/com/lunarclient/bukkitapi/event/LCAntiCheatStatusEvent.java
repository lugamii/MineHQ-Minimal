package com.lunarclient.bukkitapi.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class LCAntiCheatStatusEvent extends PlayerEvent {

    @Getter private static final HandlerList handlerList = new HandlerList();

    @Getter private final Status status;

    public LCAntiCheatStatusEvent(Player who, Status status) {
        super(who);
        this.status = status;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public enum Status {

        PROTECTED, UNPROTECTED

    }

}