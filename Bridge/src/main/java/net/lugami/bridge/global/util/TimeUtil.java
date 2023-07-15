package net.lugami.bridge.global.util;

import org.apache.commons.lang.time.DurationFormatUtils;

import java.text.*;
import java.util.*;
import java.sql.*;
import java.util.Date;
import java.util.regex.*;

public class TimeUtil {

    public static String millisToTimer(long millis) {
        long seconds = millis / 1000L;
        if (seconds > 3600L) {
            return String.format("%02d:%02d:%02d", seconds / 3600L, seconds % 3600L / 60L, seconds % 60L);
        }
        return String.format("%02d:%02d", seconds / 60L, seconds % 60L);
    }

    public static String millisToSeconds(long millis) {
        return new DecimalFormat("#0.0").format(millis / 1000.0f);
    }

    public static String dateToString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTime().toString();
    }

    public static Timestamp addDuration(long duration) {
        return truncateTimestamp(new Timestamp(System.currentTimeMillis() + duration));
    }

    public static String formatDuration(long time) {
        return DurationFormatUtils.formatDurationWords(time, true, true);
    }

    public static Timestamp truncateTimestamp(Timestamp timestamp) {
        if (timestamp.toLocalDateTime().getYear() > 2037) {
            timestamp.setYear(2037);
        }
        return timestamp;
    }

    public static Timestamp addDuration(Timestamp timestamp) {
        return truncateTimestamp(new Timestamp(System.currentTimeMillis() + timestamp.getTime()));
    }

    public static Timestamp fromMillis(long millis) {
        return new Timestamp(millis);
    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static String getTimeZoneShortName(String displayName) {

        String[] stz = displayName.split(" ");

        StringBuilder sName = new StringBuilder();

        for (String s : stz) {
            sName.append(s.charAt(0));
        }
        return sName.toString();
    }

    public static String millisToRoundedTime(long millis) {
        if(millis == Long.MAX_VALUE) {
            return "Permanent";
        }
        ++millis;
        long seconds = millis / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        long days = hours / 24L;
        long weeks = days / 7L;
        long months = weeks / 4L;
        long years = months / 12L;
        if (years > 0L) {
            return years + " year" + ((years == 1L) ? "" : "s");
        }
        if (months > 0L) {
            return months + " month" + ((months == 1L) ? "" : "s");
        }
        if (weeks > 0L) {
            return weeks + " week" + ((weeks == 1L) ? "" : "s");
        }
        if (days > 0L) {
            return days + " day" + ((days == 1L) ? "" : "s");
        }
        if (hours > 0L) {
            return hours + " hour" + ((hours == 1L) ? "" : "s");
        }
        if (minutes > 0L) {
            return minutes + " minute" + ((minutes == 1L) ? "" : "s");
        }
        return seconds + " second" + ((seconds == 1L) ? "" : "s");
    }


    public static String parseToTime(String in) {
        in = in.toLowerCase();
        in = in.replaceAll(" ", "");
        in = in.replaceAll("seconds", "s");
        in = in.replaceAll("second", "s");

        in = in.replaceAll("minutes", "m");
        in = in.replaceAll("minute", "m");

        in = in.replaceAll("hours", "h");
        in = in.replaceAll("hour", "h");

        in = in.replaceAll("days", "d");
        in = in.replaceAll("day", "d");

        in = in.replaceAll("weeks", "w");
        in = in.replaceAll("week", "w");

        in = in.replaceAll("months", "M");
        in = in.replaceAll("month", "M");

        in = in.replaceAll("years", "y");
        in = in.replaceAll("year", "y");
        return in;
    }

    public static long parseTime(String time) {
        long totalTime = 0L;
        boolean found = false;
        Matcher matcher = Pattern.compile("\\d+\\D+").matcher(time);
        while (matcher.find()) {
            String s = matcher.group();
            Long value = Long.parseLong(s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
            String s2;
            String type = s2 = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1];
            switch (s2) {
                case "s": {
                    totalTime += value;
                    found = true;
                    continue;
                }
                case "m": {
                    totalTime += value * 60L;
                    found = true;
                    continue;
                }
                case "h": {
                    totalTime += value * 60L * 60L;
                    found = true;
                    continue;
                }
                case "d": {
                    totalTime += value * 60L * 60L * 24L;
                    found = true;
                    continue;
                }
                case "w": {
                    totalTime += value * 60L * 60L * 24L * 7L;
                    found = true;
                    continue;
                }
                case "M": {
                    totalTime += value * 60L * 60L * 24L * 30L;
                    found = true;
                    continue;
                }
                case "y": {
                    totalTime += value * 60L * 60L * 24L * 365L;
                    found = true;
                    continue;
                }
            }
        }
        if (time.equalsIgnoreCase("perm")) return Long.MAX_VALUE;
        return found ? (totalTime * 1000L) : -1L;
    }
}
