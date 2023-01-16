package com.mysql.jdbc;

import java.io.CharArrayWriter;

class WatchableWriter extends CharArrayWriter {
    private WriterWatcher watcher;

    WatchableWriter() {
    }

    public void close() {
        super.close();
        WriterWatcher writerWatcher = this.watcher;
        if (writerWatcher != null) {
            writerWatcher.writerClosed(this);
        }
    }

    public void setWatcher(WriterWatcher watcher2) {
        this.watcher = watcher2;
    }
}
