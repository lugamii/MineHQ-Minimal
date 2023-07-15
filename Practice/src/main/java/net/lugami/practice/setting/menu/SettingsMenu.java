package net.lugami.practice.setting.menu;

import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.Menu;
import net.lugami.practice.setting.Setting;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Menu used by /settings to let players toggle settings
 */
public final class SettingsMenu extends Menu {

    public SettingsMenu() {
        super("Edit settings");

        setAutoUpdate(true);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (Setting setting : Setting.values()) {
            if (setting.canUpdate(player)) {
                buttons.put(index++, new SettingButton(setting));
            }
        }

        return buttons;
    }

}