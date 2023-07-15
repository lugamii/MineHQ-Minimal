package net.lugami.bridge.bukkit.commands.rank.menu;

import lombok.Getter;
import net.lugami.bridge.bukkit.commands.rank.menu.buttons.PlayerButton;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.buttons.BackButton;
import net.lugami.qlib.menu.pagination.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import net.lugami.bridge.global.ranks.Rank;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerMenu extends PaginatedMenu {

    @Getter private Rank rank;
    private boolean viewAll = false;

    public PlayerMenu(Rank rank) {
        this.rank = rank;
        setAutoUpdate(true);
    }


    @Override
    public String getPrePaginatedTitle(Player player) {
        String str = rank.getColor() + rank.getDisplayName();
        if(str.length() >= 16) str =str.substring(0, 16);
        return str + ChatColor.YELLOW + "'s Users";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        buttonMap.put(2, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + (viewAll ? "Hide All" : "View All");
            }

            @Override
            public List<String> getDescription(Player player) {
                return Collections.singletonList("");
            }

            @Override
            public Material getMaterial(Player player) {
                return viewAll ? Material.REDSTONE_BLOCK : Material.EMERALD_BLOCK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                boolean toggle = !viewAll;
                viewAll = toggle;
                player.sendMessage(ChatColor.YELLOW + "You are now " + (toggle ? ChatColor.GREEN : ChatColor.RED + "no longer ") + "viewing" + ChatColor.YELLOW + " grants on all scopes.");
            }
        });

        buttonMap.put(6, new BackButton(new RankMenu(rank)));

        return buttonMap;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        AtomicInteger atomicInteger = new AtomicInteger(0);

        if(viewAll)
            rank.getProfilesInRank().stream().filter(profile -> profile.hasGrantOf(rank)).forEach(profile -> buttonMap.put(atomicInteger.getAndIncrement(), new PlayerButton(profile)));
        else
            rank.getProfilesInRank().stream().filter(profile -> profile.hasGrantOf(rank, true)).forEach(profile -> buttonMap.put(atomicInteger.getAndIncrement(), new PlayerButton(profile)));

        return buttonMap;

    }
}
