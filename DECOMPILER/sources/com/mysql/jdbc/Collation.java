package com.mysql.jdbc;

/* compiled from: CharsetMapping */
class Collation {
    public final String collationName;
    public final int index;
    public final MysqlCharset mysqlCharset;
    public final int priority;

    public Collation(int index2, String collationName2, int priority2, String charsetName) {
        this.index = index2;
        this.collationName = collationName2;
        this.priority = priority2;
        this.mysqlCharset = CharsetMapping.CHARSET_NAME_TO_CHARSET.get(charsetName);
    }

    public String toString() {
        return "[" + "index=" + this.index + ",collationName=" + this.collationName + ",charsetName=" + this.mysqlCharset.charsetName + ",javaCharsetName=" + this.mysqlCharset.getMatchingJavaEncoding((String) null) + "]";
    }
}
