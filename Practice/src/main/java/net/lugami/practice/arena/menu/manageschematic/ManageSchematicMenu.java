package net.lugami.practice.arena.menu.manageschematic;

import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.Menu;
import net.lugami.practice.Practice;
import net.lugami.practice.arena.ArenaSchematic;
import net.lugami.practice.arena.menu.manageschematics.ManageSchematicsMenu;
import net.lugami.practice.util.menu.BooleanTraitButton;
import net.lugami.practice.util.menu.IntegerTraitButton;
import net.lugami.practice.util.menu.MenuBackButton;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class ManageSchematicMenu extends Menu {

    private final ArenaSchematic schematic;

    public ManageSchematicMenu(ArenaSchematic schematic) {
        super("Manage " + schematic.getName());
        setAutoUpdate(true);

        this.schematic = schematic;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new SchematicStatusButton(schematic));
        buttons.put(1, new ToggleEnabledButton(schematic));

        buttons.put(3, new TeleportToModelButton(schematic));
        buttons.put(4, new SaveModelButton(schematic));

        if (Practice.getInstance().getArenaHandler().getGrid().isBusy()) {
            Button busyButton = Button.placeholder(Material.WOOL, DyeColor.SILVER.getWoolData(), ChatColor.GRAY.toString() + ChatColor.BOLD + "Grid is busy");

            buttons.put(7, busyButton);
            buttons.put(8, busyButton);
        } else {
            buttons.put(7, new CreateCopiesButton(schematic));
            buttons.put(8, new RemoveCopiesButton(schematic));
        }

        buttons.put(9, new MenuBackButton(p -> new ManageSchematicsMenu().openMenu(p)));

        Consumer<ArenaSchematic> save = schematic -> {
            try {
                Practice.getInstance().getArenaHandler().saveSchematics();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        buttons.put(18, new IntegerTraitButton<>(schematic, "Max Player Count", ArenaSchematic::setMaxPlayerCount, ArenaSchematic::getMaxPlayerCount, save));
        buttons.put(19, new IntegerTraitButton<>(schematic, "Min Player Count", ArenaSchematic::setMinPlayerCount, ArenaSchematic::getMinPlayerCount, save));
        buttons.put(20, new BooleanTraitButton<>(schematic, "Supports Ranked", ArenaSchematic::setSupportsRanked, ArenaSchematic::isSupportsRanked, save));
        buttons.put(21, new BooleanTraitButton<>(schematic, "Archer Only", ArenaSchematic::setArcherOnly, ArenaSchematic::isArcherOnly, save));
        buttons.put(22, new BooleanTraitButton<>(schematic, "Sumo Only", ArenaSchematic::setSumoOnly, ArenaSchematic::isSumoOnly, save));
        buttons.put(23, new BooleanTraitButton<>(schematic, "Spleef Only", ArenaSchematic::setSpleefOnly, ArenaSchematic::isSpleefOnly, save));
        buttons.put(24, new BooleanTraitButton<>(schematic, "BuildUHC Only", ArenaSchematic::setBuildUHCOnly, ArenaSchematic::isBuildUHCOnly, save));
        buttons.put(25, new BooleanTraitButton<>(schematic, "HCF Only", ArenaSchematic::setHCFOnly, ArenaSchematic::isHCFOnly, save));
        buttons.put(26, new BooleanTraitButton<>(schematic, "Team Fights Only", ArenaSchematic::setTeamFightsOnly, ArenaSchematic::isTeamFightsOnly, save));

        return buttons;
    }

}