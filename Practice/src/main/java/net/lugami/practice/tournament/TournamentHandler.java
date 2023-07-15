package net.lugami.practice.tournament;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import mkremins.fanciful.FancyMessage;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.FrozenCommandHandler;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.event.HalfHourEvent;
import net.lugami.qlib.util.UUIDUtils;
import net.lugami.practice.PracticeLang;
import net.lugami.practice.Practice;
import net.lugami.practice.kittype.KitType;
import net.lugami.practice.match.Match;
import net.lugami.practice.match.MatchState;
import net.lugami.practice.match.MatchTeam;
import net.lugami.practice.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class TournamentHandler implements Listener {

    @Getter @Setter private Tournament tournament = null;
    private static TournamentHandler instance;

    public TournamentHandler() {
        instance = this;
        FrozenCommandHandler.registerClass(this.getClass());
        Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Practice.getInstance(), () -> {
            if (tournament != null) tournament.check();
        }, 20L, 20L);

        populateTournamentStatuses();
    }

    public boolean isInTournament(Party party) {
        return tournament != null && tournament.isInTournament(party);
    }

    public boolean isInTournament(Match match) {
        return tournament != null && tournament.getMatches().contains(match);
    }

    @Command(names = { "tournament adminstart", "tourn adminstart" }, permission = "practice.admin")
    public static void tournamentCreate(CommandSender sender, @Param(name = "kit-type") KitType type, @Param(name = "teamSize") int teamSize, @Param(name = "requiredTeams") int requiredTeams) {
        if (instance.getTournament() != null) {
            sender.sendMessage(ChatColor.RED + "There's already an ongoing tournament!");
            return;
        }

        if (type == null) {
            sender.sendMessage(ChatColor.RED + "Kit type not found!");
            return;
        }

        if (teamSize < 1 || 10 < teamSize) {
            sender.sendMessage(ChatColor.RED + "Invalid team size range. Acceptable inputs: 1 -> 10");
            return;
        }

        if (requiredTeams < 4) {
            sender.sendMessage(ChatColor.RED + "Required teams must be at least 4.");
            return;
        }
        FancyMessage message;
        Tournament tournament;
        instance.setTournament(tournament = new Tournament(type, teamSize, requiredTeams));
        sender.sendMessage(ChatColor.GREEN + "Successfully started a tournament.");
        message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&7A " + Practice.getInstance().getDominantColor() + tournament.getRequiredPartySize() + "v" + tournament.getRequiredPartySize() + " " + tournament.getType().getDisplayName() + Practice.getInstance().getDominantColor() + " tournament&7 has started &a[Click Here] &7to join. " + "&f(0/" + (teamSize < 3 ? teamSize * requiredTeams : requiredTeams) + ")"));
        message.command("/tournament join");
        message.tooltip(ChatColor.translateAlternateColorCodes('&', "&7" + tournament.getType().getDisplayName()));
        message.tooltip(ChatColor.translateAlternateColorCodes('&', "&7" + requiredTeams + " &7players."));
        message.tooltip(ChatColor.translateAlternateColorCodes('&', "&7"));
        message.tooltip(ChatColor.translateAlternateColorCodes('&', "&a&lClick to join!"));

        for (Player player : Bukkit.getOnlinePlayers()) {
            message.send(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (instance.getTournament() == tournament) {
                        tournament.broadcastJoinMessage();
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimer(Practice.getInstance(), 60 * 20, 60 * 20);
        }
    }
    @Command(names = { "tournament join", "tourn join", "join"}, permission = "")
    public static void tournamentJoin(Player sender) {

        if (instance.getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no running tournament to join.");
            return;
        }

        int tournamentTeamSize = instance.getTournament().getRequiredPartySize();

        if ((instance.getTournament().getCurrentRound() != -1 || instance.getTournament().getBeginNextRoundIn() != 31) && (instance.getTournament().getCurrentRound() != 0 || !sender.hasPermission("tournaments.joinduringcountdown"))) {
            sender.sendMessage(ChatColor.RED + "The tournament is active, therefore you cannot join.");
            return;
        }

        Party senderParty = Practice.getInstance().getPartyHandler().getParty(sender);
        if (senderParty == null) {
            if (tournamentTeamSize == 1) {
                senderParty = Practice.getInstance().getPartyHandler().getOrCreateParty(sender); // Will auto put them in a party
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have a team to join the tournament with!");
                return;
            }
        }

        int notInplayer = 0;
        int queued = 0;
        for (UUID member : senderParty.getMembers()) {
            if (!Practice.getInstance().getLobbyHandler().isInLobby(Bukkit.getPlayer(member))) {
                notInplayer++;
            }

            if (Practice.getInstance().getQueueHandler().getQueueEntry(member) != null) {
                queued++;
            }
        }

        if (notInplayer != 0) {
            sender.sendMessage(ChatColor.RED.toString() + notInplayer + "member" + (notInplayer == 1 ? "" : "s") + " of your team aren't in the player.");
            return;
        }

        if (queued != 0) {
            sender.sendMessage(ChatColor.RED.toString() + notInplayer + "member" + (notInplayer == 1 ? "" : "s") + " of your team are currently queued.");
            return;
        }

        if (!senderParty.getLeader().equals(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You must be the leader of your team to join the tournament.");
            return;
        }

        if (instance.isInTournament(senderParty)) {
            sender.sendMessage(ChatColor.RED + "Your team is already in the tournament!");
            return;
        }

        if (senderParty.getMembers().size() != instance.getTournament().getRequiredPartySize()) {
            sender.sendMessage(ChatColor.RED + "You need exactly " + instance.getTournament().getRequiredPartySize() + " members in your party to join the tournament.");
            return;
        }

        if (Practice.getInstance().getQueueHandler().getQueueEntry(senderParty) != null) {
            sender.sendMessage(ChatColor.RED + "You can't join the tournament if your party is currently queued.");
            return;
        }

        senderParty.message(ChatColor.GREEN + "You have joined the tournament.");
        instance.getTournament().addParty(senderParty);
    }

    @Command(names = { "tournament leave", "tourn leave", "leave" }, permission = "")
        public static void tournamentLeave(Player sender) {
        Party senderParty = Practice.getInstance().getPartyHandler().getParty(sender);

        if (!instance.isInTournament(senderParty)) {
            sender.sendMessage(ChatColor.RED + "You're not in the tournament.");
            return;
        }

        senderParty.message(ChatColor.GREEN + "You have left the tournament.");
        instance.getTournament().removeParty(senderParty);
    }

    @Command(names = { "tournament status", "tstatus", "status" }, permission = "")
    public static void tournamentStatus(CommandSender sender) {
        if (instance.getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no ongoing tournament to get the status of.");
            return;
        }

        sender.sendMessage(PracticeLang.LONG_LINE);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Live &6Tournament &7Fights"));
        sender.sendMessage("");
        List<Match> ongoingMatches = instance.getTournament().getMatches().stream().filter(m -> m.getState() != MatchState.TERMINATED).collect(Collectors.toList());

        for (Match match : ongoingMatches) {
            MatchTeam firstTeam = match.getTeams().get(0);
            MatchTeam secondTeam = match.getTeams().get(1);

            if (firstTeam.getAllMembers().size() == 1) {
                sender.sendMessage("  " + ChatColor.GRAY + "» " + ChatColor.LIGHT_PURPLE + UUIDUtils.name(firstTeam.getFirstMember()) + ChatColor.GRAY + " vs " + ChatColor.LIGHT_PURPLE + UUIDUtils.name(secondTeam.getFirstMember()));
            } else {
                sender.sendMessage("  " + ChatColor.GRAY + "» " + ChatColor.LIGHT_PURPLE + UUIDUtils.name(firstTeam.getFirstMember()) + ChatColor.GRAY + "'s team vs " + ChatColor.LIGHT_PURPLE + UUIDUtils.name(secondTeam.getFirstMember()) + ChatColor.GRAY + "'s team");
            }
        }
        sender.sendMessage(PracticeLang.LONG_LINE);
    }

    @Command(names = { "tournament cancel", "tcancel"},  permission = "op")
    public static void tournamentCancel(CommandSender sender) {
        if (instance.getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no running tournament to cancel.");
            return;
        }

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&cTournament cancelled."));
        Bukkit.broadcastMessage("");
        instance.setTournament(null);
    }

    @Command(names = { "tournament forcestart"}, permission = "op")
    public static void tournamentForceStart(CommandSender sender) {
        if (instance.getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no tournament to force start.");
            return;
        }

        if (instance.getTournament().getCurrentRound() != -1 || instance.getTournament().getBeginNextRoundIn() != 31) {
            sender.sendMessage(ChatColor.RED + "This tournament is already in progress.");
            return;
        }

        instance.getTournament().start();
        sender.sendMessage(ChatColor.GREEN + "Force started tournament.");
    }

    private static List<TournamentStatus> allStatuses = Lists.newArrayList();

    private void populateTournamentStatuses() {
        List<KitType> viewableKits = KitType.getAllTypes().stream().filter(kit -> !kit.isHidden()).collect(Collectors.toList());
        allStatuses.add(new TournamentStatus(0, ImmutableList.of(1), ImmutableList.of(16, 32), viewableKits));
        allStatuses.add(new TournamentStatus(250, ImmutableList.of(1), ImmutableList.of(32), viewableKits));
        allStatuses.add(new TournamentStatus(300, ImmutableList.of(1), ImmutableList.of(48, 64), ImmutableList.of(KitType.byId("NODEBUFF"))));
        allStatuses.add(new TournamentStatus(400, ImmutableList.of(1), ImmutableList.of(64), ImmutableList.of(KitType.byId("NODEBUFF"))));
        allStatuses.add(new TournamentStatus(500, ImmutableList.of(1), ImmutableList.of(128), ImmutableList.of(KitType.byId("NODEBUFF"))));
        allStatuses.add(new TournamentStatus(600, ImmutableList.of(1), ImmutableList.of(128), ImmutableList.of(KitType.byId("NODEBUFF"))));
        allStatuses.add(new TournamentStatus(700, ImmutableList.of(1), ImmutableList.of(128), ImmutableList.of(KitType.byId("NODEBUFF"))));
        allStatuses.add(new TournamentStatus(800, ImmutableList.of(1), ImmutableList.of(128), ImmutableList.of(KitType.byId("NODEBUFF"))));
    }

    //    @EventHandler
    public void onHalfHour(HalfHourEvent event) {
        if (instance.getTournament() != null) return; // already a tournament in progress

        TournamentStatus status = TournamentStatus.forPlayerCount(Bukkit.getOnlinePlayers().size());

        int teamSize = status.getTeamSizes().get(ThreadLocalRandom.current().nextInt(status.getTeamSizes().size()));
        int teamCount = status.getTeamCounts().get(ThreadLocalRandom.current().nextInt(status.getTeamCounts().size()));
        KitType kitType = status.getKitTypes().get(ThreadLocalRandom.current().nextInt(status.getKitTypes().size()));

        Tournament tournament;
        instance.setTournament(tournament = new Tournament(kitType, teamSize, teamCount));

        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> {
            if (tournament == instance.getTournament() && instance.getTournament() != null && instance.getTournament().getCurrentRound() == -1) {
                instance.getTournament().start();
            }
        }, 3 * 20 * 60);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (instance.getTournament() == tournament) {
                    tournament.broadcastJoinMessage();
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(Practice.getInstance(), 60 * 20, 60 * 20);
    }

    @Getter
    private static class TournamentStatus {
        private int minimumPlayerCount;
        private List<Integer> teamSizes;
        private List<Integer> teamCounts;
        private List<KitType> kitTypes;

        public TournamentStatus(int minimumPlayerCount, List<Integer> teamSizes, List<Integer> teamCounts, List<KitType> kitTypes) {
            this.minimumPlayerCount = minimumPlayerCount;
            this.teamSizes = teamSizes;
            this.teamCounts = teamCounts;
            this.kitTypes = kitTypes;
        }

        public static TournamentStatus forPlayerCount(int playerCount) {
            for (int i = allStatuses.size() - 1; 0 <= i; i--) {
                if (allStatuses.get(i).minimumPlayerCount <= playerCount) return allStatuses.get(i);
            }


            throw new IllegalArgumentException("No suitable sizes found!");
        }
    }
}