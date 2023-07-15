package net.lugami.qlib.chat;

import com.google.common.primitives.Ints;
import lombok.Getter;
import lombok.Setter;
import net.lugami.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class ChatPlayer {

    private final UUID uuid;
    private final List<ChatPopulator> registeredPopulators = new ArrayList<>();
    @Setter private ChatPopulator selectedPopulator;
    @Setter private boolean chatCancelled = false, cancelCheck = false;

    public ChatPlayer(UUID uuid) {
        this.uuid = uuid;
        ChatHandler.getChatPlayers().add(this);
    }

    public void registerProvider(ChatPopulator newProvider) {
        if(hasAccess(newProvider, false)) return;
        registeredPopulators.add(newProvider);
        registeredPopulators.sort((a, b) -> Ints.compare(a.getOrder(), b.getOrder()));
    }

    public boolean hasAccess(ChatPopulator chatPopulator, boolean sendMsg) {
        boolean b = registeredPopulators.stream().anyMatch(chatPopulator1 -> chatPopulator1.getName().equals(chatPopulator.getName()) && chatPopulator1.getCommandParam().equals(chatPopulator.getCommandParam()));
        if(!b && sendMsg && Bukkit.getPlayer(uuid) != null) Bukkit.getPlayer(uuid).sendMessage(ChatColor.RED + "You can't use this chat channel.");
        return b;
    }

    public void removeProvider(ChatPopulator chatPopulator, ChatPopulator fallbackPopulator) {
        if(!hasAccess(chatPopulator, false)) return;
        if(isSelected(chatPopulator)) setSelectedPopulator(fallbackPopulator);
        getRegisteredPopulators().remove(registeredPopulators.stream().filter(chatPopulator1 -> chatPopulator1.getName().equals(chatPopulator.getName()) && chatPopulator1.getCommandParam().equals(chatPopulator.getCommandParam())).findAny().orElse(null));
    }

    public void removeProvider(ChatPopulator chatPopulator) {
        if(!hasAccess(chatPopulator, false)) return;
        getRegisteredPopulators().remove(registeredPopulators.stream().filter(chatPopulator1 -> chatPopulator1.getName().equals(chatPopulator.getName()) && chatPopulator1.getCommandParam().equals(chatPopulator.getCommandParam())).findAny().orElse(null));
        if(isSelected(chatPopulator)) setSelectedPopulator(registeredPopulators.get(0)); // gives lowest priority chat [hopefully]
    }

    public void setActiveType(ChatPopulator chatPopulator) {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) return;
        if(!hasAccess(chatPopulator, true)) return;
        if(isSelected(chatPopulator)) {
            player.sendMessage(qLib.isBridge() ? ChatColor.YELLOW + "You are already talking in " + ChatColor.RED + chatPopulator.getName() + ChatColor.YELLOW + " chat." : ChatColor.GRAY + "You are already talking in the " + ChatColor.AQUA + chatPopulator.getName() + ChatColor.GRAY + " chat channel.");
            return;
        }
        setSelectedPopulator(chatPopulator);
        player.sendMessage(qLib.isBridge() ? ChatColor.YELLOW + "You are now talking in " + ChatColor.RED + chatPopulator.getName() + ChatColor.YELLOW + " chat." : ChatColor.GRAY + "You are now talking in the " + ChatColor.AQUA + chatPopulator.getName() + ChatColor.GRAY + " chat channel.");
    }

    public boolean isSelected(ChatPopulator chatPopulator) {
        return getSelectedPopulator().getName().equals(chatPopulator.getName()) && getSelectedPopulator().getCommandParam().equals(chatPopulator.getCommandParam());
    }

}
