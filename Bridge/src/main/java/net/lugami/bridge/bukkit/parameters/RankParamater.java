package net.lugami.bridge.bukkit.parameters;

import net.lugami.bridge.global.ranks.Rank;
import net.lugami.qlib.command.ParameterType;
import net.lugami.bridge.BridgeGlobal;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RankParamater implements ParameterType<Rank> {

    public static boolean isUUID(String str) {
        try {
            UUID uuid = UUID.fromString(str);
            return true;
        }catch (Exception e) {
            return false;
        }
    }


    @Override
    public Rank transform(CommandSender sender, String source) {
        Rank rank;
        if(isUUID(source)) {
            rank = BridgeGlobal.getRankHandler().getRankByID(UUID.fromString(source));
        }else {
            rank = BridgeGlobal.getRankHandler().getRankByName(source);
        }
        if(rank == null) {
            sender.sendMessage("§cThere is no such rank with the " + (isUUID(source) ? "uuid" : "name") + " \"" + source + "\".");
            return null;
        }
        return rank;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> ranks = new ArrayList<>();
        BridgeGlobal.getRankHandler().getRanks().forEach(rank -> ranks.add(rank.getName()));
        return ranks;
    }
}
