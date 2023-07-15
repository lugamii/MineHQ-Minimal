package net.lugami.bridge.bukkit.commands.rank.menu;

import lombok.Getter;
import net.lugami.bridge.bukkit.commands.rank.menu.buttons.PermissionButton;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.buttons.BackButton;
import net.lugami.qlib.menu.pagination.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.Bridge;
import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.packet.types.RankUpdatePacket;
import net.lugami.bridge.global.ranks.Rank;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PermissionsMenu extends PaginatedMenu {

    @Getter private Rank rank;
    private boolean viewAll = false;

    public PermissionsMenu(Rank rank) {
        this.rank = rank;
        setAutoUpdate(true);
    }


    @Override
    public String getPrePaginatedTitle(Player player) {
        String str = rank.getColor() + rank.getDisplayName();
        if(str.length() >= 16) str =str.substring(0, 16);
        return str + ChatColor.YELLOW + "'s Perms";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        buttonMap.put(2, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + (viewAll ? "Hide All" : "View All");
            }

            @Override
            public List<String> getDescription(Player player) {
                return Collections.singletonList("");
            }

            @Override
            public Material getMaterial(Player player) {
                return viewAll ? Material.REDSTONE_BLOCK : Material.EMERALD_BLOCK;
            }

            @Override
            public byte getDamageValue(Player var1) {
                return 0;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                boolean toggle = !viewAll;
                viewAll = toggle;
                player.sendMessage(ChatColor.YELLOW + "You are now " + (toggle ? ChatColor.GREEN : ChatColor.RED + "no longer ") + "viewing" + ChatColor.YELLOW + " permissions on all scopes.");
            }
        });

        buttonMap.put(4, new BackButton(new RankMenu(rank)));

        buttonMap.put(6, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Add Permission";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Collections.singletonList("");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ANVIL;
            }

            @Override
            public byte getDamageValue(Player var1) {
                return 0;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();

                ConversationFactory factory = new ConversationFactory(Bridge.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

                    public String getPromptText(ConversationContext context) {
                        return ChatColor.YELLOW + "Please type a permission you wish to add, or type" + ChatColor.RED + " cancel " + ChatColor.YELLOW + "to cancel";
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext cc, String s) {
                        if (s.equalsIgnoreCase("cancel")) {
                            cc.getForWhom().sendRawMessage(ChatColor.RED + "Permission adding process cancelled.");
                            return Prompt.END_OF_CONVERSATION;
                        }
                        defineScope((Player) cc.getForWhom(), s);
                        return Prompt.END_OF_CONVERSATION;
                    }

                }).withLocalEcho(false).withEscapeSequence("/no").thatExcludesNonPlayersWithMessage("Go away evil console!");

                Conversation con = factory.buildConversation(player);
                player.beginConversation(con);
            }
        });


        return buttonMap;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        AtomicInteger atomicInteger = new AtomicInteger(0);

        if(viewAll)
            rank.getPermissions().keySet().forEach(str -> buttonMap.put(atomicInteger.getAndIncrement(), new PermissionButton(rank, str)));
        else
            rank.getActivePermissions().forEach(str -> buttonMap.put(atomicInteger.getAndIncrement(), new PermissionButton(rank, str)));

        return buttonMap;

    }

    public void defineScope(Player player, String permission) {
        ConversationFactory factory = new ConversationFactory(Bridge.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return ChatColor.YELLOW + "Type the name of the scope you wish to define this permission to. (Examples: Global, GR|Hub, Hub-1)";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if(rank.hasPermission(permission, s)) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "This rank already has this permission defined to the scope.");
                    new PermissionsMenu(rank).openMenu(player);
                    return Prompt.END_OF_CONVERSATION;
                }
                cc.getForWhom().sendRawMessage(ChatColor.YELLOW + "You have added the permission " + ChatColor.RED + permission + ChatColor.YELLOW + " to the scope " + ChatColor.RED + s + ChatColor.YELLOW + ".") ;
                rank.togglePerm(permission, s);
                rank.saveRank();
                PacketHandler.sendToAll(new RankUpdatePacket(rank, player.getName(), BridgeGlobal.getSystemName()));
                new PermissionsMenu(rank).openMenu(player);
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(player);
        player.beginConversation(con);
    }
}