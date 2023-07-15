package net.lugami.bridge.bukkit;

import net.lugami.bridge.global.grant.Grant;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.ranks.Rank;
import net.lugami.bridge.global.util.MojangUtils;
import net.lugami.bridge.BridgeGlobal;
import net.minecraft.server.v1_7_R4.Packet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Warning;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BukkitAPI {

    private static Pattern uuidPattern = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}");
    public static String LINE = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------";
    public static String RED_LINE = ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------";
    public static String BLUE_LINE = ChatColor.BLUE.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------";
    private static final UUID ZERO_UUID = new UUID(0, 0);

    public static boolean isUUID(String string) {
        return uuidPattern.matcher(string).find();
    }

    public static Player getPlayer(String string) {

        if (string == null) {
            return null;
        } else {
            return isUUID(string) ? Bukkit.getPlayer(UUID.fromString(string)) : Bukkit.getPlayer(string);
        }
    }

    public static Rank getRank(String name) {
        return BridgeGlobal.getRankHandler().getRankByName(name);
    }

    public static Rank getRank(UUID uuid) {
        return BridgeGlobal.getRankHandler().getRankByID(uuid);
    }

    public static Rank createRank(String name) {
        if(BridgeGlobal.getRankHandler().getRankByName(name) != null) {
            return null;
        }
        Rank r = new Rank(UUID.randomUUID(), name, false);
        r.saveRank();
        BridgeGlobal.getRankHandler().addRank(r);
        return r;
    }

    public static Profile getProfile(UUID uuid) {
        if(uuid == null) return Profile.getConsoleProfile();
        return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(uuid);
    }

    public static Profile getProfile(CommandSender sender) {
        return (sender instanceof Player ? BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(((Player)sender).getUniqueId()) : Profile.getConsoleProfile());
    }

    public static Profile getProfile(Player player) {
        return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(player.getUniqueId());
    }

    public static Profile getProfile(OfflinePlayer offlinePlayer) {
        return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(offlinePlayer.getUniqueId());
    }

    public static Profile getProfile(String name) {
        Profile disguisedProfile = BridgeGlobal.getProfileHandler().getProfiles().stream().filter(profile -> profile.getDisguise() != null && profile.getDisguise().getDisguiseName().equalsIgnoreCase(name)).findAny().orElse(null);
        if(disguisedProfile != null) return disguisedProfile;
        return BridgeGlobal.getProfileHandler().getProfileByUsernameOrCreate(name);
    }

    public static Profile getProfileNotCreate(UUID uuid) {
        return BridgeGlobal.getProfileHandler().getProfileByUUID(uuid);
    }

    public static Profile getProfileNotCreate(Player player) {
        return getProfileNotCreate(player.getUniqueId());
    }

    public static Profile getProfileNotCreate(OfflinePlayer player) {
        return getProfileNotCreate(player.getUniqueId());
    }


    @Warning(reason = "This requires to be ran on a seperate thread, otherwise there will be lag on the main server thread.")
    public static Profile getProfileOrCreateNew(UUID uuid) {
        String name;
        try {
            name = MojangUtils.fetchName(uuid);
        }catch(Exception e) {
            return null;
        }
        return BridgeGlobal.getProfileHandler().getNewProfileOrCreate(name, uuid);
    }

    public static Profile getProfileOrCreateNew(String username, UUID uuid) {
        return BridgeGlobal.getProfileHandler().getNewProfileOrCreate(username, uuid);
    }

    public static List<Profile> getOnlineProfiles() {
        return Bukkit.getOnlinePlayers().stream().filter(player -> getProfile(player) != null).map(BukkitAPI::getProfile).collect(Collectors.toList());
    }

    public static Rank getDefaultRank() {
        return BridgeGlobal.getRankHandler().getDefaultRank();
    }

    public static Rank getPlayerRank(CommandSender player, boolean ignoreDisguise) {
        if(!(player instanceof Player)) {
            return getRank("Owner");
        }
        if(getProfile(player) == null) {
            return getDefaultRank();
        }

        if(ignoreDisguise) {
            return getPlayerRank(((Player)player), true);
        }else {
            if(getProfile(player).getDisguisedRank() != null) {
                return getProfile(player).getDisguisedRank();
            }
        }

        return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(((Player)player).getUniqueId()).getCurrentGrant().getRank();
    }

    public static Rank getPlayerRank(CommandSender player) {
        return getPlayerRank(player, false);
    }

    public static Rank getPlayerRank(Player player) {
        if(getProfile(player) == null) {
            return getDefaultRank();
        }
        if(getProfile(player).getDisguisedRank() != null) {
            return getProfile(player).getDisguisedRank();
        }

        return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(player.getUniqueId()).getCurrentGrant().getRank();
    }

    public static Rank getPlayerRank(UUID uuid) {
        if(getProfile(uuid) == null) {
            return getDefaultRank();
        }
        if(getProfile(uuid).getDisguisedRank() != null) {
            return getProfile(uuid).getDisguisedRank();
        }
        return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(uuid).getCurrentGrant().getRank();
    }

    public static Rank getPlayerRank(OfflinePlayer offlinePlayer) {
        if(getProfile(offlinePlayer) == null) {
            return getDefaultRank();
        }
        if(getProfile(offlinePlayer).getDisguisedRank() != null) {
            return getProfile(offlinePlayer).getDisguisedRank();
        }
        return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(offlinePlayer.getUniqueId()).getCurrentGrant().getRank();
    }

    public static Rank getPlayerRank(Profile profile) {
        if(profile == null) {
            return getDefaultRank();
        }
        if(profile.getUuid().toString().equals(Profile.getConsoleProfile().getUuid().toString())) {
            return getRank("Default");
        }
        if(profile.getDisguisedRank() != null) {
            return profile.getDisguisedRank();
        }
        return profile.getCurrentGrant().getRank();
    }

    public boolean canPunish(CommandSender sender, Profile target) {
        if(!(sender instanceof Player)) return true;
        return (getPlayerRank(sender).isStaff() && getPlayerRank(target).isStaff()) && getPlayerRank(sender).getPriority() > getPlayerRank(target).getPriority();
    }

    @Deprecated
    public static boolean canOverride(CommandSender sender, Profile on) {
        return canOverride(getProfile(sender), on);
    }

    public static boolean canOverride(Profile sender, Profile on) {
        if (compareUUID(sender.getUuid(), ZERO_UUID)) // Always allow to override console
            return true;

        if (compareUUID(sender.getUuid(), on.getUuid())) // Always allow to override own punishments
            return true;

        return getRankPriority(sender) >= getRankPriority(on);
    }

    public static boolean compareUUID(UUID first, UUID second) {
        return first.getLeastSignificantBits() == second.getLeastSignificantBits() && first.getMostSignificantBits() == second.getMostSignificantBits();
    }

    public static int getRankPriority(Profile profile) {
        if (profile == null)
            return 0;

        Rank rank = getPlayerRank(profile, true);

        if (rank == null)
            return 0;

        return rank.getPriority();
    }

    public static Rank getPlayerRank(Player player, boolean ignoreDisguise) {
        if(getProfile(player) == null) {
            return getDefaultRank();
        }
        if(ignoreDisguise) return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(player.getUniqueId()).getCurrentGrant().getRank();

        else if(getProfile(player).getDisguisedRank() != null) {
            return getProfile(player).getDisguisedRank();
        }
        return null;

    }

    public static Rank getPlayerRank(UUID uuid, boolean ignoreDisguise) {
        if(ignoreDisguise) return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(uuid).getCurrentGrant().getRank();

        else if(getProfile(uuid).getDisguisedRank() != null) {
            return getProfile(uuid).getDisguisedRank();
        }
        return null;
    }

    public static Rank getPlayerRank(OfflinePlayer offlinePlayer, boolean ignoreDisguise) {
        if(getProfile(offlinePlayer) == null) {
            return getDefaultRank();
        }
        if(ignoreDisguise) return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(offlinePlayer.getUniqueId()).getCurrentGrant().getRank();

        else if(getProfile(offlinePlayer).getDisguisedRank() != null) {
            return getProfile(offlinePlayer).getDisguisedRank();
        }
        return null;
    }

    public static Rank getPlayerRank(Profile profile, boolean ignoreDisguise) {
        if(profile == null) {
            return getDefaultRank();
        }

        if (profile.getUuid().getLeastSignificantBits() == 0 && profile.getUuid().getMostSignificantBits() == 0)
            return null;

        if(ignoreDisguise) return profile.getCurrentGrant().getRank();

        else if(profile.getDisguisedRank() != null) {
            return profile.getDisguisedRank();
        }
        return null;
    }

    public static String getColor(CommandSender sender) {
        if(getProfile(sender) == null) {
            return getDefaultRank().getColor();
        }
        if(!getProfile(sender).getColor().equals("")) {
            return getProfile(sender).getColor();
        }else {
            return getPlayerRank(sender).getColor();
        }
    }

    public static String getColor(Player player) {
        if(getProfile(player) == null) {
            return getDefaultRank().getColor();
        }
        if(!getProfile(player).getColor().equals("")) {
            return getProfile(player).getColor();
        }else {
            return getPlayerRank(player).getColor();
        }
    }

    public static String getColor(OfflinePlayer player) {
        if(getProfile(player) == null) {
            return getDefaultRank().getColor();
        }
        if(!getProfile(player).getColor().equals("")) {
            return getProfile(player).getColor();
        }else {
            return getPlayerRank(player).getColor();
        }
    }

    public static String getColor(UUID player) {
        if(!getProfile(player).getColor().equals("")) {
            return getProfile(player).getColor();
        }else {
            return getPlayerRank(player).getColor();
        }
    }

    public static String getColor(Profile profile) {
        if(!profile.getColor().equals("")) {
            return profile.getColor();
        }else {
            return getPlayerRank(profile).getColor();
        }
    }

    public static String getPrefix(Player player) {
        if(getProfile(player) == null) {
            return getDefaultRank().getPrefix();
        }
        if(!getProfile(player).getPrefix().equals("")) {
            return getProfile(player).getPrefix();
        }else {
            return getPlayerRank(player).getPrefix();
        }
    }

    public static String getPrefix(OfflinePlayer player) {
        if(getProfile(player) == null) {
            return getDefaultRank().getPrefix();
        }
        if(!getProfile(player).getPrefix().equals("")) {
            return getProfile(player).getPrefix();
        }else {
            return getPlayerRank(player).getPrefix();
        }
    }
    public static String getPrefix(UUID player) {
        if(getProfile(player) == null) {
            return getDefaultRank().getPrefix();
        }
        if(!getProfile(player).getPrefix().equals("")) {
            return getProfile(player).getPrefix();
        }else {
            return getPlayerRank(player).getPrefix();
        }
    }

    public static String getPrefix(Profile profile) {
        if(profile == null) {
            return getDefaultRank().getPrefix();
        }
        if(!profile.getPrefix().equals("")) {
            return profile.getPrefix();
        }else {
            return getPlayerRank(profile).getPrefix();
        }
    }

    public static String getSuffix(Player player) {
        if(getProfile(player) == null) {
            return getDefaultRank().getSuffix();
        }
        if(!getProfile(player).getSuffix().equals("")) {
            return getProfile(player).getSuffix();
        }else {
            return getPlayerRank(player).getSuffix();
        }
    }

    public static String getSuffix(OfflinePlayer player) {
        if(getProfile(player) == null) {
            return getDefaultRank().getSuffix();
        }
        if(!getProfile(player).getSuffix().equals("")) {
            return getProfile(player).getSuffix();
        }else {
            return getPlayerRank(player).getSuffix();
        }
    }

    public static String getSuffix(UUID player) {
        if(getProfile(player) == null) {
            return getDefaultRank().getSuffix();
        }
        if(!getProfile(player).getSuffix().equals("")) {
            return getProfile(player).getSuffix();
        }else {
            return getPlayerRank(player).getSuffix();
        }
    }

    public static String getSuffix(Profile profile) {
        if(profile == null) {
            return getDefaultRank().getSuffix();
        }
        if(!profile.getSuffix().equals("")) {
            return profile.getSuffix();
        }else {
            return getPlayerRank(profile).getSuffix();
        }
    }


    public static String getName(Profile profile, boolean ignoreDisguise) {
        return (ignoreDisguise || profile.getDisguise() == null) ? profile.getUsername() : profile.getDisguise().getDisguiseName();
    }

    public static String getRealName(Profile profile) {
        return profile.getDisguise() == null ? profile.getUsername() : profile.getDisguise().getName();
    }

    public static String getRealName(UUID uuid) {
        return getRealName(getProfile(uuid));
    }

    public static String getRealName(Player player) {
        return getRealName(getProfile(player));
    }


    public static void sendPacket(Player player, Packet packet){
        CraftPlayer cp = (CraftPlayer) player;
        cp.getHandle().playerConnection.sendPacket(packet);
    }

    public static List<Grant> getActiveGrants(Player player) {
        return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(player.getUniqueId()).getActiveGrants();
    }

    public static List<Grant> getActiveGrants(UUID uuid) {
        return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(uuid).getActiveGrants();
    }

    public static List<Grant> getActiveGrants(OfflinePlayer offlinePlayer) {
        return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(offlinePlayer.getUniqueId()).getActiveGrants();
    }

    public static List<Grant> getActiveGrants(Profile profile) {
        return profile.getActiveGrants();
    }

    public static List<Grant> getCurrentScopeRanks(Player player) {
        return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(player.getUniqueId()).getActiveGrants().stream().filter(Grant::isGrantActiveOnScope).collect(Collectors.toList());
    }

    public static List<Grant> getCurrentScopeRanks(UUID uuid) {
        return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(uuid).getActiveGrants().stream().filter(Grant::isGrantActiveOnScope).collect(Collectors.toList());
    }

    public static List<Grant> getCurrentScopeRanks(OfflinePlayer offlinePlayer) {
        return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(offlinePlayer.getUniqueId()).getActiveGrants().stream().filter(Grant::isGrantActiveOnScope).collect(Collectors.toList());
    }

    public static List<Grant> getAllGrants(Player player) {
        return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(player.getUniqueId()).getGrants();
    }

    public static List<Grant> getAllGrants(UUID uuid) {
        return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(uuid).getGrants();
    }

    public static List<Grant> getAllGrants(OfflinePlayer offlinePlayer) {
        return BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(offlinePlayer.getUniqueId()).getGrants();
    }

    public static void sendToStaff(String message) {
        BridgeGlobal.sendLog(message);
        Bukkit.getOnlinePlayers().stream().filter(p -> getPlayerRank(p, true).isStaff()).forEach(p -> {
            ((Player) p).sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        });
    }

    public static void sendToStaff(List<String> message) {
        message.forEach(BridgeGlobal::sendLog);
        Bukkit.getOnlinePlayers().stream().filter(p -> getPlayerRank(p, true).isStaff()).forEach(p -> {
            message.forEach(string -> {
                ((Player) p).sendMessage(ChatColor.translateAlternateColorCodes('&', string));
            });
        });
    }

}
