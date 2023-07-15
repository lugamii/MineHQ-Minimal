package net.lugami.practice.lobby;

import lombok.experimental.UtilityClass;
import net.lugami.practice.follow.FollowHandler;
import net.lugami.practice.kit.menu.editkit.EditKitMenu;
import net.lugami.practice.party.Party;
import net.lugami.practice.party.PartyHandler;
import net.lugami.practice.party.PartyItems;
import net.lugami.practice.queue.QueueHandler;
import net.lugami.practice.queue.QueueItems;
import net.lugami.practice.rematch.RematchData;
import net.lugami.practice.rematch.RematchHandler;
import net.lugami.practice.rematch.RematchItems;
import net.lugami.qlib.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import net.lugami.practice.Practice;
import net.lugami.practice.duel.DuelHandler;
import net.lugami.practice.kit.KitItems;

@UtilityClass
public final class LobbyUtils {

    public static void resetInventory(Player player) {
        // prevents players with the kit editor from having their
        // inventory updated (kit items go into their inventory)
        // also, admins in GM don't get invs updated (to prevent annoying those editing kits)
        if (Menu.currentlyOpenedMenus.get(player.getName()) instanceof EditKitMenu || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);

        if (partyHandler.hasParty(player)) {
            renderPartyItems(player, inventory, partyHandler.getParty(player));
        } else {
            renderSoloItems(player, inventory);
        }

        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), player::updateInventory, 1L);
    }

    private void renderPartyItems(Player player, PlayerInventory inventory, Party party) {
        QueueHandler queueHandler = Practice.getInstance().getQueueHandler();

        if (party.isLeader(player.getUniqueId())) {
            int partySize = party.getMembers().size();

            if (partySize == 2) {
                if (!queueHandler.isQueuedUnranked(party)) {
                    inventory.setItem(1, QueueItems.JOIN_PARTY_UNRANKED_QUEUE_ITEM);
                    inventory.setItem(3, PartyItems.ASSIGN_CLASSES);
                } else {
                    inventory.setItem(1, QueueItems.LEAVE_PARTY_UNRANKED_QUEUE_ITEM);
                }

                if (!queueHandler.isQueuedRanked(party)) {
                    inventory.setItem(2, QueueItems.JOIN_PARTY_RANKED_QUEUE_ITEM);
                    inventory.setItem(3, PartyItems.ASSIGN_CLASSES);
                } else {
                    inventory.setItem(2, QueueItems.LEAVE_PARTY_RANKED_QUEUE_ITEM);
                }
            } else if (partySize > 2 && !queueHandler.isQueued(party)) {
                inventory.setItem(1, PartyItems.START_TEAM_SPLIT_ITEM);
                inventory.setItem(2, PartyItems.START_FFA_ITEM);
                inventory.setItem(3, PartyItems.ASSIGN_CLASSES);
            }

        } else {
            int partySize = party.getMembers().size();
            if (partySize >= 2) {
                inventory.setItem(1, PartyItems.ASSIGN_CLASSES);
            }
        }

        inventory.setItem(0, PartyItems.icon(party));
        inventory.setItem(6, PartyItems.OTHER_PARTIES_ITEM);
        inventory.setItem(7, KitItems.OPEN_EDITOR_ITEM);
        inventory.setItem(8, PartyItems.LEAVE_PARTY_ITEM);
    }

    private void renderSoloItems(Player player, PlayerInventory inventory) {
        RematchHandler rematchHandler = Practice.getInstance().getRematchHandler();
        QueueHandler queueHandler = Practice.getInstance().getQueueHandler();
        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();
        FollowHandler followHandler = Practice.getInstance().getFollowHandler();
        LobbyHandler LobbyHandler = Practice.getInstance().getLobbyHandler();

        boolean specMode = LobbyHandler.isInSpectatorMode(player);
        boolean followingSomeone = followHandler.getFollowing(player).isPresent();

        player.setAllowFlight(player.getGameMode() == GameMode.CREATIVE || specMode);

        if (specMode || followingSomeone) {
            inventory.setItem(5, LobbyItems.SPECTATE_MENU_ITEM);
            inventory.setItem(3, LobbyItems.SPECTATE_RANDOM_ITEM);
            inventory.setItem(4, LobbyItems.DISABLE_SPEC_MODE_ITEM);

            if (followingSomeone) {
                inventory.setItem(8, LobbyItems.UNFOLLOW_ITEM);
            }
        } else {
            RematchData rematchData = rematchHandler.getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());

                if (target != null) {
                    if (duelHandler.findInvite(player, target) != null) {
                        // if we've sent an invite to them
                        inventory.setItem(2, RematchItems.SENT_REMATCH_ITEM);
                    } else if (duelHandler.findInvite(target, player) != null) {
                        // if they've sent us an invite
                        inventory.setItem(2, RematchItems.ACCEPT_REMATCH_ITEM);
                    } else {
                        // if no one has sent an invite
                        inventory.setItem(2, RematchItems.REQUEST_REMATCH_ITEM);
                    }
                }
            }

            if (queueHandler.isQueuedRanked(player.getUniqueId())) {
                inventory.setItem(8, QueueItems.LEAVE_SOLO_RANKED_QUEUE_ITEM);
            } else if (queueHandler.isQueuedUnranked(player.getUniqueId())) {
                inventory.setItem(8, QueueItems.LEAVE_SOLO_UNRANKED_QUEUE_ITEM);
            } else {
                inventory.setItem(0, QueueItems.JOIN_SOLO_UNRANKED_QUEUE_ITEM);
                inventory.setItem(1, QueueItems.JOIN_SOLO_RANKED_QUEUE_ITEM);
                inventory.setItem(4, LobbyItems.ENABLE_SPEC_MODE_ITEM);
                //inventory.setItem(6, EventItems.EVENTS_ITEM);
                inventory.setItem(7, LobbyItems.PLAYER_STATISTICS);
                inventory.setItem(8, KitItems.OPEN_EDITOR_ITEM);
                }
            }
        }
    }