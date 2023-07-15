package net.lugami.bridge.bukkit.commands.grant;

import net.lugami.bridge.bukkit.commands.grant.menu.grant.RanksMenu;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.bridge.global.profile.Profile;
import org.bukkit.entity.Player;

public class GrantCommand {

    @Command(names = "grant", permission = "bridge.grant", description = "Add a grant to an player's account", async = true)
    public static void grantCmd(Player p, @Param(name = "player") Profile target) {
        new RanksMenu(target.getUsername(), target.getUuid()).openMenu(p);
    }
}
