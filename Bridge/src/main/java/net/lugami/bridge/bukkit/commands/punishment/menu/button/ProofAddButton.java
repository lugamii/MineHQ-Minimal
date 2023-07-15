package net.lugami.bridge.bukkit.commands.punishment.menu.button;

import net.lugami.qlib.menu.Button;
import org.bukkit.scheduler.BukkitRunnable;
import net.lugami.bridge.bukkit.Bridge;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.punishment.Evidence;
import net.lugami.bridge.global.punishment.Punishment;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class ProofAddButton extends Button {

    private final Punishment punishment;
    private final Profile profile;

    public ProofAddButton(Punishment punishment, Profile profile) {
        this.punishment = punishment;
        this.profile = profile;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + ("Add Proof Entry");
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SIGN;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        startAddConversation(player);
    }

    private void startAddConversation(Player player) {
        ConversationFactory factory = new ConversationFactory(Bridge.getInstance()).withFirstPrompt(new StringPrompt() {
            public String getPromptText(ConversationContext context) {
                return ChatColor.YELLOW + "Please type a proof entry to submit, or type " + ChatColor.RED + "cancel " + ChatColor.YELLOW + "to cancel.";
            }

            public Prompt acceptInput(ConversationContext context, String input) {

                if (input.equalsIgnoreCase("cancel")) {
                    context.getForWhom().sendRawMessage(ChatColor.RED + "Proof entry submission process aborted.");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (punishment.getProof().contains(new Evidence(player.getName(), input))) {
                    player.sendMessage(ChatColor.RED + "This punishment already has a proof entry similar to the one you input.");
                    return Prompt.END_OF_CONVERSATION;
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        punishment.getProof().add(new Evidence(player.getName(), input));
                        profile.saveProfile();
                        player.sendMessage(ChatColor.GREEN + "Successfully added proof entry: " + ChatColor.RESET + input);
                    }
                }.runTask(Bridge.getInstance());

                /*punishment.getProof().add(new Evidence(player.getName(), input));
                profile.saveProfile();

                Bridge.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Bridge.getInstance(), () -> {

                    player.sendMessage(ChatColor.GREEN + "Successfully added proof entry: " + ChatColor.RESET + input);
                }, 1L);*/
                return Prompt.END_OF_CONVERSATION;
            }
        }).withLocalEcho(false);

        player.closeInventory();
        player.beginConversation(factory.buildConversation(player));
    }
}