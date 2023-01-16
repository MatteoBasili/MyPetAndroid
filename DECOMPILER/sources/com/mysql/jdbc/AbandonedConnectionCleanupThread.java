package com.mysql.jdbc;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AbandonedConnectionCleanupThread implements Runnable {
    private static final ExecutorService cleanupThreadExcecutorService;
    private static final Map<ConnectionFinalizerPhantomReference, ConnectionFinalizerPhantomReference> connectionFinalizerPhantomRefs = new ConcurrentHashMap();
    private static final ReferenceQueue<MySQLConnection> referenceQueue = new ReferenceQueue<>();
    static Thread threadRef = null;
    private static Lock threadRefLock = new ReentrantLock();

    static {
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "mysql-cj-abandoned-connection-cleanup");
                t.setDaemon(true);
                ClassLoader classLoader = AbandonedConnectionCleanupThread.class.getClassLoader();
                if (classLoader == null) {
                    classLoader = ClassLoader.getSystemClassLoader();
                }
                t.setContextClassLoader(classLoader);
                AbandonedConnectionCleanupThread.threadRef = t;
                return t;
            }
        });
        cleanupThreadExcecutorService = newSingleThreadExecutor;
        newSingleThreadExecutor.execute(new AbandonedConnectionCleanupThread());
    }

    private AbandonedConnectionCleanupThread() {
    }

    public void run() {
        while (true) {
            try {
                checkThreadContextClassLoader();
                Reference<? extends MySQLConnection> reference = referenceQueue.remove(5000);
                if (reference != null) {
                    finalizeResource((ConnectionFinalizerPhantomReference) reference);
                }
            } catch (InterruptedException e) {
                threadRefLock.lock();
                threadRef = null;
                while (true) {
                    Reference<? extends MySQLConnection> poll = referenceQueue.poll();
                    Reference<? extends MySQLConnection> reference2 = poll;
                    if (poll != null) {
                        finalizeResource((ConnectionFinalizerPhantomReference) reference2);
                    } else {
                        connectionFinalizerPhantomRefs.clear();
                        threadRefLock.unlock();
                        return;
                    }
                }
            } catch (Exception e2) {
            } catch (Throwable th) {
                threadRefLock.unlock();
                throw th;
            }
        }
    }

    private void checkThreadContextClassLoader() {
        try {
            threadRef.getContextClassLoader().getResource("");
        } catch (Throwable th) {
            uncheckedShutdown();
        }
    }

    private static boolean consistentClassLoaders() {
        threadRefLock.lock();
        try {
            boolean z = false;
            if (threadRef == null) {
                return false;
            }
            ClassLoader callerCtxClassLoader = Thread.currentThread().getContextClassLoader();
            ClassLoader threadCtxClassLoader = threadRef.getContextClassLoader();
            if (!(callerCtxClassLoader == null || threadCtxClassLoader == null || callerCtxClassLoader != threadCtxClassLoader)) {
                z = true;
            }
            threadRefLock.unlock();
            return z;
        } finally {
            threadRefLock.unlock();
        }
    }

    private static void shutdown(boolean checked) {
        if (!checked || consistentClassLoaders()) {
            cleanupThreadExcecutorService.shutdownNow();
        }
    }

    public static void checkedShutdown() {
        shutdown(true);
    }

    public static void uncheckedShutdown() {
        shutdown(false);
    }

    public static boolean isAlive() {
        threadRefLock.lock();
        try {
            Thread thread = threadRef;
            return thread != null && thread.isAlive();
        } finally {
            threadRefLock.unlock();
        }
    }

    protected static void trackConnection(MySQLConnection conn, NetworkResources io2) {
        threadRefLock.lock();
        try {
            if (isAlive()) {
                ConnectionFinalizerPhantomReference reference = new ConnectionFinalizerPhantomReference(conn, io2, referenceQueue);
                connectionFinalizerPhantomRefs.put(reference, reference);
            }
        } finally {
            threadRefLock.unlock();
        }
    }

    private static void finalizeResource(ConnectionFinalizerPhantomReference reference) {
        try {
            reference.finalizeResources();
            reference.clear();
        } finally {
            connectionFinalizerPhantomRefs.remove(reference);
        }
    }

    private static class ConnectionFinalizerPhantomReference extends PhantomReference<MySQLConnection> {
        private NetworkResources networkResources;

        ConnectionFinalizerPhantomReference(MySQLConnection conn, NetworkResources networkResources2, ReferenceQueue<? super MySQLConnection> refQueue) {
            super(conn, refQueue);
            this.networkResources = networkResources2;
        }

        /* access modifiers changed from: package-private */
        public void finalizeResources() {
            NetworkResources networkResources2 = this.networkResources;
            if (networkResources2 != null) {
                try {
                    networkResources2.forceClose();
                } finally {
                    this.networkResources = null;
                }
            }
        }
    }

    public static Thread getThread() {
        return threadRef;
    }
}
