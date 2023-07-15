package net.lugami.bridge.bukkit.commands.updater.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.Menu;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.Bridge;
import net.lugami.bridge.global.updater.UpdateStatus;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter @Setter
public class UpdaterMenu extends Menu {

    private List<String> groups;
    private Map<File, Boolean> status;
    private boolean complete;

    public String getTitle(Player player) {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Updater";
    }

    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();
        List<File> plugins = BridgeGlobal.getUpdaterManager().getFilesForGroup(groups).stream().filter(file -> {
            UpdateStatus updateStatus = BridgeGlobal.getUpdaterManager().getStatus(file);
            return updateStatus != UpdateStatus.ERROR && updateStatus != UpdateStatus.LATEST;
        }).collect(Collectors.toList());

        int i = 0;
        for (File file : plugins) {
            this.status.putIfAbsent(file, false);
            buttons.put(i, new PluginButton(this, file));
            ++i;
        }
        List<File> scopes = Lists.newArrayList();
        scopes.addAll(this.status.keySet().stream().filter(this.status::get).collect(Collectors.toList()));
        buttons.put(31, new CompleteButton(this, scopes));
        return buttons;
    }

    public void onClose(Player player) {
        new BukkitRunnable() {
            public void run() {
                if (!Menu.currentlyOpenedMenus.containsKey(player.getName()) && !UpdaterMenu.this.complete) {
                    player.sendMessage(ChatColor.RED + "Updating cancelled.");
                }
            }
        }.runTaskLater(Bridge.getInstance(), 1L);
    }

    public UpdaterMenu(List<String> groups) {
        this.status = Maps.newHashMap();
        this.groups = groups;
    }
}
