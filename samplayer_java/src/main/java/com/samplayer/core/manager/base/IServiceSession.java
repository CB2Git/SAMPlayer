package com.samplayer.core.manager.base;

import android.support.annotation.Nullable;

import com.samplayer.SAMPlayer;
import com.samplayer.aidl.ISAMPlayerService;
import com.samplayer.listener.ServiceSessionListener;

/**
 * 与Remote Service连接相关操作
 */
public interface IServiceSession {

    /**
     * 服务是否连接
     */
    boolean isConnect();

    /**
     * 连接服务
     */
    void connect();

    /**
     * 断开服务连接
     */
    void disconnect();

    /**
     * 断开连接并停止服务
     */
    void stop();

    /**
     * 获取Remote Service连接的对象
     * <p>
     * 一般来说 外部的调用者还是不要自己调用了，可以使用{@link SAMPlayer#getPlayer()}去控制播放器
     *
     * @return 如果未连接  获取到的可能为空
     */
    @Nullable
    ISAMPlayerService getRemoteService();

    /**
     * 添加监听
     */
    void addSessionListener(ServiceSessionListener listener);

    /**
     * 删除监听
     */
    void removeSessionListener(ServiceSessionListener listener);
}
