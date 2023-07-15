package net.lugami.practice.kittype.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.practice.kittype.KitType;
import net.lugami.practice.Practice;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitDeleteCommand {

	@Command(names = { "kittype delete" }, permission = "op", description = "Deletes an existing kit-type")
	public static void execute(Player player, @Param(name = "kittype") KitType kitType) {
		kitType.deleteAsync();
		KitType.getAllTypes().remove(kitType);
		Practice.getInstance().getQueueHandler().removeQueues(kitType);

		player.sendMessage(ChatColor.GREEN + "You've deleted the kit-type by the ID \"" + kitType.id + "\".");
	}

}
