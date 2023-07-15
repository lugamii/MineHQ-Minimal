package net.lugami.practice.match;

import lombok.experimental.UtilityClass;
import net.lugami.practice.setting.Setting;
import net.lugami.practice.setting.SettingHandler;
import net.lugami.practice.Practice;
import net.lugami.practice.follow.FollowHandler;
import net.lugami.practice.lobby.LobbyItems;
import net.lugami.practice.party.PartyHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

@UtilityClass
public final class MatchUtils {

    public static void resetInventory(Player player) {
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        FollowHandler followHandler = Practice.getInstance().getFollowHandler();
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        Match match = matchHandler.getMatchSpectating(player);

        // because we lookup their match with getMatchSpectating this will also
        // return for players fighting in matches
        if (match == null) {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);

        // don't give players who die (and cause the match to end)
        // a fire item, they'll be sent back to the player in a few seconds anyway
        if (match.getState() != MatchState.ENDING) {
            // if they've been on any team or are staff they'll be able to
            // use this item on at least 1 player. if they can't use it all
            // we just don't give it to them (UX purposes)
            boolean canViewInventories = player.hasPermission("practice.admin");

            if (!canViewInventories) {
                for (MatchTeam team : match.getTeams()) {
                    if (team.getAllMembers().contains(player.getUniqueId())) {
                        canViewInventories = true;
                        break;
                    }
                }
            }

            // fill inventory with spectator items
            if (canViewInventories) {
                inventory.setItem(0, SpectatorItems.VIEW_INVENTORY_ITEM);
            }
            int slot = canViewInventories ? 1 : 0;

            if (settingHandler.getSetting(player, Setting.VIEW_OTHER_SPECTATORS)) {
                inventory.setItem(slot, SpectatorItems.HIDE_SPECTATORS_ITEM);
            } else {
                inventory.setItem(slot, SpectatorItems.SHOW_SPECTATORS_ITEM);
            }

            // this bit is correct; see SpectatorItems file for more
            if (partyHandler.hasParty(player)) {
                inventory.setItem(8, SpectatorItems.LEAVE_PARTY_ITEM);
            } else {
                inventory.setItem(8, SpectatorItems.RETURN_TO_LOBBY_ITEM);

                if (!followHandler.getFollowing(player).isPresent()) {
                    inventory.setItem(3, LobbyItems.SPECTATE_RANDOM_ITEM);
                    inventory.setItem(4, SpectatorItems.TP_TO_LAST_PVP);
                    inventory.setItem(5, LobbyItems.SPECTATE_MENU_ITEM);
                    inventory.setItem(8, SpectatorItems.RETURN_TO_LOBBY_ITEM);
                }else{
                    if (followHandler.getFollowing(player).isPresent()) {
                        inventory.setItem(2, SpectatorItems.TP_TO_LAST_PVP);
                        inventory.setItem(8, SpectatorItems.RETURN_TO_LOBBY_ITEM);
                    }
                }
            }

            Bukkit.getScheduler().runTaskLater(Practice.getInstance(), player::updateInventory, 1L);
        }

    }
}