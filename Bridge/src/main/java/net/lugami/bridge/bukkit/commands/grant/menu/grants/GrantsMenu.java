package net.lugami.bridge.bukkit.commands.grant.menu.grants;

import com.google.common.collect.Maps;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.pagination.PaginatedMenu;
import net.lugami.bridge.global.grant.Grant;
import net.lugami.bridge.global.profile.Profile;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Map;

public class GrantsMenu extends PaginatedMenu {

    private Profile profile;
    private List<Grant> grants;

    public GrantsMenu(Profile profile, List<Grant> grants) {
        this.profile = profile;
        this.grants = grants;
        setAutoUpdate(true);
    }

    public String getPrePaginatedTitle(final Player player) {
        return ChatColor.RED + "Grants";
    }

    public Map<Integer, Button> getGlobalButtons(final Player player) {
        final Map<Integer, Button> buttons = Maps.newHashMap();
        buttons.put(4, new Button() {
            public String getName(final Player player) {
                return ChatColor.YELLOW + "Back";
            }

            public List<String> getDescription(final Player player) {
                return null;
            }

            public Material getMaterial(final Player player) {
                return Material.PAPER;
            }

            public byte getDamageValue(final Player player) {
                return 0;
            }

            public void clicked(final Player player, final int i, final ClickType clickType) {
                player.closeInventory();
            }
        });
        return buttons;
    }

    public Map<Integer, Button> getAllPagesButtons(final Player player) {
        final Map<Integer, Button> buttons = Maps.newHashMap();
        int index = 0;
        for (Grant g : grants) {
            buttons.put(index, new GrantsButton(profile, g));
            ++index;
        }
        return buttons;
    }


}
