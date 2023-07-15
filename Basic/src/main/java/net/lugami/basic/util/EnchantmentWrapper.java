package net.lugami.basic.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public enum EnchantmentWrapper {

    PROTECTION_ENVIRONMENTAL("Protection", new String[]{"p", "prot", "protect"}),
    PROTECTION_FIRE("Fire Protection", new String[]{"fp", "fprot", "fireprot", "fireprotection", "firep"}),
    PROTECTION_FALL("Feather Falling", new String[]{"ff", "featherf", "ffalling"}),
    PROTECTION_EXPLOSIONS("Blast Protection", new String[]{"explosionsprotection", "explosionprotection", "bprotection", "bprotect", "blastprotect", "pe", "bp"}),
    PROTECTION_PROJECTILE("Projectile Protection", new String[]{"pp", "projprot", "projprotection", "projp", "pprot"}),
    THORNS("Thorns", new String[0]),
    DURABILITY("Unbreaking", new String[]{"unbr", "unb", "dur", "dura"}),
    DAMAGE_ALL("Sharpness", new String[]{"s", "sharp"}),
    DAMAGE_UNDEAD("Smite", new String[]{"du", "dz"}),
    DAMAGE_ARTHROPODS("Bane of Arthropods", new String[]{"bane", "ardmg", "baneofarthropod", "arthropod", "dar", "dspider"}),
    KNOCKBACK("Knockback", new String[]{"k", "knock", "kb"}),
    FIRE_ASPECT("Fire Aspect", new String[]{"fire", "fa"}),
    OXYGEN("Respiration", new String[]{"oxygen", "breathing", "o", "breath"}),
    WATER_WORKER("Aqua Affinity", new String[]{"aa"}),
    LOOT_BONUS_MOBS("Looting", new String[]{"moblooting", "ml", "loot"}),
    DIG_SPEED("Efficiency", new String[]{"e", "eff", "digspeed", "ds"}),
    SILK_TOUCH("Silk Touch", new String[]{"silk", "st"}),
    LOOT_BONUS_BLOCKS("Fortune", new String[]{"fort", "lbm"}),
    ARROW_DAMAGE("Power", new String[]{"apower", "adamage", "admg"}),
    ARROW_KNOCKBACK("Punch", new String[]{"akb", "arrowkb", "arrowknockback", "aknockback"}),
    ARROW_FIRE("Flame", new String[]{"afire", "arrowfire"}),
    ARROW_INFINITE("Infinity", new String[]{"infinitearrows", "infinite", "inf", "infarrows", "unlimitedarrows", "ai", "uarrows", "unlimited"}),
    LUCK("Luck of the Sea", new String[]{"rodluck", "luckofsea", "los"}),
    LURE("Lure", new String[]{"rodlure"});

    private String friendlyName;
    String[] parse;

    private EnchantmentWrapper(String friendlyName, String[] parse) {
        this.friendlyName = friendlyName;
        this.parse = parse;
    }

    public void enchant(ItemStack item, int level) {
        item.addUnsafeEnchantment(this.getBukkitEnchantment(), level);
    }

    public int getMaxLevel() {
        return this.getBukkitEnchantment().getMaxLevel();
    }

    public int getStartLevel() {
        return this.getBukkitEnchantment().getStartLevel();
    }

    public EnchantmentTarget getItemTarget() {
        return this.getBukkitEnchantment().getItemTarget();
    }

    public boolean conflictsWith(Enchantment enchantment) {
        return this.getBukkitEnchantment().conflictsWith(enchantment);
    }

    public boolean canEnchantItem(ItemStack item) {
        return this.getBukkitEnchantment().canEnchantItem(item);
    }

    public String toString() {
        return this.getBukkitEnchantment().toString();
    }

    public Enchantment getBukkitEnchantment() {
        return Enchantment.getByName(this.name());
    }

    public static EnchantmentWrapper parse(String input) {
        for (EnchantmentWrapper enchantment : EnchantmentWrapper.values()) {
            for (String str : enchantment.getParse()) {
                if (!str.equalsIgnoreCase(input)) continue;
                return enchantment;
            }
            if (enchantment.getBukkitEnchantment().getName().replace("_", "").equalsIgnoreCase(input)) {
                return enchantment;
            }
            if (enchantment.getBukkitEnchantment().getName().equalsIgnoreCase(input)) {
                return enchantment;
            }
            if (!enchantment.getFriendlyName().equalsIgnoreCase(input)) continue;
            return enchantment;
        }
        return null;
    }

    public static EnchantmentWrapper parse(Enchantment enchantment) {
        for (EnchantmentWrapper possible : EnchantmentWrapper.values()) {
            if (possible.getBukkitEnchantment() != enchantment) continue;
            return possible;
        }
        throw new IllegalArgumentException("Invalid enchantment given for parsing.");
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }

    public String[] getParse() {
        return this.parse;
    }
}

