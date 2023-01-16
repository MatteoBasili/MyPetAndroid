package javax.mail.internet;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import javax.mail.Address;
import javax.mail.Session;
import kotlin.text.Typography;

public class InternetAddress extends Address implements Cloneable {
    private static final String rfc822phrase = HeaderTokenizer.RFC822.replace(' ', 0).replace(9, 0);
    private static final long serialVersionUID = -7507595530758302903L;
    private static final String specialsNoDot = "()<>,;:\\\"[]@";
    private static final String specialsNoDotNoAt = "()<>,;:\\\"[]";
    protected String address;
    protected String encodedPersonal;
    protected String personal;

    public InternetAddress() {
    }

    public InternetAddress(String address2) throws AddressException {
        InternetAddress[] a = parse(address2, true);
        if (a.length == 1) {
            this.address = a[0].address;
            this.personal = a[0].personal;
            this.encodedPersonal = a[0].encodedPersonal;
            return;
        }
        throw new AddressException("Illegal address", address2);
    }

    public InternetAddress(String address2, boolean strict) throws AddressException {
        this(address2);
        if (strict) {
            checkAddress(this.address, true, true);
        }
    }

    public InternetAddress(String address2, String personal2) throws UnsupportedEncodingException {
        this(address2, personal2, (String) null);
    }

    public InternetAddress(String address2, String personal2, String charset) throws UnsupportedEncodingException {
        this.address = address2;
        setPersonal(personal2, charset);
    }

    public Object clone() {
        try {
            return (InternetAddress) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getType() {
        return "rfc822";
    }

    public void setAddress(String address2) {
        this.address = address2;
    }

    public void setPersonal(String name, String charset) throws UnsupportedEncodingException {
        this.personal = name;
        if (name != null) {
            this.encodedPersonal = MimeUtility.encodeWord(name, charset, (String) null);
        } else {
            this.encodedPersonal = null;
        }
    }

    public void setPersonal(String name) throws UnsupportedEncodingException {
        this.personal = name;
        if (name != null) {
            this.encodedPersonal = MimeUtility.encodeWord(name);
        } else {
            this.encodedPersonal = null;
        }
    }

    public String getAddress() {
        return this.address;
    }

    public String getPersonal() {
        String str = this.personal;
        if (str != null) {
            return str;
        }
        String str2 = this.encodedPersonal;
        if (str2 == null) {
            return null;
        }
        try {
            String decodeText = MimeUtility.decodeText(str2);
            this.personal = decodeText;
            return decodeText;
        } catch (Exception e) {
            return this.encodedPersonal;
        }
    }

    public String toString() {
        String str;
        if (this.encodedPersonal == null && (str = this.personal) != null) {
            try {
                this.encodedPersonal = MimeUtility.encodeWord(str);
            } catch (UnsupportedEncodingException e) {
            }
        }
        if (this.encodedPersonal != null) {
            return String.valueOf(quotePhrase(this.encodedPersonal)) + " <" + this.address + ">";
        }
        if (isGroup() || isSimple()) {
            return this.address;
        }
        return "<" + this.address + ">";
    }

    public String toUnicodeString() {
        String p = getPersonal();
        if (p != null) {
            return String.valueOf(quotePhrase(p)) + " <" + this.address + ">";
        }
        if (isGroup() || isSimple()) {
            return this.address;
        }
        return "<" + this.address + ">";
    }

    private static String quotePhrase(String phrase) {
        int len = phrase.length();
        boolean needQuoting = false;
        for (int i = 0; i < len; i++) {
            char c = phrase.charAt(i);
            if (c == '\"' || c == '\\') {
                StringBuffer sb = new StringBuffer(len + 3);
                sb.append(Typography.quote);
                for (int j = 0; j < len; j++) {
                    char cc = phrase.charAt(j);
                    if (cc == '\"' || cc == '\\') {
                        sb.append('\\');
                    }
                    sb.append(cc);
                }
                sb.append(Typography.quote);
                return sb.toString();
            }
            if ((c < ' ' && c != 13 && c != 10 && c != 9) || c >= 127 || rfc822phrase.indexOf(c) >= 0) {
                needQuoting = true;
            }
        }
        if (!needQuoting) {
            return phrase;
        }
        StringBuffer sb2 = new StringBuffer(len + 2);
        sb2.append(Typography.quote).append(phrase).append(Typography.quote);
        return sb2.toString();
    }

    private static String unquote(String s) {
        if (!s.startsWith("\"") || !s.endsWith("\"")) {
            return s;
        }
        String s2 = s.substring(1, s.length() - 1);
        if (s2.indexOf(92) < 0) {
            return s2;
        }
        StringBuffer sb = new StringBuffer(s2.length());
        int i = 0;
        while (i < s2.length()) {
            char c = s2.charAt(i);
            if (c == '\\' && i < s2.length() - 1) {
                i++;
                c = s2.charAt(i);
            }
            sb.append(c);
            i++;
        }
        return sb.toString();
    }

    public boolean equals(Object a) {
        if (!(a instanceof InternetAddress)) {
            return false;
        }
        String s = ((InternetAddress) a).getAddress();
        String str = this.address;
        if (s == str) {
            return true;
        }
        if (str == null || !str.equalsIgnoreCase(s)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        String str = this.address;
        if (str == null) {
            return 0;
        }
        return str.toLowerCase(Locale.ENGLISH).hashCode();
    }

    public static String toString(Address[] addresses) {
        return toString(addresses, 0);
    }

    public static String toString(Address[] addresses, int used) {
        if (addresses == null || addresses.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < addresses.length; i++) {
            if (i != 0) {
                sb.append(", ");
                used += 2;
            }
            String s = addresses[i].toString();
            if (used + lengthOfFirstSegment(s) > 76) {
                sb.append("\r\n\t");
                used = 8;
            }
            sb.append(s);
            used = lengthOfLastSegment(s, used);
        }
        return sb.toString();
    }

    private static int lengthOfFirstSegment(String s) {
        int indexOf = s.indexOf("\r\n");
        int pos = indexOf;
        if (indexOf != -1) {
            return pos;
        }
        return s.length();
    }

    private static int lengthOfLastSegment(String s, int used) {
        int lastIndexOf = s.lastIndexOf("\r\n");
        int pos = lastIndexOf;
        if (lastIndexOf != -1) {
            return (s.length() - pos) - 2;
        }
        return s.length() + used;
    }

    public static InternetAddress getLocalAddress(Session session) {
        InetAddress me;
        String user = null;
        String host = null;
        String address2 = null;
        if (session == null) {
            try {
                user = System.getProperty("user.name");
                host = InetAddress.getLocalHost().getHostName();
            } catch (SecurityException | UnknownHostException | AddressException e) {
                return null;
            }
        } else {
            address2 = session.getProperty("mail.from");
            if (address2 == null) {
                user = session.getProperty("mail.user");
                if (user == null || user.length() == 0) {
                    user = session.getProperty("user.name");
                }
                if (user == null || user.length() == 0) {
                    user = System.getProperty("user.name");
                }
                host = session.getProperty("mail.host");
                if ((host == null || host.length() == 0) && (me = InetAddress.getLocalHost()) != null) {
                    host = me.getHostName();
                }
            }
        }
        if (!(address2 != null || user == null || user.length() == 0 || host == null || host.length() == 0)) {
            address2 = String.valueOf(user) + "@" + host;
        }
        if (address2 != null) {
            return new InternetAddress(address2);
        }
        return null;
    }

    public static InternetAddress[] parse(String addresslist) throws AddressException {
        return parse(addresslist, true);
    }

    public static InternetAddress[] parse(String addresslist, boolean strict) throws AddressException {
        return parse(addresslist, strict, false);
    }

    public static InternetAddress[] parseHeader(String addresslist, boolean strict) throws AddressException {
        return parse(addresslist, strict, true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:145:0x0227, code lost:
        r11 = r11 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00a7, code lost:
        r11 = r11 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0102, code lost:
        r11 = r11 + 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static javax.mail.internet.InternetAddress[] parse(java.lang.String r20, boolean r21, boolean r22) throws javax.mail.internet.AddressException {
        /*
            r0 = r20
            r1 = -1
            r2 = -1
            int r3 = r20.length()
            r4 = 0
            r5 = 0
            r6 = 0
            java.util.Vector r7 = new java.util.Vector
            r7.<init>()
            r8 = -1
            r9 = r8
            r10 = r8
            r11 = 0
        L_0x0014:
            r12 = 0
            if (r11 < r3) goto L_0x0078
            if (r10 < 0) goto L_0x006e
            if (r9 != r8) goto L_0x001c
            r9 = r11
        L_0x001c:
            java.lang.String r8 = r0.substring(r10, r9)
            java.lang.String r8 = r8.trim()
            if (r6 != 0) goto L_0x004b
            if (r21 != 0) goto L_0x004b
            if (r22 == 0) goto L_0x002b
            goto L_0x004b
        L_0x002b:
            java.util.StringTokenizer r13 = new java.util.StringTokenizer
            r13.<init>(r8)
        L_0x0031:
            boolean r14 = r13.hasMoreTokens()
            if (r14 != 0) goto L_0x0038
            goto L_0x006e
        L_0x0038:
            java.lang.String r14 = r13.nextToken()
            checkAddress(r14, r12, r12)
            javax.mail.internet.InternetAddress r15 = new javax.mail.internet.InternetAddress
            r15.<init>()
            r15.setAddress(r14)
            r7.addElement(r15)
            goto L_0x0031
        L_0x004b:
            if (r21 != 0) goto L_0x004f
            if (r22 != 0) goto L_0x0052
        L_0x004f:
            checkAddress(r8, r5, r12)
        L_0x0052:
            javax.mail.internet.InternetAddress r12 = new javax.mail.internet.InternetAddress
            r12.<init>()
            r12.setAddress(r8)
            if (r1 < 0) goto L_0x006b
            java.lang.String r13 = r0.substring(r1, r2)
            java.lang.String r13 = r13.trim()
            java.lang.String r13 = unquote(r13)
            r12.encodedPersonal = r13
        L_0x006b:
            r7.addElement(r12)
        L_0x006e:
            int r8 = r7.size()
            javax.mail.internet.InternetAddress[] r8 = new javax.mail.internet.InternetAddress[r8]
            r7.copyInto(r8)
            return r8
        L_0x0078:
            char r13 = r0.charAt(r11)
            java.lang.String r14 = "Missing '\"'"
            r15 = 1
            switch(r13) {
                case 9: goto L_0x022a;
                case 10: goto L_0x022a;
                case 13: goto L_0x022a;
                case 32: goto L_0x022a;
                case 34: goto L_0x0202;
                case 40: goto L_0x01c5;
                case 41: goto L_0x01bd;
                case 44: goto L_0x0149;
                case 58: goto L_0x0138;
                case 59: goto L_0x010c;
                case 60: goto L_0x00b1;
                case 62: goto L_0x00a9;
                case 91: goto L_0x0088;
                default: goto L_0x0082;
            }
        L_0x0082:
            r8 = -1
            if (r10 != r8) goto L_0x022b
            r10 = r11
            goto L_0x022b
        L_0x0088:
            r12 = 1
            int r11 = r11 + 1
        L_0x008b:
            if (r11 < r3) goto L_0x008e
            goto L_0x0097
        L_0x008e:
            char r13 = r0.charAt(r11)
            switch(r13) {
                case 92: goto L_0x00a4;
                case 93: goto L_0x0096;
                default: goto L_0x0095;
            }
        L_0x0095:
            goto L_0x00a7
        L_0x0096:
        L_0x0097:
            if (r11 >= r3) goto L_0x009c
            r6 = r12
            goto L_0x022b
        L_0x009c:
            javax.mail.internet.AddressException r6 = new javax.mail.internet.AddressException
            java.lang.String r8 = "Missing ']'"
            r6.<init>(r8, r0, r11)
            throw r6
        L_0x00a4:
            int r11 = r11 + 1
        L_0x00a7:
            int r11 = r11 + r15
            goto L_0x008b
        L_0x00a9:
            javax.mail.internet.AddressException r8 = new javax.mail.internet.AddressException
            java.lang.String r12 = "Missing '<'"
            r8.<init>(r12, r0, r11)
            throw r8
        L_0x00b1:
            r6 = 1
            if (r5 != 0) goto L_0x0104
            if (r4 != 0) goto L_0x00c3
            r1 = r10
            if (r1 < 0) goto L_0x00ba
            r2 = r11
        L_0x00ba:
            int r10 = r11 + 1
            r16 = r1
            r17 = r2
            r18 = r10
            goto L_0x00c9
        L_0x00c3:
            r16 = r1
            r17 = r2
            r18 = r10
        L_0x00c9:
            r1 = 0
            int r11 = r11 + 1
        L_0x00cc:
            if (r11 < r3) goto L_0x00cf
            goto L_0x00dd
        L_0x00cf:
            char r13 = r0.charAt(r11)
            switch(r13) {
                case 34: goto L_0x00fb;
                case 62: goto L_0x00da;
                case 92: goto L_0x00d7;
                default: goto L_0x00d6;
            }
        L_0x00d6:
            goto L_0x0102
        L_0x00d7:
            int r11 = r11 + 1
            goto L_0x0102
        L_0x00da:
            if (r1 == 0) goto L_0x00dd
            goto L_0x0102
        L_0x00dd:
            if (r11 < r3) goto L_0x00ef
            if (r1 == 0) goto L_0x00e7
            javax.mail.internet.AddressException r2 = new javax.mail.internet.AddressException
            r2.<init>(r14, r0, r11)
            throw r2
        L_0x00e7:
            javax.mail.internet.AddressException r2 = new javax.mail.internet.AddressException
            java.lang.String r8 = "Missing '>'"
            r2.<init>(r8, r0, r11)
            throw r2
        L_0x00ef:
            r2 = 1
            r5 = r11
            r9 = r5
            r1 = r16
            r10 = r18
            r5 = r2
            r2 = r17
            goto L_0x022b
        L_0x00fb:
            if (r1 == 0) goto L_0x00ff
            r2 = r12
            goto L_0x0100
        L_0x00ff:
            r2 = r15
        L_0x0100:
            r1 = r2
        L_0x0102:
            int r11 = r11 + r15
            goto L_0x00cc
        L_0x0104:
            javax.mail.internet.AddressException r8 = new javax.mail.internet.AddressException
            java.lang.String r12 = "Extra route-addr"
            r8.<init>(r12, r0, r11)
            throw r8
        L_0x010c:
            if (r10 != r8) goto L_0x010f
            r10 = r11
        L_0x010f:
            if (r4 == 0) goto L_0x012f
            r4 = 0
            if (r10 != r8) goto L_0x0115
            r10 = r11
        L_0x0115:
            javax.mail.internet.InternetAddress r12 = new javax.mail.internet.InternetAddress
            r12.<init>()
            int r9 = r11 + 1
            java.lang.String r14 = r0.substring(r10, r9)
            java.lang.String r14 = r14.trim()
            r12.setAddress(r14)
            r7.addElement(r12)
            r5 = 0
            r9 = r8
            r10 = r8
            goto L_0x022b
        L_0x012f:
            javax.mail.internet.AddressException r8 = new javax.mail.internet.AddressException
            java.lang.String r12 = "Illegal semicolon, not in group"
            r8.<init>(r12, r0, r11)
            throw r8
        L_0x0138:
            r6 = 1
            if (r4 != 0) goto L_0x0141
            r4 = 1
            if (r10 != r8) goto L_0x022b
            r10 = r11
            goto L_0x022b
        L_0x0141:
            javax.mail.internet.AddressException r8 = new javax.mail.internet.AddressException
            java.lang.String r12 = "Nested group"
            r8.<init>(r12, r0, r11)
            throw r8
        L_0x0149:
            if (r10 != r8) goto L_0x0151
            r5 = 0
            r6 = 0
            r9 = r8
            r10 = r8
            goto L_0x022b
        L_0x0151:
            if (r4 == 0) goto L_0x0156
            r5 = 0
            goto L_0x022b
        L_0x0156:
            if (r9 != r8) goto L_0x0159
            r9 = r11
        L_0x0159:
            java.lang.String r14 = r0.substring(r10, r9)
            java.lang.String r14 = r14.trim()
            if (r6 != 0) goto L_0x018e
            if (r21 != 0) goto L_0x018e
            if (r22 == 0) goto L_0x0168
            goto L_0x018e
        L_0x0168:
            java.util.StringTokenizer r15 = new java.util.StringTokenizer
            r15.<init>(r14)
        L_0x016e:
            boolean r17 = r15.hasMoreTokens()
            if (r17 != 0) goto L_0x0175
            goto L_0x01b5
        L_0x0175:
            java.lang.String r8 = r15.nextToken()
            checkAddress(r8, r12, r12)
            javax.mail.internet.InternetAddress r18 = new javax.mail.internet.InternetAddress
            r18.<init>()
            r19 = r18
            r12 = r19
            r12.setAddress(r8)
            r7.addElement(r12)
            r8 = -1
            r12 = 0
            goto L_0x016e
        L_0x018e:
            if (r21 != 0) goto L_0x0192
            if (r22 != 0) goto L_0x0196
        L_0x0192:
            r8 = 0
            checkAddress(r14, r5, r8)
        L_0x0196:
            javax.mail.internet.InternetAddress r8 = new javax.mail.internet.InternetAddress
            r8.<init>()
            r8.setAddress(r14)
            if (r1 < 0) goto L_0x01b2
            java.lang.String r12 = r0.substring(r1, r2)
            java.lang.String r12 = r12.trim()
            java.lang.String r12 = unquote(r12)
            r8.encodedPersonal = r12
            r12 = -1
            r2 = r12
            r1 = r12
        L_0x01b2:
            r7.addElement(r8)
        L_0x01b5:
            r5 = 0
            r6 = 0
            r8 = -1
            r9 = r8
            r10 = r8
            r8 = -1
            goto L_0x022b
        L_0x01bd:
            javax.mail.internet.AddressException r8 = new javax.mail.internet.AddressException
            java.lang.String r12 = "Missing '('"
            r8.<init>(r12, r0, r11)
            throw r8
        L_0x01c5:
            r6 = 1
            if (r10 < 0) goto L_0x01cd
            r8 = -1
            if (r9 != r8) goto L_0x01ce
            r9 = r11
            goto L_0x01ce
        L_0x01cd:
            r8 = -1
        L_0x01ce:
            if (r1 != r8) goto L_0x01d2
            int r1 = r11 + 1
        L_0x01d2:
            int r11 = r11 + 1
            r8 = 1
        L_0x01d5:
            if (r11 >= r3) goto L_0x01ee
            if (r8 > 0) goto L_0x01da
            goto L_0x01ee
        L_0x01da:
            char r13 = r0.charAt(r11)
            switch(r13) {
                case 40: goto L_0x01e8;
                case 41: goto L_0x01e5;
                case 92: goto L_0x01e2;
                default: goto L_0x01e1;
            }
        L_0x01e1:
            goto L_0x01eb
        L_0x01e2:
            int r11 = r11 + 1
            goto L_0x01eb
        L_0x01e5:
            int r8 = r8 + -1
            goto L_0x01eb
        L_0x01e8:
            int r8 = r8 + 1
        L_0x01eb:
            r12 = 1
            int r11 = r11 + r12
            goto L_0x01d5
        L_0x01ee:
            if (r8 > 0) goto L_0x01fa
            int r11 = r11 + -1
            r12 = -1
            if (r2 != r12) goto L_0x01f8
            r2 = r11
            r8 = -1
            goto L_0x022b
        L_0x01f8:
            r8 = -1
            goto L_0x022b
        L_0x01fa:
            javax.mail.internet.AddressException r12 = new javax.mail.internet.AddressException
            java.lang.String r14 = "Missing ')'"
            r12.<init>(r14, r0, r11)
            throw r12
        L_0x0202:
            r8 = 1
            r6 = -1
            if (r10 != r6) goto L_0x0209
            r10 = r11
            r12 = r10
            goto L_0x020a
        L_0x0209:
            r12 = r10
        L_0x020a:
            int r11 = r11 + 1
        L_0x020c:
            if (r11 < r3) goto L_0x020f
            goto L_0x021b
        L_0x020f:
            char r13 = r0.charAt(r11)
            switch(r13) {
                case 34: goto L_0x021a;
                case 92: goto L_0x0217;
                default: goto L_0x0216;
            }
        L_0x0216:
            goto L_0x0227
        L_0x0217:
            int r11 = r11 + 1
            goto L_0x0227
        L_0x021a:
        L_0x021b:
            if (r11 >= r3) goto L_0x0221
            r6 = r8
            r10 = r12
            r8 = -1
            goto L_0x022b
        L_0x0221:
            javax.mail.internet.AddressException r6 = new javax.mail.internet.AddressException
            r6.<init>(r14, r0, r11)
            throw r6
        L_0x0227:
            r6 = 1
            int r11 = r11 + r6
            goto L_0x020c
        L_0x022a:
            r8 = -1
        L_0x022b:
            r12 = 1
            int r11 = r11 + r12
            goto L_0x0014
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.mail.internet.InternetAddress.parse(java.lang.String, boolean, boolean):javax.mail.internet.InternetAddress[]");
    }

    public void validate() throws AddressException {
        checkAddress(getAddress(), true, true);
    }

    private static void checkAddress(String addr, boolean routeAddr, boolean validate) throws AddressException {
        String domain;
        String local;
        int start = 0;
        if (addr.indexOf(34) < 0) {
            if (routeAddr) {
                start = 0;
                while (true) {
                    int indexOfAny = indexOfAny(addr, ",:", start);
                    int i = indexOfAny;
                    if (indexOfAny < 0) {
                        break;
                    } else if (addr.charAt(start) != '@') {
                        throw new AddressException("Illegal route-addr", addr);
                    } else if (addr.charAt(i) == ':') {
                        start = i + 1;
                        break;
                    } else {
                        start = i + 1;
                    }
                }
            }
            int indexOf = addr.indexOf(64, start);
            int i2 = indexOf;
            if (indexOf >= 0) {
                if (i2 == start) {
                    throw new AddressException("Missing local name", addr);
                } else if (i2 != addr.length() - 1) {
                    local = addr.substring(start, i2);
                    domain = addr.substring(i2 + 1);
                } else {
                    throw new AddressException("Missing domain", addr);
                }
            } else if (!validate) {
                local = addr;
                domain = null;
            } else {
                throw new AddressException("Missing final '@domain'", addr);
            }
            if (indexOfAny(addr, " \t\n\r") >= 0) {
                throw new AddressException("Illegal whitespace in address", addr);
            } else if (indexOfAny(local, specialsNoDot) >= 0) {
                throw new AddressException("Illegal character in local name", addr);
            } else if (domain != null && domain.indexOf(91) < 0 && indexOfAny(domain, specialsNoDot) >= 0) {
                throw new AddressException("Illegal character in domain", addr);
            }
        }
    }

    private boolean isSimple() {
        String str = this.address;
        return str == null || indexOfAny(str, specialsNoDotNoAt) < 0;
    }

    public boolean isGroup() {
        String str = this.address;
        return str != null && str.endsWith(";") && this.address.indexOf(58) > 0;
    }

    public InternetAddress[] getGroup(boolean strict) throws AddressException {
        int ix;
        String addr = getAddress();
        if (addr.endsWith(";") && (ix = addr.indexOf(58)) >= 0) {
            return parseHeader(addr.substring(ix + 1, addr.length() - 1), strict);
        }
        return null;
    }

    private static int indexOfAny(String s, String any) {
        return indexOfAny(s, any, 0);
    }

    private static int indexOfAny(String s, String any, int start) {
        try {
            int len = s.length();
            for (int i = start; i < len; i++) {
                if (any.indexOf(s.charAt(i)) >= 0) {
                    return i;
                }
            }
            return -1;
        } catch (StringIndexOutOfBoundsException e) {
            return -1;
        }
    }
}
