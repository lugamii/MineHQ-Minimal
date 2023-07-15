package net.lugami.qlib.visibility;

import com.google.common.base.Preconditions;
import net.lugami.qlib.qLib;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

@NoArgsConstructor
public class FrozenVisibilityHandler {

    private static final Map<String, VisibilityHandler> handlers = new LinkedHashMap<>();
    private static final Map<String, OverrideHandler> overrideHandlers = new LinkedHashMap<>();
    private static boolean initiated = false;

    public static void init() {
        Preconditions.checkState(!FrozenVisibilityHandler.initiated);
        FrozenVisibilityHandler.initiated = true;
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.LOWEST)
            public void onPlayerJoin(final PlayerJoinEvent event) {
                FrozenVisibilityHandler.update(event.getPlayer());
            }

            @EventHandler(priority = EventPriority.LOWEST)
            public void onTabComplete(final PlayerChatTabCompleteEvent event) {
                final String token = event.getLastToken();
                final Collection<String> completions = event.getTabCompletions();
                completions.clear();
                for (final Player target : Bukkit.getOnlinePlayers()) {
                    if (!FrozenVisibilityHandler.treatAsOnline(target, event.getPlayer())) {
                        continue;
                    }
                    if (!StringUtils.startsWithIgnoreCase(target.getName(), token)) {
                        continue;
                    }
                    completions.add(target.getName());
                }
            }
        }, qLib.getInstance());
    }

    public static void registerHandler(final String identifier, final VisibilityHandler handler) {
        FrozenVisibilityHandler.handlers.put(identifier, handler);
    }

    public static void registerOverride(final String identifier, final OverrideHandler handler) {
        FrozenVisibilityHandler.overrideHandlers.put(identifier, handler);
    }

    public static void update(final Player player) {
        if (FrozenVisibilityHandler.handlers.isEmpty() && FrozenVisibilityHandler.overrideHandlers.isEmpty()) {
            return;
        }
        updateAllTo(player);
        updateToAll(player);
    }

    @Deprecated
    public static void updateAllTo(final Player viewer) {
        for (final Player target : Bukkit.getOnlinePlayers()) {
            if (!shouldSee(target, viewer)) {
                viewer.hidePlayer(target);
            }
            else {
                viewer.showPlayer(target);
            }
        }
    }

    @Deprecated
    public static void updateToAll(final Player target) {
        for (final Player viewer : Bukkit.getOnlinePlayers()) {
            if (!shouldSee(target, viewer)) {
                viewer.hidePlayer(target);
            }
            else {
                viewer.showPlayer(target);
            }
        }
    }

    public static boolean treatAsOnline(final Player target, final Player viewer) {
        return viewer.canSee(target) || !target.hasMetadata("invisible") || viewer.hasPermission("basic.staff");
    }

    private static boolean shouldSee(final Player target, final Player viewer) {
        for (final OverrideHandler handler : FrozenVisibilityHandler.overrideHandlers.values()) {
            if (handler.getAction(target, viewer) == OverrideAction.SHOW) {
                return true;
            }
        }
        for (final VisibilityHandler handler2 : FrozenVisibilityHandler.handlers.values()) {
            if (handler2.getAction(target, viewer) == VisibilityAction.HIDE) {
                return false;
            }
        }
        return true;
    }

    public static List<String> getDebugInfo(final Player target, final Player viewer) {
        final List<String> debug = new ArrayList<>();
        Boolean canSee = null;
        for (final Map.Entry<String, OverrideHandler> entry : FrozenVisibilityHandler.overrideHandlers.entrySet()) {
            final OverrideHandler handler = entry.getValue();
            final OverrideAction action = handler.getAction(target, viewer);
            ChatColor color = ChatColor.GRAY;
            if (action == OverrideAction.SHOW && canSee == null) {
                canSee = true;
                color = ChatColor.GREEN;
            }
            debug.add(color + "Overriding Handler: \"" + entry.getKey() + "\": " + action);
        }
        for (final Map.Entry<String, VisibilityHandler> entry2 : FrozenVisibilityHandler.handlers.entrySet()) {
            final VisibilityHandler handler2 = entry2.getValue();
            final VisibilityAction action2 = handler2.getAction(target, viewer);
            ChatColor color = ChatColor.GRAY;
            if (action2 == VisibilityAction.HIDE && canSee == null) {
                canSee = false;
                color = ChatColor.GREEN;
            }
            debug.add(color + "Normal Handler: \"" + entry2.getKey() + "\": " + action2);
        }
        if (canSee == null) {
            canSee = true;
        }
        debug.add(ChatColor.AQUA + "Result: " + viewer.getName() + " " + (canSee ? "can" : "cannot") + " see " + target.getName());
        return debug;
    }
}