package com.abclinic.server.common.utils;

/**
 * @author tmduc
 * @package com.abclinic.server.common.utils
 * @created 4/22/2020 9:54 AM
 */
public class BinaryUtils {
    public static int and(int a, int b) {
        return a & b;
    }

    public static int or(int a, int b) {
        return a | b;
    }

    public static int xor(int a, int b) {
        return a ^ b;
    }
}
