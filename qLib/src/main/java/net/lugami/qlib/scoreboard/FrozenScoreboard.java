package net.lugami.qlib.scoreboard;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.lugami.qlib.qLib;
import net.lugami.qlib.packet.ScoreboardTeamPacketMod;
import net.lugami.qlib.util.LinkedList;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardScore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Field;
import java.util.*;

public final class FrozenScoreboard {

    private final UUID player;
    private final Objective objective;
    private final Map<String, Integer> displayedScores = new HashMap<>();
    private final Map<String, String> scorePrefixes = new HashMap<>();
    private final Map<String, String> scoreSuffixes = new HashMap<>();
    private final Set<String> sentTeamCreates = new HashSet<>();
    private final StringBuilder separateScoreBuilder = new StringBuilder();
    private final List<String> separateScores = new ArrayList<>();
    private final Set<String> recentlyUpdatedScores = new HashSet<>();
    private final Set<String> usedBunkerscores = new HashSet<>();
    private final String[] prefixScoreSuffix = new String[3];
    private final ThreadLocal<LinkedList<String>> localList = ThreadLocal.withInitial(LinkedList::new);

    public FrozenScoreboard(UUID player) {
        this.player = player;
        Scoreboard board = qLib.getInstance().getServer().getScoreboardManager().getNewScoreboard();
        this.objective = board.registerNewObjective("KEKI", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        if(Bukkit.getPlayer(player) != null) Bukkit.getPlayer(player).setScoreboard(board);
    }

    public void update() {
        Player player = Bukkit.getPlayer(this.player);
        if(player == null) return;
        String untranslatedTitle = FrozenScoreboardHandler.getConfiguration().getTitleGetter().getTitle(player);
        String title = ChatColor.translateAlternateColorCodes('&', untranslatedTitle);
        List<String> lines = this.localList.get();
        if (!lines.isEmpty()) {
            lines.clear();
        }
        FrozenScoreboardHandler.getConfiguration().getScoreGetter().getScores(this.localList.get(), player);
        this.recentlyUpdatedScores.clear();
        this.usedBunkerscores.clear();
        int nextValue = lines.size();
        Preconditions.checkArgument(lines.size() < 16, "Too many lines passed!");
        Preconditions.checkArgument(title.length() < 32, "Title is too long!");
        if (!this.objective.getDisplayName().equals(title)) {
            this.objective.setDisplayName(title);
        }
        for (String line : lines) {
            if (48 <= line.length()) {
                throw new IllegalArgumentException("Line is too long! Offending line: " + line);
            }
            String[] separated = this.separate(line, this.usedBunkerscores);
            String prefix = separated[0];
            String score = separated[1];
            String suffix = separated[2];
            this.recentlyUpdatedScores.add(score);
            if (!this.sentTeamCreates.contains(score)) {
                this.createAndAddMember(score);
            }
            if (!this.displayedScores.containsKey(score) || this.displayedScores.get(score) != nextValue) {
                this.setScore(score, nextValue);
            }
            if (!(this.scorePrefixes.containsKey(score) && this.scorePrefixes.get(score).equals(prefix) && this.scoreSuffixes.get(score).equals(suffix))) {
                this.updateScore(score, prefix, suffix);
            }
            --nextValue;
        }
        for (String displayedScore : ImmutableSet.copyOf(this.displayedScores.keySet())) {
            if (this.recentlyUpdatedScores.contains(displayedScore)) continue;
            this.removeScore(displayedScore);
        }
    }

    private void setField(Packet packet, String field, Object value) {
        try {
            Field fieldObject = packet.getClass().getDeclaredField(field);
            fieldObject.setAccessible(true);
            fieldObject.set(packet, value);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAndAddMember(String scoreTitle) {
        Player player = Bukkit.getPlayer(this.player);
        if(player == null) return;
        ScoreboardTeamPacketMod scoreboardTeamAdd = new ScoreboardTeamPacketMod(scoreTitle, "_", "_", ImmutableList.of(), 0);
        ScoreboardTeamPacketMod scoreboardTeamAddMember = new ScoreboardTeamPacketMod(scoreTitle, ImmutableList.of(scoreTitle), 3);
        scoreboardTeamAdd.sendToPlayer(player);
        scoreboardTeamAddMember.sendToPlayer(player);
        this.sentTeamCreates.add(scoreTitle);
    }

    private void setScore(String score, int value) {
        Player player = Bukkit.getPlayer(this.player);
        if(player == null) return;
        PacketPlayOutScoreboardScore scoreboardScorePacket = new PacketPlayOutScoreboardScore();
        this.setField(scoreboardScorePacket, "a", score);
        this.setField(scoreboardScorePacket, "b", this.objective.getName());
        this.setField(scoreboardScorePacket, "c", value);
        this.setField(scoreboardScorePacket, "d", 0);
        this.displayedScores.put(score, value);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(scoreboardScorePacket);
    }

    private void removeScore(String score) {
        Player player = Bukkit.getPlayer(this.player);
        if(player == null) return;
        this.displayedScores.remove(score);
        this.scorePrefixes.remove(score);
        this.scoreSuffixes.remove(score);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardScore(score));
    }

    private void updateScore(String score, String prefix, String suffix) {
        Player player = Bukkit.getPlayer(this.player);
        if(player == null) return;
        this.scorePrefixes.put(score, prefix);
        this.scoreSuffixes.put(score, suffix);
        new ScoreboardTeamPacketMod(score, prefix, suffix, null, 2).sendToPlayer(player);
    }

    private String[] separate(String line, Collection<String> usedBunkerscores) {
        line = ChatColor.translateAlternateColorCodes('&', line);
        String prefix = "";
        String score = "";
        String suffix = "";
        this.separateScores.clear();
        this.separateScoreBuilder.setLength(0);
        for (int i = 0; i < line.length(); ++i) {
            int c = line.charAt(i);
            if (c == 42 || this.separateScoreBuilder.length() == 16 && this.separateScores.size() < 3) {
                this.separateScores.add(this.separateScoreBuilder.toString());
                this.separateScoreBuilder.setLength(0);
                if (c == 42) continue;
            }
            this.separateScoreBuilder.append((char)c);
        }
        this.separateScores.add(this.separateScoreBuilder.toString());
        switch (this.separateScores.size()) {
            case 1: {
                score = this.separateScores.get(0);
                break;
            }
            case 2: {
                score = this.separateScores.get(0);
                suffix = this.separateScores.get(1);
                break;
            }
            case 3: {
                prefix = this.separateScores.get(0);
                score = this.separateScores.get(1);
                suffix = this.separateScores.get(2);
                break;
            }
            default: {
                qLib.getInstance().getLogger().warning("Failed to separate scoreboard line. Input: " + line);
            }
        }
        if (usedBunkerscores.contains(score)) {
            if (score.length() <= 14) {
                for (ChatColor chatColor : ChatColor.values()) {
                    String possibleScore = chatColor + score;
                    if (usedBunkerscores.contains(possibleScore)) continue;
                    score = possibleScore;
                    break;
                }
                if (usedBunkerscores.contains(score)) {
                    qLib.getInstance().getLogger().warning("Failed to find alternate color code for: " + score);
                }
            } else {
                qLib.getInstance().getLogger().warning("Found a scoreboard base collision to shift: " + score);
            }
        }
        if (prefix.length() > 16) {
            prefix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";
        }
        if (score.length() > 16) {
            score = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";
        }
        if (suffix.length() > 16) {
            suffix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";
        }
        usedBunkerscores.add(score);
        this.prefixScoreSuffix[0] = prefix;
        this.prefixScoreSuffix[1] = score;
        this.prefixScoreSuffix[2] = suffix;
        return this.prefixScoreSuffix;
    }
}
