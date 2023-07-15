package net.lugami.qlib.menu;

import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.lugami.qlib.qLib;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Menu {
    private static Method openInventoryMethod;
    private final ConcurrentHashMap<Integer, Button> buttons = new ConcurrentHashMap<>();
    private boolean autoUpdate = false;
    private boolean updateAfterClick = true;
    private boolean placeholder = false;
    private boolean noncancellingInventory = false;
    private String staticTitle = null;
    public static Map<String, Menu> currentlyOpenedMenus;
    public static Map<String, BukkitRunnable> checkTasks;

    private Inventory createInventory(Player player) {
        Map<Integer, Button> invButtons = this.getButtons(player);
        Inventory inv = Bukkit.createInventory(player, this.size(invButtons), this.getTitle(player));
        for (Map.Entry<Integer, Button> buttonEntry : invButtons.entrySet()) {
            this.buttons.put(buttonEntry.getKey(), buttonEntry.getValue());
            inv.setItem(buttonEntry.getKey(), buttonEntry.getValue().getButtonItem(player));
        }
        if (this.isPlaceholder()) {
            Button placeholder = Button.placeholder(Material.STAINED_GLASS_PANE, (byte)15);
            for (int index = 0; index < this.size(invButtons); ++index) {
                if (invButtons.get(index) != null) continue;
                this.buttons.put(index, placeholder);
                inv.setItem(index, placeholder.getButtonItem(player));
            }
        }
        return inv;
    }

    private static Method getOpenInventoryMethod() {
        if (openInventoryMethod == null) {
            try {
                openInventoryMethod = CraftHumanEntity.class.getDeclaredMethod("openCustomInventory", Inventory.class, EntityPlayer.class, Integer.TYPE);
                openInventoryMethod.setAccessible(true);
            }
            catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        }
        return openInventoryMethod;
    }

    public Menu() {
    }

    public Menu(String staticTitle) {
        this.staticTitle = (String)Preconditions.checkNotNull((Object)staticTitle);
    }

    public void openMenu(Player player) {
        EntityPlayer ep = ((CraftPlayer)player).getHandle();
        Inventory inv = this.createInventory(player);
        try {
            Menu.getOpenInventoryMethod().invoke(player, inv, ep, 0);
            this.update(player);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void update(final Player player) {
        Menu.cancelCheck(player);
        currentlyOpenedMenus.put(player.getName(), this);
        this.onOpen(player);
        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                if (!player.isOnline()) {
                    Menu.cancelCheck(player);
                    currentlyOpenedMenus.remove(player.getName());
                }

                if (Menu.this.isAutoUpdate()) {
                    player.getOpenInventory().getTopInventory().setContents(Menu.this.createInventory(player).getContents());
                }
            }
        };
        runnable.runTaskTimer(qLib.getInstance(), 10L, 10L);
        checkTasks.put(player.getName(), runnable);
    }

    public static void cancelCheck(Player player) {
        if (checkTasks.containsKey(player.getName())) {
            checkTasks.remove(player.getName()).cancel();
        }
    }

    public int size(Map<Integer, Button> buttons) {
        int highest = 0;
        for (int buttonValue : buttons.keySet()) {
            if (buttonValue <= highest) continue;
            highest = buttonValue;
        }
        return (int)(Math.ceil((double)(highest + 1) / 9.0) * 9.0);
    }

    public int getSlot(int x, int y) {
        return 9 * y + x;
    }

    public String getTitle(Player player) {
        return this.staticTitle;
    }

    public abstract Map<Integer, Button> getButtons(Player var1);

    public void onOpen(Player player) {
    }

    public void onClose(Player player) {
    }

    public ConcurrentHashMap<Integer, Button> getButtons() {
        return this.buttons;
    }

    public boolean isAutoUpdate() {
        return this.autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public boolean isUpdateAfterClick() {
        return this.updateAfterClick;
    }

    public void setUpdateAfterClick(boolean updateAfterClick) {
        this.updateAfterClick = updateAfterClick;
    }

    public boolean isPlaceholder() {
        return this.placeholder;
    }

    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
    }

    public boolean isNoncancellingInventory() {
        return this.noncancellingInventory;
    }

    public void setNoncancellingInventory(boolean noncancellingInventory) {
        this.noncancellingInventory = noncancellingInventory;
    }

    static {
        qLib.getInstance().getServer().getPluginManager().registerEvents(new ButtonListener(), qLib.getInstance());
        currentlyOpenedMenus = new HashMap<>();
        checkTasks = new HashMap<>();
    }

}

