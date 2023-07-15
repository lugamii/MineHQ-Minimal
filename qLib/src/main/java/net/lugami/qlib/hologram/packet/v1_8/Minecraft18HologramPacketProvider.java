package net.lugami.qlib.hologram.packet.v1_8;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.lugami.qlib.hologram.construct.HologramLine;
import net.lugami.qlib.hologram.packet.HologramPacket;
import net.lugami.qlib.hologram.packet.HologramPacketProvider;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class Minecraft18HologramPacketProvider implements HologramPacketProvider {

	public HologramPacket getPacketsFor(Location location, HologramLine line) {

		final List<PacketContainer> packets = Collections.singletonList(this.createArmorStandPacket(line.getSkullId(),line.getText(),location));

		return new HologramPacket(packets,Arrays.asList(line.getSkullId(),-1337));
	}

	protected PacketContainer createArmorStandPacket(int witherSkullId, String text, Location location) {

		final PacketContainer displayPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		final StructureModifier<Integer> ints = displayPacket.getIntegers();

		ints.write(0,witherSkullId);
		ints.write(1,30);
		ints.write(2,(int)(location.getX()*32.0D));
		ints.write(3,(int)((location.getY()-2.0D)*32.0D));
		ints.write(4,(int)(location.getZ()*32.0D));

		final WrappedDataWatcher watcher = new WrappedDataWatcher();

		watcher.setObject(0,(byte)32);
		watcher.setObject(2, ChatColor.translateAlternateColorCodes('&', text));

		if (text.equalsIgnoreCase("blank")) {
			watcher.setObject(3,(byte)0);
		} else {
			watcher.setObject(3,(byte)1);
		}

		displayPacket.getDataWatcherModifier().write(0, watcher);

		return displayPacket;
	}

}
