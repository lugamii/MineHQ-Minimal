package net.lugami.bridge.bukkit.commands.disguise.menu;

import com.google.common.collect.Maps;
import net.lugami.bridge.bukkit.commands.grant.menu.grant.RankButton;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.ranks.Rank;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DisguiseRankMenu extends Menu {

    private String nickName;
    private boolean real;

    public DisguiseRankMenu(String nickName, boolean real) {
        super(ChatColor.DARK_GRAY + "Pick a rank");

        this.nickName = nickName;
        this.real = real;
        this.setPlaceholder(true);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        List<Rank> ranks = BridgeGlobal.getRankHandler().getRanks().stream()
                .filter(rank -> {
                    Profile profile = BukkitAPI.getProfile(player);
                    if(rank.isHidden() || !rank.isGrantable()) return false;
                    if(profile.hasPermission("bridge.disguise.all")) return true;
                    if(profile.hasPermission("bridge.disguise." + rank.getName().toLowerCase())) return true;
                    return Objects.requireNonNull(BukkitAPI.getPlayerRank(profile, true)).getPriority() >= rank.getPriority();
                })
                .sorted(Comparator.comparingInt(Rank::getPriority))
                .collect(Collectors.toList());

        int slot = 1;
        for(Rank rank : ranks) {
            buttons.put(slot, new Button() {

                @Override
                public String getName(Player player) {
                    return rank.getColor() + rank.getDisplayName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    return null;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.LEATHER_CHESTPLATE;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return 0;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    ItemStack item = super.getButtonItem(player);
                    LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                    meta.setColor(RankButton.getColor(rank.getColor().charAt(1)).getColor());
                    item.setItemMeta(meta);
                    return item;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    player.closeInventory();
                    Button.playSuccess(player);
                    new DisguiseSkinMenu(nickName, rank, false, real).openMenu(player);
                }
            });

            slot += slot == 7 || slot == 16 || slot == 25 || slot == 34
                    || slot == 43 || slot == 52 || slot == 61 || slot == 70 ? 3 : 2;
        }

        return buttons;
    }
}
