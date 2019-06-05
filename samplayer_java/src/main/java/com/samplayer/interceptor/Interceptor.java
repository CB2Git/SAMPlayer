package com.samplayer.interceptor;

import android.support.annotation.NonNull;

import com.samplayer.model.SongInfo;

/**
 * 拦截器  类似于OkHttp
 */
public interface Interceptor {

    /**
     * 拦截器处理时机   需要注意的是  可以进行耗时操作、网络请求
     *
     * @param chain
     * @return
     * @throws Exception
     */
    SongInfo intercept(Chain chain) throws Exception;

    interface Chain {

        /**
         * 获取传入的歌曲信息
         */
        SongInfo before();

        /**
         * 直接打断链并报错
         *
         * @param errorCode 错误码
         */
        void error(int errorCode) throws Exception;

        /**
         * 继续下一个拦截器处理
         */
        SongInfo proceed(@NonNull SongInfo request) throws Exception;
    }
}
