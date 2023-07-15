package net.lugami.practice.party.event;

import net.lugami.practice.party.PartyHandler;
import net.lugami.practice.party.commands.PartyCreateCommand;
import net.lugami.practice.party.Party;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import lombok.Getter;

/**
 * Called when a {@link Party} is created.
 * @see PartyCreateCommand
 * @see PartyHandler#getOrCreateParty(Player)
 */
public final class PartyCreateEvent extends PartyEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public PartyCreateEvent(Party party) {
        super(party);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}