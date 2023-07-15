package net.lugami.bridge.bukkit.commands.rank;

import net.lugami.bridge.bukkit.commands.rank.menu.RankMenu;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.ranks.Rank;

public class RankManageCommand {

    @Command(names = {"rank manage"}, permission = "bridge.rank", description = "Manage a rank", hidden = true, async = true)
    public static void RankMangeCmd(CommandSender s, @Param(name = "rank") String name) {
        Rank r = BridgeGlobal.getRankHandler().getRankByName(name);
        if (r == null) {
            s.sendMessage("§cThere is no such rank with the name \"" + name + "\".");
            return;
        }

        new RankMenu(r).openMenu((Player) s);
    }
}
