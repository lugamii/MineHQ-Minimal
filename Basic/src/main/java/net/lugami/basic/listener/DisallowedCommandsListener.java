package net.lugami.basic.listener;

import net.lugami.basic.Basic;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class DisallowedCommandsListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().contains(" ") ? event.getMessage().split(" ")[0] : event.getMessage();
        Basic.getInstance().getServerManager().getDisallowedCommands().stream().filter(blocked -> blocked.equalsIgnoreCase(this.strip(command))).forEach(blocked -> {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "This action can only be performed by the console.");
        });
    }

    private String strip(String command) {
        command = command.toLowerCase().replaceFirst("/", "");
        command = command.replace("minecraft:", "");
        command = command.replace("bukkit:", "");
        command = command.replace("worldedit:", "");
        return command;
    }
}

