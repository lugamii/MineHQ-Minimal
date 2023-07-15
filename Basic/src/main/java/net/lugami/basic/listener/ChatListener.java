package net.lugami.basic.listener;

import net.lugami.basic.Basic;
import net.lugami.basic.chat.MessagingManager;
import net.lugami.qlib.scoreboard.ScoreFunction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private Map<UUID, Long> lastChatMessage = new HashMap<UUID, Long>();
    private final Map<Pattern, String> mutable = new HashMap<Pattern, String>();

    public ChatListener() {
        this.mutable.put(Pattern.compile("n+[i1l|]+gg+[e3]+r+", 2), "7d");
        this.mutable.put(Pattern.compile("k+i+l+l+ *(y+o+)?u+r+ *s+e+l+f+", 2), "7d");
        this.mutable.put(Pattern.compile("\\bk+y+s+\\b", 2), "7d");
        this.mutable.put(Pattern.compile("\\d{1,3} *\\. *\\d{1,3} *\\. *\\d{1,3} *\\. *\\d{1,3}", 2), "7d");
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!event.isCancelled()) {
            for (Map.Entry<Pattern, String> entry : this.mutable.entrySet()) {
                if (!entry.getKey().matcher(event.getMessage()).find()) continue;
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ("mute " + player.getName() + " " + entry.getValue() + " \"" + event.getMessage() + "\""));
                event.setCancelled(true);
                return;
            }
        }
        if (!player.hasPermission("basic.staff")) {
            if (event.isCancelled()) {
                return;
            }
        }
        if (Basic.getInstance().getChatManager().isMuted() && !player.hasPermission("basic.staff")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Public chat is currently muted.");
            return;
        }
        if (Basic.getInstance().getChatManager().isSlowed() && !player.hasPermission("basic.staff") && this.lastChatMessage.containsKey(player.getUniqueId()) && System.currentTimeMillis() - this.lastChatMessage.get(player.getUniqueId()) < 15000L) {
            long diff = TimeUnit.SECONDS.toMillis(15L) - (System.currentTimeMillis() - this.lastChatMessage.get(player.getUniqueId()));
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Public chat is currently slowed. You can only chat every " + 15 + " seconds.");
            player.sendMessage(ChatColor.RED + "Please wait another " + ChatColor.BOLD + ScoreFunction.TIME_FANCY.apply((float) diff / 1000.0f) + ChatColor.RED + ".");
            return;
        }
        Iterator iterator2 = event.getRecipients().iterator();
        MessagingManager manager = Basic.getInstance().getMessagingManager();
        while (iterator2.hasNext()) {
            Player recipient = (Player)iterator2.next();
            if (!manager.isIgnored(player.getUniqueId(), recipient.getUniqueId())) continue;
            iterator2.remove();
        }
        this.lastChatMessage.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onChatMonitor(AsyncPlayerChatEvent event) {
        event.getPlayer().removeMetadata("NoSpamCheck", Basic.getInstance());
    }
}

