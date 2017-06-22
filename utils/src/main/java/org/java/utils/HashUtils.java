package org.java.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by msamoylych on 27.04.2017.
 */
public class HashUtils {

    private static final Random RND = new SecureRandom();

    private static final String SHA512 = "SHA-512";
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();

    public static byte[] sha512(byte[]... inputs) {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA512);
            for (byte[] input : inputs) {
                md.update(input);
            }
            return md.digest();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static String sha512String(byte[]... inputs) {
        return bytesToHex(sha512(inputs));
    }

    public static byte[] salt() {
        byte[] salt = new byte[32];
        RND.nextBytes(salt);
        return salt;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX[v >>> 4];
            hexChars[i * 2 + 1] = HEX[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
