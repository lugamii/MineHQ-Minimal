package net.lugami.qlib.menu.pagination;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

import net.lugami.qlib.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class PageButton extends Button {

    private final int mod;
    private final PaginatedMenu menu;

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        if (clickType == ClickType.RIGHT) {
            new ViewAllPagesMenu(this.menu).openMenu(player);
            PageButton.playNeutral(player);
        } else if (this.hasNext(player)) {
            this.menu.modPage(player, this.mod);
            Button.playNeutral(player);
        } else {
            Button.playFail(player);
        }
    }

    private boolean hasNext(Player player) {
        int pg = this.menu.getPage() + this.mod;
        return pg > 0 && this.menu.getPages(player) >= pg;
    }

    @Override
    public String getName(Player player) {
        if (!this.hasNext(player)) {
            return this.mod > 0 ? "\u00a77Last page" : "\u00a77First page";
        }
        String str = "(\u00a7e" + (this.menu.getPage() + this.mod) + "/\u00a7e" + this.menu.getPages(player) + "\u00a7a)";
        return this.mod > 0 ? "\u00a7a\u27f6" : "\u00a7c\u27f5";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public byte getDamageValue(Player player) {
        return this.hasNext(player) ? (byte)11 : 7;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.CARPET;
    }

    @ConstructorProperties(value={"mod", "menu"})
    public PageButton(int mod, PaginatedMenu menu) {
        this.mod = mod;
        this.menu = menu;
    }
}

