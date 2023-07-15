package net.lugami.bridge.bukkit.commands.server.menu.deployment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.Bridge;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeploymentMenu extends Menu {

    private String serverName;

    private Map<File, Boolean> status;

    private boolean complete;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setStatus(Map<File, Boolean> status) {
        this.status = status;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Map<File, Boolean> getStatus() {
        return this.status;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public String getTitle(Player player) {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Deployment - " + this.serverName;
    }

    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();
        List<File> plugins = BridgeGlobal.getUpdaterManager().getFilesForGroup("ALL");
        int i = 0;
        for (File file : plugins) {
            this.status.putIfAbsent(file, Boolean.FALSE);
            buttons.put(i, new PluginButton(this, file));
            i++;
        }
        List<File> scopes = Lists.newArrayList();
        scopes.addAll(this.status.keySet().stream().filter(this.status::get).collect(Collectors.toList()));
        buttons.put(31, new CompleteButton(this, scopes));
        return buttons;
    }

    public void onClose(final Player player) {
        (new BukkitRunnable() {
            public void run() {
                if (!Menu.currentlyOpenedMenus.containsKey(player.getName()) && !DeploymentMenu.this.complete)
                    player.sendMessage(ChatColor.RED + "Server deployment cancelled.");
            }
        }).runTaskLater(Bridge.getInstance(), 1L);
    }

    public DeploymentMenu(String serverName) {
        this.status = Maps.newHashMap();
        this.serverName = serverName;
    }
}
