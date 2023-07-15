package net.lugami.bridge.bukkit.commands.punishment.menu;

import com.google.common.collect.Maps;
import net.lugami.bridge.bukkit.commands.punishment.menu.button.ProofAddButton;
import net.lugami.bridge.bukkit.commands.punishment.menu.button.ProofButton;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.pagination.PaginatedMenu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.punishment.Evidence;
import net.lugami.bridge.global.punishment.Punishment;
import net.lugami.bridge.global.punishment.PunishmentType;

import java.util.List;
import java.util.Map;

public class ProofMenu extends PaginatedMenu {

    private final Punishment punishment;
    private String targetUUID;
    private String targetName;
    private PunishmentType type;
    private Map<Punishment, String> punishments;
    Profile profile;

    public ProofMenu(Punishment punishment, String targetUUID, PunishmentType type, String targetName, Map<Punishment, String> punishments){
        this.punishment = punishment;
        this.targetUUID = targetUUID;
        this.targetName = targetName;
        this.type = type;
        this.punishments = punishments;
        profile = punishment.getTarget();
    }

    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();
        buttons.put(5, new ProofAddButton(punishment, profile));
        buttons.put(4, new Button() {
            public String getName(Player player) {
                return ChatColor.YELLOW + "Back";
            }

            public List<String> getDescription(Player player) {
                return null;
            }

            public Material getMaterial(Player player) {
                return Material.PAPER;
            }

            public byte getDamageValue(Player player) {
                return 0;
            }

            public void clicked(Player player, int i, ClickType clickType) {
                player.closeInventory();
                new PunishmentMenu(ProofMenu.this.targetUUID, ProofMenu.this.targetName, type, punishments).openMenu(player);
            }
        });
        return buttons;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Proof Editor";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();
        int index = 0;
        for(Evidence entry : punishment.getProof()){
            buttons.put(index, new ProofButton(punishment, profile, entry));
            index++;
        }
        return buttons;
    }
}