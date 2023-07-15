package net.lugami.bridge.bukkit.commands.server;

import net.lugami.bridge.bukkit.commands.server.menu.deployment.DeploymentMenu;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.entity.Player;

public class DeploymentCommand {

    @Command(names = {"deploy", "deployment"}, permission = "bridge.deployment", description = "Deploy a server", hidden = true)
    public static void deployCmd(Player s, @Param(name = "serverName") String serverName) {
        new DeploymentMenu(serverName).openMenu(s);
    }
}
