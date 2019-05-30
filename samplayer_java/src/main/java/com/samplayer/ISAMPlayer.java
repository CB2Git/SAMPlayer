package com.samplayer;

import com.samplayer.core.manager.base.IPlayer;
import com.samplayer.core.manager.base.IServiceSession;

/**
 * SAMPlayer 对外提供的接口
 */
public interface ISAMPlayer {

    /**
     * 获取Remote Service连接管理对象
     * <p>
     * 通过{@link IServiceSession}可以控制远程服务连接以及监听
     */
    IServiceSession getServiceSession();

    /**
     * 对外提供的播放接口,会转发给Remote Service去播放
     */
    IPlayer getPlayer();

    void release();
}
