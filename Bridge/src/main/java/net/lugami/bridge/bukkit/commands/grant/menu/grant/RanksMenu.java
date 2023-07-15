package net.lugami.bridge.bukkit.commands.grant.menu.grant;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.Menu;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.Bridge;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.lugami.bridge.global.ranks.Rank;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class RanksMenu extends Menu {

    private String targetName;
    private UUID targetUUID;

    public String getTitle(Player player) {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Choose a Rank";
    }

    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();
        List<Rank> ranks = this.getAllowedRanks(player);
        for (int i = 0; i < ranks.size(); ++i) {
            buttons.put(i, new RankButton(this.targetName, this.targetUUID, ranks.get(i)));
        }
        return buttons;
    }

    private List<Rank> getAllowedRanks(Player player) {
        List<Rank> allRanks = new ArrayList<>(BridgeGlobal.getRankHandler().getRanks());
        List<Rank> ranks = Lists.newArrayList();
        for (int i = 0; i < allRanks.size(); ++i) {
            //if (i != 0) {
                if(allRanks.get(i).isDefaultRank()) continue;
                if (this.isAllowed(allRanks.get(i), player)) ranks.add(allRanks.get(i));
            //}
        }
        ranks.sort((o1, o2) -> o2.getPriority() - o1.getPriority());
        return ranks;
    }

    private boolean isAllowed(Rank rank, Player player) {
        return BukkitAPI.getProfile(player).hasPermission("bridge.grant.create.*") || BukkitAPI.getProfile(player).hasPermission("bridge.grant.create." + rank.getName());
    }

    public void onClose(Player player) {
        new BukkitRunnable() {
            public void run() {
                if (!Menu.currentlyOpenedMenus.containsKey(player.getName())) player.sendMessage(ChatColor.RED + "Granting cancelled.");
            }
        }.runTaskLater(Bridge.getInstance(), 1L);
    }

}
