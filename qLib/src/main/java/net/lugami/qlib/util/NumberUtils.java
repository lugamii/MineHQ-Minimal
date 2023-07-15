package net.lugami.qlib.util;

public class NumberUtils {

    public static boolean isInteger(String s) {
        int radix = 10;
        int result = 0;
        int i = 0;
        int len = s.length();
        int limit = -2147483647;
        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') {
                if (firstChar == '-') {
                    limit = Integer.MIN_VALUE;
                } else if (firstChar != '+') {
                    return false;
                }
                if (len == 1) {
                    return false;
                }
                ++i;
            }
            int multmin = limit / radix;
            while (i < len) {
                int digit;
                if ((digit = Character.digit(s.charAt(i++), radix)) < 0) {
                    return false;
                }
                if (result < multmin) {
                    return false;
                }
                if ((result *= radix) < limit + digit) {
                    return false;
                }
                result -= digit;
            }
        } else {
            return false;
        }
        return true;
    }

    public static boolean isShort(String input) {
        if (!NumberUtils.isInteger(input)) {
            return false;
        }
        int value = Integer.parseInt(input);
        return value > -32768 && value < 32767;
    }
}

