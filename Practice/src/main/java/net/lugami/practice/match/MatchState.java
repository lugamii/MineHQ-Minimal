package net.lugami.practice.match;

import net.lugami.practice.match.event.MatchCountdownStartEvent;
import net.lugami.practice.match.event.MatchEndEvent;
import net.lugami.practice.match.event.MatchStartEvent;
import net.lugami.practice.match.event.MatchTerminateEvent;
import net.lugami.practice.match.listener.MatchDurationLimitListener;

/**
 * Represents a possible state of a {@link Match}
 */
public enum MatchState {

    /**
     * The match is currently in countdown and will soon transition to
     * {@link MatchState#IN_PROGRESS}
     * @see Match#startCountdown()
     * @see MatchCountdownStartEvent
     */
    COUNTDOWN,

    /**
     * The match is currently in progress and will transition to
     * {@link MatchState#ENDING} once all players have died (or time
     * has expired)
     * @see Match#checkEnded()
     * @see MatchDurationLimitListener
     * @see MatchStartEvent
     */
    IN_PROGRESS,

    /**
     * The match is currently ending (giving players a moment to realize the
     * match has ended and react) and will soon transition to
     * {@link MatchState#TERMINATED}.
     * @see MatchEndEvent
     */
    ENDING,

    /**
     * The match is completely ended, and all players have been teleported back
     * to the player. The match has been removed from {@link MatchHandler#getHostedMatches()}
     * and will soon be garbage collected
     * @see MatchTerminateEvent
     */
    TERMINATED

}