package net.lugami.qlib.util;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lugami.qlib.qLib;
import net.minecraft.util.org.apache.commons.io.IOUtils;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
    private static final Map<String, ItemData> NAME_MAP = new HashMap<String, ItemData>();

    public static void load() {
        NAME_MAP.clear();
        List<String> lines = ItemUtils.readLines();
        for (String line : lines) {
            String[] parts = line.split(",");
            NAME_MAP.put(parts[0], new ItemData(Material.getMaterial((int)Integer.parseInt(parts[1])), Short.parseShort(parts[2])));
        }
    }

    public static void setDisplayName(ItemStack itemStack, String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
    }

    public static ItemBuilder builder(Material type) {
        return new ItemBuilder(type);
    }

    public static ItemStack get(String input, int amount) {
        ItemStack item = ItemUtils.get(input);
        if (item != null) {
            item.setAmount(amount);
        }
        return item;
    }

    public static ItemStack get(String input) {
        if (NumberUtils.isInteger(input = input.toLowerCase().replace(" ", ""))) {
            return new ItemStack(Material.getMaterial((int)Integer.parseInt(input)));
        }
        if (input.contains(":")) {
            if (NumberUtils.isShort(input.split(":")[1])) {
                if (NumberUtils.isInteger(input.split(":")[0])) {
                    return new ItemStack(Material.getMaterial((int)Integer.parseInt(input.split(":")[0])), 1, Short.parseShort(input.split(":")[1]));
                }
                if (!NAME_MAP.containsKey(input.split(":")[0].toLowerCase())) {
                    return null;
                }
                ItemData data = NAME_MAP.get(input.split(":")[0].toLowerCase());
                return new ItemStack(data.getMaterial(), 1, Short.parseShort(input.split(":")[1]));
            }
            return null;
        }
        if (!NAME_MAP.containsKey(input)) {
            return null;
        }
        return NAME_MAP.get(input).toItemStack();
    }

    public static String getName(ItemStack item) {
        String name = CraftItemStack.asNMSCopy(item).getName();
        if (name.contains(".")) {
            name = WordUtils.capitalize((String)item.getType().toString().toLowerCase().replace("_", " "));
        }
        return name;
    }

    private static List<String> readLines() {
        try {
            return IOUtils.readLines((InputStream) qLib.class.getClassLoader().getResourceAsStream("items.csv"));
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final class ItemBuilder {
        private Material type;
        private int amount = 1;
        private short data = 0;
        private String name;
        private List<String> lore = new ArrayList<String>();
        private Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();

        private ItemBuilder(Material type) {
            this.type = type;
        }

        public ItemBuilder type(Material type) {
            this.type = type;
            return this;
        }

        public ItemBuilder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public ItemBuilder data(short data) {
            this.data = data;
            return this;
        }

        public ItemBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ItemBuilder addLore(String ... lore) {
            this.lore.addAll(Arrays.asList(lore));
            return this;
        }

        public ItemBuilder addLore(int index, String lore) {
            this.lore.set(index, lore);
            return this;
        }

        public ItemBuilder setLore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public ItemBuilder enchant(Enchantment enchantment, int level) {
            this.enchantments.put(enchantment, level);
            return this;
        }

        public ItemBuilder unenchant(Enchantment enchantment) {
            this.enchantments.remove((Object)enchantment);
            return this;
        }

        public ItemStack build() {
            ItemStack item = new ItemStack(this.type, this.amount, this.data);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)this.name));
            ArrayList<String> finalLore = new ArrayList<String>();
            for (int index = 0; index < this.lore.size(); ++index) {
                if (this.lore.get(index) == null) {
                    finalLore.set(index, "");
                    continue;
                }
                finalLore.set(index, ChatColor.translateAlternateColorCodes((char)'&', (String)this.lore.get(index)));
            }
            meta.setLore(finalLore);
            for (Map.Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
                item.addUnsafeEnchantment(entry.getKey(), entry.getValue().intValue());
            }
            item.setItemMeta(meta);
            return item;
        }
    }

    public static class ItemData {
        private final Material material;
        private final short data;

        public String getName() {
            return ItemUtils.getName(this.toItemStack());
        }

        public boolean matches(ItemStack item) {
            return item != null && item.getType() == this.material && item.getDurability() == this.data;
        }

        public ItemStack toItemStack() {
            return new ItemStack(this.material, 1, this.data);
        }

        public Material getMaterial() {
            return this.material;
        }

        public short getData() {
            return this.data;
        }

        @ConstructorProperties(value={"material", "data"})
        public ItemData(Material material, short data) {
            this.material = material;
            this.data = data;
        }
    }

}

