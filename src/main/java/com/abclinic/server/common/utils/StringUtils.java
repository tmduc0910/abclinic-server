package com.abclinic.server.common.utils;

/**
 * @author tmduc
 * @package com.abclinic.server.utils
 * @created 4/22/2020 9:42 AM
 */
public class StringUtils {
    public static boolean isNull(String s) {
        return s.length() == 0 || s.trim().equals("");
    }

    public static String toBinary(int i) {
        return Integer.toBinaryString(i);
    }

    public static boolean endsWith(String s, String c) {
        return s.endsWith(c);
    }

    public static boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean contains(String s, String c) {
        if (!isNull(s) && !isNull(c)) {
            return s.contains(c);
        }
        return false;
    }
}
