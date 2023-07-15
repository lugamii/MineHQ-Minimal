package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import org.bukkit.entity.Player;

public class CraftCommand {

    @Command(names={"craft"}, permission="basic.craft", description = "Opens a crafting table")
    public static void rename(Player sender) {
        sender.openWorkbench(sender.getLocation(), true);
    }
}

