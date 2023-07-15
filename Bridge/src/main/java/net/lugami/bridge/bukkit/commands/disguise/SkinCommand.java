package net.lugami.bridge.bukkit.commands.disguise;

import net.lugami.bridge.bukkit.commands.disguise.menu.DisguiseSkinMenu;
import net.lugami.bridge.global.disguise.runnable.DisguiseRunnable;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.nametag.FrozenNametagHandler;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.disguise.DisguisePlayer;
import net.lugami.bridge.global.disguise.DisguisePlayerSkin;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.ranks.Rank;
import net.lugami.bridge.global.util.Tasks;
import net.lugami.bridge.global.util.mojang.GameProfileUtil;
import net.lugami.bridge.global.util.mojang.UUIDFetcher;

import java.util.*;

public class SkinCommand {

    @Command(names = {"skin"}, permission = "bridge.disguise", async = true, hidden = true)
    public static void skin(Player player, @Param(name = "skin", defaultValue = "N/A") String skin) {
        DisguisePlayer disguisePlayer = BridgeGlobal.getDisguiseManager().getDisguisePlayers().get(player.getUniqueId());
        Profile playerProfile = BridgeGlobal.getProfileHandler().getProfileByUUID(player.getUniqueId());
        Rank current = playerProfile.getCurrentGrant().getRank();

        if (skin.equals("N/A")) {
            skin = disguisePlayer != null ? disguisePlayer.getDisguiseName() : player.getName();
            new DisguiseSkinMenu(skin, disguisePlayer != null ? disguisePlayer.getDisguiseRank() : current != null ? current : BridgeGlobal.getRankHandler().getDefaultRank(), true, true).openMenu(player);
            return;
        }

        if (disguisePlayer != null) {
            disguisePlayer.setDisguiseSkin(skin);

            GameProfile profile = GameProfileUtil.getSkinCache().get(skin.toLowerCase());

            if (profile == null) {
                Map<String, UUID> fetched = null;

                try {
                    fetched = new UUIDFetcher(Collections.singletonList(skin)).call();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Optional<UUID> fetchedUuid = Objects.requireNonNull(fetched).values().stream().findFirst();
                if (!fetchedUuid.isPresent()) {
                    player.sendMessage(ChatColor.RED + "Skin by that name doesn't exist.");
                    return;
                }

                profile = GameProfileUtil.loadGameProfile(fetchedUuid.get(), skin);
            }

            DisguisePlayerSkin fakeSkin = BridgeGlobal.getDisguiseManager().setupDisguiseSkin(profile);

            if (fakeSkin == null) {
                player.sendMessage(ChatColor.RED + "Failed to setup skin.");
                return;
            }

            disguisePlayer.setFakeSkin(fakeSkin);

            Tasks.run(new DisguiseRunnable(player, profile, disguisePlayer.getDisguiseName()));

            Tasks.run(() -> {
                FrozenNametagHandler.reloadPlayer(player);
                FrozenNametagHandler.reloadOthersFor(player);
            });
            Rank rank = disguisePlayer.getDisguiseRank();
            String nickName = disguisePlayer.getDisguiseName();
            player.sendMessage(ChatColor.GREEN + "Success! You now look like " + rank.getPrefix() + rank.getColor() + nickName + ChatColor.GREEN + (!nickName.equals(skin) && !nickName.equals(ChatColor.stripColor(skin)) ? " (in the skin of " + ChatColor.YELLOW + ChatColor.stripColor(skin) + ChatColor.GREEN + ")" : "") + "!");

            String realName = GameProfileUtil.getRealName(nickName);
            if (realName != null) {
                player.sendMessage(ChatColor.RED + nickName + " is an existing Minecraft player, so if they log on for the first time as you're disguised, you will be kicked.");
            }

            String realSkin = GameProfileUtil.getRealName(skin);
            String finalSkin = skin;
            if (realSkin == null && BridgeGlobal.getDisguiseManager().getDisguiseProfiles().values().stream().noneMatch(p -> p.getSkinName().equalsIgnoreCase(finalSkin)) || skin.equals("Steve") || skin.equals("Alex")) {
                player.sendMessage(ChatColor.YELLOW + "Note: You will look like " + (skin.equals("Alex") ? "Alex" : "Steve") + " since the account \"" + skin + "\" does not exist.");
            }
        } else {
            try {
                disguisePlayer = new DisguisePlayer(player.getName());

                disguisePlayer.setDisguiseRank(current != null ? current : BridgeGlobal.getRankHandler().getDefaultRank());
                disguisePlayer.setDisguiseName(player.getName());
                disguisePlayer.setDisguiseSkin(skin);

                Rank rank = disguisePlayer.getDisguiseRank();
                String nickName = disguisePlayer.getDisguiseName();

                if (BridgeGlobal.getDisguiseManager().disguise(player, disguisePlayer, null, false, true, false)) {
                    player.sendMessage(ChatColor.GREEN + "Success! You now look like " + rank.getPrefix() + rank.getColor() + nickName + ChatColor.GREEN + (!nickName.equals(skin) && !nickName.equals(ChatColor.stripColor(skin)) ? " (in the skin of " + ChatColor.YELLOW + ChatColor.stripColor(skin) + ChatColor.GREEN + ")" : "") + "!");

                    String realName = GameProfileUtil.getRealName(nickName);
                    if (realName != null) {
                        player.sendMessage(ChatColor.RED + nickName + " is an existing Minecraft player, so if they log on for the first time as you're disguised, you will be kicked.");
                    }

                    String realSkin = GameProfileUtil.getRealName(skin);
                    String finalSkin = skin;
                    if (realSkin == null && BridgeGlobal.getDisguiseManager().getDisguiseProfiles().values().stream().noneMatch(p -> p.getSkinName().equalsIgnoreCase(finalSkin)) || skin.equals("Steve") || skin.equals("Alex")) {
                        player.sendMessage(ChatColor.YELLOW + "Note: You will look like " + (skin.equals("Alex") ? "Alex" : "Steve") + " since the account \"" + skin + "\" does not exist.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.RED + "Something went wrong while disguising you! Please contact a staff member or any online developer.");
            }
        }
    }
}
