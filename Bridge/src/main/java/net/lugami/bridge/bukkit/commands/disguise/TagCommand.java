package net.lugami.bridge.bukkit.commands.disguise;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.nametag.FrozenNametagHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.disguise.DisguisePlayer;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.ranks.Rank;

public class TagCommand {

    @Command(names = {"tag"}, permission = "bridge.disguise", async = true, hidden = true)
    public static void tag(Player player, @Param(name = "rank") String rank) {
        Profile profile = BridgeGlobal.getProfileHandler().getProfileByUUID(player.getUniqueId());

        Rank current = profile.getCurrentGrant().getRank();
        Rank select = BridgeGlobal.getRankHandler().getRankByName(rank);

        if (rank.equalsIgnoreCase("clear")) {
            select = current;
            BridgeGlobal.getDisguiseManager().undisguise(player, false, false);
        }

        if (select == null) {
            select = BridgeGlobal.getRankHandler().getDefaultRank();
        }

        if (select.getPriority() > current.getPriority()) {
            player.sendMessage(ChatColor.RED + "You are not allowed to set your tag to " + select.getColor() + select.getDisplayName() + ChatColor.RED + ".");
            return;
        }

        DisguisePlayer disguisePlayer = BridgeGlobal.getDisguiseManager().getDisguisePlayers().get(player.getUniqueId());

        if (disguisePlayer != null) {
            disguisePlayer.setDisguiseRank(select);

            player.setPlayerListName(select.getColor() + disguisePlayer.getDisguiseName());
            player.setDisplayName(select.getColor() + disguisePlayer.getDisguiseName());
            player.setCustomName(select.getColor() + disguisePlayer.getDisguiseName());
            profile.updateColor();
            profile.saveProfile();

            BridgeGlobal.getDisguiseManager().save(player.getUniqueId(), false);
        } else {
            try {
                disguisePlayer = new DisguisePlayer(player.getName());
                disguisePlayer.setDisguiseRank(select);
                disguisePlayer.setDisguiseName(player.getName());
                disguisePlayer.setDisguiseSkin(player.getName());

                BridgeGlobal.getDisguiseManager().disguise(player, disguisePlayer,  null,false, false, false);
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.RED + "Something went wrong while disguising you! Please contact a staff member or any online developer.");
            }
        }

        FrozenNametagHandler.reloadPlayer(player);
        FrozenNametagHandler.reloadOthersFor(player);

        player.sendMessage(ChatColor.GREEN + "Setting your tag to " + select.getColor() + select.getDisplayName() + ChatColor.GREEN + "...");
    }
}
