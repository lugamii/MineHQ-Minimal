package net.lugami.practice.tournament.menu.button;

import net.lugami.qlib.menu.Button;
import net.lugami.qlib.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class SetRequiredTeamSizeButton extends Button {
    private int size;
    private Callback<Integer> callback;

    public SetRequiredTeamSizeButton(int size, Callback<Integer> callback) {
        this.size = size;
        this.callback = callback;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Edit Required Team Size";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();
        description.add(ChatColor.GOLD + "Current: " + ChatColor.WHITE +  size + "v" + size);
        description.add(" ");
        if (size == 1) {
            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "1v1");
            description.add(ChatColor.GRAY + " 2v2");
            description.add(ChatColor.GRAY + " 3v3");
            description.add(ChatColor.GRAY + " 4v4");
            description.add(ChatColor.GRAY + " 5v5");
            description.add(ChatColor.GRAY + " 6v6");
            description.add(ChatColor.GRAY + " 7v7");
            description.add(ChatColor.GRAY + " 8v8");
            description.add(ChatColor.GRAY + " 9v9");
            description.add(ChatColor.GRAY + " 10v10");
            description.add(ChatColor.GRAY + " 15v15");
            description.add(ChatColor.GRAY + " 20v20");
            description.add(ChatColor.GRAY + " 25v25");
            description.add(ChatColor.GRAY + " 30v30");
        } else if (size == 2) {
            description.add(ChatColor.GRAY + " 1v1");
            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "2v2");
            description.add(ChatColor.GRAY + " 3v3");
            description.add(ChatColor.GRAY + " 4v4");
            description.add(ChatColor.GRAY + " 5v5");
            description.add(ChatColor.GRAY + " 6v6");
            description.add(ChatColor.GRAY + " 7v7");
            description.add(ChatColor.GRAY + " 8v8");
            description.add(ChatColor.GRAY + " 9v9");
            description.add(ChatColor.GRAY + " 10v10");
            description.add(ChatColor.GRAY + " 15v15");
            description.add(ChatColor.GRAY + " 20v20");
            description.add(ChatColor.GRAY + " 25v25");
            description.add(ChatColor.GRAY + " 30v30");
        } else if (size == 3) {
            description.add(ChatColor.GRAY + " 1v1");
            description.add(ChatColor.GRAY + " 2v2");
            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "3v3");
            description.add(ChatColor.GRAY + " 4v4");
            description.add(ChatColor.GRAY + " 5v5");
            description.add(ChatColor.GRAY + " 6v6");
            description.add(ChatColor.GRAY + " 7v7");
            description.add(ChatColor.GRAY + " 8v8");
            description.add(ChatColor.GRAY + " 9v9");
            description.add(ChatColor.GRAY + " 10v10");
            description.add(ChatColor.GRAY + " 15v15");
            description.add(ChatColor.GRAY + " 20v20");
            description.add(ChatColor.GRAY + " 25v25");
            description.add(ChatColor.GRAY + " 30v30");
        } else if (size == 4) {
            description.add(ChatColor.GRAY + " 1v1");
            description.add(ChatColor.GRAY + " 2v2");
            description.add(ChatColor.GRAY + " 3v3");
            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "4v4");
            description.add(ChatColor.GRAY + " 5v5");
            description.add(ChatColor.GRAY + " 6v6");
            description.add(ChatColor.GRAY + " 7v7");
            description.add(ChatColor.GRAY + " 8v8");
            description.add(ChatColor.GRAY + " 9v9");
            description.add(ChatColor.GRAY + " 10v10");
            description.add(ChatColor.GRAY + " 15v15");
            description.add(ChatColor.GRAY + " 20v20");
            description.add(ChatColor.GRAY + " 25v25");
            description.add(ChatColor.GRAY + " 30v30");
        } else if (size == 5) {
            description.add(ChatColor.GRAY + " 1v1");
            description.add(ChatColor.GRAY + " 2v2");
            description.add(ChatColor.GRAY + " 3v3");
            description.add(ChatColor.GRAY + " 4v4");
            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "5v5");
            description.add(ChatColor.GRAY + " 6v6");
            description.add(ChatColor.GRAY + " 7v7");
            description.add(ChatColor.GRAY + " 8v8");
            description.add(ChatColor.GRAY + " 9v9");
            description.add(ChatColor.GRAY + " 10v10");
            description.add(ChatColor.GRAY + " 15v15");
            description.add(ChatColor.GRAY + " 20v20");
            description.add(ChatColor.GRAY + " 25v25");
            description.add(ChatColor.GRAY + " 30v30");
        } else if (size == 6) {
            description.add(ChatColor.GRAY + " 1v1");
            description.add(ChatColor.GRAY + " 2v2");
            description.add(ChatColor.GRAY + " 3v3");
            description.add(ChatColor.GRAY + " 4v4");
            description.add(ChatColor.GRAY + " 5v5");
            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "6v6");
            description.add(ChatColor.GRAY + " 7v7");
            description.add(ChatColor.GRAY + " 8v8");
            description.add(ChatColor.GRAY + " 9v9");
            description.add(ChatColor.GRAY + " 10v10");
            description.add(ChatColor.GRAY + " 15v15");
            description.add(ChatColor.GRAY + " 20v20");
            description.add(ChatColor.GRAY + " 25v25");
            description.add(ChatColor.GRAY + " 30v30");
        } else if (size == 7) {
            description.add(ChatColor.GRAY + " 1v1");
            description.add(ChatColor.GRAY + " 2v2");
            description.add(ChatColor.GRAY + " 3v3");
            description.add(ChatColor.GRAY + " 4v4");
            description.add(ChatColor.GRAY + " 5v5");
            description.add(ChatColor.GRAY + " 6v6");
            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "7v7");
            description.add(ChatColor.GRAY + " 8v8");
            description.add(ChatColor.GRAY + " 9v9");
            description.add(ChatColor.GRAY + " 10v10");
            description.add(ChatColor.GRAY + " 15v15");
            description.add(ChatColor.GRAY + " 20v20");
            description.add(ChatColor.GRAY + " 25v25");
            description.add(ChatColor.GRAY + " 30v30");
        } else if (size == 8) {
            description.add(ChatColor.GRAY + " 1v1");
            description.add(ChatColor.GRAY + " 2v2");
            description.add(ChatColor.GRAY + " 3v3");
            description.add(ChatColor.GRAY + " 4v4");
            description.add(ChatColor.GRAY + " 5v5");
            description.add(ChatColor.GRAY + " 6v6");
            description.add(ChatColor.GRAY + " 7v7");
            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "8v8");
            description.add(ChatColor.GRAY + " 9v9");
            description.add(ChatColor.GRAY + " 10v10");
            description.add(ChatColor.GRAY + " 15v15");
            description.add(ChatColor.GRAY + " 20v20");
            description.add(ChatColor.GRAY + " 25v25");
            description.add(ChatColor.GRAY + " 30v30");
        } else if (size == 9) {
            description.add(ChatColor.GRAY + " 1v1");
            description.add(ChatColor.GRAY + " 2v2");
            description.add(ChatColor.GRAY + " 3v3");
            description.add(ChatColor.GRAY + " 4v4");
            description.add(ChatColor.GRAY + " 5v5");
            description.add(ChatColor.GRAY + " 6v6");
            description.add(ChatColor.GRAY + " 7v7");
            description.add(ChatColor.GRAY + " 8v8");
            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "9v9");
            description.add(ChatColor.GRAY + " 10v10");
            description.add(ChatColor.GRAY + " 15v15");
            description.add(ChatColor.GRAY + " 20v20");
            description.add(ChatColor.GRAY + " 25v25");
            description.add(ChatColor.GRAY + " 30v30");
        } else if (size == 10) {
            description.add(ChatColor.GRAY + " 1v1");
            description.add(ChatColor.GRAY + " 2v2");
            description.add(ChatColor.GRAY + " 3v3");
            description.add(ChatColor.GRAY + " 4v4");
            description.add(ChatColor.GRAY + " 5v5");
            description.add(ChatColor.GRAY + " 6v6");
            description.add(ChatColor.GRAY + " 7v7");
            description.add(ChatColor.GRAY + " 8v8");
            description.add(ChatColor.GRAY + " 9v9");
            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "10v10");
            description.add(ChatColor.GRAY + " 15v15");
            description.add(ChatColor.GRAY + " 20v20");
            description.add(ChatColor.GRAY + " 25v25");
            description.add(ChatColor.GRAY + " 30v30");
        } else if (size == 15) {
            description.add(ChatColor.GRAY + " 1v1");
            description.add(ChatColor.GRAY + " 2v2");
            description.add(ChatColor.GRAY + " 3v3");
            description.add(ChatColor.GRAY + " 4v4");
            description.add(ChatColor.GRAY + " 5v5");
            description.add(ChatColor.GRAY + " 6v6");
            description.add(ChatColor.GRAY + " 7v7");
            description.add(ChatColor.GRAY + " 8v8");
            description.add(ChatColor.GRAY + " 9v9");
            description.add(ChatColor.GRAY + " 10v10");
            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "15v15");
            description.add(ChatColor.GRAY + " 20v20");
            description.add(ChatColor.GRAY + " 25v25");
            description.add(ChatColor.GRAY + " 30v30");
        } else if (size == 20) {
            description.add(ChatColor.GRAY + " 1v1");
            description.add(ChatColor.GRAY + " 2v2");
            description.add(ChatColor.GRAY + " 3v3");
            description.add(ChatColor.GRAY + " 4v4");
            description.add(ChatColor.GRAY + " 5v5");
            description.add(ChatColor.GRAY + " 6v6");
            description.add(ChatColor.GRAY + " 7v7");
            description.add(ChatColor.GRAY + " 8v8");
            description.add(ChatColor.GRAY + " 9v9");
            description.add(ChatColor.GRAY + " 10v10");
            description.add(ChatColor.GRAY + " 15v15");
            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + " 20v20");
            description.add(ChatColor.GRAY + " 25v25");
            description.add(ChatColor.GRAY + " 30v30");
        } else if (size == 25) {
            description.add(ChatColor.GRAY + " 1v1");
            description.add(ChatColor.GRAY + " 2v2");
            description.add(ChatColor.GRAY + " 3v3");
            description.add(ChatColor.GRAY + " 4v4");
            description.add(ChatColor.GRAY + " 5v5");
            description.add(ChatColor.GRAY + " 6v6");
            description.add(ChatColor.GRAY + " 7v7");
            description.add(ChatColor.GRAY + " 8v8");
            description.add(ChatColor.GRAY + " 9v9");
            description.add(ChatColor.GRAY + " 10v10");
            description.add(ChatColor.GRAY + " 15v15");
            description.add(ChatColor.GRAY + " 20v20");
            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "25v25");
            description.add(ChatColor.GRAY + " 30v30");
        } else if (size == 30) {
            description.add(ChatColor.GRAY + " 1v1");
            description.add(ChatColor.GRAY + " 2v2");
            description.add(ChatColor.GRAY + " 3v3");
            description.add(ChatColor.GRAY + " 4v4");
            description.add(ChatColor.GRAY + " 5v5");
            description.add(ChatColor.GRAY + " 6v6");
            description.add(ChatColor.GRAY + " 7v7");
            description.add(ChatColor.GRAY + " 8v8");
            description.add(ChatColor.GRAY + " 9v9");
            description.add(ChatColor.GRAY + " 10v10");
            description.add(ChatColor.GRAY + " 15v15");
            description.add(ChatColor.GRAY + " 20v20");
            description.add(ChatColor.GRAY + " 25v25");
            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "30v30");

        }
        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SKULL_ITEM;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 3;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if(size == 1) {
            size = 2;
            player.sendMessage(ChatColor.GREEN + "Set Required Team Size trait to " + size);
        } else if(size == 2) {
            size = 3;
            player.sendMessage(ChatColor.GREEN + "Set Required Team Size trait to " + size);
        } else if(size == 3) {
            size = 4;
            player.sendMessage(ChatColor.GREEN + "Set Required Team Size trait to " + size);
        } else if(size == 4) {
            size = 5;
            player.sendMessage(ChatColor.GREEN + "Set Required Team Size trait to " + size);
        } else if(size == 5) {
            size = 6;
            player.sendMessage(ChatColor.GREEN + "Set Required Team Size trait to " + size);
        } else if(size == 6) {
            size = 7;
            player.sendMessage(ChatColor.GREEN + "Set Required Team Size trait to " + size);
        } else if(size == 7) {
            size = 8;
            player.sendMessage(ChatColor.GREEN + "Set Required Team Size trait to " + size);
        } else if(size == 8) {
            size = 9;
            player.sendMessage(ChatColor.GREEN + "Set Required Team Size trait to " + size);
        } else if(size == 9) {
            size = 10;
            player.sendMessage(ChatColor.GREEN + "Set Required Team Size trait to " + size);
        } else if(size == 10) {
            size = 15;
            player.sendMessage(ChatColor.GREEN + "Set Required Team Size trait to " + size);
        } else if(size ==15) {
            size = 20;
            player.sendMessage(ChatColor.GREEN + "Set Required Team Size trait to " + size);
        } else if(size == 20) {
            size = 25;
            player.sendMessage(ChatColor.GREEN + "Set Required Team Size trait to " + size);
        } else if(size == 25) {
            size = 30;
            player.sendMessage(ChatColor.GREEN + "Set Required Team Size trait to " + size);
        } else if(size == 30) {
            size = 1;
            player.sendMessage(ChatColor.GREEN + "Set Required Team Size trait to " + size);
        }
        callback.callback(size);
    }
}