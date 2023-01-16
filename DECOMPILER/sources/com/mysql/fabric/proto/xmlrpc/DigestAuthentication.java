package com.mysql.fabric.proto.xmlrpc;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DigestAuthentication {
    private static Random random = new Random();

    public static String getChallengeHeader(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoOutput(true);
        conn.getOutputStream().close();
        try {
            conn.getInputStream().close();
            return null;
        } catch (IOException ex) {
            if (401 == conn.getResponseCode()) {
                String hdr = conn.getHeaderField("WWW-Authenticate");
                if (hdr == null || "".equals(hdr)) {
                    return null;
                }
                return hdr;
            } else if (400 == conn.getResponseCode()) {
                throw new IOException("Fabric returns status 400. If authentication is disabled on the Fabric node, omit the `fabricUsername' and `fabricPassword' properties from your connection.");
            } else {
                throw ex;
            }
        }
    }

    public static String calculateMD5RequestDigest(String uri, String username, String password, String realm, String nonce, String nc, String cnonce, String qop) {
        String reqA2 = "POST:" + uri;
        return digestMD5(checksumMD5(username + ":" + realm + ":" + password), nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + checksumMD5(reqA2));
    }

    private static String checksumMD5(String data) {
        try {
            return hexEncode(MessageDigest.getInstance("MD5").digest(data.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Unable to create MD5 instance", ex);
        }
    }

    private static String digestMD5(String secret, String data) {
        return checksumMD5(secret + ":" + data);
    }

    private static String hexEncode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            sb.append(String.format("%02x", new Object[]{Byte.valueOf(data[i])}));
        }
        return sb.toString();
    }

    public static String serializeDigestResponse(Map<String, String> paramMap) {
        StringBuilder sb = new StringBuilder("Digest ");
        boolean prefixComma = false;
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            if (!prefixComma) {
                prefixComma = true;
            } else {
                sb.append(", ");
            }
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
        }
        return sb.toString();
    }

    public static Map<String, String> parseDigestChallenge(String headerValue) {
        if (headerValue.startsWith("Digest ")) {
            String params = headerValue.substring(7);
            Map<String, String> paramMap = new HashMap<>();
            for (String param : params.split(",\\s*")) {
                String[] pieces = param.split("=");
                paramMap.put(pieces[0], pieces[1].replaceAll("^\"(.*)\"$", "$1"));
            }
            return paramMap;
        }
        throw new IllegalArgumentException("Header is not a digest challenge");
    }

    public static String generateCnonce(String nonce, String nc) {
        byte[] buf = new byte[8];
        random.nextBytes(buf);
        for (int i = 0; i < 8; i++) {
            buf[i] = (byte) ((buf[i] % 95) + 32);
        }
        try {
            return hexEncode(MessageDigest.getInstance("SHA-1").digest(String.format("%s:%s:%s:%s", new Object[]{nonce, nc, new Date().toGMTString(), new String(buf)}).getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Unable to create SHA-1 instance", ex);
        }
    }

    private static String quoteParam(String param) {
        if (!param.contains("\"") && !param.contains("'")) {
            return "\"" + param + "\"";
        }
        throw new IllegalArgumentException("Invalid character in parameter");
    }

    public static String generateAuthorizationHeader(Map<String, String> digestChallenge, String username, String password) {
        Map<String, String> map = digestChallenge;
        String nonce = map.get("nonce");
        String cnonce = generateCnonce(nonce, "00000001");
        String realm = map.get("realm");
        String requestDigest = calculateMD5RequestDigest("/RPC2", username, password, realm, nonce, "00000001", cnonce, "auth");
        Map<String, String> digestResponseMap = new HashMap<>();
        digestResponseMap.put("algorithm", "MD5");
        digestResponseMap.put("username", quoteParam(username));
        digestResponseMap.put("realm", quoteParam(realm));
        digestResponseMap.put("nonce", quoteParam(nonce));
        digestResponseMap.put("uri", quoteParam("/RPC2"));
        digestResponseMap.put("qop", "auth");
        digestResponseMap.put("nc", "00000001");
        digestResponseMap.put("cnonce", quoteParam(cnonce));
        digestResponseMap.put("response", quoteParam(requestDigest));
        digestResponseMap.put("opaque", quoteParam(map.get("opaque")));
        return serializeDigestResponse(digestResponseMap);
    }
}
