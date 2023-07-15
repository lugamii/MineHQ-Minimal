package net.lugami.practice.kittype.menu.select.teamfights;

import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.Menu;
import net.lugami.qlib.util.Callback;
import net.lugami.practice.kittype.KitType;
import net.lugami.practice.kittype.menu.select.KitTypeButton;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class TeamfightMenu extends Menu {

    private Callback<KitType> callback;

    @Override
    public String getTitle(Player player) {
        return "Edit a Teamfight Kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(buttons.size(), new KitTypeButton(KitType.byId("BARD_HCF"), callback));
        buttons.put(buttons.size(), new KitTypeButton(KitType.byId("ARCHER_HCF"), callback));
        buttons.put(buttons.size(), new KitTypeButton(KitType.byId("DIAMOND_HCF"), callback));

        return buttons;
    }
}
