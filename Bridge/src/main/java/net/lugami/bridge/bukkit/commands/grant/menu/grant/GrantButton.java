package net.lugami.bridge.bukkit.commands.grant.menu.grant;

import com.google.common.collect.Lists;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.util.TimeUtils;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.lugami.bridge.bukkit.commands.grant.events.GrantCreateEvent;
import net.lugami.bridge.global.grant.Grant;
import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.packet.types.GrantCreatePacket;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.ranks.Rank;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public class GrantButton extends Button {
    private Rank rank;
    private String targetName;
    private UUID targetUUID;
    private String reason;
    private ScopesMenu parent;
    private List<String> scopes;
    private long duration;

    public String getName(Player player) {
        return ChatColor.GREEN + "Confirm and Grant";
    }

    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));
        description.add(ChatColor.BLUE + "Click to add the " + ChatColor.WHITE + this.rank.getColor() + this.rank.getDisplayName() + ChatColor.BLUE + " to " + ChatColor.WHITE + this.targetName + ChatColor.BLUE + ".");
        if (this.parent.isGlobal()) {
            description.add(ChatColor.BLUE + "This grant will be " + ChatColor.WHITE + "Global" + ChatColor.BLUE + ".");
        }
        else {
            List<String> scopes = new ArrayList<>(this.scopes);
            description.add(ChatColor.BLUE + "This grant will apply on: " + ChatColor.WHITE + scopes.toString());
        }
        description.add(ChatColor.BLUE + "Reasoning: " + ChatColor.WHITE + this.reason);
        description.add(ChatColor.BLUE + "Duration: " + ChatColor.WHITE + ((this.duration < Long.MAX_VALUE) ? TimeUtils.formatIntoDetailedString((int) (this.duration / 1000)) : "Permanent"));
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));
        return description;
    }

    public Material getMaterial(Player player) {
        return Material.DIAMOND_SWORD;
    }

    public byte getDamageValue(Player player) {
        return 0;
    }

    public void clicked(Player player, int i, ClickType clickType) {
        this.grant(this.targetUUID, this.targetName, this.reason, this.scopes, this.rank, this.duration, player);
        player.closeInventory();
    }

    private void grant(UUID user, String targetName, String reason, List<String> scopes, Rank rank, long duration, Player sender) {
        List<String> finalScopes = (this.parent.isGlobal() || scopes.isEmpty() ? new ArrayList< >(Collections.singleton("Global")) : scopes.stream().map(s -> "GR-" + s).collect(Collectors.toList()));
        Grant grant;
        Profile pr = BukkitAPI.getProfile(user);
        pr.applyGrant((grant=new Grant(rank, duration, finalScopes, reason, sender.getUniqueId().toString(), BridgeGlobal.getSystemName())), sender.getUniqueId());
        pr.saveProfile();
        PacketHandler.sendToAll(new GrantCreatePacket(grant, user, sender.getDisplayName(), BridgeGlobal.getSystemName()));
        new GrantCreateEvent(pr.getUuid(), grant).call();
        sender.sendMessage(ChatColor.GREEN + "Successfully granted " + ChatColor.WHITE + targetName + ChatColor.GREEN + " the " + ChatColor.WHITE + rank.getColor() + rank.getDisplayName() + ChatColor.GREEN + " rank.");
        this.parent.setComplete(true);

    }
    
}
