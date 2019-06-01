package com.samplayer.demo;

import android.app.Application;

import com.samplayer.SMAManager;
import com.samplayer.core.remote.player.PlayerFactory;
import com.samplayer.demo.config.OutInterceptor;
import com.samplayer.demo.config.OutNotificationConfig;
import com.samplayer.demo.config.RemotePlayListener;
import com.samplayer.exo.ExoPlayManager;
import com.samplayer.outconfig.OutConfigFactory;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SMAManager.init(this);
        //日志开关
        //SMAManager.debug(false);

        //不设置默认使用MediaPlayer
        //使用ijk
        //PlayerFactory.setPlayManager(IJKPlayManager.class);
        //使用exo
        PlayerFactory.setPlayManager(ExoPlayManager.class);

        //设置拦截器  在播放之前允许用户去改变一下播放地址  需要注意的是此类运行在播放进程
        OutConfigFactory.setInterceptorConfig(OutInterceptor.class);

        //设置通知栏的实现  需要注意的是此类运行运行在播放进程
        OutConfigFactory.setNotificationConfig(OutNotificationConfig.class);

        //将此监听设置在播放进程 实现播放历史、流水上报推荐使用这个监听
        OutConfigFactory.setPlayerListener(RemotePlayListener.class);
    }
}
