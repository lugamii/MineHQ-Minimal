package net.lugami.basic.commands;

import net.lugami.basic.Basic;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.hologram.FrozenHologramHandler;
import java.util.ArrayList;
import java.util.Map;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.lugami.qlib.hologram.construct.Hologram;

public class HologramCommands {

    @Command(names={"hologram create", "holo create"}, permission="basic.holograms")
    public static void hologram_create(Player sender, @Param(name="text", wildcard=true) String text) {
        Hologram hologram = FrozenHologramHandler.createHologram().at(sender.getEyeLocation()).addLines(text).build();
        hologram.send();
        int id = Basic.getInstance().getHologramManager().register(hologram);
        sender.sendMessage(ChatColor.GOLD + "Hologram " + ChatColor.WHITE + "#" + id + ChatColor.GOLD + " has been created.");
    }

    @Command(names={"hologram addline", "holo addline"}, permission="basic.holograms")
    public static void hologram_addline(Player sender, @Param(name="id") int id, @Param(name="text", wildcard=true) String text) {
        Hologram hologram = Basic.getInstance().getHologramManager().getHolograms().get(id);
        if (hologram == null) {
            sender.sendMessage(ChatColor.RED + "No hologram with that id found.");
            return;
        }
        hologram.addLines(text);
        Basic.getInstance().getHologramManager().save();
    }

    @Command(names={"hologram removeline", "holo removeline"}, permission="basic.holograms")
    public static void hologram_removeline(Player sender, @Param(name="id") int id, @Param(name="lineNumber") int line) {
        Hologram hologram = Basic.getInstance().getHologramManager().getHolograms().get(id);
        if (--line < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid index.");
            return;
        }
        if (hologram == null) {
            sender.sendMessage(ChatColor.RED + "No hologram with that id found.");
            return;
        }
        ArrayList<String> lines = new ArrayList<>(hologram.getLines());
        try {
            lines.remove(line);
            hologram.setLines(lines);
            Basic.getInstance().getHologramManager().save();
            sender.sendMessage(ChatColor.GREEN + "Success.");
        }
        catch (IndexOutOfBoundsException e) {
            sender.sendMessage(ChatColor.RED + "Invalid index.");
        }
    }

    @Command(names={"hologram insertbefore", "holo insertbefore"}, permission="basic.holograms")
    public static void hologram_insertbefore(Player sender, @Param(name="id") int id, @Param(name="beforeLineNumber") int line, @Param(name="text", wildcard=true) String text) {
        if (--line < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid index.");
            return;
        }
        Hologram hologram = Basic.getInstance().getHologramManager().getHolograms().get(id);
        if (hologram == null) {
            sender.sendMessage(ChatColor.RED + "No hologram with that id found.");
            return;
        }
        ArrayList<String> lines = new ArrayList<String>(hologram.getLines());
        try {
            lines.add(line, text);
            hologram.setLines(lines);
            Basic.getInstance().getHologramManager().save();
            sender.sendMessage(ChatColor.GREEN + "Success.");
        }
        catch (IndexOutOfBoundsException e) {
            sender.sendMessage(ChatColor.RED + "Invalid index.");
        }
    }

    @Command(names={"hologram insertafter", "holo insertafter"}, permission="basic.holograms")
    public static void hologram_insertafter(Player sender, @Param(name="id") int id, @Param(name="afterLineNumber") int line, @Param(name="text", wildcard=true) String text) {
        if (--line < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid index.");
            return;
        }
        Hologram hologram = Basic.getInstance().getHologramManager().getHolograms().get(id);
        if (hologram == null) {
            sender.sendMessage(ChatColor.RED + "No hologram with that id found.");
            return;
        }
        ArrayList<String> lines = new ArrayList<String>(hologram.getLines());
        try {
            lines.add(line + 1, text);
            hologram.setLines(lines);
            Basic.getInstance().getHologramManager().save();
            sender.sendMessage(ChatColor.GREEN + "Success.");
        }
        catch (IndexOutOfBoundsException e) {
            sender.sendMessage(ChatColor.RED + "Invalid index.");
        }
    }

    @Command(names={"hologram edit", "holo edit"}, permission="basic.holograms")
    public static void hologram_edit(Player sender, @Param(name="id") int id, @Param(name="lineToEdit") int line, @Param(name="newText", wildcard=true) String newText) {
        if (--line < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid index.");
            return;
        }
        Hologram hologram = Basic.getInstance().getHologramManager().getHolograms().get(id);
        if (hologram == null) {
            sender.sendMessage(ChatColor.RED + "No hologram with that id found.");
            return;
        }
        hologram.setLine(line, newText);
        Basic.getInstance().getHologramManager().save();
        sender.sendMessage(ChatColor.GREEN + "Success.");
    }

    @Command(names={"hologram list", "holo list"}, permission="basic.holograms")
    public static void hologram_list(Player sender) {
        if (Basic.getInstance().getHologramManager().getHolograms().size() == 0) {
            sender.sendMessage(ChatColor.RED + "There are no active holograms.");
            return;
        }
        for (Map.Entry<Integer, Hologram> entry : Basic.getInstance().getHologramManager().getHolograms().entrySet()) {
            ArrayList<String> tooltip = new ArrayList<String>();
            tooltip.add(ChatColor.GREEN + "Location: " + String.format("[%.1f, %.1f, %.1f]", entry.getValue().getLocation().getX(), entry.getValue().getLocation().getY(), entry.getValue().getLocation().getZ()));
            tooltip.add(ChatColor.YELLOW + "Click to teleport");
            tooltip.add("");
            int i = 0;
            for (String line : entry.getValue().getLines()) {
                line = ChatColor.translateAlternateColorCodes('&', line);
                tooltip.add(ChatColor.GRAY.toString() + ++i + ". " + ChatColor.RESET + line);
            }
            FancyMessage message = new FancyMessage("#" + entry.getKey()).color(ChatColor.RED).tooltip(tooltip).command("/tppos " + String.format("%.1f %.1f %.1f", entry.getValue().getLocation().getX(), entry.getValue().getLocation().getY(), entry.getValue().getLocation().getZ()));
            message.send(sender);
        }
    }

    @Command(names={"hologram movehere", "holo tphere"}, permission="basic.holograms")
    public static void hologram_movehere(Player sender, @Param(name="id") int id) {
        Hologram hologram = Basic.getInstance().getHologramManager().getHolograms().get(id);
        if (hologram == null) {
            sender.sendMessage(ChatColor.RED + "No hologram with that id found.");
            return;
        }
        Basic.getInstance().getHologramManager().move(id, sender.getEyeLocation());
    }

    @Command(names={"hologram delete", "holo delete"}, permission="basic.holograms")
    public static void hologram_delete(Player sender, @Param(name="id") int id) {
        Hologram hologram = Basic.getInstance().getHologramManager().getHolograms().get(id);
        if (hologram == null) {
            sender.sendMessage(ChatColor.RED + "No hologram with that id found.");
            return;
        }
        Basic.getInstance().getHologramManager().getHolograms().remove(id);
        Basic.getInstance().getHologramManager().save();
        hologram.destroy();
        sender.sendMessage(ChatColor.YELLOW + "Deleted Hologram #" + id + "");
    }
}

