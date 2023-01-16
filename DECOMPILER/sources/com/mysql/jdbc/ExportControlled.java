package com.mysql.jdbc;

import com.mysql.jdbc.SocketMetadata;
import com.mysql.jdbc.util.Base64Decoder;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.crypto.Cipher;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

public class ExportControlled {
    private static final String SQL_STATE_BAD_SSL_PARAMS = "08000";
    private static final String[] TLS_PROTOCOLS = {TLSv1_2, TLSv1_1, TLSv1};
    private static final String TLSv1 = "TLSv1";
    private static final String TLSv1_1 = "TLSv1.1";
    private static final String TLSv1_2 = "TLSv1.2";

    protected static boolean enabled() {
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x008a A[Catch:{ IOException -> 0x01ae }] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00bb A[Catch:{ IOException -> 0x01ae }] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00bd A[Catch:{ IOException -> 0x01ae }] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00c1 A[Catch:{ IOException -> 0x01ae }] */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00ed A[Catch:{ IOException -> 0x01ae }] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0161 A[Catch:{ IOException -> 0x01ae }] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0182 A[Catch:{ IOException -> 0x01ae }] */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x018b A[Catch:{ IOException -> 0x01ae }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static void transformSocketToSSLSocket(com.mysql.jdbc.MysqlIO r25) throws java.sql.SQLException {
        /*
            r1 = r25
            com.mysql.jdbc.ExportControlled$StandardSSLSocketFactory r0 = new com.mysql.jdbc.ExportControlled$StandardSSLSocketFactory
            javax.net.ssl.SSLSocketFactory r2 = getSSLSocketFactoryDefaultOrConfigured(r25)
            com.mysql.jdbc.SocketFactory r3 = r1.socketFactory
            java.net.Socket r4 = r1.mysqlConnection
            r0.<init>(r2, r3, r4)
            r2 = r0
            java.lang.String r0 = r1.host     // Catch:{ IOException -> 0x01ae }
            int r3 = r1.port     // Catch:{ IOException -> 0x01ae }
            r4 = 0
            java.net.Socket r0 = r2.connect(r0, r3, r4)     // Catch:{ IOException -> 0x01ae }
            r1.mysqlConnection = r0     // Catch:{ IOException -> 0x01ae }
            r0 = 0
            com.mysql.jdbc.MySQLConnection r3 = r1.connection     // Catch:{ IOException -> 0x01ae }
            java.lang.String r3 = r3.getEnabledTLSProtocols()     // Catch:{ IOException -> 0x01ae }
            java.lang.String r4 = "\\s*,\\s*"
            r5 = 7
            r6 = 6
            r7 = 0
            r8 = 5
            if (r3 == 0) goto L_0x0036
            int r9 = r3.length()     // Catch:{ IOException -> 0x01ae }
            if (r9 <= 0) goto L_0x0036
            java.lang.String[] r9 = r3.split(r4)     // Catch:{ IOException -> 0x01ae }
            r0 = r9
            goto L_0x006a
        L_0x0036:
            r9 = 28
            boolean r9 = r1.versionMeetsMinimum(r8, r5, r9)     // Catch:{ IOException -> 0x01ae }
            if (r9 != 0) goto L_0x0067
            r9 = 46
            boolean r9 = r1.versionMeetsMinimum(r8, r6, r9)     // Catch:{ IOException -> 0x01ae }
            if (r9 == 0) goto L_0x004c
            boolean r9 = r1.versionMeetsMinimum(r8, r5, r7)     // Catch:{ IOException -> 0x01ae }
            if (r9 == 0) goto L_0x0067
        L_0x004c:
            boolean r9 = r1.versionMeetsMinimum(r8, r6, r7)     // Catch:{ IOException -> 0x01ae }
            if (r9 == 0) goto L_0x005d
            java.lang.String r9 = r25.getServerVersion()     // Catch:{ IOException -> 0x01ae }
            boolean r9 = com.mysql.jdbc.Util.isEnterpriseEdition(r9)     // Catch:{ IOException -> 0x01ae }
            if (r9 == 0) goto L_0x005d
            goto L_0x0067
        L_0x005d:
            java.lang.String r9 = "TLSv1.1"
            java.lang.String r10 = "TLSv1"
            java.lang.String[] r9 = new java.lang.String[]{r9, r10}     // Catch:{ IOException -> 0x01ae }
            r0 = r9
            goto L_0x006a
        L_0x0067:
            java.lang.String[] r9 = TLS_PROTOCOLS     // Catch:{ IOException -> 0x01ae }
            r0 = r9
        L_0x006a:
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ IOException -> 0x01ae }
            java.util.List r10 = java.util.Arrays.asList(r0)     // Catch:{ IOException -> 0x01ae }
            r9.<init>(r10)     // Catch:{ IOException -> 0x01ae }
            java.net.Socket r10 = r1.mysqlConnection     // Catch:{ IOException -> 0x01ae }
            javax.net.ssl.SSLSocket r10 = (javax.net.ssl.SSLSocket) r10     // Catch:{ IOException -> 0x01ae }
            java.lang.String[] r10 = r10.getSupportedProtocols()     // Catch:{ IOException -> 0x01ae }
            java.util.List r10 = java.util.Arrays.asList(r10)     // Catch:{ IOException -> 0x01ae }
            java.util.ArrayList r11 = new java.util.ArrayList     // Catch:{ IOException -> 0x01ae }
            r11.<init>()     // Catch:{ IOException -> 0x01ae }
            java.lang.String[] r12 = TLS_PROTOCOLS     // Catch:{ IOException -> 0x01ae }
            int r13 = r12.length     // Catch:{ IOException -> 0x01ae }
            r14 = 0
        L_0x0088:
            if (r14 >= r13) goto L_0x009e
            r15 = r12[r14]     // Catch:{ IOException -> 0x01ae }
            boolean r16 = r10.contains(r15)     // Catch:{ IOException -> 0x01ae }
            if (r16 == 0) goto L_0x009b
            boolean r16 = r9.contains(r15)     // Catch:{ IOException -> 0x01ae }
            if (r16 == 0) goto L_0x009b
            r11.add(r15)     // Catch:{ IOException -> 0x01ae }
        L_0x009b:
            int r14 = r14 + 1
            goto L_0x0088
        L_0x009e:
            java.net.Socket r12 = r1.mysqlConnection     // Catch:{ IOException -> 0x01ae }
            javax.net.ssl.SSLSocket r12 = (javax.net.ssl.SSLSocket) r12     // Catch:{ IOException -> 0x01ae }
            java.lang.String[] r13 = new java.lang.String[r7]     // Catch:{ IOException -> 0x01ae }
            java.lang.Object[] r13 = r11.toArray(r13)     // Catch:{ IOException -> 0x01ae }
            java.lang.String[] r13 = (java.lang.String[]) r13     // Catch:{ IOException -> 0x01ae }
            r12.setEnabledProtocols(r13)     // Catch:{ IOException -> 0x01ae }
            com.mysql.jdbc.MySQLConnection r12 = r1.connection     // Catch:{ IOException -> 0x01ae }
            java.lang.String r12 = r12.getEnabledSSLCipherSuites()     // Catch:{ IOException -> 0x01ae }
            if (r12 == 0) goto L_0x00bd
            int r13 = r12.length()     // Catch:{ IOException -> 0x01ae }
            if (r13 <= 0) goto L_0x00bd
            r13 = 1
            goto L_0x00be
        L_0x00bd:
            r13 = r7
        L_0x00be:
            r14 = 0
            if (r13 == 0) goto L_0x00ed
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ IOException -> 0x01ae }
            r5.<init>()     // Catch:{ IOException -> 0x01ae }
            r14 = r5
            java.net.Socket r5 = r1.mysqlConnection     // Catch:{ IOException -> 0x01ae }
            javax.net.ssl.SSLSocket r5 = (javax.net.ssl.SSLSocket) r5     // Catch:{ IOException -> 0x01ae }
            java.lang.String[] r5 = r5.getEnabledCipherSuites()     // Catch:{ IOException -> 0x01ae }
            java.util.List r5 = java.util.Arrays.asList(r5)     // Catch:{ IOException -> 0x01ae }
            java.lang.String[] r4 = r12.split(r4)     // Catch:{ IOException -> 0x01ae }
            int r6 = r4.length     // Catch:{ IOException -> 0x01ae }
            r8 = 0
        L_0x00d9:
            if (r8 >= r6) goto L_0x00e9
            r15 = r4[r8]     // Catch:{ IOException -> 0x01ae }
            boolean r16 = r5.contains(r15)     // Catch:{ IOException -> 0x01ae }
            if (r16 == 0) goto L_0x00e6
            r14.add(r15)     // Catch:{ IOException -> 0x01ae }
        L_0x00e6:
            int r8 = r8 + 1
            goto L_0x00d9
        L_0x00e9:
            r17 = r0
            goto L_0x015f
        L_0x00ed:
            r4 = 0
            r15 = 45
            boolean r15 = r1.versionMeetsMinimum(r8, r8, r15)     // Catch:{ IOException -> 0x01ae }
            r5 = 8
            if (r15 == 0) goto L_0x00fe
            boolean r15 = r1.versionMeetsMinimum(r8, r6, r7)     // Catch:{ IOException -> 0x01ae }
            if (r15 == 0) goto L_0x0114
        L_0x00fe:
            r15 = 26
            boolean r15 = r1.versionMeetsMinimum(r8, r6, r15)     // Catch:{ IOException -> 0x01ae }
            if (r15 == 0) goto L_0x010d
            r15 = 7
            boolean r17 = r1.versionMeetsMinimum(r8, r15, r7)     // Catch:{ IOException -> 0x01ae }
            if (r17 == 0) goto L_0x0114
        L_0x010d:
            r15 = 7
            boolean r6 = r1.versionMeetsMinimum(r8, r15, r6)     // Catch:{ IOException -> 0x01ae }
            if (r6 == 0) goto L_0x011c
        L_0x0114:
            int r6 = com.mysql.jdbc.Util.getJVMVersion()     // Catch:{ IOException -> 0x01ae }
            if (r6 >= r5) goto L_0x0123
            r4 = 1
            goto L_0x0123
        L_0x011c:
            int r6 = com.mysql.jdbc.Util.getJVMVersion()     // Catch:{ IOException -> 0x01ae }
            if (r6 < r5) goto L_0x0123
            r4 = 1
        L_0x0123:
            if (r4 == 0) goto L_0x015d
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ IOException -> 0x01ae }
            r5.<init>()     // Catch:{ IOException -> 0x01ae }
            r14 = r5
            java.net.Socket r5 = r1.mysqlConnection     // Catch:{ IOException -> 0x01ae }
            javax.net.ssl.SSLSocket r5 = (javax.net.ssl.SSLSocket) r5     // Catch:{ IOException -> 0x01ae }
            java.lang.String[] r5 = r5.getEnabledCipherSuites()     // Catch:{ IOException -> 0x01ae }
            int r6 = r5.length     // Catch:{ IOException -> 0x01ae }
            r8 = 0
        L_0x0135:
            if (r8 >= r6) goto L_0x015a
            r15 = r5[r8]     // Catch:{ IOException -> 0x01ae }
            if (r4 == 0) goto L_0x014f
            java.lang.String r7 = "_DHE_"
            int r7 = r15.indexOf(r7)     // Catch:{ IOException -> 0x01ae }
            r17 = r0
            r0 = -1
            if (r7 > r0) goto L_0x0154
            java.lang.String r7 = "_DH_"
            int r7 = r15.indexOf(r7)     // Catch:{ IOException -> 0x01ae }
            if (r7 > r0) goto L_0x0154
            goto L_0x0151
        L_0x014f:
            r17 = r0
        L_0x0151:
            r14.add(r15)     // Catch:{ IOException -> 0x01ae }
        L_0x0154:
            int r8 = r8 + 1
            r0 = r17
            r7 = 0
            goto L_0x0135
        L_0x015a:
            r17 = r0
            goto L_0x015f
        L_0x015d:
            r17 = r0
        L_0x015f:
            if (r14 == 0) goto L_0x0171
            java.net.Socket r0 = r1.mysqlConnection     // Catch:{ IOException -> 0x01ae }
            javax.net.ssl.SSLSocket r0 = (javax.net.ssl.SSLSocket) r0     // Catch:{ IOException -> 0x01ae }
            r4 = 0
            java.lang.String[] r4 = new java.lang.String[r4]     // Catch:{ IOException -> 0x01ae }
            java.lang.Object[] r4 = r14.toArray(r4)     // Catch:{ IOException -> 0x01ae }
            java.lang.String[] r4 = (java.lang.String[]) r4     // Catch:{ IOException -> 0x01ae }
            r0.setEnabledCipherSuites(r4)     // Catch:{ IOException -> 0x01ae }
        L_0x0171:
            java.net.Socket r0 = r1.mysqlConnection     // Catch:{ IOException -> 0x01ae }
            javax.net.ssl.SSLSocket r0 = (javax.net.ssl.SSLSocket) r0     // Catch:{ IOException -> 0x01ae }
            r0.startHandshake()     // Catch:{ IOException -> 0x01ae }
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ IOException -> 0x01ae }
            boolean r0 = r0.getUseUnbufferedInput()     // Catch:{ IOException -> 0x01ae }
            r4 = 16384(0x4000, float:2.2959E-41)
            if (r0 == 0) goto L_0x018b
            java.net.Socket r0 = r1.mysqlConnection     // Catch:{ IOException -> 0x01ae }
            java.io.InputStream r0 = r0.getInputStream()     // Catch:{ IOException -> 0x01ae }
            r1.mysqlInput = r0     // Catch:{ IOException -> 0x01ae }
            goto L_0x0198
        L_0x018b:
            java.io.BufferedInputStream r0 = new java.io.BufferedInputStream     // Catch:{ IOException -> 0x01ae }
            java.net.Socket r5 = r1.mysqlConnection     // Catch:{ IOException -> 0x01ae }
            java.io.InputStream r5 = r5.getInputStream()     // Catch:{ IOException -> 0x01ae }
            r0.<init>(r5, r4)     // Catch:{ IOException -> 0x01ae }
            r1.mysqlInput = r0     // Catch:{ IOException -> 0x01ae }
        L_0x0198:
            java.io.BufferedOutputStream r0 = new java.io.BufferedOutputStream     // Catch:{ IOException -> 0x01ae }
            java.net.Socket r5 = r1.mysqlConnection     // Catch:{ IOException -> 0x01ae }
            java.io.OutputStream r5 = r5.getOutputStream()     // Catch:{ IOException -> 0x01ae }
            r0.<init>(r5, r4)     // Catch:{ IOException -> 0x01ae }
            r1.mysqlOutput = r0     // Catch:{ IOException -> 0x01ae }
            java.io.BufferedOutputStream r0 = r1.mysqlOutput     // Catch:{ IOException -> 0x01ae }
            r0.flush()     // Catch:{ IOException -> 0x01ae }
            r1.socketFactory = r2     // Catch:{ IOException -> 0x01ae }
            return
        L_0x01ae:
            r0 = move-exception
            r23 = r0
            com.mysql.jdbc.MySQLConnection r0 = r1.connection
            long r19 = r25.getLastPacketSentTimeMs()
            long r21 = r25.getLastPacketReceivedTimeMs()
            com.mysql.jdbc.ExceptionInterceptor r24 = r25.getExceptionInterceptor()
            r18 = r0
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createCommunicationsException(r18, r19, r21, r23, r24)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ExportControlled.transformSocketToSSLSocket(com.mysql.jdbc.MysqlIO):void");
    }

    public static class StandardSSLSocketFactory implements SocketFactory, SocketMetadata {
        private final Socket existingSocket;
        private final SocketFactory existingSocketFactory;
        private SSLSocket rawSocket = null;
        private final SSLSocketFactory sslFact;

        public StandardSSLSocketFactory(SSLSocketFactory sslFact2, SocketFactory existingSocketFactory2, Socket existingSocket2) {
            this.sslFact = sslFact2;
            this.existingSocketFactory = existingSocketFactory2;
            this.existingSocket = existingSocket2;
        }

        public Socket afterHandshake() throws SocketException, IOException {
            this.existingSocketFactory.afterHandshake();
            return this.rawSocket;
        }

        public Socket beforeHandshake() throws SocketException, IOException {
            return this.rawSocket;
        }

        public Socket connect(String host, int portNumber, Properties props) throws SocketException, IOException {
            SSLSocket sSLSocket = (SSLSocket) this.sslFact.createSocket(this.existingSocket, host, portNumber, true);
            this.rawSocket = sSLSocket;
            return sSLSocket;
        }

        public boolean isLocallyConnected(ConnectionImpl conn) throws SQLException {
            return SocketMetadata.Helper.isLocallyConnected(conn);
        }
    }

    private ExportControlled() {
    }

    public static class X509TrustManagerWrapper implements X509TrustManager {
        private CertificateFactory certFactory = null;
        private X509TrustManager origTm = null;
        private CertPathValidator validator = null;
        private PKIXParameters validatorParams = null;
        private boolean verifyServerCert = false;

        public X509TrustManagerWrapper(X509TrustManager tm, boolean verifyServerCertificate) throws CertificateException {
            this.origTm = tm;
            this.verifyServerCert = verifyServerCertificate;
            if (verifyServerCertificate) {
                try {
                    Set<TrustAnchor> anch = new HashSet<>();
                    for (X509Certificate cert : tm.getAcceptedIssuers()) {
                        anch.add(new TrustAnchor(cert, (byte[]) null));
                    }
                    PKIXParameters pKIXParameters = new PKIXParameters(anch);
                    this.validatorParams = pKIXParameters;
                    pKIXParameters.setRevocationEnabled(false);
                    this.validator = CertPathValidator.getInstance("PKIX");
                    this.certFactory = CertificateFactory.getInstance("X.509");
                } catch (Exception e) {
                    throw new CertificateException(e);
                }
            }
        }

        public X509TrustManagerWrapper() {
        }

        public X509Certificate[] getAcceptedIssuers() {
            X509TrustManager x509TrustManager = this.origTm;
            return x509TrustManager != null ? x509TrustManager.getAcceptedIssuers() : new X509Certificate[0];
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            for (X509Certificate checkValidity : chain) {
                checkValidity.checkValidity();
            }
            if (this.validatorParams != null) {
                new X509CertSelector().setSerialNumber(chain[0].getSerialNumber());
                try {
                    ((PKIXCertPathValidatorResult) this.validator.validate(this.certFactory.generateCertPath(Arrays.asList(chain)), this.validatorParams)).getTrustAnchor().getTrustedCert().checkValidity();
                } catch (InvalidAlgorithmParameterException e) {
                    throw new CertificateException(e);
                } catch (CertPathValidatorException e2) {
                    throw new CertificateException(e2);
                }
            }
            if (this.verifyServerCert) {
                this.origTm.checkServerTrusted(chain, authType);
            }
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            this.origTm.checkClientTrusted(chain, authType);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:120:0x028f A[SYNTHETIC, Splitter:B:120:0x028f] */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x03ca A[SYNTHETIC, Splitter:B:177:0x03ca] */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x04b4 A[SYNTHETIC, Splitter:B:239:0x04b4] */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x04c1  */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x0722 A[SYNTHETIC, Splitter:B:322:0x0722] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static javax.net.ssl.SSLSocketFactory getSSLSocketFactoryDefaultOrConfigured(com.mysql.jdbc.MysqlIO r28) throws java.sql.SQLException {
        /*
            r1 = r28
            java.lang.String r2 = "08000"
            com.mysql.jdbc.MySQLConnection r0 = r1.connection
            java.lang.String r0 = r0.getClientCertificateKeyStoreUrl()
            com.mysql.jdbc.MySQLConnection r3 = r1.connection
            java.lang.String r3 = r3.getClientCertificateKeyStorePassword()
            com.mysql.jdbc.MySQLConnection r4 = r1.connection
            java.lang.String r4 = r4.getClientCertificateKeyStoreType()
            com.mysql.jdbc.MySQLConnection r5 = r1.connection
            java.lang.String r5 = r5.getTrustCertificateKeyStoreUrl()
            com.mysql.jdbc.MySQLConnection r6 = r1.connection
            java.lang.String r6 = r6.getTrustCertificateKeyStorePassword()
            com.mysql.jdbc.MySQLConnection r7 = r1.connection
            java.lang.String r7 = r7.getTrustCertificateKeyStoreType()
            boolean r8 = com.mysql.jdbc.StringUtils.isNullOrEmpty(r0)
            java.lang.String r9 = "file:"
            if (r8 == 0) goto L_0x006c
            java.lang.String r8 = "javax.net.ssl.keyStore"
            java.lang.String r8 = java.lang.System.getProperty(r8)
            java.lang.String r0 = "javax.net.ssl.keyStorePassword"
            java.lang.String r3 = java.lang.System.getProperty(r0)
            java.lang.String r0 = "javax.net.ssl.keyStoreType"
            java.lang.String r0 = java.lang.System.getProperty(r0)
            boolean r4 = com.mysql.jdbc.StringUtils.isNullOrEmpty(r0)
            if (r4 == 0) goto L_0x004c
            java.lang.String r0 = "JKS"
            r4 = r0
            goto L_0x004d
        L_0x004c:
            r4 = r0
        L_0x004d:
            boolean r0 = com.mysql.jdbc.StringUtils.isNullOrEmpty(r8)
            if (r0 != 0) goto L_0x006d
            java.net.URL r0 = new java.net.URL     // Catch:{ MalformedURLException -> 0x0059 }
            r0.<init>(r8)     // Catch:{ MalformedURLException -> 0x0059 }
            goto L_0x006d
        L_0x0059:
            r0 = move-exception
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.StringBuilder r10 = r10.append(r9)
            java.lang.StringBuilder r10 = r10.append(r8)
            java.lang.String r8 = r10.toString()
            goto L_0x006d
        L_0x006c:
            r8 = r0
        L_0x006d:
            boolean r0 = com.mysql.jdbc.StringUtils.isNullOrEmpty(r5)
            if (r0 == 0) goto L_0x00ae
            java.lang.String r0 = "javax.net.ssl.trustStore"
            java.lang.String r5 = java.lang.System.getProperty(r0)
            java.lang.String r0 = "javax.net.ssl.trustStorePassword"
            java.lang.String r6 = java.lang.System.getProperty(r0)
            java.lang.String r0 = "javax.net.ssl.trustStoreType"
            java.lang.String r0 = java.lang.System.getProperty(r0)
            boolean r7 = com.mysql.jdbc.StringUtils.isNullOrEmpty(r0)
            if (r7 == 0) goto L_0x008f
            java.lang.String r0 = "JKS"
            r7 = r0
            goto L_0x0090
        L_0x008f:
            r7 = r0
        L_0x0090:
            boolean r0 = com.mysql.jdbc.StringUtils.isNullOrEmpty(r5)
            if (r0 != 0) goto L_0x00ae
            java.net.URL r0 = new java.net.URL     // Catch:{ MalformedURLException -> 0x009c }
            r0.<init>(r5)     // Catch:{ MalformedURLException -> 0x009c }
            goto L_0x00ae
        L_0x009c:
            r0 = move-exception
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.StringBuilder r9 = r10.append(r9)
            java.lang.StringBuilder r9 = r9.append(r5)
            java.lang.String r5 = r9.toString()
        L_0x00ae:
            r9 = 0
            r10 = 0
            r11 = 0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r12 = r0
            java.lang.String r0 = javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm()     // Catch:{ NoSuchAlgorithmException -> 0x073b }
            javax.net.ssl.TrustManagerFactory r0 = javax.net.ssl.TrustManagerFactory.getInstance(r0)     // Catch:{ NoSuchAlgorithmException -> 0x073b }
            r9 = r0
            java.lang.String r0 = javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm()     // Catch:{ NoSuchAlgorithmException -> 0x072b }
            javax.net.ssl.KeyManagerFactory r0 = javax.net.ssl.KeyManagerFactory.getInstance(r0)     // Catch:{ NoSuchAlgorithmException -> 0x072b }
            r10 = r0
            boolean r0 = com.mysql.jdbc.StringUtils.isNullOrEmpty(r8)
            java.lang.String r14 = " ["
            java.lang.String r15 = " keystore from "
            java.lang.String r13 = " does not appear to be a valid URL."
            r16 = r11
            java.lang.String r11 = "Cannot open "
            r17 = r12
            java.lang.String r12 = "Unsupported keystore algorithm ["
            java.lang.String r1 = "Could not create KeyStore instance ["
            r18 = r9
            java.lang.String r9 = "]"
            if (r0 != 0) goto L_0x0298
            r19 = 0
            boolean r0 = com.mysql.jdbc.StringUtils.isNullOrEmpty(r4)     // Catch:{ UnrecoverableKeyException -> 0x0278, NoSuchAlgorithmException -> 0x024e, KeyStoreException -> 0x0224, CertificateException -> 0x01f9, MalformedURLException -> 0x01d7, IOException -> 0x01a1, all -> 0x0197 }
            if (r0 != 0) goto L_0x0184
            java.security.KeyStore r0 = java.security.KeyStore.getInstance(r4)     // Catch:{ UnrecoverableKeyException -> 0x0278, NoSuchAlgorithmException -> 0x024e, KeyStoreException -> 0x0224, CertificateException -> 0x01f9, MalformedURLException -> 0x01d7, IOException -> 0x01a1, all -> 0x0197 }
            r20 = r6
            java.net.URL r6 = new java.net.URL     // Catch:{ UnrecoverableKeyException -> 0x017d, NoSuchAlgorithmException -> 0x0176, KeyStoreException -> 0x016f, CertificateException -> 0x0168, MalformedURLException -> 0x0161, IOException -> 0x015b, all -> 0x0153 }
            r6.<init>(r8)     // Catch:{ UnrecoverableKeyException -> 0x017d, NoSuchAlgorithmException -> 0x0176, KeyStoreException -> 0x016f, CertificateException -> 0x0168, MalformedURLException -> 0x0161, IOException -> 0x015b, all -> 0x0153 }
            if (r3 != 0) goto L_0x0101
            r22 = r5
            r21 = r7
            r7 = 0
            char[] r5 = new char[r7]     // Catch:{ UnrecoverableKeyException -> 0x0150, NoSuchAlgorithmException -> 0x014d, KeyStoreException -> 0x014a, CertificateException -> 0x0147, MalformedURLException -> 0x0144, IOException -> 0x0141 }
            goto L_0x0109
        L_0x0101:
            r22 = r5
            r21 = r7
            char[] r5 = r3.toCharArray()     // Catch:{ UnrecoverableKeyException -> 0x0150, NoSuchAlgorithmException -> 0x014d, KeyStoreException -> 0x014a, CertificateException -> 0x0147, MalformedURLException -> 0x0144, IOException -> 0x0141 }
        L_0x0109:
            java.io.InputStream r7 = r6.openStream()     // Catch:{ UnrecoverableKeyException -> 0x0150, NoSuchAlgorithmException -> 0x014d, KeyStoreException -> 0x014a, CertificateException -> 0x0147, MalformedURLException -> 0x0144, IOException -> 0x0141 }
            r0.load(r7, r5)     // Catch:{ UnrecoverableKeyException -> 0x013c, NoSuchAlgorithmException -> 0x0137, KeyStoreException -> 0x0132, CertificateException -> 0x012d, MalformedURLException -> 0x0128, IOException -> 0x0123, all -> 0x011d }
            r10.init(r0, r5)     // Catch:{ UnrecoverableKeyException -> 0x013c, NoSuchAlgorithmException -> 0x0137, KeyStoreException -> 0x0132, CertificateException -> 0x012d, MalformedURLException -> 0x0128, IOException -> 0x0123, all -> 0x011d }
            javax.net.ssl.KeyManager[] r19 = r10.getKeyManagers()     // Catch:{ UnrecoverableKeyException -> 0x013c, NoSuchAlgorithmException -> 0x0137, KeyStoreException -> 0x0132, CertificateException -> 0x012d, MalformedURLException -> 0x0128, IOException -> 0x0123, all -> 0x011d }
            r16 = r19
            r19 = r7
            goto L_0x018a
        L_0x011d:
            r0 = move-exception
            r1 = r0
            r19 = r7
            goto L_0x028d
        L_0x0123:
            r0 = move-exception
            r19 = r7
            goto L_0x01a8
        L_0x0128:
            r0 = move-exception
            r19 = r7
            goto L_0x01de
        L_0x012d:
            r0 = move-exception
            r19 = r7
            goto L_0x0200
        L_0x0132:
            r0 = move-exception
            r19 = r7
            goto L_0x022b
        L_0x0137:
            r0 = move-exception
            r19 = r7
            goto L_0x0255
        L_0x013c:
            r0 = move-exception
            r19 = r7
            goto L_0x027f
        L_0x0141:
            r0 = move-exception
            goto L_0x01a8
        L_0x0144:
            r0 = move-exception
            goto L_0x01de
        L_0x0147:
            r0 = move-exception
            goto L_0x0200
        L_0x014a:
            r0 = move-exception
            goto L_0x022b
        L_0x014d:
            r0 = move-exception
            goto L_0x0255
        L_0x0150:
            r0 = move-exception
            goto L_0x027f
        L_0x0153:
            r0 = move-exception
            r22 = r5
            r21 = r7
            r1 = r0
            goto L_0x028d
        L_0x015b:
            r0 = move-exception
            r22 = r5
            r21 = r7
            goto L_0x01a8
        L_0x0161:
            r0 = move-exception
            r22 = r5
            r21 = r7
            goto L_0x01de
        L_0x0168:
            r0 = move-exception
            r22 = r5
            r21 = r7
            goto L_0x0200
        L_0x016f:
            r0 = move-exception
            r22 = r5
            r21 = r7
            goto L_0x022b
        L_0x0176:
            r0 = move-exception
            r22 = r5
            r21 = r7
            goto L_0x0255
        L_0x017d:
            r0 = move-exception
            r22 = r5
            r21 = r7
            goto L_0x027f
        L_0x0184:
            r22 = r5
            r20 = r6
            r21 = r7
        L_0x018a:
            if (r19 == 0) goto L_0x0192
            r19.close()     // Catch:{ IOException -> 0x0191 }
            goto L_0x0192
        L_0x0191:
            r0 = move-exception
        L_0x0192:
            r5 = r16
            goto L_0x02a0
        L_0x0197:
            r0 = move-exception
            r22 = r5
            r20 = r6
            r21 = r7
            r1 = r0
            goto L_0x028d
        L_0x01a1:
            r0 = move-exception
            r22 = r5
            r20 = r6
            r21 = r7
        L_0x01a8:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x028b }
            r1.<init>()     // Catch:{ all -> 0x028b }
            java.lang.StringBuilder r1 = r1.append(r11)     // Catch:{ all -> 0x028b }
            java.lang.StringBuilder r1 = r1.append(r8)     // Catch:{ all -> 0x028b }
            java.lang.StringBuilder r1 = r1.append(r14)     // Catch:{ all -> 0x028b }
            java.lang.String r5 = r0.getMessage()     // Catch:{ all -> 0x028b }
            java.lang.StringBuilder r1 = r1.append(r5)     // Catch:{ all -> 0x028b }
            java.lang.StringBuilder r1 = r1.append(r9)     // Catch:{ all -> 0x028b }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x028b }
            com.mysql.jdbc.ExceptionInterceptor r5 = r28.getExceptionInterceptor()     // Catch:{ all -> 0x028b }
            r6 = 0
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (int) r6, (boolean) r6, (com.mysql.jdbc.ExceptionInterceptor) r5)     // Catch:{ all -> 0x028b }
            r1.initCause(r0)     // Catch:{ all -> 0x028b }
            throw r1     // Catch:{ all -> 0x028b }
        L_0x01d7:
            r0 = move-exception
            r22 = r5
            r20 = r6
            r21 = r7
        L_0x01de:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x028b }
            r1.<init>()     // Catch:{ all -> 0x028b }
            java.lang.StringBuilder r1 = r1.append(r8)     // Catch:{ all -> 0x028b }
            java.lang.StringBuilder r1 = r1.append(r13)     // Catch:{ all -> 0x028b }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x028b }
            com.mysql.jdbc.ExceptionInterceptor r5 = r28.getExceptionInterceptor()     // Catch:{ all -> 0x028b }
            r6 = 0
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (int) r6, (boolean) r6, (com.mysql.jdbc.ExceptionInterceptor) r5)     // Catch:{ all -> 0x028b }
            throw r1     // Catch:{ all -> 0x028b }
        L_0x01f9:
            r0 = move-exception
            r22 = r5
            r20 = r6
            r21 = r7
        L_0x0200:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x028b }
            r1.<init>()     // Catch:{ all -> 0x028b }
            java.lang.String r2 = "Could not load client"
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x028b }
            java.lang.StringBuilder r1 = r1.append(r4)     // Catch:{ all -> 0x028b }
            java.lang.StringBuilder r1 = r1.append(r15)     // Catch:{ all -> 0x028b }
            java.lang.StringBuilder r1 = r1.append(r8)     // Catch:{ all -> 0x028b }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x028b }
            com.mysql.jdbc.ExceptionInterceptor r2 = r28.getExceptionInterceptor()     // Catch:{ all -> 0x028b }
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException(r1, r2)     // Catch:{ all -> 0x028b }
            throw r1     // Catch:{ all -> 0x028b }
        L_0x0224:
            r0 = move-exception
            r22 = r5
            r20 = r6
            r21 = r7
        L_0x022b:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x028b }
            r5.<init>()     // Catch:{ all -> 0x028b }
            java.lang.StringBuilder r1 = r5.append(r1)     // Catch:{ all -> 0x028b }
            java.lang.String r5 = r0.getMessage()     // Catch:{ all -> 0x028b }
            java.lang.StringBuilder r1 = r1.append(r5)     // Catch:{ all -> 0x028b }
            java.lang.StringBuilder r1 = r1.append(r9)     // Catch:{ all -> 0x028b }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x028b }
            com.mysql.jdbc.ExceptionInterceptor r5 = r28.getExceptionInterceptor()     // Catch:{ all -> 0x028b }
            r6 = 0
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (int) r6, (boolean) r6, (com.mysql.jdbc.ExceptionInterceptor) r5)     // Catch:{ all -> 0x028b }
            throw r1     // Catch:{ all -> 0x028b }
        L_0x024e:
            r0 = move-exception
            r22 = r5
            r20 = r6
            r21 = r7
        L_0x0255:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x028b }
            r1.<init>()     // Catch:{ all -> 0x028b }
            java.lang.StringBuilder r1 = r1.append(r12)     // Catch:{ all -> 0x028b }
            java.lang.String r5 = r0.getMessage()     // Catch:{ all -> 0x028b }
            java.lang.StringBuilder r1 = r1.append(r5)     // Catch:{ all -> 0x028b }
            java.lang.StringBuilder r1 = r1.append(r9)     // Catch:{ all -> 0x028b }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x028b }
            com.mysql.jdbc.ExceptionInterceptor r5 = r28.getExceptionInterceptor()     // Catch:{ all -> 0x028b }
            r6 = 0
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (int) r6, (boolean) r6, (com.mysql.jdbc.ExceptionInterceptor) r5)     // Catch:{ all -> 0x028b }
            throw r1     // Catch:{ all -> 0x028b }
        L_0x0278:
            r0 = move-exception
            r22 = r5
            r20 = r6
            r21 = r7
        L_0x027f:
            java.lang.String r1 = "Could not recover keys from client keystore.  Check password?"
            com.mysql.jdbc.ExceptionInterceptor r5 = r28.getExceptionInterceptor()     // Catch:{ all -> 0x028b }
            r6 = 0
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (int) r6, (boolean) r6, (com.mysql.jdbc.ExceptionInterceptor) r5)     // Catch:{ all -> 0x028b }
            throw r1     // Catch:{ all -> 0x028b }
        L_0x028b:
            r0 = move-exception
            r1 = r0
        L_0x028d:
            if (r19 == 0) goto L_0x0295
            r19.close()     // Catch:{ IOException -> 0x0293 }
            goto L_0x0295
        L_0x0293:
            r0 = move-exception
            goto L_0x0296
        L_0x0295:
        L_0x0296:
            throw r1
        L_0x0298:
            r22 = r5
            r20 = r6
            r21 = r7
            r5 = r16
        L_0x02a0:
            r6 = 0
            r0 = 0
            boolean r7 = com.mysql.jdbc.StringUtils.isNullOrEmpty(r22)     // Catch:{ MalformedURLException -> 0x06f2, KeyStoreException -> 0x06be, NoSuchAlgorithmException -> 0x068a, CertificateException -> 0x0654, IOException -> 0x060e, all -> 0x05fa }
            if (r7 != 0) goto L_0x039e
            boolean r7 = com.mysql.jdbc.StringUtils.isNullOrEmpty(r21)     // Catch:{ MalformedURLException -> 0x038b, KeyStoreException -> 0x0378, NoSuchAlgorithmException -> 0x0365, CertificateException -> 0x0352, IOException -> 0x0341, all -> 0x032d }
            if (r7 != 0) goto L_0x039e
            java.net.URL r7 = new java.net.URL     // Catch:{ MalformedURLException -> 0x038b, KeyStoreException -> 0x0378, NoSuchAlgorithmException -> 0x0365, CertificateException -> 0x0352, IOException -> 0x0341, all -> 0x032d }
            r19 = r3
            r3 = r22
            r7.<init>(r3)     // Catch:{ MalformedURLException -> 0x031e, KeyStoreException -> 0x030f, NoSuchAlgorithmException -> 0x0300, CertificateException -> 0x02f1, IOException -> 0x02e4, all -> 0x02d4 }
            java.io.InputStream r7 = r7.openStream()     // Catch:{ MalformedURLException -> 0x031e, KeyStoreException -> 0x030f, NoSuchAlgorithmException -> 0x0300, CertificateException -> 0x02f1, IOException -> 0x02e4, all -> 0x02d4 }
            r6 = r7
            if (r20 != 0) goto L_0x02c4
            r16 = r0
            r7 = 0
            char[] r0 = new char[r7]     // Catch:{ MalformedURLException -> 0x031e, KeyStoreException -> 0x030f, NoSuchAlgorithmException -> 0x0300, CertificateException -> 0x02f1, IOException -> 0x02e4, all -> 0x02d4 }
            goto L_0x02ca
        L_0x02c4:
            r16 = r0
            char[] r0 = r20.toCharArray()     // Catch:{ MalformedURLException -> 0x031e, KeyStoreException -> 0x030f, NoSuchAlgorithmException -> 0x0300, CertificateException -> 0x02f1, IOException -> 0x02e4, all -> 0x02d4 }
        L_0x02ca:
            java.security.KeyStore r7 = java.security.KeyStore.getInstance(r21)     // Catch:{ MalformedURLException -> 0x031e, KeyStoreException -> 0x030f, NoSuchAlgorithmException -> 0x0300, CertificateException -> 0x02f1, IOException -> 0x02e4, all -> 0x02d4 }
            r7.load(r6, r0)     // Catch:{ MalformedURLException -> 0x031e, KeyStoreException -> 0x030f, NoSuchAlgorithmException -> 0x0300, CertificateException -> 0x02f1, IOException -> 0x02e4, all -> 0x02d4 }
            r0 = r7
            goto L_0x03a6
        L_0x02d4:
            r0 = move-exception
            r1 = r0
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r7 = r21
            r18 = r4
            goto L_0x0720
        L_0x02e4:
            r0 = move-exception
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r18 = r4
            goto L_0x061d
        L_0x02f1:
            r0 = move-exception
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r7 = r21
            r18 = r4
            goto L_0x0665
        L_0x0300:
            r0 = move-exception
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r7 = r21
            r18 = r4
            goto L_0x069b
        L_0x030f:
            r0 = move-exception
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r7 = r21
            r18 = r4
            goto L_0x06cf
        L_0x031e:
            r0 = move-exception
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r7 = r21
            r18 = r4
            goto L_0x0703
        L_0x032d:
            r0 = move-exception
            r19 = r3
            r3 = r22
            r1 = r0
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r7 = r21
            r18 = r4
            goto L_0x0720
        L_0x0341:
            r0 = move-exception
            r19 = r3
            r3 = r22
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r18 = r4
            goto L_0x061d
        L_0x0352:
            r0 = move-exception
            r19 = r3
            r3 = r22
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r7 = r21
            r18 = r4
            goto L_0x0665
        L_0x0365:
            r0 = move-exception
            r19 = r3
            r3 = r22
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r7 = r21
            r18 = r4
            goto L_0x069b
        L_0x0378:
            r0 = move-exception
            r19 = r3
            r3 = r22
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r7 = r21
            r18 = r4
            goto L_0x06cf
        L_0x038b:
            r0 = move-exception
            r19 = r3
            r3 = r22
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r7 = r21
            r18 = r4
            goto L_0x0703
        L_0x039e:
            r16 = r0
            r19 = r3
            r3 = r22
            r0 = r16
        L_0x03a6:
            r7 = r18
            r7.init(r0)     // Catch:{ MalformedURLException -> 0x05eb, KeyStoreException -> 0x05dc, NoSuchAlgorithmException -> 0x05cd, CertificateException -> 0x05be, IOException -> 0x05b1, all -> 0x05a1 }
            javax.net.ssl.TrustManager[] r16 = r7.getTrustManagers()     // Catch:{ MalformedURLException -> 0x05eb, KeyStoreException -> 0x05dc, NoSuchAlgorithmException -> 0x05cd, CertificateException -> 0x05be, IOException -> 0x05b1, all -> 0x05a1 }
            r22 = r0
            r18 = r4
            r4 = r28
            com.mysql.jdbc.MySQLConnection r0 = r4.connection     // Catch:{ MalformedURLException -> 0x0594, KeyStoreException -> 0x0587, NoSuchAlgorithmException -> 0x057a, CertificateException -> 0x056d, IOException -> 0x0562, all -> 0x0554 }
            boolean r0 = r0.getVerifyServerCertificate()     // Catch:{ MalformedURLException -> 0x0594, KeyStoreException -> 0x0587, NoSuchAlgorithmException -> 0x057a, CertificateException -> 0x056d, IOException -> 0x0562, all -> 0x0554 }
            r23 = r16
            r4 = r23
            r23 = r7
            int r7 = r4.length     // Catch:{ MalformedURLException -> 0x0549, KeyStoreException -> 0x053e, NoSuchAlgorithmException -> 0x0533, CertificateException -> 0x0528, IOException -> 0x051f, all -> 0x0513 }
            r24 = 0
            r25 = r8
            r8 = r24
        L_0x03c8:
            if (r8 >= r7) goto L_0x04a9
            r24 = r4[r8]     // Catch:{ MalformedURLException -> 0x04a0, KeyStoreException -> 0x0497, NoSuchAlgorithmException -> 0x048e, CertificateException -> 0x0485, IOException -> 0x047e, all -> 0x0474 }
            r26 = r24
            r24 = r4
            r4 = r26
            r26 = r7
            boolean r7 = r4 instanceof javax.net.ssl.X509TrustManager     // Catch:{ MalformedURLException -> 0x04a0, KeyStoreException -> 0x0497, NoSuchAlgorithmException -> 0x048e, CertificateException -> 0x0485, IOException -> 0x047e, all -> 0x0474 }
            if (r7 == 0) goto L_0x0442
            com.mysql.jdbc.ExportControlled$X509TrustManagerWrapper r7 = new com.mysql.jdbc.ExportControlled$X509TrustManagerWrapper     // Catch:{ MalformedURLException -> 0x0439, KeyStoreException -> 0x0430, NoSuchAlgorithmException -> 0x0427, CertificateException -> 0x041e, IOException -> 0x0417, all -> 0x040d }
            r27 = r10
            r10 = r4
            javax.net.ssl.X509TrustManager r10 = (javax.net.ssl.X509TrustManager) r10     // Catch:{ MalformedURLException -> 0x0406, KeyStoreException -> 0x03ff, NoSuchAlgorithmException -> 0x03f8, CertificateException -> 0x03f1, IOException -> 0x03ec, all -> 0x03e4 }
            r7.<init>(r10, r0)     // Catch:{ MalformedURLException -> 0x0406, KeyStoreException -> 0x03ff, NoSuchAlgorithmException -> 0x03f8, CertificateException -> 0x03f1, IOException -> 0x03ec, all -> 0x03e4 }
            goto L_0x0445
        L_0x03e4:
            r0 = move-exception
            r1 = r0
            r10 = r17
            r7 = r21
            goto L_0x0720
        L_0x03ec:
            r0 = move-exception
            r10 = r17
            goto L_0x061d
        L_0x03f1:
            r0 = move-exception
            r10 = r17
            r7 = r21
            goto L_0x0665
        L_0x03f8:
            r0 = move-exception
            r10 = r17
            r7 = r21
            goto L_0x069b
        L_0x03ff:
            r0 = move-exception
            r10 = r17
            r7 = r21
            goto L_0x06cf
        L_0x0406:
            r0 = move-exception
            r10 = r17
            r7 = r21
            goto L_0x0703
        L_0x040d:
            r0 = move-exception
            r27 = r10
            r1 = r0
            r10 = r17
            r7 = r21
            goto L_0x0720
        L_0x0417:
            r0 = move-exception
            r27 = r10
            r10 = r17
            goto L_0x061d
        L_0x041e:
            r0 = move-exception
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x0665
        L_0x0427:
            r0 = move-exception
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x069b
        L_0x0430:
            r0 = move-exception
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x06cf
        L_0x0439:
            r0 = move-exception
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x0703
        L_0x0442:
            r27 = r10
            r7 = r4
        L_0x0445:
            r10 = r17
            r10.add(r7)     // Catch:{ MalformedURLException -> 0x046f, KeyStoreException -> 0x046a, NoSuchAlgorithmException -> 0x0465, CertificateException -> 0x0460, IOException -> 0x045d, all -> 0x0457 }
            int r8 = r8 + 1
            r17 = r10
            r4 = r24
            r7 = r26
            r10 = r27
            goto L_0x03c8
        L_0x0457:
            r0 = move-exception
            r1 = r0
            r7 = r21
            goto L_0x0720
        L_0x045d:
            r0 = move-exception
            goto L_0x061d
        L_0x0460:
            r0 = move-exception
            r7 = r21
            goto L_0x0665
        L_0x0465:
            r0 = move-exception
            r7 = r21
            goto L_0x069b
        L_0x046a:
            r0 = move-exception
            r7 = r21
            goto L_0x06cf
        L_0x046f:
            r0 = move-exception
            r7 = r21
            goto L_0x0703
        L_0x0474:
            r0 = move-exception
            r27 = r10
            r10 = r17
            r1 = r0
            r7 = r21
            goto L_0x0720
        L_0x047e:
            r0 = move-exception
            r27 = r10
            r10 = r17
            goto L_0x061d
        L_0x0485:
            r0 = move-exception
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x0665
        L_0x048e:
            r0 = move-exception
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x069b
        L_0x0497:
            r0 = move-exception
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x06cf
        L_0x04a0:
            r0 = move-exception
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x0703
        L_0x04a9:
            r24 = r4
            r26 = r7
            r27 = r10
            r10 = r17
            if (r6 == 0) goto L_0x04b9
            r6.close()     // Catch:{ IOException -> 0x04b8 }
            goto L_0x04b9
        L_0x04b8:
            r0 = move-exception
        L_0x04b9:
            int r0 = r10.size()
            if (r0 != 0) goto L_0x04c9
            com.mysql.jdbc.ExportControlled$X509TrustManagerWrapper r0 = new com.mysql.jdbc.ExportControlled$X509TrustManagerWrapper
            r0.<init>()
            r10.add(r0)
        L_0x04c9:
            java.lang.String r0 = "TLS"
            javax.net.ssl.SSLContext r0 = javax.net.ssl.SSLContext.getInstance(r0)     // Catch:{ NoSuchAlgorithmException -> 0x0506, KeyManagementException -> 0x04e4 }
            int r1 = r10.size()     // Catch:{ NoSuchAlgorithmException -> 0x0506, KeyManagementException -> 0x04e4 }
            javax.net.ssl.TrustManager[] r1 = new javax.net.ssl.TrustManager[r1]     // Catch:{ NoSuchAlgorithmException -> 0x0506, KeyManagementException -> 0x04e4 }
            java.lang.Object[] r1 = r10.toArray(r1)     // Catch:{ NoSuchAlgorithmException -> 0x0506, KeyManagementException -> 0x04e4 }
            javax.net.ssl.TrustManager[] r1 = (javax.net.ssl.TrustManager[]) r1     // Catch:{ NoSuchAlgorithmException -> 0x0506, KeyManagementException -> 0x04e4 }
            r4 = 0
            r0.init(r5, r1, r4)     // Catch:{ NoSuchAlgorithmException -> 0x0506, KeyManagementException -> 0x04e4 }
            javax.net.ssl.SSLSocketFactory r1 = r0.getSocketFactory()     // Catch:{ NoSuchAlgorithmException -> 0x0506, KeyManagementException -> 0x04e4 }
            return r1
        L_0x04e4:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "KeyManagementException: "
            java.lang.StringBuilder r1 = r1.append(r4)
            java.lang.String r4 = r0.getMessage()
            java.lang.StringBuilder r1 = r1.append(r4)
            java.lang.String r1 = r1.toString()
            com.mysql.jdbc.ExceptionInterceptor r4 = r28.getExceptionInterceptor()
            r7 = 0
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (int) r7, (boolean) r7, (com.mysql.jdbc.ExceptionInterceptor) r4)
            throw r1
        L_0x0506:
            r0 = move-exception
            r7 = 0
            com.mysql.jdbc.ExceptionInterceptor r1 = r28.getExceptionInterceptor()
            java.lang.String r4 = "TLS is not a valid SSL protocol."
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r4, (java.lang.String) r2, (int) r7, (boolean) r7, (com.mysql.jdbc.ExceptionInterceptor) r1)
            throw r1
        L_0x0513:
            r0 = move-exception
            r25 = r8
            r27 = r10
            r10 = r17
            r1 = r0
            r7 = r21
            goto L_0x0720
        L_0x051f:
            r0 = move-exception
            r25 = r8
            r27 = r10
            r10 = r17
            goto L_0x061d
        L_0x0528:
            r0 = move-exception
            r25 = r8
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x0665
        L_0x0533:
            r0 = move-exception
            r25 = r8
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x069b
        L_0x053e:
            r0 = move-exception
            r25 = r8
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x06cf
        L_0x0549:
            r0 = move-exception
            r25 = r8
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x0703
        L_0x0554:
            r0 = move-exception
            r23 = r7
            r25 = r8
            r27 = r10
            r10 = r17
            r1 = r0
            r7 = r21
            goto L_0x0720
        L_0x0562:
            r0 = move-exception
            r23 = r7
            r25 = r8
            r27 = r10
            r10 = r17
            goto L_0x061d
        L_0x056d:
            r0 = move-exception
            r23 = r7
            r25 = r8
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x0665
        L_0x057a:
            r0 = move-exception
            r23 = r7
            r25 = r8
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x069b
        L_0x0587:
            r0 = move-exception
            r23 = r7
            r25 = r8
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x06cf
        L_0x0594:
            r0 = move-exception
            r23 = r7
            r25 = r8
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x0703
        L_0x05a1:
            r0 = move-exception
            r18 = r4
            r23 = r7
            r25 = r8
            r27 = r10
            r10 = r17
            r1 = r0
            r7 = r21
            goto L_0x0720
        L_0x05b1:
            r0 = move-exception
            r18 = r4
            r23 = r7
            r25 = r8
            r27 = r10
            r10 = r17
            goto L_0x061d
        L_0x05be:
            r0 = move-exception
            r18 = r4
            r23 = r7
            r25 = r8
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x0665
        L_0x05cd:
            r0 = move-exception
            r18 = r4
            r23 = r7
            r25 = r8
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x069b
        L_0x05dc:
            r0 = move-exception
            r18 = r4
            r23 = r7
            r25 = r8
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x06cf
        L_0x05eb:
            r0 = move-exception
            r18 = r4
            r23 = r7
            r25 = r8
            r27 = r10
            r10 = r17
            r7 = r21
            goto L_0x0703
        L_0x05fa:
            r0 = move-exception
            r19 = r3
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r3 = r22
            r18 = r4
            r1 = r0
            r7 = r21
            goto L_0x0720
        L_0x060e:
            r0 = move-exception
            r19 = r3
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r3 = r22
            r18 = r4
        L_0x061d:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x064e }
            r1.<init>()     // Catch:{ all -> 0x064e }
            java.lang.StringBuilder r1 = r1.append(r11)     // Catch:{ all -> 0x064e }
            r7 = r21
            java.lang.StringBuilder r1 = r1.append(r7)     // Catch:{ all -> 0x071e }
            java.lang.StringBuilder r1 = r1.append(r14)     // Catch:{ all -> 0x071e }
            java.lang.String r4 = r0.getMessage()     // Catch:{ all -> 0x071e }
            java.lang.StringBuilder r1 = r1.append(r4)     // Catch:{ all -> 0x071e }
            java.lang.StringBuilder r1 = r1.append(r9)     // Catch:{ all -> 0x071e }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x071e }
            com.mysql.jdbc.ExceptionInterceptor r4 = r28.getExceptionInterceptor()     // Catch:{ all -> 0x071e }
            r8 = 0
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (int) r8, (boolean) r8, (com.mysql.jdbc.ExceptionInterceptor) r4)     // Catch:{ all -> 0x071e }
            r1.initCause(r0)     // Catch:{ all -> 0x071e }
            throw r1     // Catch:{ all -> 0x071e }
        L_0x064e:
            r0 = move-exception
            r7 = r21
            r1 = r0
            goto L_0x0720
        L_0x0654:
            r0 = move-exception
            r19 = r3
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r7 = r21
            r3 = r22
            r18 = r4
        L_0x0665:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x071e }
            r1.<init>()     // Catch:{ all -> 0x071e }
            java.lang.String r4 = "Could not load trust"
            java.lang.StringBuilder r1 = r1.append(r4)     // Catch:{ all -> 0x071e }
            java.lang.StringBuilder r1 = r1.append(r7)     // Catch:{ all -> 0x071e }
            java.lang.StringBuilder r1 = r1.append(r15)     // Catch:{ all -> 0x071e }
            java.lang.StringBuilder r1 = r1.append(r3)     // Catch:{ all -> 0x071e }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x071e }
            com.mysql.jdbc.ExceptionInterceptor r4 = r28.getExceptionInterceptor()     // Catch:{ all -> 0x071e }
            r8 = 0
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (int) r8, (boolean) r8, (com.mysql.jdbc.ExceptionInterceptor) r4)     // Catch:{ all -> 0x071e }
            throw r1     // Catch:{ all -> 0x071e }
        L_0x068a:
            r0 = move-exception
            r19 = r3
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r7 = r21
            r3 = r22
            r18 = r4
        L_0x069b:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x071e }
            r1.<init>()     // Catch:{ all -> 0x071e }
            java.lang.StringBuilder r1 = r1.append(r12)     // Catch:{ all -> 0x071e }
            java.lang.String r4 = r0.getMessage()     // Catch:{ all -> 0x071e }
            java.lang.StringBuilder r1 = r1.append(r4)     // Catch:{ all -> 0x071e }
            java.lang.StringBuilder r1 = r1.append(r9)     // Catch:{ all -> 0x071e }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x071e }
            com.mysql.jdbc.ExceptionInterceptor r4 = r28.getExceptionInterceptor()     // Catch:{ all -> 0x071e }
            r8 = 0
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (int) r8, (boolean) r8, (com.mysql.jdbc.ExceptionInterceptor) r4)     // Catch:{ all -> 0x071e }
            throw r1     // Catch:{ all -> 0x071e }
        L_0x06be:
            r0 = move-exception
            r19 = r3
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r7 = r21
            r3 = r22
            r18 = r4
        L_0x06cf:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x071e }
            r4.<init>()     // Catch:{ all -> 0x071e }
            java.lang.StringBuilder r1 = r4.append(r1)     // Catch:{ all -> 0x071e }
            java.lang.String r4 = r0.getMessage()     // Catch:{ all -> 0x071e }
            java.lang.StringBuilder r1 = r1.append(r4)     // Catch:{ all -> 0x071e }
            java.lang.StringBuilder r1 = r1.append(r9)     // Catch:{ all -> 0x071e }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x071e }
            com.mysql.jdbc.ExceptionInterceptor r4 = r28.getExceptionInterceptor()     // Catch:{ all -> 0x071e }
            r8 = 0
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (int) r8, (boolean) r8, (com.mysql.jdbc.ExceptionInterceptor) r4)     // Catch:{ all -> 0x071e }
            throw r1     // Catch:{ all -> 0x071e }
        L_0x06f2:
            r0 = move-exception
            r19 = r3
            r25 = r8
            r27 = r10
            r10 = r17
            r23 = r18
            r7 = r21
            r3 = r22
            r18 = r4
        L_0x0703:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x071e }
            r1.<init>()     // Catch:{ all -> 0x071e }
            java.lang.StringBuilder r1 = r1.append(r3)     // Catch:{ all -> 0x071e }
            java.lang.StringBuilder r1 = r1.append(r13)     // Catch:{ all -> 0x071e }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x071e }
            com.mysql.jdbc.ExceptionInterceptor r4 = r28.getExceptionInterceptor()     // Catch:{ all -> 0x071e }
            r8 = 0
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (int) r8, (boolean) r8, (com.mysql.jdbc.ExceptionInterceptor) r4)     // Catch:{ all -> 0x071e }
            throw r1     // Catch:{ all -> 0x071e }
        L_0x071e:
            r0 = move-exception
            r1 = r0
        L_0x0720:
            if (r6 == 0) goto L_0x0728
            r6.close()     // Catch:{ IOException -> 0x0726 }
            goto L_0x0728
        L_0x0726:
            r0 = move-exception
            goto L_0x0729
        L_0x0728:
        L_0x0729:
            throw r1
        L_0x072b:
            r0 = move-exception
            r19 = r3
            r18 = r4
            r3 = r5
            r20 = r6
            r25 = r8
            r23 = r9
            r16 = r11
            r1 = r12
            goto L_0x0748
        L_0x073b:
            r0 = move-exception
            r19 = r3
            r18 = r4
            r3 = r5
            r20 = r6
            r25 = r8
            r16 = r11
            r1 = r12
        L_0x0748:
            com.mysql.jdbc.ExceptionInterceptor r4 = r28.getExceptionInterceptor()
            java.lang.String r5 = "Default algorithm definitions for TrustManager and/or KeyManager are invalid.  Check java security properties file."
            r6 = 0
            java.sql.SQLException r2 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r5, (java.lang.String) r2, (int) r6, (boolean) r6, (com.mysql.jdbc.ExceptionInterceptor) r4)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ExportControlled.getSSLSocketFactoryDefaultOrConfigured(com.mysql.jdbc.MysqlIO):javax.net.ssl.SSLSocketFactory");
    }

    public static boolean isSSLEstablished(Socket socket) {
        if (socket == null) {
            return false;
        }
        return SSLSocket.class.isAssignableFrom(socket.getClass());
    }

    public static RSAPublicKey decodeRSAPublicKey(String key, ExceptionInterceptor interceptor) throws SQLException {
        if (key != null) {
            try {
                int offset = key.indexOf("\n") + 1;
                return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64Decoder.decode(key.getBytes(), offset, key.indexOf("-----END PUBLIC KEY-----") - offset)));
            } catch (Exception ex) {
                throw SQLError.createSQLException("Unable to decode public key", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (Throwable) ex, interceptor);
            }
        } else {
            throw new SQLException("key parameter is null");
        }
    }

    public static byte[] encryptWithRSAPublicKey(byte[] source, RSAPublicKey key, String transformation, ExceptionInterceptor interceptor) throws SQLException {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(1, key);
            return cipher.doFinal(source);
        } catch (Exception ex) {
            throw SQLError.createSQLException(ex.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (Throwable) ex, interceptor);
        }
    }
}
