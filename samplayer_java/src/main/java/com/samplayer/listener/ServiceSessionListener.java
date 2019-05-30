package com.samplayer.listener;

/**
 * Service连接状态的监听
 */
public interface ServiceSessionListener {

    /**
     * Service已经连接
     */
    void onServiceConnect();

    /**
     * Service断开连接
     *
     * @param isError true Service异常退出连接<br/>
     *                false 主动断开连接
     */
    void onServiceDisconnect(boolean isError);
}
