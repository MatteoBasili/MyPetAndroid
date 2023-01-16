package com.mysql.jdbc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.SQLException;
import java.sql.SQLXML;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class JDBC4MysqlSQLXML implements SQLXML {
    private ByteArrayOutputStream asByteArrayOutputStream;
    private DOMResult asDOMResult;
    private SAXResult asSAXResult;
    private StringWriter asStringWriter;
    private int columnIndexOfXml;
    private ExceptionInterceptor exceptionInterceptor;
    private boolean fromResultSet;
    private XMLInputFactory inputFactory;
    private boolean isClosed;
    private XMLOutputFactory outputFactory;
    private ResultSetInternalMethods owningResultSet;
    private SimpleSaxToReader saxToReaderConverter;
    private String stringRep;
    private boolean workingWithResult;

    protected JDBC4MysqlSQLXML(ResultSetInternalMethods owner, int index, ExceptionInterceptor exceptionInterceptor2) {
        this.isClosed = false;
        this.owningResultSet = owner;
        this.columnIndexOfXml = index;
        this.fromResultSet = true;
        this.exceptionInterceptor = exceptionInterceptor2;
    }

    protected JDBC4MysqlSQLXML(ExceptionInterceptor exceptionInterceptor2) {
        this.isClosed = false;
        this.fromResultSet = false;
        this.exceptionInterceptor = exceptionInterceptor2;
    }

    public synchronized void free() throws SQLException {
        this.stringRep = null;
        this.asDOMResult = null;
        this.asSAXResult = null;
        this.inputFactory = null;
        this.outputFactory = null;
        this.owningResultSet = null;
        this.workingWithResult = false;
        this.isClosed = true;
    }

    public synchronized String getString() throws SQLException {
        checkClosed();
        checkWorkingWithResult();
        if (this.fromResultSet) {
            return this.owningResultSet.getString(this.columnIndexOfXml);
        }
        return this.stringRep;
    }

    private synchronized void checkClosed() throws SQLException {
        if (this.isClosed) {
            throw SQLError.createSQLException("SQLXMLInstance has been free()d", this.exceptionInterceptor);
        }
    }

    private synchronized void checkWorkingWithResult() throws SQLException {
        if (this.workingWithResult) {
            throw SQLError.createSQLException("Can't perform requested operation after getResult() has been called to write XML data", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        }
    }

    public synchronized void setString(String str) throws SQLException {
        checkClosed();
        checkWorkingWithResult();
        this.stringRep = str;
        this.fromResultSet = false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0018, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean isEmpty() throws java.sql.SQLException {
        /*
            r2 = this;
            monitor-enter(r2)
            r2.checkClosed()     // Catch:{ all -> 0x001b }
            r2.checkWorkingWithResult()     // Catch:{ all -> 0x001b }
            boolean r0 = r2.fromResultSet     // Catch:{ all -> 0x001b }
            r1 = 0
            if (r0 != 0) goto L_0x0019
            java.lang.String r0 = r2.stringRep     // Catch:{ all -> 0x001b }
            if (r0 == 0) goto L_0x0016
            int r0 = r0.length()     // Catch:{ all -> 0x001b }
            if (r0 != 0) goto L_0x0017
        L_0x0016:
            r1 = 1
        L_0x0017:
            monitor-exit(r2)
            return r1
        L_0x0019:
            monitor-exit(r2)
            return r1
        L_0x001b:
            r0 = move-exception
            monitor-exit(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.JDBC4MysqlSQLXML.isEmpty():boolean");
    }

    public synchronized InputStream getBinaryStream() throws SQLException {
        checkClosed();
        checkWorkingWithResult();
        return this.owningResultSet.getBinaryStream(this.columnIndexOfXml);
    }

    public synchronized Reader getCharacterStream() throws SQLException {
        checkClosed();
        checkWorkingWithResult();
        return this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0053, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        r1 = com.mysql.jdbc.SQLError.createSQLException(r0.getMessage(), com.mysql.jdbc.SQLError.SQL_STATE_ILLEGAL_ARGUMENT, r6.exceptionInterceptor);
        r1.initCause(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0063, code lost:
        throw r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00b6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        r1 = com.mysql.jdbc.SQLError.createSQLException(r0.getMessage(), com.mysql.jdbc.SQLError.SQL_STATE_ILLEGAL_ARGUMENT, r6.exceptionInterceptor);
        r1.initCause(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00c6, code lost:
        throw r1;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:9:0x001b, B:35:0x0093] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized <T extends javax.xml.transform.Source> T getSource(java.lang.Class<T> r7) throws java.sql.SQLException {
        /*
            r6 = this;
            monitor-enter(r6)
            r6.checkClosed()     // Catch:{ all -> 0x0115 }
            r6.checkWorkingWithResult()     // Catch:{ all -> 0x0115 }
            if (r7 == 0) goto L_0x00ed
            java.lang.Class<javax.xml.transform.sax.SAXSource> r0 = javax.xml.transform.sax.SAXSource.class
            boolean r0 = r7.equals(r0)     // Catch:{ all -> 0x0115 }
            if (r0 == 0) goto L_0x0013
            goto L_0x00ed
        L_0x0013:
            java.lang.Class<javax.xml.transform.dom.DOMSource> r0 = javax.xml.transform.dom.DOMSource.class
            boolean r0 = r7.equals(r0)     // Catch:{ all -> 0x0115 }
            if (r0 == 0) goto L_0x0064
            javax.xml.parsers.DocumentBuilderFactory r0 = javax.xml.parsers.DocumentBuilderFactory.newInstance()     // Catch:{ all -> 0x0053 }
            r1 = 1
            r0.setNamespaceAware(r1)     // Catch:{ all -> 0x0053 }
            javax.xml.parsers.DocumentBuilder r1 = r0.newDocumentBuilder()     // Catch:{ all -> 0x0053 }
            r2 = 0
            boolean r3 = r6.fromResultSet     // Catch:{ all -> 0x0053 }
            if (r3 == 0) goto L_0x003b
            org.xml.sax.InputSource r3 = new org.xml.sax.InputSource     // Catch:{ all -> 0x0053 }
            com.mysql.jdbc.ResultSetInternalMethods r4 = r6.owningResultSet     // Catch:{ all -> 0x0053 }
            int r5 = r6.columnIndexOfXml     // Catch:{ all -> 0x0053 }
            java.io.Reader r4 = r4.getCharacterStream(r5)     // Catch:{ all -> 0x0053 }
            r3.<init>(r4)     // Catch:{ all -> 0x0053 }
            r2 = r3
            goto L_0x0048
        L_0x003b:
            org.xml.sax.InputSource r3 = new org.xml.sax.InputSource     // Catch:{ all -> 0x0053 }
            java.io.StringReader r4 = new java.io.StringReader     // Catch:{ all -> 0x0053 }
            java.lang.String r5 = r6.stringRep     // Catch:{ all -> 0x0053 }
            r4.<init>(r5)     // Catch:{ all -> 0x0053 }
            r3.<init>(r4)     // Catch:{ all -> 0x0053 }
            r2 = r3
        L_0x0048:
            javax.xml.transform.dom.DOMSource r3 = new javax.xml.transform.dom.DOMSource     // Catch:{ all -> 0x0053 }
            org.w3c.dom.Document r4 = r1.parse(r2)     // Catch:{ all -> 0x0053 }
            r3.<init>(r4)     // Catch:{ all -> 0x0053 }
            monitor-exit(r6)
            return r3
        L_0x0053:
            r0 = move-exception
            java.lang.String r1 = r0.getMessage()     // Catch:{ all -> 0x0115 }
            java.lang.String r2 = "S1009"
            com.mysql.jdbc.ExceptionInterceptor r3 = r6.exceptionInterceptor     // Catch:{ all -> 0x0115 }
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r3)     // Catch:{ all -> 0x0115 }
            r1.initCause(r0)     // Catch:{ all -> 0x0115 }
            throw r1     // Catch:{ all -> 0x0115 }
        L_0x0064:
            java.lang.Class<javax.xml.transform.stream.StreamSource> r0 = javax.xml.transform.stream.StreamSource.class
            boolean r0 = r7.equals(r0)     // Catch:{ all -> 0x0115 }
            if (r0 == 0) goto L_0x008a
            r0 = 0
            boolean r1 = r6.fromResultSet     // Catch:{ all -> 0x0115 }
            if (r1 == 0) goto L_0x007b
            com.mysql.jdbc.ResultSetInternalMethods r1 = r6.owningResultSet     // Catch:{ all -> 0x0115 }
            int r2 = r6.columnIndexOfXml     // Catch:{ all -> 0x0115 }
            java.io.Reader r1 = r1.getCharacterStream(r2)     // Catch:{ all -> 0x0115 }
            r0 = r1
            goto L_0x0083
        L_0x007b:
            java.io.StringReader r1 = new java.io.StringReader     // Catch:{ all -> 0x0115 }
            java.lang.String r2 = r6.stringRep     // Catch:{ all -> 0x0115 }
            r1.<init>(r2)     // Catch:{ all -> 0x0115 }
            r0 = r1
        L_0x0083:
            javax.xml.transform.stream.StreamSource r1 = new javax.xml.transform.stream.StreamSource     // Catch:{ all -> 0x0115 }
            r1.<init>(r0)     // Catch:{ all -> 0x0115 }
            monitor-exit(r6)
            return r1
        L_0x008a:
            java.lang.Class<javax.xml.transform.stax.StAXSource> r0 = javax.xml.transform.stax.StAXSource.class
            boolean r0 = r7.equals(r0)     // Catch:{ all -> 0x0115 }
            if (r0 == 0) goto L_0x00c7
            r0 = 0
            boolean r1 = r6.fromResultSet     // Catch:{ XMLStreamException -> 0x00b6 }
            if (r1 == 0) goto L_0x00a1
            com.mysql.jdbc.ResultSetInternalMethods r1 = r6.owningResultSet     // Catch:{ XMLStreamException -> 0x00b6 }
            int r2 = r6.columnIndexOfXml     // Catch:{ XMLStreamException -> 0x00b6 }
            java.io.Reader r1 = r1.getCharacterStream(r2)     // Catch:{ XMLStreamException -> 0x00b6 }
            r0 = r1
            goto L_0x00a9
        L_0x00a1:
            java.io.StringReader r1 = new java.io.StringReader     // Catch:{ XMLStreamException -> 0x00b6 }
            java.lang.String r2 = r6.stringRep     // Catch:{ XMLStreamException -> 0x00b6 }
            r1.<init>(r2)     // Catch:{ XMLStreamException -> 0x00b6 }
            r0 = r1
        L_0x00a9:
            javax.xml.transform.stax.StAXSource r1 = new javax.xml.transform.stax.StAXSource     // Catch:{ XMLStreamException -> 0x00b6 }
            javax.xml.stream.XMLInputFactory r2 = r6.inputFactory     // Catch:{ XMLStreamException -> 0x00b6 }
            javax.xml.stream.XMLStreamReader r2 = r2.createXMLStreamReader(r0)     // Catch:{ XMLStreamException -> 0x00b6 }
            r1.<init>(r2)     // Catch:{ XMLStreamException -> 0x00b6 }
            monitor-exit(r6)
            return r1
        L_0x00b6:
            r0 = move-exception
            java.lang.String r1 = r0.getMessage()     // Catch:{ all -> 0x0115 }
            java.lang.String r2 = "S1009"
            com.mysql.jdbc.ExceptionInterceptor r3 = r6.exceptionInterceptor     // Catch:{ all -> 0x0115 }
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r3)     // Catch:{ all -> 0x0115 }
            r1.initCause(r0)     // Catch:{ all -> 0x0115 }
            throw r1     // Catch:{ all -> 0x0115 }
        L_0x00c7:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0115 }
            r0.<init>()     // Catch:{ all -> 0x0115 }
            java.lang.String r1 = "XML Source of type \""
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ all -> 0x0115 }
            java.lang.String r1 = r7.toString()     // Catch:{ all -> 0x0115 }
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ all -> 0x0115 }
            java.lang.String r1 = "\" Not supported."
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ all -> 0x0115 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0115 }
            java.lang.String r1 = "S1009"
            com.mysql.jdbc.ExceptionInterceptor r2 = r6.exceptionInterceptor     // Catch:{ all -> 0x0115 }
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r0, (java.lang.String) r1, (com.mysql.jdbc.ExceptionInterceptor) r2)     // Catch:{ all -> 0x0115 }
            throw r0     // Catch:{ all -> 0x0115 }
        L_0x00ed:
            r0 = 0
            boolean r1 = r6.fromResultSet     // Catch:{ all -> 0x0115 }
            if (r1 == 0) goto L_0x0101
            org.xml.sax.InputSource r1 = new org.xml.sax.InputSource     // Catch:{ all -> 0x0115 }
            com.mysql.jdbc.ResultSetInternalMethods r2 = r6.owningResultSet     // Catch:{ all -> 0x0115 }
            int r3 = r6.columnIndexOfXml     // Catch:{ all -> 0x0115 }
            java.io.Reader r2 = r2.getCharacterStream(r3)     // Catch:{ all -> 0x0115 }
            r1.<init>(r2)     // Catch:{ all -> 0x0115 }
            r0 = r1
            goto L_0x010e
        L_0x0101:
            org.xml.sax.InputSource r1 = new org.xml.sax.InputSource     // Catch:{ all -> 0x0115 }
            java.io.StringReader r2 = new java.io.StringReader     // Catch:{ all -> 0x0115 }
            java.lang.String r3 = r6.stringRep     // Catch:{ all -> 0x0115 }
            r2.<init>(r3)     // Catch:{ all -> 0x0115 }
            r1.<init>(r2)     // Catch:{ all -> 0x0115 }
            r0 = r1
        L_0x010e:
            javax.xml.transform.sax.SAXSource r1 = new javax.xml.transform.sax.SAXSource     // Catch:{ all -> 0x0115 }
            r1.<init>(r0)     // Catch:{ all -> 0x0115 }
            monitor-exit(r6)
            return r1
        L_0x0115:
            r7 = move-exception
            monitor-exit(r6)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.JDBC4MysqlSQLXML.getSource(java.lang.Class):javax.xml.transform.Source");
    }

    public synchronized OutputStream setBinaryStream() throws SQLException {
        checkClosed();
        checkWorkingWithResult();
        this.workingWithResult = true;
        return setBinaryStreamInternal();
    }

    private synchronized OutputStream setBinaryStreamInternal() throws SQLException {
        ByteArrayOutputStream byteArrayOutputStream;
        byteArrayOutputStream = new ByteArrayOutputStream();
        this.asByteArrayOutputStream = byteArrayOutputStream;
        return byteArrayOutputStream;
    }

    public synchronized Writer setCharacterStream() throws SQLException {
        checkClosed();
        checkWorkingWithResult();
        this.workingWithResult = true;
        return setCharacterStreamInternal();
    }

    private synchronized Writer setCharacterStreamInternal() throws SQLException {
        StringWriter stringWriter;
        stringWriter = new StringWriter();
        this.asStringWriter = stringWriter;
        return stringWriter;
    }

    public synchronized <T extends Result> T setResult(Class<T> clazz) throws SQLException {
        checkClosed();
        checkWorkingWithResult();
        this.workingWithResult = true;
        this.asDOMResult = null;
        this.asSAXResult = null;
        this.saxToReaderConverter = null;
        this.stringRep = null;
        this.asStringWriter = null;
        this.asByteArrayOutputStream = null;
        if (clazz != null) {
            if (!clazz.equals(SAXResult.class)) {
                if (clazz.equals(DOMResult.class)) {
                    T dOMResult = new DOMResult();
                    this.asDOMResult = dOMResult;
                    return dOMResult;
                } else if (clazz.equals(StreamResult.class)) {
                    return new StreamResult(setCharacterStreamInternal());
                } else if (clazz.equals(StAXResult.class)) {
                    try {
                        if (this.outputFactory == null) {
                            this.outputFactory = XMLOutputFactory.newInstance();
                        }
                        return new StAXResult(this.outputFactory.createXMLEventWriter(setCharacterStreamInternal()));
                    } catch (XMLStreamException ex) {
                        SQLException sqlEx = SQLError.createSQLException(ex.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                        sqlEx.initCause(ex);
                        throw sqlEx;
                    }
                } else {
                    throw SQLError.createSQLException("XML Result of type \"" + clazz.toString() + "\" Not supported.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                }
            }
        }
        this.saxToReaderConverter = new SimpleSaxToReader();
        T sAXResult = new SAXResult(this.saxToReaderConverter);
        this.asSAXResult = sAXResult;
        return sAXResult;
    }

    private Reader binaryInputStreamStreamToReader(ByteArrayOutputStream out) {
        String encoding = "UTF-8";
        try {
            XMLStreamReader reader = this.inputFactory.createXMLStreamReader(new ByteArrayInputStream(out.toByteArray()));
            while (true) {
                int next = reader.next();
                int eventType = next;
                if (next != 8) {
                    if (eventType == 7) {
                        String possibleEncoding = reader.getEncoding();
                        if (possibleEncoding != null) {
                            encoding = possibleEncoding;
                        }
                    }
                }
            }
        } catch (Throwable th) {
        }
        try {
            return new StringReader(new String(out.toByteArray(), encoding));
        } catch (UnsupportedEncodingException badEnc) {
            throw new RuntimeException(badEnc);
        }
    }

    /* access modifiers changed from: protected */
    public String readerToString(Reader reader) throws SQLException {
        StringBuilder buf = new StringBuilder();
        char[] charBuf = new char[512];
        while (true) {
            try {
                int read = reader.read(charBuf);
                int charsRead = read;
                if (read == -1) {
                    return buf.toString();
                }
                buf.append(charBuf, 0, charsRead);
            } catch (IOException ioEx) {
                SQLException sqlEx = SQLError.createSQLException(ioEx.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                sqlEx.initCause(ioEx);
                throw sqlEx;
            }
        }
    }

    /* access modifiers changed from: protected */
    public synchronized Reader serializeAsCharacterStream() throws SQLException {
        checkClosed();
        if (this.workingWithResult) {
            if (this.stringRep != null) {
                return new StringReader(this.stringRep);
            } else if (this.asDOMResult != null) {
                return new StringReader(domSourceToString());
            } else if (this.asStringWriter != null) {
                return new StringReader(this.asStringWriter.toString());
            } else if (this.asSAXResult != null) {
                return this.saxToReaderConverter.toReader();
            } else {
                ByteArrayOutputStream byteArrayOutputStream = this.asByteArrayOutputStream;
                if (byteArrayOutputStream != null) {
                    return binaryInputStreamStreamToReader(byteArrayOutputStream);
                }
            }
        }
        return this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
    }

    /* access modifiers changed from: protected */
    public String domSourceToString() throws SQLException {
        try {
            DOMSource source = new DOMSource(this.asDOMResult.getNode());
            Transformer identity = TransformerFactory.newInstance().newTransformer();
            StringWriter stringOut = new StringWriter();
            identity.transform(source, new StreamResult(stringOut));
            return stringOut.toString();
        } catch (Throwable t) {
            SQLException sqlEx = SQLError.createSQLException(t.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
            sqlEx.initCause(t);
            throw sqlEx;
        }
    }

    /* access modifiers changed from: protected */
    public synchronized String serializeAsString() throws SQLException {
        checkClosed();
        if (this.workingWithResult) {
            String str = this.stringRep;
            if (str != null) {
                return str;
            }
            if (this.asDOMResult != null) {
                return domSourceToString();
            }
            StringWriter stringWriter = this.asStringWriter;
            if (stringWriter != null) {
                return stringWriter.toString();
            } else if (this.asSAXResult != null) {
                return readerToString(this.saxToReaderConverter.toReader());
            } else {
                ByteArrayOutputStream byteArrayOutputStream = this.asByteArrayOutputStream;
                if (byteArrayOutputStream != null) {
                    return readerToString(binaryInputStreamStreamToReader(byteArrayOutputStream));
                }
            }
        }
        return this.owningResultSet.getString(this.columnIndexOfXml);
    }

    class SimpleSaxToReader extends DefaultHandler {
        StringBuilder buf = new StringBuilder();
        private boolean inCDATA = false;

        SimpleSaxToReader() {
        }

        public void startDocument() throws SAXException {
            this.buf.append("<?xml version='1.0' encoding='UTF-8'?>");
        }

        public void endDocument() throws SAXException {
        }

        public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
            this.buf.append("<");
            this.buf.append(qName);
            if (attrs != null) {
                for (int i = 0; i < attrs.getLength(); i++) {
                    this.buf.append(" ");
                    this.buf.append(attrs.getQName(i)).append("=\"");
                    escapeCharsForXml(attrs.getValue(i), true);
                    this.buf.append("\"");
                }
            }
            this.buf.append(">");
        }

        public void characters(char[] buf2, int offset, int len) throws SAXException {
            if (!this.inCDATA) {
                escapeCharsForXml(buf2, offset, len, false);
            } else {
                this.buf.append(buf2, offset, len);
            }
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            characters(ch, start, length);
        }

        public void startCDATA() throws SAXException {
            this.buf.append("<![CDATA[");
            this.inCDATA = true;
        }

        public void endCDATA() throws SAXException {
            this.inCDATA = false;
            this.buf.append("]]>");
        }

        public void comment(char[] ch, int start, int length) throws SAXException {
            this.buf.append("<!--");
            for (int i = 0; i < length; i++) {
                this.buf.append(ch[start + i]);
            }
            this.buf.append("-->");
        }

        /* access modifiers changed from: package-private */
        public Reader toReader() {
            return new StringReader(this.buf.toString());
        }

        private void escapeCharsForXml(String str, boolean isAttributeData) {
            if (str != null) {
                int strLen = str.length();
                for (int i = 0; i < strLen; i++) {
                    escapeCharsForXml(str.charAt(i), isAttributeData);
                }
            }
        }

        private void escapeCharsForXml(char[] buf2, int offset, int len, boolean isAttributeData) {
            if (buf2 != null) {
                for (int i = 0; i < len; i++) {
                    escapeCharsForXml(buf2[offset + i], isAttributeData);
                }
            }
        }

        private void escapeCharsForXml(char c, boolean isAttributeData) {
            switch (c) {
                case 13:
                    this.buf.append("&#xD;");
                    return;
                case '\"':
                    if (!isAttributeData) {
                        this.buf.append("\"");
                        return;
                    } else {
                        this.buf.append("&quot;");
                        return;
                    }
                case '&':
                    this.buf.append("&amp;");
                    return;
                case '<':
                    this.buf.append("&lt;");
                    return;
                case '>':
                    this.buf.append("&gt;");
                    return;
                default:
                    if ((c < 1 || c > 31 || c == 9 || c == 10) && ((c < 127 || c > 159) && c != 8232 && (!isAttributeData || !(c == 9 || c == 10)))) {
                        this.buf.append(c);
                        return;
                    }
                    this.buf.append("&#x");
                    this.buf.append(Integer.toHexString(c).toUpperCase());
                    this.buf.append(";");
                    return;
            }
        }
    }
}
