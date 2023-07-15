package net.lugami.practice.validation;

import lombok.experimental.UtilityClass;
import net.lugami.practice.tournament.TournamentHandler;
import net.lugami.practice.Practice;
import net.lugami.practice.follow.FollowHandler;
import net.lugami.practice.lobby.LobbyHandler;
import net.lugami.practice.match.MatchHandler;
import net.lugami.practice.party.Party;
import net.lugami.practice.party.PartyHandler;
import net.lugami.practice.queue.QueueHandler;
import net.lugami.practice.setting.Setting;
import net.lugami.practice.setting.SettingHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

@UtilityClass
public final class PracticeValidation {

    private static final String CANNOT_DUEL_SELF = ChatColor.RED + "You can't duel yourself!";
    private static final String CANNOT_DUEL_OWN_PARTY = ChatColor.RED + "You can't duel your own party!";

    private static final String CANNOT_DO_THIS_WHILE_IN_PARTY = ChatColor.RED + "You can't do this while in a party!";
    private static final String CANNOT_DO_THIS_WHILE_QUEUED = ChatColor.RED + "You can't do this while queued!";
    private static final String CANNOT_DO_THIS_WHILE_NOT_IN_player = ChatColor.RED + "You can't do this while you're not in the lobby!";
    private static final String CANNOT_DO_THIS_WHILE_IN_MATCH = ChatColor.RED + "You can't do this while participating in or spectating a match!";
    private static final String CANNOT_DO_THIS_WHILE_FOLLOWING = ChatColor.RED + "You cannot do this while following someone! Type /unfollow to exit.";
    private static final String CANNOT_DO_THIS_IN_SILENT_MODE = ChatColor.RED + "You cannot do this while in silent mode!";
    private static final String CANNOT_DO_THIS_WHILST_IN_TOURNAMENT = ChatColor.RED + "You cannot do this whilst in the tournament!";

    private static final String TARGET_PARTY_NOT_IN_player = ChatColor.RED + "That party is not in the lobby!";
    private static final String TARGET_PLAYER_NOT_IN_player = ChatColor.RED + "That player is not in the lobby!";
    private static final String TARGET_PLAYER_FOLLOWING_SOMEONE = ChatColor.RED + "That player is currently following someone!";
    private static final String TARGET_PLAYER_HAS_DUELS_DISABLED = ChatColor.RED + "The player has duels disabled!";
    private static final String TARGET_IN_PARTY = ChatColor.RED + "That player is in a party!";
    private static final String TARGET_PARTY_HAS_DUELS_DISABLED = ChatColor.RED + "The party has duels disabled!";
    private static final String TARGET_PARTY_REACHED_MAXIMUM_SIZE = ChatColor.RED + "The party is full.";
    private static final String TARGET_PARTY_IN_TOURNAMENT = ChatColor.RED + "That party is in a tournament!";

    public static boolean canSendDuel(Player sender, Player target) {
        if (isInSilentMode(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_IN_SILENT_MODE);
            return false;
        }

        if (isInSilentMode(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_IN_SILENT_MODE);
            return false;
        }

        if (sender == target) {
            sender.sendMessage(CANNOT_DUEL_SELF);
            return false;
        }

        if (!isInLobby(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_player);
            return false;
        }

        if (!isInLobby(target)) {
            sender.sendMessage(TARGET_PLAYER_NOT_IN_player);
            return false;
        }

        if (isFollowingSomeone(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_WHILE_FOLLOWING);
            return false;
        }

        if (!getSetting(target, Setting.RECEIVE_DUELS)) {
            sender.sendMessage(TARGET_PLAYER_HAS_DUELS_DISABLED);
            return false;
        }

        return true;
    }

    // sender = the one who typed /accept
    public static boolean canAcceptDuel(Player sender, Player duelSentBy) {
        if (isInSilentMode(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_IN_SILENT_MODE);
            return false;
        }

        if (!isInLobby(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_player);
            return false;
        }

        if (!isInLobby(duelSentBy)) {
            sender.sendMessage(TARGET_PLAYER_NOT_IN_player);
            return false;
        }

        if (isFollowingSomeone(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_WHILE_FOLLOWING);
            return false;
        }

        if (isFollowingSomeone(duelSentBy)) {
            sender.sendMessage(TARGET_PLAYER_FOLLOWING_SOMEONE);
            return false;
        }

        if (isInParty(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_WHILE_IN_PARTY);
            return false;
        }

        if (isInParty(duelSentBy)) {
            sender.sendMessage(TARGET_IN_PARTY);
            return false;
        }

        return true;
    }

    public static boolean canSendDuel(Party sender, Party target, Player initiator) {
        if (sender == target) {
            initiator.sendMessage(CANNOT_DUEL_OWN_PARTY);
            return false;
        }

        if (!isInLobby(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_player);
            return false;
        }

        if (!isInLobby(Bukkit.getPlayer(target.getLeader()))) {
            initiator.sendMessage(TARGET_PARTY_NOT_IN_player);
            return false;
        }

        if (!getSetting(Bukkit.getPlayer(target.getLeader()), Setting.RECEIVE_DUELS)) {
            initiator.sendMessage(TARGET_PARTY_HAS_DUELS_DISABLED);
            return false;
        }

        if (isInTournament(sender)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILST_IN_TOURNAMENT);
            return false;
        }

        return true;
    }

    public static boolean canAcceptDuel(Party target, Party sender, Player initiator) {
        if (!isInLobby(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_player);
            return false;
        }

        if (!isInLobby(Bukkit.getPlayer(target.getLeader()))) {
            initiator.sendMessage(TARGET_PLAYER_NOT_IN_player);
            return false;
        }

        if (isInTournament(target)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILST_IN_TOURNAMENT);
            return false;
        }

        return true;
    }

    public static boolean canJoinParty(Player player, Party party) {
        if (isInParty(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_PARTY);
            return false;
        }

        if (!isInLobby(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_player);
            return false;
        }

        if (isFollowingSomeone(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_FOLLOWING);
            return false;
        }

        if (party.getMembers().size() >= Party.MAX_SIZE && !Bukkit.getPlayer(party.getLeader()).isOp()) {
            player.sendMessage(TARGET_PARTY_REACHED_MAXIMUM_SIZE);
            return false;
        }

        if (isInTournament(party)) {
            player.sendMessage(TARGET_PARTY_IN_TOURNAMENT);
            return false;
        }

        return true;
    }

    public static boolean canUseSpectateItem(Player player) {
        if (!isInLobby(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_player);
            return false;
        }

        return canUseSpectateItemIgnoreMatchSpectating(player);
    }

    public static boolean canUseSpectateItemIgnoreMatchSpectating(Player player) {
        if (isInParty(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_PARTY);
            return false;
        }

        if (isInQueue(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        if (isInMatch(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_MATCH);
            return false;
        }

        if (isFollowingSomeone(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_FOLLOWING);
            return false;
        }

        return true;
    }

    public static boolean canFollowSomeone(Player player) {
        if (isInParty(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_PARTY);
            return false;
        }

        if (isInQueue(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        if (isInMatch(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_MATCH);
            return false;
        }

        if (!(isInLobby(player))) {
            player.sendMessage(ChatColor.RED + "You can't do that here!");
            return false;
        }

        return isInLobby(player);
    }

    public static boolean canJoinQueue(Player player) {
        if (isInSilentMode(player)) {
            player.sendMessage(CANNOT_DO_THIS_IN_SILENT_MODE);
            return false;
        }

        if (isInParty(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_PARTY);
            return false;
        }

        if (isInQueue(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        if (!isInLobby(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_player);
            return false;
        }

        if (isFollowingSomeone(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_FOLLOWING);
            return false;
        }

        return true;
    }

    public static boolean canJoinQueue(Party party) {
        if (isInQueue(party)) {
            // we shouldn't really message the whole party
            // here, but players should never really be able to click
            // this item while in a queue anyway (and it takes a lot of work
            // to rework this validation to include an initiator)
            party.message(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        if (isInTournament(party)) {
            party.message(CANNOT_DO_THIS_WHILST_IN_TOURNAMENT);
            return false;
        }

        return true;
    }

    public static boolean canStartTeamSplit(Party party, Player initiator) {
        if (isInQueue(party)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        if (!isInLobby(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_player);
            return false;
        }

        if (isInTournament(party)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILST_IN_TOURNAMENT);
            return false;
        }
        return true;
    }

    public static boolean canStartFfa(Party party, Player initiator) {
        if (isInQueue(party)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        if (!isInLobby(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_NOT_IN_player);
            return false;
        }

        if (isInTournament(party)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILST_IN_TOURNAMENT);
            return false;
        }
        return true;
    }

    private static boolean getSetting(Player player, Setting setting) {
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        return settingHandler.getSetting(player, setting);
    }

    private static boolean isInParty(Player player) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        return partyHandler.hasParty(player);
    }

    private static boolean isInQueue(Player player) {
        QueueHandler queueHandler = Practice.getInstance().getQueueHandler();
        return queueHandler.isQueued(player.getUniqueId());
    }

    private static boolean isInQueue(Party party) {
        QueueHandler queueHandler = Practice.getInstance().getQueueHandler();
        return queueHandler.isQueued(party);
    }

    private boolean isInMatch(Player player) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        return matchHandler.isPlayingMatch(player);
    }

    private boolean isInLobby(Player player) {
        LobbyHandler LobbyHandler = Practice.getInstance().getLobbyHandler();
        return LobbyHandler.isInLobby(player);
    }

    private boolean isFollowingSomeone(Player player) {
        FollowHandler followHandler = Practice.getInstance().getFollowHandler();
        return followHandler.getFollowing(player).isPresent();
    }

    private boolean isInTournament(Party party) {
        TournamentHandler tournamentHandler = Practice.getInstance().getTournamentHandler();
        return tournamentHandler.isInTournament(party);
    }

    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            final Player damager = (Player) event.getDamager();
            if (isInSilentMode(damager)) {
                event.setCancelled(true);
                damager.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You cannot do this while in mod mode!");
            }
        }
    }

    @EventHandler
    public void onPlayerDropitem(final PlayerDropItemEvent event) {
        if (isInSilentMode(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        if (isInSilentMode(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(final BlockPlaceEvent event) {
        if (isInSilentMode(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You cannot do this while in mod mode!");
        }
    }

    @EventHandler
    public void onBlockBreakEvent(final BlockBreakEvent event) {
        if (isInSilentMode(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You cannot do this while in mod mode!");
        }
    }

    @EventHandler
    public void onPlayerBucketFill(final PlayerBucketFillEvent event) {
        if (isInSilentMode(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You cannot do this while in mod mode!");
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
        if (isInSilentMode(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You cannot do this while in mod mode!");
        }
    }

            private boolean isInSilentMode (Player player){
                return player.hasMetadata("ModMode");
            }
        }