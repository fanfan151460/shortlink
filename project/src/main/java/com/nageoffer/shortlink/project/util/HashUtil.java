package com.nageoffer.shortlink.project.util;

import cn.hutool.core.lang.hash.MurmurHash;

import java.io.Serializable;

public class HashUtil implements Serializable {
    private static final char[] CHARS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };
    private static final int SIZE = CHARS.length;

    private static String convertDecToBase62(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int i = (int) (num % SIZE);
            sb.append(CHARS[i]);
            num /= SIZE;
        }
        return sb.reverse().toString();
    }

    private static long toHashCode(String base62Str) {
        int hash32 = MurmurHash.hash32(base62Str);
        return hash32 < 0 ? Integer.MAX_VALUE - (long) hash32 : hash32;
    }

    public static String createBase62Link(String decStr) {
        return convertDecToBase62(toHashCode(decStr));
    }

}
