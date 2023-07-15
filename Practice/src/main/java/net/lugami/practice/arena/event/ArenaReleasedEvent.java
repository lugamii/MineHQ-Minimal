package net.lugami.practice.arena.event;

import lombok.Getter;
import net.lugami.practice.arena.Arena;
import org.bukkit.event.HandlerList;
import net.lugami.practice.match.Match;

/**
 * Called when an {@link Arena} is done being used by a
 * {@link Match}
 */
public final class ArenaReleasedEvent extends ArenaEvent {

    @Getter
    private static HandlerList handlerList = new HandlerList();

    public ArenaReleasedEvent(Arena arena) {
        super(arena);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}