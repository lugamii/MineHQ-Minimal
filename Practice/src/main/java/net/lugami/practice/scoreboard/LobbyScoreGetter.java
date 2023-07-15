
package net.lugami.practice.scoreboard;

import net.lugami.practice.tournament.Tournament;
import net.lugami.qlib.autoreboot.AutoRebootHandler;
import net.lugami.qlib.util.LinkedList;
import net.lugami.qlib.util.TimeUtils;
import net.lugami.practice.Practice;
import net.lugami.practice.elo.EloHandler;
import net.lugami.practice.match.MatchHandler;
import net.lugami.practice.party.Party;
import net.lugami.practice.party.PartyHandler;
import net.lugami.practice.queue.MatchQueue;
import net.lugami.practice.queue.MatchQueueEntry;
import net.lugami.practice.queue.QueueHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

final class LobbyScoreGetter implements BiConsumer<Player, LinkedList<String>> {

    private int LAST_ONLINE_COUNT = 0;
    private int LAST_IN_FIGHTS_COUNT = 0;
    private int LAST_IN_QUEUES_COUNT = 0;

    private long lastUpdated = System.currentTimeMillis();

    @Override
    public void accept(Player player, LinkedList<String> scores) {
        Optional<UUID> followingOpt = Practice.getInstance().getFollowHandler().getFollowing(player);
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        QueueHandler queueHandler = Practice.getInstance().getQueueHandler();
        EloHandler eloHandler = Practice.getInstance().getEloHandler();

        Party playerParty = partyHandler.getParty(player);
        if (playerParty != null) {
            int size = playerParty.getMembers().size();
            scores.add("&9Your Party: &f" + size);
        }

        scores.add("&eOnline: &f" + LAST_ONLINE_COUNT);
        scores.add("&dIn Fights: &f" + LAST_IN_FIGHTS_COUNT);
        scores.add("&bIn Queues: &f" + LAST_IN_QUEUES_COUNT);


        if (2500 <= System.currentTimeMillis() - lastUpdated) {
            lastUpdated = System.currentTimeMillis();
            LAST_ONLINE_COUNT = Bukkit.getOnlinePlayers().size();
            LAST_IN_FIGHTS_COUNT = matchHandler.countPlayersPlayingInProgressMatches();
            LAST_IN_QUEUES_COUNT = queueHandler.getQueuedCount();
        }

        // this definitely can be a .ifPresent, however creating the new lambda that often
        // was causing some performance issues, so we do this less pretty (but more efficent)
        // check (we can't define the lambda up top and reference because we reference the
        // scores variable)
        if (followingOpt.isPresent()) {
            Player following = Bukkit.getPlayer(followingOpt.get());
            scores.add("&6Following: *&f" + following.getName());

            if (player.hasPermission("basic.staff")) {
                MatchQueueEntry targetEntry = getQueueEntry(following);

                if (targetEntry != null) {
                    MatchQueue queue = targetEntry.getQueue();

                    scores.add("&6Target queue:");
                    scores.add("&7" + (queue.isRanked() ? "Ranked" : "Unranked") + " " + queue.getKitType().getDisplayName());
                }
            }
        }

        MatchQueueEntry entry = getQueueEntry(player);

        if (entry != null) {
            String waitTimeFormatted = TimeUtils.formatIntoMMSS(entry.getWaitSeconds());
            MatchQueue queue = entry.getQueue();

            scores.add("&b&7&m--------------------");
            scores.add(queue.getKitType().getDisplayColor() + (queue.isRanked() ? "Ranked" : "Unranked") + " " + queue.getKitType().getDisplayName());
            scores.add("&6Time: *&f" + waitTimeFormatted);

            if (queue.isRanked()) {
                int elo = eloHandler.getElo(entry.getMembers(), queue.getKitType());
                int window = entry.getWaitSeconds() * QueueHandler.RANKED_WINDOW_GROWTH_PER_SECOND;

                scores.add("&6Search range: *&f" + Math.max(0, elo - window) + " - " + (elo + window));
            }
        }

        if (AutoRebootHandler.isRebooting()) {
            String secondsStr = TimeUtils.formatIntoMMSS(AutoRebootHandler.getRebootSecondsRemaining());
            scores.add("&c&lRebooting: &c" + secondsStr);
        }

        if (player.hasMetadata("ModMode")) {
            scores.add(ChatColor.GRAY.toString() + ChatColor.BOLD + "In Silent Mode");
        }

        Tournament tournament = Practice.getInstance().getTournamentHandler().getTournament();
        if (tournament != null) {
            scores.add("&7&m--------------------");
            scores.add("&c&lTournament");

            if (tournament.getStage() == Tournament.TournamentStage.WAITING_FOR_TEAMS) {
                int teamSize = tournament.getRequiredPartySize();
                scores.add("&6Kit&f: " + tournament.getType().getDisplayName());
                scores.add("&6Team Size&f: " + teamSize + "v" + teamSize);
                int multiplier = teamSize < 3 ? teamSize : 1;
                scores.add("&6" + (teamSize < 3 ? "Players"  : "Teams") + "&f: " + (tournament.getActiveParties().size() * multiplier + "/" + tournament.getRequiredPartiesToStart() * multiplier));
            } else if (tournament.getStage() == Tournament.TournamentStage.COUNTDOWN) {
                if (tournament.getCurrentRound() == 0) {
                    scores.add("&9");
                    scores.add("&fBegins in &6" + tournament.getBeginNextRoundIn() + "&f second" + (tournament.getBeginNextRoundIn() == 1 ? "." : "s."));
                } else {
                    scores.add("&9");
                    scores.add("&6&lRound " + (tournament.getCurrentRound() + 1));
                    scores.add("&fBegins in &6" + tournament.getBeginNextRoundIn() + "&f second" + (tournament.getBeginNextRoundIn() == 1 ? "." : "s."));
                }
            } else if (tournament.getStage() == Tournament.TournamentStage.IN_PROGRESS) {
                scores.add("&6Round&f: " + tournament.getCurrentRound());

                int teamSize = tournament.getRequiredPartySize();
                int multiplier = teamSize < 3 ? teamSize : 1;

                scores.add("&6" + (teamSize < 3 ? "Players" : "Teams") + "&f: " + tournament.getActiveParties().size() * multiplier + "/" + tournament.getRequiredPartiesToStart() * multiplier);
                scores.add("&6Duration&f: " + TimeUtils.formatIntoMMSS((int) (System.currentTimeMillis() - tournament.getRoundStartedAt()) / 1000));
            }
        }

    }


    private MatchQueueEntry getQueueEntry(Player player) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        QueueHandler queueHandler = Practice.getInstance().getQueueHandler();

        Party playerParty = partyHandler.getParty(player);
        if (playerParty != null) {
            return queueHandler.getQueueEntry(playerParty);
        } else {
            return queueHandler.getQueueEntry(player.getUniqueId());
        }
    }

}