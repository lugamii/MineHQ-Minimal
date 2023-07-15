package net.lugami.bridge.bukkit.commands.punishment.menu.staffhistory;

import com.google.common.collect.Maps;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.pagination.PaginatedMenu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import net.lugami.bridge.global.punishment.Punishment;
import net.lugami.bridge.global.punishment.PunishmentType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class StaffPunishmentListMenu extends PaginatedMenu {

    private String targetName;
    private String targetUUID;
    private PunishmentType type;
    private Map<Punishment, String> punishments;


    public String getPrePaginatedTitle(Player player) {
        return ChatColor.RED + this.type.getDisplayName() + "s";
    }

    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();
        buttons.put(4, new Button() {
            public String getName(Player player) {
                return ChatColor.YELLOW + "Back";
            }

            public List<String> getDescription(Player player) {
                return null;
            }

            public Material getMaterial(Player player) {
                return Material.PAPER;
            }

            public byte getDamageValue(Player player) {
                return 0;
            }

            public void clicked(Player player, int i, ClickType clickType) {
                player.closeInventory();
                new MainStaffPunishmentListMenu(StaffPunishmentListMenu.this.targetUUID, StaffPunishmentListMenu.this.targetName).openMenu(player);
            }
        });
        return buttons;
    }

    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();
        int index = 0;
        for (Map.Entry<Punishment, String> entry : this.punishments.entrySet()) {
            buttons.put(index, new StaffPunishmentButton(entry.getKey()));
            ++index;
        }

        return buttons;
    }

    public StaffPunishmentListMenu(String targetUUID, String targetName, PunishmentType type, Map<Punishment, String> punishments) {
        this.targetUUID = targetUUID;
        this.targetName = targetName;
        this.punishments = punishments;
        this.type = type;
        setAutoUpdate(true);
    }

}
