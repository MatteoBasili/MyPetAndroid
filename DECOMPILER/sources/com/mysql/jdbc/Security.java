package com.mysql.jdbc;

import java.io.UnsupportedEncodingException;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {
    private static int CACHING_SHA2_DIGEST_LENGTH = 32;
    private static final char PVERSION41_CHAR = '*';
    private static final int SHA1_HASH_SIZE = 20;

    private static int charVal(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        return ((c < 'A' || c > 'Z') ? c - 'a' : c - 'A') + 10;
    }

    static byte[] createKeyFromOldPassword(String passwd) throws NoSuchAlgorithmException {
        return getBinaryPassword(getSaltFromPassword(makeScrambledPassword(passwd)), false);
    }

    static byte[] getBinaryPassword(int[] salt, boolean usingNewPasswords) throws NoSuchAlgorithmException {
        byte[] binaryPassword = new byte[20];
        if (usingNewPasswords) {
            int pos = 0;
            for (int i = 0; i < 4; i++) {
                int val = salt[i];
                int t = 3;
                while (t >= 0) {
                    binaryPassword[pos] = (byte) (val & 255);
                    val >>= 8;
                    t--;
                    pos++;
                }
            }
            return binaryPassword;
        }
        int offset = 0;
        for (int i2 = 0; i2 < 2; i2++) {
            int val2 = salt[i2];
            for (int t2 = 3; t2 >= 0; t2--) {
                binaryPassword[t2 + offset] = (byte) (val2 % 256);
                val2 >>= 8;
            }
            offset += 4;
        }
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(binaryPassword, 0, 8);
        return md.digest();
    }

    private static int[] getSaltFromPassword(String password) {
        int[] result = new int[6];
        if (password == null || password.length() == 0) {
            return result;
        }
        if (password.charAt(0) == '*') {
            String saltInHex = password.substring(1, 5);
            int val = 0;
            for (int i = 0; i < 4; i++) {
                val = (val << 4) + charVal(saltInHex.charAt(i));
            }
            return result;
        }
        int resultPos = 0;
        int pos = 0;
        int length = password.length();
        while (pos < length) {
            int val2 = 0;
            int i2 = 0;
            while (i2 < 8) {
                val2 = (val2 << 4) + charVal(password.charAt(pos));
                i2++;
                pos++;
            }
            result[resultPos] = val2;
            resultPos++;
        }
        return result;
    }

    private static String longToHex(long val) {
        String longHex = Long.toHexString(val);
        int length = longHex.length();
        if (length >= 8) {
            return longHex.substring(0, 8);
        }
        int padding = 8 - length;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < padding; i++) {
            buf.append("0");
        }
        buf.append(longHex);
        return buf.toString();
    }

    static String makeScrambledPassword(String password) throws NoSuchAlgorithmException {
        long[] passwordHash = Util.hashPre41Password(password);
        return longToHex(passwordHash[0]) + longToHex(passwordHash[1]);
    }

    public static void xorString(byte[] from, byte[] to, byte[] scramble, int length) {
        int scrambleLength = scramble.length;
        for (int pos = 0; pos < length; pos++) {
            to[pos] = (byte) (from[pos] ^ scramble[pos % scrambleLength]);
        }
    }

    static byte[] passwordHashStage1(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        StringBuilder cleansedPassword = new StringBuilder();
        int passwordLength = password.length();
        for (int i = 0; i < passwordLength; i++) {
            char c = password.charAt(i);
            if (!(c == ' ' || c == 9)) {
                cleansedPassword.append(c);
            }
        }
        return md.digest(StringUtils.getBytes(cleansedPassword.toString()));
    }

    static byte[] passwordHashStage2(byte[] hashedPassword, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(salt, 0, 4);
        md.update(hashedPassword, 0, 20);
        return md.digest();
    }

    public static byte[] scramble411(String password, String seed, String passwordEncoding) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] passwordHashStage1 = md.digest((passwordEncoding == null || passwordEncoding.length() == 0) ? StringUtils.getBytes(password) : StringUtils.getBytes(password, passwordEncoding));
        md.reset();
        byte[] passwordHashStage2 = md.digest(passwordHashStage1);
        md.reset();
        md.update(StringUtils.getBytes(seed, "ASCII"));
        md.update(passwordHashStage2);
        byte[] toBeXord = md.digest();
        int numToXor = toBeXord.length;
        for (int i = 0; i < numToXor; i++) {
            toBeXord[i] = (byte) (toBeXord[i] ^ passwordHashStage1[i]);
        }
        return toBeXord;
    }

    public static byte[] scrambleCachingSha2(byte[] password, byte[] seed) throws DigestException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            int i = CACHING_SHA2_DIGEST_LENGTH;
            byte[] dig1 = new byte[i];
            byte[] dig2 = new byte[i];
            byte[] scramble1 = new byte[i];
            md.update(password, 0, password.length);
            md.digest(dig1, 0, CACHING_SHA2_DIGEST_LENGTH);
            md.reset();
            md.update(dig1, 0, dig1.length);
            md.digest(dig2, 0, CACHING_SHA2_DIGEST_LENGTH);
            md.reset();
            md.update(dig2, 0, dig1.length);
            md.update(seed, 0, seed.length);
            md.digest(scramble1, 0, CACHING_SHA2_DIGEST_LENGTH);
            int i2 = CACHING_SHA2_DIGEST_LENGTH;
            byte[] mysqlScrambleBuff = new byte[i2];
            xorString(dig1, mysqlScrambleBuff, scramble1, i2);
            return mysqlScrambleBuff;
        } catch (NoSuchAlgorithmException ex) {
            throw new AssertionFailedException(ex);
        }
    }

    private Security() {
    }
}
