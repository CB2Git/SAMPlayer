package com.samplayer;

import android.content.Context;

import com.samplayer.core.manager.PlayManager;
import com.samplayer.core.manager.ServiceSessionManager;
import com.samplayer.core.manager.base.IPlayer;
import com.samplayer.core.manager.base.IReleaseAble;
import com.samplayer.core.manager.base.IServiceSession;
import com.samplayer.utils.SAMLog;

/**
 * SAMPlayer对外提供的功能
 */
public class SAMPlayer implements ISAMPlayer {

    private static final String TAG = "SAMPlayer";

    private static ISAMPlayer sInstance = null;

    /**
     * Remote Service连接管理
     */
    private IServiceSession mServiceSessionManager;

    /**
     * 播放操作相关
     */
    private IPlayer mISamPlayer;

    private SAMPlayer() {
        init(SMAManager.getAppContext());
    }

    public static ISAMPlayer getInstance() {
        if (sInstance == null) {
            synchronized (SAMPlayer.class) {
                if (sInstance == null) {
                    sInstance = new SAMPlayer();
                }
            }
        }
        return sInstance;
    }

    private void init(Context context) {
        mServiceSessionManager = new ServiceSessionManager(context);
        mISamPlayer = new PlayManager(mServiceSessionManager);
        SAMLog.i(TAG, "init player");
    }

    @Override
    public IServiceSession getServiceSession() {
        return mServiceSessionManager;
    }

    @Override
    public IPlayer getPlayer() {
        return mISamPlayer;
    }

    @Override
    public void release() {
        if (mServiceSessionManager instanceof IReleaseAble) {
            ((IReleaseAble) mServiceSessionManager).release();
        }
        sInstance = null;
    }
}
