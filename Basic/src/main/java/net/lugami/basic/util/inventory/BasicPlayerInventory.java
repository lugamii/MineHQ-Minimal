package net.lugami.basic.util.inventory;

import net.lugami.basic.Basic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.PlayerInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BasicPlayerInventory extends PlayerInventory {

    private static final Map<UUID, BasicPlayerInventory> storage = new HashMap<>();
    private static final Set<UUID> open = new HashSet<>();
    private CraftPlayer owner;
    public boolean playerOnline;
    private ItemStack[] extra = new ItemStack[5];
    private CraftInventory inventory = new CraftInventory(this);

    public static BasicPlayerInventory get(Player player) {
        return storage.getOrDefault(player.getUniqueId(), new BasicPlayerInventory(player));
    }

    private BasicPlayerInventory(Player player) {
        super(((CraftPlayer)player).getHandle());
        this.owner = (CraftPlayer)player;
        this.playerOnline = player.isOnline();
        this.items = this.player.inventory.items;
        this.armor = this.player.inventory.armor;
        storage.put(this.owner.getUniqueId(), this);
    }

    public Inventory getBukkitInventory() {
        return this.inventory;
    }

    public void removalCheck() {
        Bukkit.getScheduler().runTaskAsynchronously(Basic.getInstance(), () -> this.owner.saveData());
        if (this.transaction.isEmpty() && !this.playerOnline) {
            storage.remove(this.owner.getUniqueId());
        }
    }

    public void onJoin(Player joined) {
        if (!this.playerOnline) {
            CraftPlayer player = (CraftPlayer)joined;
            player.getHandle().inventory.items = this.items;
            player.getHandle().inventory.armor = this.armor;
            this.playerOnline = true;
            Bukkit.getScheduler().runTaskAsynchronously(Basic.getInstance(), () -> this.owner.saveData());
        }
    }

    public void onQuit() {
        this.playerOnline = false;
        this.removalCheck();
    }

    public void onClose(CraftHumanEntity who) {
        super.onClose(who);
        if (who instanceof Player && !this.playerOnline) {
            ((Player)who).sendMessage(ChatColor.RED + "Saving inventory for offline player.");
        }
        open.remove(who.getUniqueId());
        this.removalCheck();
    }

    public ItemStack[] getContents() {
        ItemStack[] contents = new ItemStack[this.getSize()];
        System.arraycopy(this.items, 0, contents, 0, this.items.length);
        System.arraycopy(this.items, 0, contents, this.items.length, this.armor.length);
        return contents;
    }

    public int getSize() {
        return super.getSize() + 5;
    }

    public ItemStack getItem(int i) {
        ItemStack[] is = this.items;
        if (i >= is.length) {
            i -= is.length;
            is = this.armor;
        } else {
            i = this.getReversedItemSlotNum(i);
        }
        if (i >= is.length) {
            i -= is.length;
            is = this.extra;
        } else if (is == this.armor) {
            i = this.getReversedArmorSlotNum(i);
        }
        return is[i];
    }

    public ItemStack splitStack(int i, int j) {
        ItemStack[] is = this.items;
        if (i >= is.length) {
            i -= is.length;
            is = this.armor;
        } else {
            i = this.getReversedItemSlotNum(i);
        }
        if (i >= is.length) {
            i -= is.length;
            is = this.extra;
        } else if (is == this.armor) {
            i = this.getReversedArmorSlotNum(i);
        }
        if (is[i] == null) {
            return null;
        }
        if (is[i].count <= j) {
            ItemStack itemstack = is[i];
            is[i] = null;
            return itemstack;
        }
        ItemStack itemstack = is[i].a(j);
        if (is[i].count == 0) {
            is[i] = null;
        }
        return itemstack;
    }

    public ItemStack splitWithoutUpdate(int i) {
        ItemStack[] is = this.items;
        if (i >= is.length) {
            i -= is.length;
            is = this.armor;
        } else {
            i = this.getReversedItemSlotNum(i);
        }
        if (i >= is.length) {
            i -= is.length;
            is = this.extra;
        } else if (is == this.armor) {
            i = this.getReversedArmorSlotNum(i);
        }
        if (is[i] != null) {
            ItemStack itemstack = is[i];
            is[i] = null;
            return itemstack;
        }
        return null;
    }

    public void setItem(int i, ItemStack itemstack) {
        ItemStack[] is = this.items;
        if (i >= is.length) {
            i -= is.length;
            is = this.armor;
        } else {
            i = this.getReversedItemSlotNum(i);
        }
        if (i >= is.length) {
            i -= is.length;
            is = this.extra;
        } else if (is == this.armor) {
            i = this.getReversedArmorSlotNum(i);
        }
        if (is == this.extra) {
            this.owner.getHandle().drop(itemstack, true);
            itemstack = null;
        }
        is[i] = itemstack;
        this.owner.getHandle().defaultContainer.b();
    }

    private int getReversedItemSlotNum(int i) {
        return i >= 27 ? i - 27 : i + 9;
    }

    private int getReversedArmorSlotNum(int i) {
        switch (i) {
            case 0: {
                return 3;
            }
            case 1: {
                return 2;
            }
            case 2: {
                return 1;
            }
            case 3: {
                return 0;
            }
        }
        return i;
    }

    public String getInventoryName() {
        return "Inventory: " + this.owner.getDisplayName();
    }

    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    public static void open(Player owner, Player openFor) {
        BasicPlayerInventory inventory = storage.containsKey(owner.getUniqueId()) ? storage.get(owner.getUniqueId()) : new BasicPlayerInventory(owner);
        openFor.openInventory(inventory.getBukkitInventory());
        open.add(openFor.getUniqueId());
    }

    public static boolean isViewing(Player player) {
        return open.contains(player.getUniqueId());
    }

    public static void join(Player player) {
        if (storage.containsKey(player.getUniqueId())) {
            storage.get(player.getUniqueId()).onJoin(player);
        }
    }

    public static void quit(Player player) {
        if (storage.containsKey(player.getUniqueId())) {
            storage.get(player.getUniqueId()).onQuit();
        }
    }

    public static Map<UUID, BasicPlayerInventory> getStorage() {
        return storage;
    }

    public static Set<UUID> getOpen() {
        return open;
    }
}

