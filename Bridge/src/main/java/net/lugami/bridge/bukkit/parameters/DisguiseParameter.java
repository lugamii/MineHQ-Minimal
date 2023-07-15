package net.lugami.bridge.bukkit.parameters;

import net.lugami.bridge.global.disguise.DisguiseProfile;
import net.lugami.qlib.command.ParameterType;
import net.lugami.bridge.BridgeGlobal;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DisguiseParameter implements ParameterType<DisguiseProfile> {

    public static boolean isUUID(String str) {
        try {
            UUID uuid = UUID.fromString(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public DisguiseProfile transform(CommandSender sender, String source) {
        DisguiseProfile disguiseProfile;
        if (isUUID(source)) {
            disguiseProfile = BridgeGlobal.getDisguiseManager().getDisguiseProfiles().get(UUID.fromString(source));
        } else {
            disguiseProfile = BridgeGlobal.getDisguiseManager().getProfile(source);
        }
        if (disguiseProfile == null) {
            sender.sendMessage("§cThere is no such disguise profile with the " + (isUUID(source) ? "uuid" : "name") + " \"" + source + "\".");
            return null;
        }
        return disguiseProfile;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> set, String s) {
        return null;
    }
}
