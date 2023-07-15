package net.lugami.practice.setting.menu;

import com.google.common.base.Preconditions;

import net.lugami.qlib.menu.Button;
import net.lugami.practice.Practice;
import net.lugami.practice.setting.Setting;
import net.lugami.practice.setting.SettingHandler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

/**
 * Button used by {@link SettingsMenu} to render a {@link Setting}
 */
public class SettingButton extends Button {

    private static final String ENABLED_ARROW = ChatColor.BLUE.toString() + ChatColor.BOLD + "  ► ";
    private static final String DISABLED_SPACER = "    ";

    private final Setting setting;

    SettingButton(Setting setting) {
        this.setting = Preconditions.checkNotNull(setting, "setting");
    }

    @Override
    public String getName(Player player) {
        return setting.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();

        description.add("");
        description.addAll(setting.getDescription());
        description.add("");

        if (Practice.getInstance().getSettingHandler().getSetting(player, setting)) {
            description.add(ENABLED_ARROW + setting.getEnabledText());
            description.add(DISABLED_SPACER + setting.getDisabledText());
        } else {
            description.add(DISABLED_SPACER + setting.getEnabledText());
            description.add(ENABLED_ARROW + setting.getDisabledText());
        }

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return setting.getIcon();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (!setting.canUpdate(player)) {
            return;
        }

        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();

        boolean enabled = !settingHandler.getSetting(player, setting);
        settingHandler.updateSetting(player, setting, enabled);
    }

}