package net.lugami.basic.commands;

import org.bukkit.scheduler.BukkitRunnable;
import net.lugami.basic.Basic;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand {

    @Command(names = {"screenshare", "ss", "freeze"}, permission = "basic.staff", description = "Freeze and send a message to a player telling them to join TeamSpeak")
    public static void screenshare(CommandSender sender, final @Param(name = "player") Player player) {
        Basic.getInstance().getServerManager().freeze(player);
        sender.sendMessage(player.getDisplayName() + ChatColor.GOLD + " has been frozen.");
        new BukkitRunnable() {

            public void run() {
                if (!player.hasMetadata("frozen")) {
                    this.cancel();
                    return;
                }
                for (int i = 0; i < 5; ++i) {
                    player.sendMessage("");
                }
                FreezeCommand.sendDangerSign(player, "", ChatColor.DARK_RED + "Do " + ChatColor.BOLD + "NOT" + ChatColor.DARK_RED + " log out!", ChatColor.RED + "If you do, you will be banned!", ChatColor.YELLOW + "Please download " + ChatColor.BOLD + "TeamSpeak 3" + ChatColor.YELLOW + " and join", ChatColor.YELLOW + Basic.getInstance().getTeamSpeakIp(), "", "");
                player.sendMessage("");
            }
        }.runTaskTimer(Basic.getInstance(), 0L, 100L);
    }

    private static void sendDangerSign(Player player, String... args) {
        String[] lines = new String[]{"", "", "", "", "", "", ""};
        System.arraycopy(args, 0, lines, 0, args.length);
        player.sendMessage(ChatColor.WHITE + "\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.WHITE + "\u2588\u2588\u2588\u2588" + ChatColor.RESET + (lines[0].isEmpty() ? "" : " " + lines[0]));
        player.sendMessage(ChatColor.WHITE + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.WHITE + "\u2588\u2588\u2588" + ChatColor.RESET + (lines[1].isEmpty() ? "" : " " + lines[1]));
        player.sendMessage(ChatColor.WHITE + "\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.BLACK + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.WHITE + "\u2588\u2588" + ChatColor.RESET + (lines[2].isEmpty() ? "" : " " + lines[2]));
        player.sendMessage(ChatColor.WHITE + "\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.BLACK + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.WHITE + "\u2588\u2588" + ChatColor.RESET + (lines[3].isEmpty() ? "" : " " + lines[3]));
        player.sendMessage(ChatColor.WHITE + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588" + ChatColor.BLACK + "\u2588" + ChatColor.GOLD + "\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.WHITE + "\u2588" + ChatColor.RESET + (lines[4].isEmpty() ? "" : " " + lines[4]));
        player.sendMessage(ChatColor.WHITE + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.WHITE + "\u2588" + ChatColor.RESET + (lines[5].isEmpty() ? "" : " " + lines[5]));
        player.sendMessage(ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.BLACK + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.RESET + (lines[6].isEmpty() ? "" : " " + lines[6]));
    }

    @Command(names = {"unfreeze"}, permission = "basic.staff", description = "Unfreeze a player")
    public static void unfreeze(CommandSender sender, @Param(name = "player") Player player) {
        Basic.getInstance().getServerManager().unfreeze(player.getUniqueId());
        sender.sendMessage(player.getDisplayName() + ChatColor.GOLD + " has been unfrozen.");
    }
}

