package net.lugami.basic.commands;

import net.lugami.basic.Basic;
import net.lugami.qlib.command.Command;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DonateCommand {

    private static final FancyMessage MESSAGE = new FancyMessage("You can purchase a rank at: ").color(ChatColor.YELLOW).then("store." + Basic.getInstance().getNetworkWebsite()).link("https://store." + Basic.getInstance().getNetworkWebsite()).color(ChatColor.LIGHT_PURPLE).tooltip(ChatColor.GREEN + "Click to open the store!").then(".").color(ChatColor.YELLOW);

    @Command(names={"donate", "buy", "store"}, permission= "", description = "Be awesome!")
    public static void donate(CommandSender sender) {
        MESSAGE.send(sender);
    }
}

