package net.lugami.practice.kit.menu.kits;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.lugami.qlib.menu.Button;
import net.lugami.practice.Practice;
import net.lugami.practice.kit.Kit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class KitRenameButton extends Button {

    private final Kit kit;

    KitRenameButton(Kit kit) {
        this.kit = Preconditions.checkNotNull(kit, "kit");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Rename";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.YELLOW + "Click to rename this kit."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SIGN;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        ConversationFactory factory = new ConversationFactory(Practice.getInstance()).withFirstPrompt(new StringPrompt() {

            @Override
            public String getPromptText(ConversationContext context) {
                return ChatColor.YELLOW + "Renaming " + ChatColor.BOLD + kit.getName() + ChatColor.YELLOW +"... " + ChatColor.GREEN + "Enter the new name now.";
            }

            @Override
            public Prompt acceptInput(ConversationContext ctx, String s) {
                if (s.length() > 20) {
                    ctx.getForWhom().sendRawMessage(ChatColor.RED + "Kit names can't have more than 20 characters!");
                    return Prompt.END_OF_CONVERSATION;
                }

                kit.setName(s);

                Practice.getInstance().getKitHandler().saveKitsAsync(player);

                ctx.getForWhom().sendRawMessage(ChatColor.YELLOW + "Kit renamed.");
                if (!Practice.getInstance().getMatchHandler().isPlayingMatch(player)) {
                    new KitsMenu(kit.getType()).openMenu(player);
                }
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false);

        player.closeInventory();
        player.beginConversation(factory.buildConversation(player));
    }

}