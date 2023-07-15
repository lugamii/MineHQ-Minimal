package net.lugami.bridge.bukkit.commands.server.menu.server;

import lombok.AllArgsConstructor;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.buttons.BackButton;
import net.lugami.qlib.menu.pagination.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import net.lugami.bridge.bukkit.commands.server.menu.server.buttons.DataButton;
import net.lugami.bridge.global.status.BridgeServer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class ExtraInformationMenu extends PaginatedMenu {

    private final BridgeServer server;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.YELLOW + "Extra information: " + server.getName();
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(4, new BackButton(new ServerMenu()));
        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        AtomicInteger atomicInteger = new AtomicInteger(0);

        try {
            server.getMetadata().entrySet().forEach(str -> {
                buttons.put(atomicInteger.getAndIncrement(), new DataButton(str.getKey(), server.getMetadata().get(str.getKey()).toString()));
            });
        } catch (Exception ex) {
            buttons.put(4, Button.placeholder(Material.REDSTONE_BLOCK, ChatColor.RED + "This server has no extra information."));
    }
        return buttons;

    }
}