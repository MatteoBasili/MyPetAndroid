package com.sun.mail.smtp;

import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class DigestMD5 {
    private static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private String clientResponse;
    private PrintStream debugout;
    private MessageDigest md5;
    private String uri;

    public DigestMD5(PrintStream debugout2) {
        this.debugout = debugout2;
        if (debugout2 != null) {
            debugout2.println("DEBUG DIGEST-MD5: Loaded");
        }
    }

    public byte[] authClient(String host, String user, String passwd, String realm, String serverChallenge) throws IOException {
        String realm2;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStream b64os = new BASE64EncoderStream(bos, Integer.MAX_VALUE);
        try {
            SecureRandom random = new SecureRandom();
            this.md5 = MessageDigest.getInstance("MD5");
            StringBuffer result = new StringBuffer();
            String str = host;
            this.uri = "smtp/" + str;
            byte[] bytes = new byte[32];
            PrintStream printStream = this.debugout;
            if (printStream != null) {
                printStream.println("DEBUG DIGEST-MD5: Begin authentication ...");
            }
            Hashtable map = tokenize(serverChallenge);
            if (realm == null) {
                String text = (String) map.get("realm");
                if (text != null) {
                    realm2 = new StringTokenizer(text, ",").nextToken();
                } else {
                    realm2 = str;
                }
            } else {
                realm2 = realm;
            }
            String nonce = (String) map.get("nonce");
            random.nextBytes(bytes);
            b64os.write(bytes);
            b64os.flush();
            String cnonce = bos.toString();
            bos.reset();
            MessageDigest messageDigest = this.md5;
            SecureRandom secureRandom = random;
            messageDigest.update(messageDigest.digest(ASCIIUtility.getBytes(String.valueOf(user) + ":" + realm2 + ":" + passwd)));
            this.md5.update(ASCIIUtility.getBytes(":" + nonce + ":" + cnonce));
            this.clientResponse = String.valueOf(toHex(this.md5.digest())) + ":" + nonce + ":" + "00000001" + ":" + cnonce + ":" + "auth" + ":";
            this.md5.update(ASCIIUtility.getBytes("AUTHENTICATE:" + this.uri));
            this.md5.update(ASCIIUtility.getBytes(String.valueOf(this.clientResponse) + toHex(this.md5.digest())));
            result.append("username=\"" + user + "\"");
            result.append(",realm=\"" + realm2 + "\"");
            result.append(",qop=" + "auth");
            result.append(",nc=" + "00000001");
            result.append(",nonce=\"" + nonce + "\"");
            result.append(",cnonce=\"" + cnonce + "\"");
            result.append(",digest-uri=\"" + this.uri + "\"");
            result.append(",response=" + toHex(this.md5.digest()));
            PrintStream printStream2 = this.debugout;
            if (printStream2 != null) {
                printStream2.println("DEBUG DIGEST-MD5: Response => " + result.toString());
            }
            b64os.write(ASCIIUtility.getBytes(result.toString()));
            b64os.flush();
            return bos.toByteArray();
        } catch (NoSuchAlgorithmException ex) {
            String str2 = user;
            String str3 = serverChallenge;
            PrintStream printStream3 = this.debugout;
            if (printStream3 != null) {
                printStream3.println("DEBUG DIGEST-MD5: " + ex);
            }
            throw new IOException(ex.toString());
        }
    }

    public boolean authServer(String serverResponse) throws IOException {
        Hashtable map = tokenize(serverResponse);
        this.md5.update(ASCIIUtility.getBytes(":" + this.uri));
        this.md5.update(ASCIIUtility.getBytes(String.valueOf(this.clientResponse) + toHex(this.md5.digest())));
        String text = toHex(this.md5.digest());
        if (text.equals((String) map.get("rspauth"))) {
            return true;
        }
        PrintStream printStream = this.debugout;
        if (printStream == null) {
            return false;
        }
        printStream.println("DEBUG DIGEST-MD5: Expected => rspauth=" + text);
        return false;
    }

    private Hashtable tokenize(String serverResponse) throws IOException {
        Hashtable map = new Hashtable();
        byte[] bytes = serverResponse.getBytes();
        String key = null;
        StreamTokenizer tokens = new StreamTokenizer(new InputStreamReader(new BASE64DecoderStream(new ByteArrayInputStream(bytes, 4, bytes.length - 4))));
        tokens.ordinaryChars(48, 57);
        tokens.wordChars(48, 57);
        while (true) {
            int nextToken = tokens.nextToken();
            int ttype = nextToken;
            if (nextToken != -1) {
                switch (ttype) {
                    case -3:
                        if (key == null) {
                            key = tokens.sval;
                            break;
                        }
                    case 34:
                        PrintStream printStream = this.debugout;
                        if (printStream != null) {
                            printStream.println("DEBUG DIGEST-MD5: Received => " + key + "='" + tokens.sval + "'");
                        }
                        if (map.containsKey(key)) {
                            map.put(key, map.get(key) + "," + tokens.sval);
                        } else {
                            map.put(key, tokens.sval);
                        }
                        key = null;
                        break;
                }
            } else {
                return map;
            }
        }
    }

    private static String toHex(byte[] bytes) {
        char[] result = new char[(bytes.length * 2)];
        int i = 0;
        for (byte b : bytes) {
            int temp = b & 255;
            int i2 = i + 1;
            char[] cArr = digits;
            result[i] = cArr[temp >> 4];
            i = i2 + 1;
            result[i2] = cArr[temp & 15];
        }
        return new String(result);
    }
}
