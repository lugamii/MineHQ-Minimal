package net.lugami.practice.kittype.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.practice.kittype.KitType;
import java.util.Comparator;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitSetSortCommand {

	@Command(names = { "kittype setsort" }, permission = "op", description = "Sets a kit-type's sort")
	public static void execute(Player player, @Param(name = "kittype") KitType kitType, @Param(name = "sort") String sort) {
		kitType.setSort(Integer.parseInt(sort));
		kitType.saveAsync();

		KitType.allTypes.sort(Comparator.comparing(KitType::getSort));

		player.sendMessage(ChatColor.GREEN + "You've updated this kit-type's sort.");
	}

}
