package net.lugami.bridge.bukkit.commands.user;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.bukkit.listener.GeneralListener;
import net.lugami.bridge.global.profile.Profile;

public class UserPermissionCommand {

    @Command(names = {"user permission", "user perm"}, permission = "bridge.user", description = "Add/Remove a player's permission", hidden = true, async = true)
    public static void UserPermissionCmd(CommandSender s, @Param(name = "player") Profile pf, @Param(name = "permission") String perm, @Param(name = "group", defaultValue = "§") String serverGroup) {
        String group = serverGroup.equals("§") ? "Global" : serverGroup;
        boolean b = pf.togglePerm(perm, group);
        pf.saveProfile();
        GeneralListener.updatePermissions(pf.getUuid());
        s.sendMessage("§aSuccessfully " + (b ? "added" : "removed") + " the permission " + perm + " to the scope: " + group);
    }
}
