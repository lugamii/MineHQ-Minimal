package net.lugami.qlib.hologram.type;


import com.comphenix.net.sf.cglib.core.MethodInfoTransformer;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.Getter;

import mkremins.fanciful.shaded.gson.internal.Pair;
import net.lugami.qlib.hologram.builder.HologramBuilder;
import net.lugami.qlib.hologram.construct.Hologram;
import net.lugami.qlib.hologram.construct.HologramLine;
import net.lugami.qlib.hologram.packet.v1_7.Minecraft17HologramPacketProvider;
import net.lugami.qlib.hologram.packet.v1_8.Minecraft18HologramPacketProvider;
import net.lugami.qlib.hologram.packet.HologramPacket;
import net.lugami.qlib.hologram.packet.HologramPacketProvider;
import net.lugami.qlib.qLib;
import net.lugami.qlib.util.PlayerUtils;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class BaseHologram implements Hologram {

	@Getter private Location location;
	@Getter private Collection<UUID> viewers;

	@Getter private final Set<UUID> currentWatchers = new HashSet<>();

	private List<HologramLine> lines = new ArrayList<>();
	private List<HologramLine> lastLines = new ArrayList<>();

	public BaseHologram(HologramBuilder builder) {

		if (builder.getLocation() == null) {
			throw new IllegalArgumentException("Please provide a location for the hologram using HologramBuilder#at(Location)");
		}

		this.viewers = builder.getViewers();
		this.location = builder.getLocation();

		builder.getLines().forEach(line -> this.lines.add(new HologramLine(line)));

	}

	public void send() {
		Collection<UUID> viewers = this.viewers;

		if (viewers == null) {
			viewers = Bukkit.getServer().getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList());
		}

		viewers.stream().filter(viewer -> Bukkit.getServer().getPlayer(viewer) != null).map(Bukkit.getServer()::getPlayer).forEach(this::show);
	}


	public void destroy() {
		Collection<UUID> viewers = this.viewers;

		if (viewers == null) {
			viewers = qLib.getInstance().getServer().getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList());
		}

		viewers.stream().filter(viewer -> qLib.getInstance().getServer().getPlayer(viewer) != null).map(qLib.getInstance().getServer()::getPlayer).forEach(this::destroy0);

		if (this.viewers != null) {
			this.viewers.clear();
		}

	}

	@Override
	public void delete() {
		this.destroy();
	}

	@Override
	public void move(Location location) {
		this.location = location;
		this.update();
	}


	public void addLines(String... lines) {

		for (String line : lines) {
			this.lines.add(new HologramLine(line));
		}

		this.update();
	}

	@Override
	public void removeLine(int paramVarArgs) {
		this.lines.remove(paramVarArgs);

		this.update();
	}


	public void setLine(int index, String line) {

		if (index > this.lines.size() - 1) {
			this.lines.add(new HologramLine(line));
		} else if (this.lines.get(index) != null) {
			this.lines.get(index).setText(line);
		} else {
			this.lines.set(index, new HologramLine(line));
		}

		this.update();
	}


	public void setLines(Collection<String> lines) {
		Collection<UUID> viewers = this.viewers;

		if (viewers == null) {
			viewers = qLib.getInstance().getServer().getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList());
		}

		viewers.stream().filter(viewer -> qLib.getInstance().getServer().getPlayer(viewer) != null).map(qLib.getInstance().getServer()::getPlayer).forEach(this::destroy0);

		this.lines.clear();

		lines.forEach(line -> this.lines.add(new HologramLine(line)));

		this.update();
	}


	public List<String> getLines() {

		List<String> lines = new ArrayList<>();

		for (HologramLine line : this.lines) {
			lines.add(line.getText());
		}

		return lines;
	}

	public List<HologramLine> getRawLines() {
		return this.lines;
	}

	public void show(Player player) {

		if (!player.getLocation().getWorld().equals(this.location.getWorld())) {
			return;
		}

		final Location first = this.location.clone().add(0.0D, this.lines.size() * 0.23D, 0.0D);

		for (HologramLine line : this.lines) {
			this.showLine(player, first.clone(), line);
			first.subtract(0.0D, 0.23D, 0.0D);
		}

		this.currentWatchers.add(player.getUniqueId());
	}

	private Pair<Integer, Integer> showLine(Player player, Location loc, HologramLine line) {

		final HologramPacketProvider packetProvider = getPacketProviderForPlayer(player);
		final HologramPacket hologramPacket = packetProvider.getPacketsFor(loc,line);

		if (hologramPacket != null) {
			hologramPacket.sendToPlayer(player);
			return new Pair<>(hologramPacket.getEntityIds().get(0),hologramPacket.getEntityIds().get(1));
		}

		return null;
	}


	public void destroy0(Player player) {

		final List<Integer> ints = new ArrayList<>();

		for (HologramLine line : this.lines) {

			if (line.getHorseId() == -1337) {
				ints.add(line.getSkullId());
				continue;
			}

			ints.add(line.getSkullId());
			ints.add(line.getHorseId());
		}

		final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(convertIntegers(ints));

		(((CraftPlayer) player).getHandle()).playerConnection.sendPacket(packet);

		this.currentWatchers.remove(player.getUniqueId());
	}

	private int[] convertIntegers(List<Integer> integers) {

		final int[] toReturn = new int[integers.size()];

		for (int i = 0; i < toReturn.length; i++) {
			toReturn[i] = integers.get(i);
		}

		return toReturn;
	}

	public void update() {

		Collection<UUID> viewers = this.viewers;

		if (viewers == null) {
			viewers = qLib.getInstance().getServer().getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList());
		}

		viewers.stream().filter(viewer -> qLib.getInstance().getServer().getPlayer(viewer) != null).map(qLib.getInstance().getServer()::getPlayer).forEach(this::update);

		this.lastLines.addAll(this.lines);
	}

	public void update(Player player) {

		if (!player.getLocation().getWorld().equals(this.location.getWorld())) {
			return;
		}

		if (this.lastLines.size() != this.lines.size()) {
			this.destroy0(player);
			this.show(player);
			return;
		}

		for (int index = 0; index < getRawLines().size(); index++) {

			final HologramLine line = getRawLines().get(index);
			final String text = ChatColor.translateAlternateColorCodes('&', line.getText());
			final boolean is18 = PlayerUtils.is18(player);

			try {

				final PacketContainer container = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

				container.getIntegers().write(0, is18 ? line.getSkullId() : line.getHorseId());

				final WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();

				wrappedDataWatcher.setObject(is18 ? 2:10,text);

				final List<WrappedWatchableObject> watchableObjects = Arrays.asList(Iterators.toArray(wrappedDataWatcher.iterator(),WrappedWatchableObject.class));

				container.getWatchableCollectionModifier().write(0,watchableObjects);

				try {
					ProtocolLibrary.getProtocolManager().sendServerPacket(player,container);
				} catch (Exception ignored) {}

			} catch (IndexOutOfBoundsException e) {
				this.destroy0(player);
				this.show(player);
			}

		}
	}

	private HologramPacketProvider getPacketProviderForPlayer(Player player) {
		return ((((CraftPlayer) player).getHandle()).playerConnection.networkManager.getVersion() > 5) ? new Minecraft18HologramPacketProvider() : new Minecraft17HologramPacketProvider();
	}

}
