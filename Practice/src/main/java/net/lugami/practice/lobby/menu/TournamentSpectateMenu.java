package net.lugami.practice.lobby.menu;

import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.pagination.PaginatedMenu;
import net.lugami.practice.Practice;
import net.lugami.practice.match.Match;
import net.lugami.practice.match.MatchState;
import net.lugami.practice.match.MatchTeam;
import net.lugami.practice.setting.Setting;
import net.lugami.practice.setting.SettingHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TournamentSpectateMenu extends PaginatedMenu {

    public TournamentSpectateMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Tournament listings";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 0;

        for (Match match : Practice.getInstance().getTournamentHandler().getTournament().getMatches()) {
            // players can view this menu while spectating
            if (match.isSpectator(player.getUniqueId())) {
                continue;
            }

            if (match.getState() == MatchState.ENDING) {
                continue;
            }

            if (Practice.getInstance().getTournamentHandler().isInTournament(match)) {
                int numTotalPlayers = 0;
                int numSpecDisabled = 0;

                for (MatchTeam team : match.getTeams()) {
                    for (UUID member : team.getAliveMembers()) {
                        numTotalPlayers++;

                        if (!settingHandler.getSetting(Bukkit.getPlayer(member), Setting.ALLOW_SPECTATORS)) {
                            numSpecDisabled++;
                        }
                    }
                }

                // if >= 50% of participants have spectators disabled
                // we won't render this match in the menu
                if ((float) numSpecDisabled / (float) numTotalPlayers >= 0.5) {
                    continue;
                }
            }

            buttons.put(i++, new TournamentSpectateButton(match));
        }

        return buttons;
    }

    // we lock the size of this inventory at full, otherwise we'll have
    // issues if it 'grows' into the next line while it's open (say we open
    // the menu with 8 entries, then it grows to 11 [and onto the second row]
    // - this breaks things)
    @Override
    public int size(Map<Integer, Button> buttons) {
        return 9 * 6;
    }

}