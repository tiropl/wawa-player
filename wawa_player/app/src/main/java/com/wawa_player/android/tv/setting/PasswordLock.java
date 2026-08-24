package com.wawa_player.android.tv.setting;

import com.github.catvod.utils.Prefers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class PasswordLock {

    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 8;

    private static final String KEY_HASH = "lock_pass_hash";
    private static final String KEY_SALT = "lock_pass_salt";

    public static boolean isSet() {
        return !Prefers.getString(KEY_HASH).isEmpty();
    }

    public static boolean isValid(String pass) {
        return pass != null && pass.length() >= MIN_LENGTH && pass.length() <= MAX_LENGTH;
    }

    public static boolean verify(String pass) {
        if (pass == null || pass.isEmpty()) return false;
        return hash(pass, Prefers.getString(KEY_SALT)).equals(Prefers.getString(KEY_HASH));
    }

    public static void setPassword(String pass) {
        String salt = generateSalt();
        Prefers.put(KEY_SALT, salt);
        Prefers.put(KEY_HASH, hash(pass, salt));
    }

    public static void clear() {
        Prefers.put(KEY_HASH, "");
        Prefers.put(KEY_SALT, "");
    }

    private static String generateSalt() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return hex(bytes);
    }

    private static String hash(String pass, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return hex(digest.digest((salt + pass).getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            return "";
        }
    }

    private static String hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
