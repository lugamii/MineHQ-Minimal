package net.lugami.practice.queue.listener;

import com.google.common.collect.ImmutableList;
import net.lugami.practice.kittype.menu.select.CustomSelectKitTypeMenu;
import net.lugami.qlib.autoreboot.AutoRebootHandler;
import net.lugami.qlib.util.PlayerUtils;
import net.lugami.qlib.util.UUIDUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import net.lugami.practice.Practice;
import net.lugami.practice.kittype.KitType;
import net.lugami.practice.listener.RankedMatchQualificationListener;
import net.lugami.practice.match.MatchHandler;
import net.lugami.practice.party.Party;
import net.lugami.practice.queue.QueueHandler;
import net.lugami.practice.queue.QueueItems;
import net.lugami.practice.util.ItemListener;
import net.lugami.practice.validation.PracticeValidation;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class QueueItemListener extends ItemListener {

    private final BiFunction<Player, KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionAdditionRanked = selectionMenuAddition(true);
    private final BiFunction<Player, KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionAdditionUnranked = selectionMenuAddition(false);
    private final QueueHandler queueHandler;

    public QueueItemListener(QueueHandler queueHandler) {
        this.queueHandler = queueHandler;

        addHandler(QueueItems.JOIN_SOLO_UNRANKED_QUEUE_ITEM, joinSoloConsumer(false));
        addHandler(QueueItems.JOIN_SOLO_RANKED_QUEUE_ITEM, joinSoloConsumer(true));

        addHandler(QueueItems.JOIN_PARTY_UNRANKED_QUEUE_ITEM, joinPartyConsumer(false));
        addHandler(QueueItems.JOIN_PARTY_RANKED_QUEUE_ITEM, joinPartyConsumer(true));

        addHandler(QueueItems.LEAVE_SOLO_UNRANKED_QUEUE_ITEM, p -> queueHandler.leaveQueue(p, false));
        addHandler(QueueItems.LEAVE_SOLO_RANKED_QUEUE_ITEM, p -> queueHandler.leaveQueue(p, false));

        Consumer<Player> leaveQueuePartyConsumer = player -> {
            Party party = Practice.getInstance().getPartyHandler().getParty(player);

            // don't message, players who aren't leader shouldn't even get this item
            if (party != null && party.isLeader(player.getUniqueId())) {
                queueHandler.leaveQueue(party, false);
            }
        };

        addHandler(QueueItems.LEAVE_PARTY_UNRANKED_QUEUE_ITEM, leaveQueuePartyConsumer);
        addHandler(QueueItems.LEAVE_PARTY_RANKED_QUEUE_ITEM, leaveQueuePartyConsumer);
    }

    private Consumer<Player> joinSoloConsumer(boolean ranked) {
        return player -> {
            if (ranked) {
                if (rebootSoon()) {
                    player.sendMessage(ChatColor.RED + "You can't join ranked queues with a reboot scheduled soon.");
                    return;
                }

                int ping = PlayerUtils.getPing(player);
                if (ping > 299) {
                    player.sendMessage(ChatColor.RED + "You can't join this queue because your ping is more than 300!");
                    return;
                }

                if (!RankedMatchQualificationListener.isQualified(player.getUniqueId())) {
                    if (!player.hasPermission("Practice.rmq.bypass")) {
                        int needed = RankedMatchQualificationListener.getWinsNeededToQualify(player.getUniqueId());
                        player.sendMessage(ChatColor.RED + "You can't enter a ranked queue until you have won " + RankedMatchQualificationListener.MIN_MATCH_WINS + " unranked games. " + ChatColor.RED + "You need " +
                                needed + " more wins!");
                        return;
                    }
                }
            }

            if (PracticeValidation.canJoinQueue(player)) {
                new CustomSelectKitTypeMenu(kitType -> {
                    queueHandler.joinQueue(player, kitType, ranked);
                    player.closeInventory();
                }, ranked ? selectionAdditionRanked : selectionAdditionUnranked, ChatColor.BLUE + "" + ChatColor.BOLD + "Join " + (ranked ? "Ranked" : "Unranked") + " Queue...", ranked).openMenu(player);
            }
        };
    }

    private Consumer<Player> joinPartyConsumer(boolean ranked) {
        return player -> {
            Party party = Practice.getInstance().getPartyHandler().getParty(player);

            // just fail silently, players who aren't a leader
            // of a party shouldn't even have this item
            if (party == null || !party.isLeader(player.getUniqueId())) {
                return;
            }
            if (ranked) {
                if (rebootSoon()) {
                    player.sendMessage(ChatColor.RED + "Your party can't join ranked queues with a reboot scheduled soon.");
                    return;
                }

                int ping = PlayerUtils.getPing(player);
                if (ping > 299) {
                    player.sendMessage(ChatColor.RED + "Your party can't join this queue because one of your member's ping is more than 300!");
                    return;
                }

                for (UUID member : party.getMembers()) {
                    if (!RankedMatchQualificationListener.isQualified(member)) {
                        int needed = RankedMatchQualificationListener.getWinsNeededToQualify(member);
                        player.sendMessage(
                                ChatColor.RED + "Your party can't join ranked queues because " + UUIDUtils.name(member) + " has less than " + RankedMatchQualificationListener.MIN_MATCH_WINS + " unranked 1v1 wins. They need " +
                                        needed + " more wins!");
                        return;
                    }
                }
            }

            // try to check validation issues in advance
            // (will be called again in QueueHandler#joinQueue)
            if (PracticeValidation.canJoinQueue(party)) {
                new CustomSelectKitTypeMenu(kitType -> {
                    queueHandler.joinQueue(party, kitType, ranked);
                    player.closeInventory();
                }, ranked ? selectionAdditionRanked : selectionAdditionUnranked, "Play " + (ranked ? "Ranked" : "Unranked"), ranked).openMenu(player);
            }
        };
    }

    private BiFunction<Player, KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionMenuAddition(boolean ranked) {
        return (player, kitType) -> {
            MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

            int inFightsRanked = matchHandler.countPlayersPlayingMatches(m -> m.getKitType() == kitType && m.isRanked());
            int inQueueRanked = queueHandler.countPlayersQueued(kitType, true);

            int inFightsUnranked = matchHandler.countPlayersPlayingMatches(m -> m.getKitType() == kitType && !m.isRanked());
            int inQueueUnranked = queueHandler.countPlayersQueued(kitType, false);

            return new CustomSelectKitTypeMenu.CustomKitTypeMeta(
                    // clamp value to >= 1 && <= 64
                    Math.max(1, Math.min(64, ranked ? inQueueRanked + inFightsRanked : inQueueUnranked + inFightsUnranked)),
                    ranked ? ImmutableList.of(
                            " ",
                            ChatColor.GOLD + "Fighting: " + ChatColor.WHITE + inFightsRanked,
                            ChatColor.GOLD + "Queueing: " + ChatColor.WHITE + inQueueRanked
                    ) :
                            ImmutableList.of(
                                    " ",
                                    ChatColor.GOLD + "Fighting: " + ChatColor.WHITE + inFightsUnranked,
                                    ChatColor.GOLD + "Queueing: " + ChatColor.WHITE + inQueueUnranked
                            )
            );
        };
    }

    private boolean rebootSoon() {
        return AutoRebootHandler.isRebooting() && AutoRebootHandler.getRebootSecondsRemaining() <= TimeUnit.MINUTES.toSeconds(5);
    }

}