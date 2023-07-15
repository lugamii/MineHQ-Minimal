package net.lugami.practice.match;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.lugami.practice.kittype.KitType;
import net.lugami.practice.match.listener.*;
import net.lugami.practice.Practice;
import net.lugami.practice.arena.Arena;
import net.lugami.practice.arena.ArenaHandler;
import net.lugami.practice.arena.ArenaSchematic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.lugami.practice.match.listener.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public final class MatchHandler {

    public static final String MONGO_COLLECTION_NAME = "matches";

    private final Set<Match> hostedMatches = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Getter @Setter private boolean rankedMatchesDisabled;
    @Getter @Setter private boolean unrankedMatchesDisabled;

    // these two maps are one of my least favorite bits of code in the match module, but
    // they let us run O(1) lookups of player matches, so we absolutely should keep them.
    // we do, however, have to be very careful to keep them updated with the "actuaL" data.
    // if we ever have issues with these, we have /pstatus to compare the "actual" data (O(n) scan)
    // with the O(1) data here
    @Getter(AccessLevel.PACKAGE) private final Map<UUID, Match> playingMatchCache = new ConcurrentHashMap<>();
    @Getter(AccessLevel.PACKAGE) private final Map<UUID, Match> spectatingMatchCache = new ConcurrentHashMap<>();

    public MatchHandler() {
        Bukkit.getPluginManager().registerEvents(new GoldenHeadListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new KitSelectionListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchBlockPickupListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchBuildListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchComboListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchCountdownListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchDeathMessageListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchDurationLimitListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchEnderPearlDamageListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchFreezeListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchGeneralListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchHardcoreHealingListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchHealthDisplayListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchPartySpectateListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchRodListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchSoupListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchStatsListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchWizardListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new SpectatorItemListener(this), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new SpectatorPreventionListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchBoxingListener(), Practice.getInstance());
    }

    public Match startMatch(List<MatchTeam> teams, KitType kitType, boolean ranked, boolean allowRematches) {
        boolean anyOps = false;

        for (MatchTeam team : teams) {
            for (UUID member : team.getAllMembers()) {
                Player memberPlayer = Bukkit.getPlayer(member);

                if (!anyOps && memberPlayer.isOp()) {
                    anyOps = true;
                }

                if (isPlayingOrSpectatingMatch(memberPlayer)) {
                    throw new IllegalArgumentException(memberPlayer.getDisplayName() + " is already in a match!");
                }
            }
        }

        if (!anyOps) {
            if (ranked && rankedMatchesDisabled) {
                throw new IllegalArgumentException("Ranked match creation is disabled!");
            } else if (unrankedMatchesDisabled) {
                throw new IllegalArgumentException("Unranked match creation is disabled!");
            }
        }

        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
        long matchSize = teams.stream()
                .mapToInt(t -> t.getAllMembers().size())
                .sum();

        // the archer only logic here was often a source of confusion while
        // this code was being written. below is a table of the desired
        // results / if a match can run in a given arena
        //
        //              Arena is archer only    Arena is not archer only
        //  Is Archer           Yes                         Yes
        // Not Archer           No                          Yes
        //
        // the left side of the or statement covers the top row, and the
        // right side covers the right side
        Optional<Arena> openArenaOpt = arenaHandler.allocateUnusedArena(schematic ->
                schematic.isEnabled() &&
                        !schematic.isTeamFightsOnly() &&
                        canUseSchematic(kitType, schematic) &&
                        matchSize <= schematic.getMaxPlayerCount() &&
                        matchSize >= schematic.getMinPlayerCount() &&
                        (!ranked || schematic.isSupportsRanked())
        );

        if (!openArenaOpt.isPresent()) {
            Practice.getInstance().getLogger().warning("Failed to start match: No open arenas found");
            return null;
        }

        Match match = new Match(kitType, openArenaOpt.get(), teams, ranked, allowRematches);

        hostedMatches.add(match);
        match.startCountdown();

        return match;
    }

    public Match startMatch(List<MatchTeam> teams, KitType kitType, boolean ranked, boolean allowRematches, ArenaSchematic arena) {
        boolean anyOps = false;

        for (MatchTeam team : teams) {
            for (UUID member : team.getAllMembers()) {
                Player memberPlayer = Bukkit.getPlayer(member);

                if (!anyOps && memberPlayer.isOp()) {
                    anyOps = true;
                }

                if (isPlayingOrSpectatingMatch(memberPlayer)) {
                    throw new IllegalArgumentException(memberPlayer.getName() + " is already in a match!");
                }
            }
        }

        if (!anyOps) {
            if (ranked && rankedMatchesDisabled) {
                throw new IllegalArgumentException("Ranked match creation is disabled!");
            } else if (unrankedMatchesDisabled) {
                throw new IllegalArgumentException("Unranked match creation is disabled!");
            }
        }

        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
        long matchSize = teams.stream()
                .mapToInt(t -> t.getAllMembers().size())
                .sum();

        // the archer only logic here was often a source of confusion while
        // this code was being written. below is a table of the desired
        // results / if a match can run in a given arena
        //
        //              Arena is archer only    Arena is not archer only
        //  Is Archer           Yes                         Yes
        // Not Archer           No                          Yes
        //
        // the left side of the or statement covers the top row, and the
        // right side covers the right side

        Optional<Arena> openArenaOpt = Optional.empty();

        if(arena == null) {
            openArenaOpt = arenaHandler.allocateUnusedArena(schematic ->
                    schematic.isEnabled() &&
                            canUseSchematic(kitType, schematic) &&
                            matchSize <= schematic.getMaxPlayerCount() &&
                            matchSize >= schematic.getMinPlayerCount() &&
                            (!ranked || schematic.isSupportsRanked())
            );
        }

        if(arena != null) {
            openArenaOpt = arenaHandler.allocateUnusedArena(schematic ->
                    schematic.equals(arena)
            );
        }

        if (!openArenaOpt.isPresent()) {
            Practice.getInstance().getLogger().warning("Failed to start match: No open arenas found");
            return null;
        }

        Match match = new Match(kitType, openArenaOpt.get(), teams, ranked, allowRematches);

        hostedMatches.add(match);
        match.startCountdown();

        return match;
    }

    public static boolean canUseSchematic(KitType kitType, ArenaSchematic schematic) {
        String kitId = kitType.getId();

        if (kitId.equals("BUILDUHC")) return schematic.isBuildUHCOnly();
        if (kitId.equals("DualWield")) return schematic.isBuildUHCOnly();
        if (kitId.equals("SPLEEF")) return schematic.isSpleefOnly();
        if (kitId.equals("SUMO")) return schematic.isSumoOnly();
        if (kitId.equals("HCF")) return schematic.isHCFOnly();

        if (schematic.isBuildUHCOnly()) return kitId.equals("BUILDUHC");
        if (schematic.isBuildUHCOnly()) return kitId.equals("DualWield");
        if (schematic.isSpleefOnly()) return kitId.equals("SPLEEF");
        if (schematic.isSumoOnly()) return kitId.equals("SUMO");
        if (schematic.isHCFOnly()) return kitId.equals("HCF");

        return true;
    }

    void removeMatch(Match match) {
        hostedMatches.remove(match);
    }

    /**
     * Gets a read-only set containing all matches currently being hosted.
     * This includes matches that are pre-start and ending.
     * @return a read-only representation of all hosted matches
     */
    public Set<Match> getHostedMatches() {
        return ImmutableSet.copyOf(hostedMatches);
    }

    /**
     * Returns a sum of all players who are playing in an IN_PROGRESS match
     * @return number of players playing in IN_PROGRESS matches
     */
    public int countPlayersPlayingInProgressMatches() {
        return countPlayersPlayingMatches(m -> m.getState() == MatchState.COUNTDOWN || m.getState() == MatchState.IN_PROGRESS);
    }

    /**
     * Returns a sum of all players who are playing in a {@link Match}
     * that passes the {@link Predicate} provided.
     * @return number of players playing in matches that
     *          pass the {@link Predicate} provided
     */
    public int countPlayersPlayingMatches(Predicate<Match> inclusionPredicate) {
        int result = 0;

        for (Match match : hostedMatches) {
            if (inclusionPredicate.test(match)) {
                for (MatchTeam team : match.getTeams()) {
                    result += team.getAliveMembers().size();
                }
            }
        }

        return result;
    }

    /**
     * Gets the match currently being played by the player provided.
     * In this context, played means a player who alive and fighting on a team
     *
     * @param player player to look up match for
     * @return the match being played by the provided player
     */
    public Match getMatchPlaying(Player player) {
        return playingMatchCache.get(player.getUniqueId());
    }

    /**
     * Gets the match currently being spectated by the player provided.
     * In this context, spectated includes both players who died while
     * fighting who have not left and players who joined via /spectate.
     *
     * @param player player to look up match for
     * @return the match being spectated by the provided player
     */
    public Match getMatchSpectating(Player player) {
        return spectatingMatchCache.get(player.getUniqueId());
    }

    /**
     * Gets the match currently being spectated or played by the player provided.
     * This method acts as a combination of {@link MatchHandler#getMatchPlaying(Player)}
     * and {@link MatchHandler#getMatchSpectating(Player)}.
     *
     * @param player player to look up match for
     * @return the match being played or spectated by the provided player
     */
    public Match getMatchPlayingOrSpectating(Player player) {
        Match playing = playingMatchCache.get(player.getUniqueId());

        if (playing != null) {
            return playing;
        } else {
            return spectatingMatchCache.get(player.getUniqueId());
        }
    }

    /**
     * Checks if the player specified is playing a match.
     * See {@link MatchHandler#getMatchPlaying(Player)} for a definition
     * of the term playing.
     * @param player player to look up match for
     * @return if a match is being played by the provided player
     */
    public boolean isPlayingMatch(Player player) {
        return playingMatchCache.containsKey(player.getUniqueId());
    }

    /**
     * Checks if the player specified is spectating a match.
     * See {@link MatchHandler#getMatchSpectating(Player)} (UUID)} for a
     * definition of the term spectating.
     * @param player player to look up match for
     * @return if a match is being spectated by the provided player
     */
    public boolean isSpectatingMatch(Player player) {
        return spectatingMatchCache.containsKey(player.getUniqueId());
    }

    /**
     * Checks if the player specified is playing or spectating a match.
     * See {@link MatchHandler#getMatchPlayingOrSpectating(Player)} for a definition
     * of the term playing or spectating.
     * @param player player to look up match for
     * @return if a match is being played or spectated by the provided player
     */
    public boolean isPlayingOrSpectatingMatch(Player player) {
        if (player == null) return false;
        return playingMatchCache.containsKey(player.getUniqueId()) || spectatingMatchCache.containsKey(player.getUniqueId());
    }

}