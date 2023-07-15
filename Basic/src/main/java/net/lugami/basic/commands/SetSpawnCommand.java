package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import java.util.EnumMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class SetSpawnCommand {

    public static final BlockFace[] RADIAL = new BlockFace[]{BlockFace.WEST, BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST};
    private static final EnumMap<BlockFace, Integer> notches = new EnumMap(BlockFace.class);

    @Command(names={"setspawn"}, permission="basic.setspawn")
    public static void setspawn(Player sender) {
        Location location = sender.getLocation();
        BlockFace face = SetSpawnCommand.yawToFace(location.getYaw());
        sender.getWorld().setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ(), (float)SetSpawnCommand.faceToYaw(face), 0.0f);
        sender.sendMessage(ChatColor.GOLD + "Set the spawn for " + ChatColor.WHITE + sender.getWorld().getName() + ChatColor.GOLD + ".");
    }

    private static BlockFace yawToFace(float yaw) {
        return RADIAL[Math.round(yaw / 45.0f) & 7];
    }

    public static int faceToYaw(BlockFace face) {
        return SetSpawnCommand.wrapAngle(45 * SetSpawnCommand.faceToNotch(face));
    }

    public static int faceToNotch(BlockFace face) {
        Integer notch = notches.get(face);
        return notch == null ? 0 : notch;
    }

    private static int wrapAngle(int angle) {
        int wrappedAngle;
        for (wrappedAngle = angle; wrappedAngle <= -180; wrappedAngle += 360) {
        }
        while (wrappedAngle > 180) {
            wrappedAngle -= 360;
        }
        return wrappedAngle;
    }

    static {
        for (int i = 0; i < RADIAL.length; ++i) {
            notches.put(RADIAL[i], i);
        }
    }
}

