package com.mysql.jdbc;

public class EscapeTokenizer {
    private static final char CHR_BEGIN_TOKEN = '{';
    private static final char CHR_COMMENT = '-';
    private static final char CHR_CR = '\r';
    private static final char CHR_DBL_QUOTE = '\"';
    private static final char CHR_END_TOKEN = '}';
    private static final char CHR_ESCAPE = '\\';
    private static final char CHR_LF = '\n';
    private static final char CHR_SGL_QUOTE = '\'';
    private static final char CHR_VARIABLE = '@';
    private int bracesLevel = 0;
    private boolean emittingEscapeCode = false;
    private boolean inQuotes = false;
    private int pos = 0;
    private char quoteChar = 0;
    private boolean sawVariableUse = false;
    private String source = null;
    private int sourceLength = 0;

    public EscapeTokenizer(String source2) {
        this.source = source2;
        this.sourceLength = source2.length();
        this.pos = 0;
    }

    public synchronized boolean hasMoreTokens() {
        return this.pos < this.sourceLength;
    }

    public synchronized String nextToken() {
        int i;
        StringBuilder tokenBuf = new StringBuilder();
        boolean backslashEscape = false;
        if (this.emittingEscapeCode) {
            tokenBuf.append("{");
            this.emittingEscapeCode = false;
        }
        while (true) {
            int i2 = this.pos;
            if (i2 < this.sourceLength) {
                char c = this.source.charAt(i2);
                if (c == '\\') {
                    tokenBuf.append(c);
                    backslashEscape = !backslashEscape;
                } else if ((c == '\'' || c == '\"') && !backslashEscape) {
                    tokenBuf.append(c);
                    if (!this.inQuotes) {
                        this.inQuotes = true;
                        this.quoteChar = c;
                    } else if (c == this.quoteChar) {
                        int i3 = this.pos;
                        if (i3 + 1 >= this.sourceLength || this.source.charAt(i3 + 1) != this.quoteChar) {
                            this.inQuotes = false;
                        } else {
                            tokenBuf.append(c);
                            this.pos++;
                        }
                    }
                } else {
                    if (c != 10) {
                        if (c != 13) {
                            if (!this.inQuotes && !backslashEscape) {
                                if (c == '-') {
                                    tokenBuf.append(c);
                                    int i4 = this.pos;
                                    if (i4 + 1 < this.sourceLength && this.source.charAt(i4 + 1) == '-') {
                                        while (true) {
                                            i = this.pos + 1;
                                            this.pos = i;
                                            if (i >= this.sourceLength || c == 10 || c == 13) {
                                                this.pos = i - 1;
                                            } else {
                                                c = this.source.charAt(i);
                                                tokenBuf.append(c);
                                            }
                                        }
                                        this.pos = i - 1;
                                    }
                                } else if (c == '{') {
                                    int i5 = this.bracesLevel + 1;
                                    this.bracesLevel = i5;
                                    if (i5 == 1) {
                                        this.emittingEscapeCode = true;
                                        this.pos++;
                                        return tokenBuf.toString();
                                    }
                                    tokenBuf.append(c);
                                } else if (c == '}') {
                                    tokenBuf.append(c);
                                    int i6 = this.bracesLevel - 1;
                                    this.bracesLevel = i6;
                                    if (i6 == 0) {
                                        this.pos++;
                                        return tokenBuf.toString();
                                    }
                                } else if (c == '@') {
                                    this.sawVariableUse = true;
                                }
                            }
                            tokenBuf.append(c);
                            backslashEscape = false;
                        }
                    }
                    tokenBuf.append(c);
                    backslashEscape = false;
                }
                this.pos++;
            } else {
                return tokenBuf.toString();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean sawVariableUse() {
        return this.sawVariableUse;
    }
}
