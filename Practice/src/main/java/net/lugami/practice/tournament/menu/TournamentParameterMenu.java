package net.lugami.practice.tournament.menu;

import com.google.common.base.Preconditions;
import net.lugami.practice.tournament.menu.button.EditRequiredTeamsButton;
import net.lugami.practice.tournament.menu.button.SetRequiredTeamSizeButton;
import net.lugami.practice.tournament.menu.button.StartTournamentButton;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.Menu;
import net.lugami.practice.kittype.KitType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TournamentParameterMenu extends Menu {
        KitType kitType;
    private int size;
    private int size1;

    public TournamentParameterMenu(KitType kitType, String title) {
        this(kitType, true, title);
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.size = 1;
        this.size1 = 128;
setAutoUpdate(true);
    }

    public TournamentParameterMenu(KitType kitType, boolean reset, String title) {
        super(ChatColor.BLUE.toString() + ChatColor.BOLD + "Select a tournament settings");

    }

    @Override
    public void onClose(Player player) {
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(3, new EditRequiredTeamsButton(size1, size1 -> {
            this.size1 = size1;
        }));
            buttons.put(4, new StartTournamentButton(kitType, size, size1));
            buttons.put(5, new SetRequiredTeamSizeButton(size, size -> {
                this.size = size;
            }));


        return buttons;
    }

}