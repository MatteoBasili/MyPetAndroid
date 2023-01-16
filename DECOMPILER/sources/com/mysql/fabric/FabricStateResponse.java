package com.mysql.fabric;

import java.util.concurrent.TimeUnit;

public class FabricStateResponse<T> {
    private T data;
    private long expireTimeMillis;
    private int secsTtl;

    public FabricStateResponse(T data2, int secsTtl2) {
        this.data = data2;
        this.secsTtl = secsTtl2;
        this.expireTimeMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis((long) secsTtl2);
    }

    public FabricStateResponse(T data2, int secsTtl2, long presetExpireTimeMillis) {
        this.data = data2;
        this.secsTtl = secsTtl2;
        this.expireTimeMillis = presetExpireTimeMillis;
    }

    public T getData() {
        return this.data;
    }

    public int getTtl() {
        return this.secsTtl;
    }

    public long getExpireTimeMillis() {
        return this.expireTimeMillis;
    }
}
