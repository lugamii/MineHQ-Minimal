
package net.lugami.bridge.bukkit.commands.server.menu.server;

import lombok.AllArgsConstructor;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.buttons.BackButton;
import net.lugami.qlib.menu.menus.ConfirmMenu;
import net.lugami.qlib.menu.pagination.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import net.lugami.bridge.global.status.BridgeServer;
import net.lugami.bridge.global.status.start.ServerStartThread;
import net.lugami.bridge.global.util.JsonChain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class ServerActionMenu extends PaginatedMenu {

    private final BridgeServer server;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.YELLOW + "Server Actions: " + server.getName();
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        buttonMap.put(4, new BackButton(new ServerMenu()));
        return buttonMap;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
//        buttons.put(4, Button.placeholder(Material.REDSTONE_BLOCK, ChatColor.RED + "This server has no additional actions."));
        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return server.isOnline() ? ChatColor.RED + ChatColor.BOLD.toString() + "Shutdown" : ChatColor.GREEN + ChatColor.BOLD.toString() + "Start Up";
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return server.isOnline() ? Material.REDSTONE_BLOCK : Material.EMERALD_BLOCK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new ConfirmMenu("Are you sure?", (b) -> {
                    if (b) {
                        if (server.isOnline()) {
                            new JsonChain().addProperty("server", server.getName()).get();
                        } else {
                            new ServerStartThread(server).start();
                        }
                    }
                    new ServerMenu().openMenu(player);
                }).openMenu(player);
            }
        });

        return buttons;

    }
}