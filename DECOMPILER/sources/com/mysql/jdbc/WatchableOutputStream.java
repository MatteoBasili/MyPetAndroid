package com.mysql.jdbc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

class WatchableOutputStream extends ByteArrayOutputStream {
    private OutputStreamWatcher watcher;

    WatchableOutputStream() {
    }

    public void close() throws IOException {
        super.close();
        OutputStreamWatcher outputStreamWatcher = this.watcher;
        if (outputStreamWatcher != null) {
            outputStreamWatcher.streamClosed(this);
        }
    }

    public void setWatcher(OutputStreamWatcher watcher2) {
        this.watcher = watcher2;
    }
}
