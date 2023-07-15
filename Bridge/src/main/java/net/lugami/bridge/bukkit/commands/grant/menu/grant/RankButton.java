package net.lugami.bridge.bukkit.commands.grant.menu.grant;

import com.google.common.collect.Lists;
import net.lugami.qlib.menu.Button;
import net.lugami.bridge.bukkit.Bridge;
import net.lugami.bridge.global.ranks.Rank;
import net.lugami.bridge.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class RankButton extends Button
{
    private String targetName;
    private UUID targetUUID;
    private Rank rank;

    public String getName(Player player) {
        return this.rank.getColor() + this.rank.getDisplayName();
    }

    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));
        description.add(ChatColor.BLUE + "Click to grant " + ChatColor.WHITE + this.targetName + ChatColor.BLUE + " the " + ChatColor.WHITE + this.rank.getColor() + this.rank.getDisplayName() + ChatColor.BLUE + " rank.");
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));
        return description;
    }

    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    public byte getDamageValue(Player player) {
        return getColor(this.rank.getColor().charAt(1)).getWoolData();
    }

    public void clicked(Player player, int i, ClickType clickType) {
        player.closeInventory();
        ConversationFactory factory = new ConversationFactory(Bridge.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
            public String getPromptText(ConversationContext context) {
                return ChatColor.YELLOW + "Please type a reason for this grant to be added, or type " + ChatColor.RED + "cancel" + ChatColor.YELLOW + " to cancel.";
            }

            public Prompt acceptInput(ConversationContext context, String input) {
                if (input.equalsIgnoreCase("cancel")) {
                    context.getForWhom().sendRawMessage(ChatColor.RED + "Granting cancelled.");
                    return Prompt.END_OF_CONVERSATION;
                }
                new BukkitRunnable() {
                    public void run() {
                        RankButton.this.promptTime(player, input);
                    }
                }.runTask(Bridge.getInstance());
                return Prompt.END_OF_CONVERSATION;
            }
        }).withEscapeSequence("/no").withLocalEcho(false).withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(player);
        player.beginConversation(con);
    }

    private void promptTime(Player player, String reason) {
        ConversationFactory factory = new ConversationFactory(Bridge.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
            public String getPromptText(ConversationContext context) {
                return ChatColor.YELLOW + "Please type a duration for this grant, (\"perm\" for permanent) or type " + ChatColor.RED + "cancel" + ChatColor.YELLOW + " to cancel.";
            }

            public Prompt acceptInput(ConversationContext context, String input) {
                if (input.equalsIgnoreCase("cancel")) {
                    context.getForWhom().sendRawMessage(ChatColor.RED + "Granting cancelled.");
                    return Prompt.END_OF_CONVERSATION;
                }
                long duration = TimeUtil.parseTime(input);
                if (duration != -1L) {
                    new BukkitRunnable() {
                        public void run() {
                            new ScopesMenu(false, false, RankButton.this.rank, RankButton.this.targetName, RankButton.this.targetUUID, reason, duration).openMenu(player);
                        }
                    }.runTask(Bridge.getInstance());
                    return Prompt.END_OF_CONVERSATION;
                }
                context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid duration.");
                return Prompt.END_OF_CONVERSATION;
            }
        }).withEscapeSequence("/no").withLocalEcho(false).withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(player);
        player.beginConversation(con);
    }

    public static DyeColor getColor(char str) {
        ChatColor color = ChatColor.getByChar(str);
        switch (color) {
            case DARK_BLUE:
            case BLUE: {
                return DyeColor.BLUE;
            }
            case DARK_GREEN: {
                return DyeColor.GREEN;
            }
            case DARK_AQUA:
            case AQUA: {
                return DyeColor.CYAN;
            }
            case DARK_RED:
            case RED: {
                return DyeColor.RED;
            }
            case DARK_PURPLE: {
                return DyeColor.PURPLE;
            }
            case GOLD: {
                return DyeColor.ORANGE;
            }
            case GRAY:
            case DARK_GRAY: {
                return DyeColor.GRAY;
            }
            case GREEN: {
                return DyeColor.LIME;
            }
            case LIGHT_PURPLE: {
                return DyeColor.PINK;
            }
            case YELLOW: {
                return DyeColor.YELLOW;
            }
            case WHITE: {
                return DyeColor.WHITE;
            }
            default: {
                return DyeColor.BLACK;
            }
        }
    }

}
