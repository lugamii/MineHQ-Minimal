package net.lugami.practice.postmatchinv.menu;

import com.google.common.base.Preconditions;
import net.lugami.qlib.menu.Button;
import net.lugami.practice.postmatchinv.PostMatchPlayer;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class PostMatchSwapTargetButton extends Button {

    private final PostMatchPlayer newTarget;

    PostMatchSwapTargetButton(PostMatchPlayer newTarget) {
        this.newTarget = Preconditions.checkNotNull(newTarget, "newTarget");
    }

    @Override
    public String getName(Player sender) {
        Player player = Bukkit.getPlayer(newTarget.getPlayerUuid());
        return ChatColor.GREEN + "View " + player.getDisguisedName() + "'s inventory";
    }

    @Override
    public List<String> getDescription(Player target) {
        Player player = Bukkit.getPlayer(newTarget.getPlayerUuid());
        return ImmutableList.of(
                "",
                ChatColor.YELLOW + "Swap your view to " + player.getDisguisedName() + "'s inventory"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.LEVER;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        new PostMatchMenu(newTarget).openMenu(player);
    }
}