package net.lugami.qlib.menu;

import net.lugami.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ButtonListener implements Listener {

    @EventHandler(priority=EventPriority.MONITOR)
    public void onButtonPress(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());
        if (openMenu != null) {
            if (event.getSlot() != event.getRawSlot()) {
                if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                    event.setCancelled(true);
                    if (openMenu.isNoncancellingInventory() && event.getCurrentItem() != null) {
                        player.getOpenInventory().getTopInventory().addItem(event.getCurrentItem());
                        event.setCurrentItem(null);
                    }
                }
                return;
            }
            if (openMenu.getButtons().containsKey(event.getSlot())) {
                Menu newMenu;
                Button button = openMenu.getButtons().get(event.getSlot());
                boolean cancel = button.shouldCancel(player, event.getSlot(), event.getClick());
                if (!(cancel || event.getClick() != ClickType.SHIFT_LEFT && event.getClick() != ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);
                    if (event.getCurrentItem() != null) {
                        player.getInventory().addItem(event.getCurrentItem());
                    }
                } else {
                    event.setCancelled(cancel);
                }
                button.clicked(player, event.getSlot(), event.getClick());
                if (Menu.currentlyOpenedMenus.containsKey(player.getName()) && (newMenu = Menu.currentlyOpenedMenus.get(player.getName())) == openMenu && newMenu.isUpdateAfterClick()) {
                    newMenu.openMenu(player);
                }
                if (event.isCancelled()) {
                    Bukkit.getScheduler().runTaskLater(qLib.getInstance(), player::updateInventory, 1L);
                }
            } else if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                event.setCancelled(true);
                if (openMenu.isNoncancellingInventory() && event.getCurrentItem() != null) {
                    player.getOpenInventory().getTopInventory().addItem(event.getCurrentItem());
                    event.setCurrentItem(null);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());
        if (openMenu != null) {
            openMenu.onClose(player);
            Menu.cancelCheck(player);
            Menu.currentlyOpenedMenus.remove(player.getName());
        }
    }
}

