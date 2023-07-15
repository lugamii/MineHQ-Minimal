package net.lugami.qlib.menu.menus;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;

import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.Menu;
import net.lugami.qlib.util.Callback;
import net.lugami.qlib.menu.buttons.BooleanButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ConfirmMenu extends Menu {

    private final String title;
    private final Callback<Boolean> response;

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
        for (int i = 0; i < 9; ++i) {
            if (i == 3) {
                buttons.put(i, new BooleanButton(true, this.response));
                continue;
            }
            if (i == 5) {
                buttons.put(i, new BooleanButton(false, this.response));
                continue;
            }
            buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)14));
        }
        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return this.title;
    }

    @ConstructorProperties(value={"title", "response"})
    public ConfirmMenu(String title, Callback<Boolean> response) {
        this.title = title;
        this.response = response;
    }
}

