package com.mysql.jdbc;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* compiled from: CharsetMapping */
class MysqlCharset {
    public final String charsetName;
    public final List<String> javaEncodingsUc;
    public int major;
    public final int mblen;
    public int minor;
    public final int priority;
    public int subminor;

    public MysqlCharset(String charsetName2, int mblen2, int priority2, String[] javaEncodings) {
        this.javaEncodingsUc = new ArrayList();
        this.major = 4;
        this.minor = 1;
        this.subminor = 0;
        this.charsetName = charsetName2;
        this.mblen = mblen2;
        this.priority = priority2;
        for (String encoding : javaEncodings) {
            try {
                Charset cs = Charset.forName(encoding);
                addEncodingMapping(cs.name());
                for (String addEncodingMapping : cs.aliases()) {
                    addEncodingMapping(addEncodingMapping);
                }
            } catch (Exception e) {
                if (mblen2 == 1) {
                    addEncodingMapping(encoding);
                }
            }
        }
        if (this.javaEncodingsUc.size() != 0) {
            return;
        }
        if (mblen2 > 1) {
            addEncodingMapping("UTF-8");
        } else {
            addEncodingMapping("Cp1252");
        }
    }

    private void addEncodingMapping(String encoding) {
        String encodingUc = encoding.toUpperCase(Locale.ENGLISH);
        if (!this.javaEncodingsUc.contains(encodingUc)) {
            this.javaEncodingsUc.add(encodingUc);
        }
    }

    public MysqlCharset(String charsetName2, int mblen2, int priority2, String[] javaEncodings, int major2, int minor2) {
        this(charsetName2, mblen2, priority2, javaEncodings);
        this.major = major2;
        this.minor = minor2;
    }

    public MysqlCharset(String charsetName2, int mblen2, int priority2, String[] javaEncodings, int major2, int minor2, int subminor2) {
        this(charsetName2, mblen2, priority2, javaEncodings);
        this.major = major2;
        this.minor = minor2;
        this.subminor = subminor2;
    }

    public String toString() {
        return "[" + "charsetName=" + this.charsetName + ",mblen=" + this.mblen + "]";
    }

    /* access modifiers changed from: package-private */
    public boolean isOkayForVersion(Connection conn) throws SQLException {
        return conn.versionMeetsMinimum(this.major, this.minor, this.subminor);
    }

    /* access modifiers changed from: package-private */
    public String getMatchingJavaEncoding(String javaEncoding) {
        if (javaEncoding == null || !this.javaEncodingsUc.contains(javaEncoding.toUpperCase(Locale.ENGLISH))) {
            return this.javaEncodingsUc.get(0);
        }
        return javaEncoding;
    }
}
