package net.lugami.bridge.bukkit.commands.punishment.menu.button;

import com.google.common.collect.Lists;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.util.TimeUtils;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
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

import java.util.Date;
import java.util.List;

public class ProofButton extends Button {

    private final Punishment punishment;
    private final Profile profile;
    private final Evidence entry;

    public ProofButton(Punishment punishment, Profile profile, Evidence entry){
        this.punishment = punishment;
        this.profile = profile;
        this.entry = entry;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + TimeUtils.formatIntoCalendarString(new Date(punishment.getTime()));
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 25));
        description.add(ChatColor.YELLOW + "Added by: " + ChatColor.RED + entry.getSender());
        description.add(ChatColor.YELLOW + "Proof: " + ChatColor.RED + entry.getURL());
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 25));
        description.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "Left Click to view");
        description.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "this proof");
        description.add(" ");
        description.add(ChatColor.RED.toString() + ChatColor.BOLD + "Right Click to remove");
        description.add(ChatColor.RED.toString() + ChatColor.BOLD + "this proof");
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 25));
        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 5;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        switch(clickType){
            case LEFT: {
                player.sendMessage(ChatColor.YELLOW + entry.getURL());
                break;
            }case RIGHT: {
                startRemoveConversation(player);
            } default: {

            }
        }
    }

    private void startRemoveConversation(Player player){
        player.closeInventory();
        ConversationFactory factory = new ConversationFactory(Bridge.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
            public String getPromptText(ConversationContext context) {
                return ChatColor.YELLOW + "Please type " + ChatColor.RED + "confirm" + ChatColor.YELLOW + " to confirm removal of this proof entry, or type " + ChatColor.RED + "cancel " + ChatColor.YELLOW + "to cancel.";
            }

            public Prompt acceptInput(ConversationContext context, String input) {
                if (input.equalsIgnoreCase("cancel")) {
                    context.getForWhom().sendRawMessage(ChatColor.RED + "Proof entry removal process aborted.");
                    return Prompt.END_OF_CONVERSATION;
                } else if (input.equalsIgnoreCase("confirm")) {


                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            punishment.getProof().remove(entry);
                            profile.saveProfile();
                            player.sendMessage(ChatColor.GREEN + "Successfully removed the proof entry.");
                        }
                    }.runTask(Bridge.getInstance());

                    return Prompt.END_OF_CONVERSATION;
                }else{
                    Bridge.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Bridge.getInstance(), () -> {
                        player.sendMessage(ChatColor.RED + "Invalid response; proof entry removal process aborted.");
                    }, 1L);
                    return Prompt.END_OF_CONVERSATION;
                }
            }
        }).withEscapeSequence("/no").withLocalEcho(false).withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(player);
        player.beginConversation(con);
    }
}
