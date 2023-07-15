package net.lugami.practice.arena.command;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.practice.Practice;
import net.lugami.practice.arena.Arena;
import net.lugami.practice.arena.ArenaHandler;
import net.lugami.practice.arena.ArenaSchematic;
import net.lugami.practice.util.LocationUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ArenaListArenasCommand {

    @Command(names = { "arena listArenas" }, permission = "op")
    public static void arenaListArenas(Player sender, @Param(name="schematic") String schematicName) {
        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
        ArenaSchematic schematic = arenaHandler.getSchematic(schematicName);

        if (schematic == null) {
            sender.sendMessage(ChatColor.RED + "Schematic " + schematicName + " not found.");
            sender.sendMessage(ChatColor.RED + "List all schematics with /arena listSchematics");
            return;
        }

        sender.sendMessage(ChatColor.RED + "------ " + ChatColor.WHITE + schematic.getName() + " Arenas" + ChatColor.RED + " ------");

        for (Arena arena : arenaHandler.getArenas(schematic)) {
            String locationStr = LocationUtils.locToStr(arena.getSpectatorSpawn());
            String occupiedStr = arena.isInUse() ? ChatColor.RED + "In Use" : ChatColor.GREEN + "Open";

            sender.sendMessage(arena.getCopy() + ": " + ChatColor.GREEN + locationStr + ChatColor.GRAY + " - " + occupiedStr);
        }
    }

}