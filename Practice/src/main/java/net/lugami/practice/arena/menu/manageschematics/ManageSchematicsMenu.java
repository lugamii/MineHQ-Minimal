package net.lugami.practice.arena.menu.manageschematics;

import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.Menu;
import net.lugami.practice.Practice;
import net.lugami.practice.arena.ArenaHandler;
import net.lugami.practice.arena.ArenaSchematic;
import net.lugami.practice.commands.ManageCommand;
import net.lugami.practice.util.menu.MenuBackButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class ManageSchematicsMenu extends Menu {

    public ManageSchematicsMenu() {
        super("Manage schematics");
        setAutoUpdate(true);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        buttons.put(index++, new MenuBackButton(p -> new ManageCommand.ManageMenu().openMenu(p)));

        for (ArenaSchematic schematic : arenaHandler.getSchematics()) {
            buttons.put(index++, new ManageSchematicButton(schematic));
        }

        return buttons;
    }

}