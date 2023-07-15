package net.lugami.bridge.bukkit.util;

import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.Bridge;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;

public class BukkitUtils {

    public static void registerListeners(Class<?>... listeners) {
        for (Class<?> listener : listeners) {
            if (Listener.class.isAssignableFrom(listener)) {
                try {
                    Bukkit.getServer().getPluginManager().registerEvents(((Listener) listener.newInstance()), Bridge.getInstance());
                    BridgeGlobal.sendLog("Successfully registered listener " + listener.getSimpleName() + "!");
                } catch (Exception ex) {
                    BridgeGlobal.sendLog("Failed to registered listener "   + listener.getSimpleName() + ". (" + ex.getClass().getSimpleName() + ": " + ex.getMessage() + ")");
                }
            } else {
                BridgeGlobal.sendLog("The class: " + listener.getSimpleName() + " does not parameters Listener!");
            }
        }
    }

    public static Material materialFix(String name) {
        Material materialFromDB;
        try {
            materialFromDB = (Material) Enum.valueOf((Class) Material.class, name);
        } catch (IllegalArgumentException ignore) {
            materialFromDB = (Material) Enum.valueOf((Class) Material.class, "LEGACY_" + name);
        }
        return materialFromDB;

    }

    public static int stringToWoolColor(String r) {
        String fixed = r.replaceAll("§l", "").replaceAll("§r", "").replaceAll("§k", "").replaceAll("§n", "").replaceAll("§m", "").replaceAll("§o", "");
        switch (fixed) {
            case "§1":
                return 11;
            case "§2":
                return 13;

            case "§3":
                return 9;

            case "§4":
                return 14;

            case "§5":
                return 10;

            case "§6":
                return 1;

            case "§7":
                return 8;

            case "§8":
                return 7;

            case "§9":
                return 11;

            case "§0":
                return 15;

            case "§a":
                return 5;

            case "§b":

                return 3;
            case "§c":
                return 14;

            case "§d":
                return 6;

            case "§e":

                return 4;
            case "§f":
                return 0;

            default:
                return 0;
        }
    }

}
