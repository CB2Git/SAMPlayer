package com.samplayer.demo.config;

import android.os.SystemClock;

import com.samplayer.interceptor.Interceptor;
import com.samplayer.model.SongInfo;
import com.samplayer.utils.SAMLog;

/**
 * 注意这个玩意在播放进程
 */
public class OutInterceptor implements Interceptor {

    private static final String TAG = "OutInterceptor";


    @Override
    public SongInfo intercept(Chain chain) throws Exception {
        //获取上一步处理后的音乐信息
        SongInfo info = chain.before();

        //请求后台获取播放地址或者解密然后设置新的播放地址
        //注意:不要使用info.setSongUrl();！！！！
        //注意:不要使用info.setSongUrl();！！！！
        //注意:不要使用info.setSongUrl();！！！！
        //info.setSongInterceptorUrl("新的url");
        SAMLog.i(TAG, "intercept: " + info.getSongUrl());
        SAMLog.i(TAG, "intercept: " + Thread.currentThread().getName());
        //在这里做处理  耗时操作也可以的
        SystemClock.sleep(3000);
        //直接报错
        //chain.error(2333);

        //处理完毕
        return chain.proceed(info);
    }
}
