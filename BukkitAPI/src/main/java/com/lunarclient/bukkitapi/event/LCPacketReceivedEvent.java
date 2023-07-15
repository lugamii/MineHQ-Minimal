package com.lunarclient.bukkitapi.event;

import com.lunarclient.bukkitapi.nethandler.LCPacket;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class LCPacketReceivedEvent extends PlayerEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    @Getter private final LCPacket packet;

    public LCPacketReceivedEvent(Player player, LCPacket packet) {
        super(player);
        this.packet = packet;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}