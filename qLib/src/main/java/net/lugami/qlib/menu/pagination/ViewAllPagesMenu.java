package net.lugami.qlib.menu.pagination;

import net.lugami.qlib.menu.buttons.BackButton;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.Menu;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;

public class ViewAllPagesMenu extends Menu {

    @NonNull PaginatedMenu menu;

    @Override
    public String getTitle(Player player) {
        return "Jump to page";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        buttons.put(0, new BackButton(this.menu));
        int index = 10;
        for (int i = 1; i <= this.menu.getPages(player); ++i) {
            buttons.put(index++, new JumpToPageButton(i, this.menu));
            if ((index - 8) % 9 != 0) continue;
            index += 2;
        }
        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @ConstructorProperties(value={"menu"})
    public ViewAllPagesMenu(@NonNull PaginatedMenu menu) {
        if (menu == null) {
            throw new NullPointerException("menu");
        }
        this.menu = menu;
    }

    @NonNull
    public PaginatedMenu getMenu() {
        return this.menu;
    }
}

