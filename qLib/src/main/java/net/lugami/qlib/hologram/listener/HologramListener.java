package net.lugami.qlib.hologram.listener;


import net.lugami.qlib.hologram.FrozenHologramHandler;
import net.lugami.qlib.hologram.construct.Hologram;
import net.lugami.qlib.hologram.type.BaseHologram;
import net.lugami.qlib.qLib;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class HologramListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {

		qLib.getInstance().getServer().getScheduler().runTaskLater(qLib.getInstance(),() -> FrozenHologramHandler.getCache().forEach(hologram -> {

			final BaseHologram baseHologram = (BaseHologram) hologram;

			if ((baseHologram.getViewers() == null || baseHologram.getViewers().contains(event.getPlayer().getUniqueId()))
					&& baseHologram.getLocation().getWorld().equals(event.getPlayer().getWorld()) && hologram.getLocation().distance(event.getPlayer().getLocation()) <= 1600.0D) {

				baseHologram.show(event.getPlayer());
			}

		}),20L);

	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event) {

		final Player player = event.getPlayer();

		final Location to = event.getTo();
		final Location from = event.getFrom();

		if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) {
			return;
		}

		for (Hologram hologram : FrozenHologramHandler.getCache()) {

			final BaseHologram baseHologram = (BaseHologram)hologram;

			if ((baseHologram.getViewers() == null || baseHologram.getViewers().contains(event.getPlayer().getUniqueId())) && hologram.getLocation().getWorld().equals(event.getPlayer().getWorld())) {

				if (!baseHologram.getCurrentWatchers().contains(player.getUniqueId()) && hologram.getLocation().distanceSquared(player.getLocation()) <= 1600.0D) {
					baseHologram.show(player);
					continue;
				} if (baseHologram.getCurrentWatchers().contains(player.getUniqueId()) && hologram.getLocation().distanceSquared(player.getLocation()) > 1600.0D) {
					baseHologram.destroy0(player);
				}

			}
		}

	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {

		for (Hologram hologram : FrozenHologramHandler.getCache()) {

			final BaseHologram baseHologram = (BaseHologram) hologram;

			if ((baseHologram.getViewers() == null || baseHologram.getViewers().contains(event.getPlayer().getUniqueId())) && baseHologram.getLocation().getWorld().equals(event.getPlayer().getWorld())) {
				baseHologram.show(event.getPlayer());
			}

		}
	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void onRespawn(PlayerRespawnEvent event) {

		qLib.getInstance().getServer().getScheduler().runTaskLater(qLib.getInstance(),() -> {

			for (Hologram hologram : FrozenHologramHandler.getCache()) {

				final BaseHologram baseHologram = (BaseHologram) hologram;

				baseHologram.destroy0(event.getPlayer());

				if ((baseHologram.getViewers() == null || baseHologram.getViewers().contains(event.getPlayer().getUniqueId())) && baseHologram.getLocation().getWorld().equals(event.getPlayer().getWorld())) {
					baseHologram.show(event.getPlayer());
				}
			}

		},10L);

	}


}
