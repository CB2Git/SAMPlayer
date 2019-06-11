package com.samplayer.cache;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

public class CacheServer {

    private static HttpProxyCacheServer httpProxyCacheServer;

    // 512M for cache
    private static final long CACHE_SIZE = 512 * 1024 * 1024;

    public static synchronized HttpProxyCacheServer getInstance(Context context) {
        if (httpProxyCacheServer == null) {
            httpProxyCacheServer = newProxy(context);
        }
        return httpProxyCacheServer;
    }

    private static HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(CACHE_SIZE)
                .build();
    }
}
