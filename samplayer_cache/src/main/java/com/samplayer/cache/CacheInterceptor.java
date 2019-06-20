package com.samplayer.cache;

import com.danikula.videocache.HttpProxyCacheServer;
import com.samplayer.SAMConfig;
import com.samplayer.interceptor.Interceptor;
import com.samplayer.model.SongInfo;

/**
 * 进行音乐缓存的拦截器
 * <p>
 * 基于  https://github.com/danikula/AndroidVideoCache
 */
public class CacheInterceptor implements Interceptor {

    @Override
    public SongInfo intercept(Chain chain) throws Exception {
        SongInfo info = chain.before();
        if (isValidUrl(info.getSongUrl())) {
            HttpProxyCacheServer instance = CacheServer.getInstance(SAMConfig.getAppContext());
            String proxyUrl = instance.getProxyUrl(info.getSongUrl());
            //直接设置拦截器设置的url
            info.setSongInterceptorUrl(proxyUrl);
        }
        return chain.proceed(info);
    }

    /**
     * 检查url是否可以进行缓存
     *
     * @param url 待检查的url
     * @return true 可以进行缓存  false 不能进行缓存
     */
    private boolean isValidUrl(String url) {
        /**
         * 1.本地url   eg:/storage/emulated/0/netease/cloudmusic/Music/张振宇 - 不要再来伤害我.mp3
         * 2.代理url   eg:http://127.0.0.1
         * 3.m3u8 一般是直播地址  这个不用缓存
         */
        if (url.startsWith("/") || url.contains("127.0.0.1") || url.contains(".m3u8")) {
            return false;
        }
        return true;
    }

}
