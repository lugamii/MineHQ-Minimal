package net.lugami.practice.tab;

import net.lugami.practice.match.Match;
import net.lugami.practice.match.MatchTeam;
import net.lugami.qlib.tab.TabLayout;
import net.lugami.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiConsumer;

final class MatchParticipantLayoutProvider implements BiConsumer<Player, TabLayout> {

    @Override
    public void accept(Player player, TabLayout tabLayout) {
        Match match = Practice.getInstance().getMatchHandler().getMatchPlaying(player);
        List<MatchTeam> teams = match.getTeams();

        // if it's one team versus another
        if (teams.size() == 2) {
            // this method won't be called if the player isn't a participant
            MatchTeam ourTeam = match.getTeam(player.getUniqueId());
            MatchTeam otherTeam = teams.get(0) == ourTeam ? teams.get(1) : teams.get(0);

            boolean duel = ourTeam.getAllMembers().size() == 1 && otherTeam.getAllMembers().size() == 1;

            {
                // Column 1
                // we handle duels a bit differently
                if (!duel) {
                    tabLayout.set(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "Team " + ChatColor.GREEN + "(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");
                } else {
                    tabLayout.set(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "You");
                }
                renderTeamMemberOverviewEntries(tabLayout, ourTeam, 0, 4, ChatColor.GREEN);
            }

            {
                // Column 3
                // we handle duels a bit differently
                if (!duel) {
                    tabLayout.set(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Enemies " + ChatColor.RED + "(" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size() + ")");
                } else {
                    tabLayout.set(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Opponent");
                }
                renderTeamMemberOverviewEntries(tabLayout, otherTeam, 2, 4, ChatColor.RED);
            }
        } else { // it's an FFA or something else like that
            tabLayout.set(1, 3, ChatColor.BLUE + ChatColor.BOLD.toString() + "Party FFA");

            int x = 0;
            int y = 4;

            Map<String, Integer> entries = new LinkedHashMap<>();

            MatchTeam ourTeam = match.getTeam(player.getUniqueId());

            {
                // this is where we'll be adding our team members

                Map<String, Integer> aliveLines = new LinkedHashMap<>();
                Map<String, Integer> deadLines = new LinkedHashMap<>();

                // separate lists to sort alive players before dead
                // + color differently
                for (UUID teamMember : ourTeam.getAllMembers()) {
                    if (ourTeam.isAlive(teamMember)) {
                        aliveLines.put(ChatColor.GREEN + player.getName(),  PracticeLayoutProvider.getPingOrDefault(teamMember));
                    } else {
                        deadLines.put("&7&m" + player.getName(), PracticeLayoutProvider.getPingOrDefault(teamMember));
                    }
                }

                entries.putAll(aliveLines);
                entries.putAll(deadLines);
            }

            {
                // this is where we'll be adding everyone else
                Map<String, Integer> deadLines = new LinkedHashMap<>();

                for (MatchTeam otherTeam : match.getTeams()) {
                    if (otherTeam == ourTeam) {
                        continue;
                    }

                    // separate lists to sort alive players before dead
                    // + color differently
                    for (UUID enemy : otherTeam.getAllMembers()) {
                        if (otherTeam.isAlive(enemy)) {
                            entries.put(ChatColor.RED + player.getName(), PracticeLayoutProvider.getPingOrDefault(enemy));
                        } else {
                            deadLines.put("&7&m" + player.getName(), PracticeLayoutProvider.getPingOrDefault(enemy));
                        }
                    }
                }

                entries.putAll(deadLines);
            }

            List<Map.Entry<String, Integer>> result = new ArrayList<>(entries.entrySet());

            // actually display our entries
            for (int index = 0; index < result.size(); index++) {
                Map.Entry<String, Integer> entry = result.get(index);

                tabLayout.set(x++, y, entry.getKey(), entry.getValue());

                if (x == 3 && y == PracticeLayoutProvider.MAX_TAB_Y) {
                    // if we're at the last slot, we want to see if we still have alive players to show
                    int aliveLeft = 0;

                    for (int i = index; i < result.size(); i++) {
                        String currentEntry = result.get(i).getKey();
                        boolean dead = ChatColor.getLastColors(currentEntry).equals(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString());

                        if (!dead) {
                            aliveLeft++;
                        }
                    }

                    if (aliveLeft != 0 && aliveLeft != 1) {
                        // if there are players we weren't able to show and if it's more than one
                        // (if it's only one they'll be shown as the last entry [see 17 lines above]), display the number
                        // of alive players we weren't able to show instead.
                        tabLayout.set(x, y, ChatColor.GREEN + "+" + aliveLeft);
                    }

                    break;
                }

                if (x == 3) {
                    x = 0;
                    y++;
                }
            }
        }
    }

    private void renderTeamMemberOverviewEntries(TabLayout layout, MatchTeam team, int column, int start, ChatColor color) {
        List<Map.Entry<String, Integer>> result = new ArrayList<>(renderTeamMemberOverviewLines(team, color).entrySet());

        // how many spots we have left
        int spotsLeft = PracticeLayoutProvider.MAX_TAB_Y - start;

        // we could've used the 'start' variable, but we create a new one for readability.
        int y = start;

        for (int index = 0; index < result.size(); index++) {
            Map.Entry<String, Integer> entry = result.get(index);

            // we check if we only have 1 more spot to show
            if (spotsLeft == 1) {
                // if so, count how many alive players we have left to show
                int aliveLeft = 0;

                for (int i = index; i < result.size(); i++) {
                    String currentEntry = result.get(i).getKey();
                    boolean dead = !ChatColor.getLastColors(currentEntry).equals(color.toString());

                    if (!dead) {
                        aliveLeft++;
                    }
                }

                // if we have any
                if (aliveLeft != 0) {
                    if (aliveLeft == 1) {
                        // if it's only one, we display them as the last entry
                        layout.set(column, y, entry.getKey(), entry.getValue());
                    } else {
                        // if it's more than one, display a number of how many we couldn't display.
                        layout.set(column, y, color + "+" + aliveLeft);
                    }
                }

                break;
            }

            // if not, just display the entry.
            layout.set(column, y, entry.getKey(), entry.getValue());
            y++;
            spotsLeft--;
        }
    }

    private Map<String, Integer> renderTeamMemberOverviewLines(MatchTeam team, ChatColor aliveColor) {
        Map<String, Integer> aliveLines = new LinkedHashMap<>();
        Map<String, Integer> deadLines = new LinkedHashMap<>();

        for (UUID member : team.getAllMembers()) {
            Player player = Bukkit.getPlayer(member);
            int ping = PracticeLayoutProvider.getPingOrDefault(member);

            if (team.isAlive(member)) {
                aliveLines.put(aliveColor + player.getName(), ping);
            } else {
                deadLines.put("&7&m" + player.getName(), ping);
            }
        }

        Map<String, Integer> result = new LinkedHashMap<>();

        result.putAll(aliveLines);
        result.putAll(deadLines);

        return result;
    }

}