package net.lugami.qlib.tab;

import net.lugami.qlib.packet.ScoreboardTeamPacketMod;
import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.spigotmc.ProtocolInjector;

import java.util.*;

public final class FrozenTab {

    private final Player player;
    private final Map<String, String> previousNames = new HashMap<>();
    private final Map<String, Integer> previousPings = new HashMap<>();
    private String lastHeader = "{\"translate\":\"\"}";
    private String lastFooter = "{\"translate\":\"\"}";
    private final Set<String> createdTeams = new HashSet<>();
    private TabLayout initialLayout;
    private boolean initiated = false;
    private final StringBuilder removeColorCodesBuilder = new StringBuilder();

    public FrozenTab(Player player) {
        this.player = player;
    }

    private void createAndAddMember(String name, String member) {
        ScoreboardTeamPacketMod scoreboardTeamAdd = new ScoreboardTeamPacketMod("$" + name, "", "", Collections.singletonList(member), 0);
        scoreboardTeamAdd.sendToPlayer(this.player);
    }

    private void init() {
        if (!this.initiated) {
            this.initiated = true;
            TabLayout initialLayout = TabLayout.createEmpty(this.player);
            if (!initialLayout.is18()) {
                for (Player n : Bukkit.getOnlinePlayers()) {
                    this.updateTabList(n.getName(), -1, ((CraftPlayer)n).getProfile(), 4);
                }
            }
            for (String s : initialLayout.getTabNames()) {
                this.updateTabList(s, -1, 0);
                String teamName = s.replaceAll("\u00a7", "");
                if (this.createdTeams.contains(teamName)) continue;
                this.createAndAddMember(teamName, s);
                this.createdTeams.add(teamName);
            }
            this.initialLayout = initialLayout;
        }
    }

    private void updateScore(String score, String prefix, String suffix) {
        ScoreboardTeamPacketMod scoreboardTeamModify = new ScoreboardTeamPacketMod(score, prefix, suffix, null, 2);
        scoreboardTeamModify.sendToPlayer(this.player);
    }

    private void updateTabList(String name, int ping, int action) {
        this.updateTabList(name, ping, TabUtils.getOrCreateProfile(name), action);
    }

    private void updateTabList(String name, int ping, GameProfile profile, int action) {
        PlayerInfoPacketMod playerInfoPacketMod = new PlayerInfoPacketMod("$" + name, ping, profile, action);
        playerInfoPacketMod.sendToPlayer(this.player);
    }

    private String[] splitString(String line) {
        if (line.length() <= 16) {
            return new String[]{line, ""};
        }
        return new String[]{line.substring(0, 16), line.substring(16)};
    }

    protected void update() {

        if (FrozenTabHandler.getLayoutProvider() != null) {
            TabLayout tabLayout = FrozenTabHandler.getLayoutProvider().provide(this.player);
            if (tabLayout == null) {
                if (this.initiated) {
                    this.reset();
                }
                return;
            }
            this.init();
            for (int y = 0; y < TabLayout.HEIGHT; ++y) {
                for (int x = 0; x < TabLayout.WIDTH; ++x) {
                    String entry = tabLayout.getStringAt(x, y);
                    int ping = -1 + tabLayout.getPingAt(x, y);
                    String entryName = this.initialLayout.getStringAt(x, y);
                    this.removeColorCodesBuilder.setLength(0);
                    this.removeColorCodesBuilder.append('$');
                    this.removeColorCodesBuilder.append(entryName);
                    int j = 0;
                    for (int i = 0; i < this.removeColorCodesBuilder.length(); ++i) {
                        if ('\u00a7' == this.removeColorCodesBuilder.charAt(i)) continue;
                        this.removeColorCodesBuilder.setCharAt(j++, this.removeColorCodesBuilder.charAt(i));
                    }
                    this.removeColorCodesBuilder.delete(j, this.removeColorCodesBuilder.length());
                    String teamName = this.removeColorCodesBuilder.toString();
                    if (this.previousNames.containsKey(entryName)) {
                        if (!this.previousNames.get(entryName).equals(entry)) {
                            this.update(entryName, teamName, entry, ping);
                            continue;
                        }
                    } else {
                        this.update(entryName, teamName, entry, ping);
                        continue;
                    }
                    if (!this.previousPings.containsKey(entryName) || this.pingToBars(this.previousPings.get(entryName)) == this.pingToBars(ping)) continue;
                    this.updateTabList(entryName, ping, 2);
                    this.previousPings.put(entryName, ping);
                }
            }
            boolean sendHeader = false;
            boolean sendFooter = false;
            String header = tabLayout.getHeader();
            String footer = tabLayout.getFooter();
            if (!header.equals(this.lastHeader)) {
                sendHeader = true;
            }
            if (!footer.equals(this.lastFooter)) {
                sendFooter = true;
            }
            if (tabLayout.is18() && (sendHeader || sendFooter)) {
                ProtocolInjector.PacketTabHeader packet = new ProtocolInjector.PacketTabHeader(ChatSerializer.a(header), ChatSerializer.a(footer));
                ((CraftPlayer)this.player).getHandle().playerConnection.sendPacket(packet);
                this.lastHeader = header;
                this.lastFooter = footer;
            }
        }
    }

    private void reset() {
        this.initiated = false;
        for (String s : this.initialLayout.getTabNames()) {
            this.updateTabList(s, -1, 4);
        }
        EntityPlayer ePlayer = ((CraftPlayer)this.player).getHandle();
        this.updateTabList(this.player.getName(), ePlayer.ping, ePlayer.getProfile(), 0);
        int count = 1;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.player == player) continue;
            if (count > this.initialLayout.getTabNames().length - 1) break;
            ePlayer = ((CraftPlayer)player).getHandle();
            this.updateTabList(this.player.getName(), ePlayer.ping, ePlayer.getProfile(), 0);
            ++count;
        }
    }

    private void update(String entryName, String teamName, String entry, int ping) {
        String[] entryStrings = this.splitString(entry);
        String prefix = entryStrings[0];
        String suffix = entryStrings[1];
        if (!suffix.isEmpty()) {
            if (prefix.charAt(prefix.length() - 1) == '\u00a7') {
                prefix = prefix.substring(0, prefix.length() - 1);
                suffix = '\u00a7' + suffix;
            }
            String suffixPrefix = ChatColor.RESET.toString();
            if (!ChatColor.getLastColors(prefix).isEmpty()) {
                suffixPrefix = ChatColor.getLastColors(prefix);
            }
            suffix = suffix.length() <= 14 ? suffixPrefix + suffix : suffixPrefix + suffix.substring(0, 14);
        }
        this.updateScore(teamName, prefix, suffix);
        this.updateTabList(entryName, ping, 2);
        this.previousNames.put(entryName, entry);
        this.previousPings.put(entryName, ping);
    }

    private int pingToBars(int ping) {
        if (ping < 0) {
            return 5;
        }
        if (ping < 150) {
            return 0;
        }
        if (ping < 300) {
            return 1;
        }
        if (ping < 600) {
            return 2;
        }
        if (ping < 1000) {
            return 3;
        }
        if (ping < 32767) {
            return 4;
        }
        return 5;
    }

    public boolean isInitiated() {
        return this.initiated;
    }
}
