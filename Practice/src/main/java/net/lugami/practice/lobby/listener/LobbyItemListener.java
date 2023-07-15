package net.lugami.practice.lobby.listener;

import net.lugami.practice.follow.command.UnfollowCommand;
import net.lugami.practice.lobby.menu.SpectateMenu;
import net.lugami.practice.lobby.menu.StatisticsMenu;
import net.lugami.qlib.qLib;
import net.lugami.practice.Practice;
import net.lugami.practice.commands.ManageCommand;
import net.lugami.practice.lobby.LobbyHandler;
import net.lugami.practice.lobby.LobbyItems;
import net.lugami.practice.match.Match;
import net.lugami.practice.match.MatchHandler;
import net.lugami.practice.match.MatchState;
import net.lugami.practice.util.ItemListener;
import net.lugami.practice.validation.PracticeValidation;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class LobbyItemListener extends ItemListener {

    private final Map<UUID, Long> canUseRandomSpecItem = new HashMap<>();

    public LobbyItemListener(LobbyHandler LobbyHandler) {
        addHandler(LobbyItems.MANAGE_ITEM, p -> {
            // even though we don't shouldn't need to do this
            // we do anyway because of the sensitivity of the manage
            // menu.
            if (p.hasPermission("practice.admin")) {
                ManageCommand.manage(p);
            }
        });

        addHandler(LobbyItems.DISABLE_SPEC_MODE_ITEM, player -> {
            if (LobbyHandler.isInLobby(player)) {
                LobbyHandler.setSpectatorMode(player, false);
            }
        });

        addHandler(LobbyItems.ENABLE_SPEC_MODE_ITEM, player -> {
            if (LobbyHandler.isInLobby(player) && PracticeValidation.canUseSpectateItem(player)) {
                LobbyHandler.setSpectatorMode(player, true);
            }
        });

        addHandler(LobbyItems.SPECTATE_MENU_ITEM, player -> {
            if (PracticeValidation.canUseSpectateItemIgnoreMatchSpectating(player)) {
                new SpectateMenu().openMenu(player);
            }
        });

        addHandler(LobbyItems.SPECTATE_RANDOM_ITEM, player -> {
            MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

            if (!PracticeValidation.canUseSpectateItemIgnoreMatchSpectating(player)) {
                return;
            }

            if (canUseRandomSpecItem.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis()) {
                player.sendMessage(ChatColor.RED + "Please wait before doing this again!");
                return;
            }

            List<Match> matches = new ArrayList<>(matchHandler.getHostedMatches());
            matches.removeIf(m -> m.isSpectator(player.getUniqueId()) || m.getState() == MatchState.ENDING);

            if (matches.isEmpty()) {
                player.sendMessage(ChatColor.RED + "There are no matches available to spectate.");
            } else {
                Match currentlySpectating = matchHandler.getMatchSpectating(player);
                Match newSpectating = matches.get(qLib.RANDOM.nextInt(matches.size()));

                if (currentlySpectating != null) {
                    currentlySpectating.removeSpectator(player, false);
                }

                newSpectating.addSpectator(player, null);
                canUseRandomSpecItem.put(player.getUniqueId(), System.currentTimeMillis() + 3_000L);
            }
        });

        addHandler(LobbyItems.PLAYER_STATISTICS, player -> {
            new StatisticsMenu().openMenu(player);
        });

        addHandler(LobbyItems.UNFOLLOW_ITEM, UnfollowCommand::unfollow);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        canUseRandomSpecItem.remove(event.getPlayer().getUniqueId());
    }

}