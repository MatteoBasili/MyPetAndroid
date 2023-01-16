package com.mysql.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class NetworkResources {
    private final Socket mysqlConnection;
    private final InputStream mysqlInput;
    private final OutputStream mysqlOutput;

    protected NetworkResources(Socket mysqlConnection2, InputStream mysqlInput2, OutputStream mysqlOutput2) {
        this.mysqlConnection = mysqlConnection2;
        this.mysqlInput = mysqlInput2;
        this.mysqlOutput = mysqlOutput2;
    }

    /* access modifiers changed from: protected */
    public final void forceClose() {
        try {
            if (!ExportControlled.isSSLEstablished(this.mysqlConnection)) {
                InputStream inputStream = this.mysqlInput;
                if (inputStream != null) {
                    inputStream.close();
                }
                Socket socket = this.mysqlConnection;
                if (socket != null && !socket.isClosed() && !this.mysqlConnection.isInputShutdown()) {
                    try {
                        this.mysqlConnection.shutdownInput();
                    } catch (UnsupportedOperationException e) {
                    }
                }
            }
        } catch (IOException e2) {
        } catch (Throwable th) {
            Socket socket2 = this.mysqlConnection;
            if (socket2 != null && !socket2.isClosed() && !this.mysqlConnection.isInputShutdown()) {
                try {
                    this.mysqlConnection.shutdownInput();
                } catch (UnsupportedOperationException e3) {
                }
            }
            throw th;
        }
        try {
            if (!ExportControlled.isSSLEstablished(this.mysqlConnection)) {
                OutputStream outputStream = this.mysqlOutput;
                if (outputStream != null) {
                    outputStream.close();
                }
                Socket socket3 = this.mysqlConnection;
                if (socket3 != null && !socket3.isClosed() && !this.mysqlConnection.isOutputShutdown()) {
                    try {
                        this.mysqlConnection.shutdownOutput();
                    } catch (UnsupportedOperationException e4) {
                    }
                }
            }
        } catch (IOException e5) {
        } catch (Throwable th2) {
            Socket socket4 = this.mysqlConnection;
            if (socket4 != null && !socket4.isClosed() && !this.mysqlConnection.isOutputShutdown()) {
                try {
                    this.mysqlConnection.shutdownOutput();
                } catch (UnsupportedOperationException e6) {
                }
            }
            throw th2;
        }
        try {
            Socket socket5 = this.mysqlConnection;
            if (socket5 != null) {
                socket5.close();
            }
        } catch (IOException e7) {
        }
    }
}
