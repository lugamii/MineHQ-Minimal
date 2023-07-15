package net.lugami.practice.tab;

import net.lugami.practice.match.Match;
import net.lugami.qlib.tab.LayoutProvider;
import net.lugami.qlib.tab.TabLayout;
import net.lugami.qlib.util.PlayerUtils;
import net.lugami.practice.Practice;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.BiConsumer;

public final class PracticeLayoutProvider implements LayoutProvider {

    static final int MAX_TAB_Y = 20;

    private final BiConsumer<Player, TabLayout> headerLayoutProvider = new HeaderLayoutProvider();
    private final BiConsumer<Player, TabLayout> lobbyLayoutProvider = new LobbyLayoutProvider();
    private final BiConsumer<Player, TabLayout> matchSpectatorLayoutProvider = new MatchSpectatorLayoutProvider();
    private final BiConsumer<Player, TabLayout> matchParticipantLayoutProvider = new MatchParticipantLayoutProvider();

    @Override
    public TabLayout provide(Player player) {
        Match match = Practice.getInstance().getMatchHandler().getMatchPlayingOrSpectating(player);
        TabLayout tabLayout = TabLayout.create(player);

        headerLayoutProvider.accept(player, tabLayout);

        if (match != null) {
            if (match.isSpectator(player.getUniqueId())) {
                matchSpectatorLayoutProvider.accept(player, tabLayout);
            } else {
                matchParticipantLayoutProvider.accept(player, tabLayout);
            }
        } else {
            lobbyLayoutProvider.accept(player, tabLayout);
        }

        return tabLayout;
    }

    static int getPingOrDefault(UUID check) {
        Player player = Bukkit.getPlayer(check);
        return player != null ? PlayerUtils.getPing(player) : 0;
    }
}