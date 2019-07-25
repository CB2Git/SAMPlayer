package com.samplayer.core.manager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.samplayer.aidl.ISAMPlayerCallBack;
import com.samplayer.core.manager.base.IPlayer;
import com.samplayer.core.manager.base.IReleaseAble;
import com.samplayer.core.manager.base.IServiceSession;
import com.samplayer.listener.IPlayerListener;
import com.samplayer.listener.ServiceSessionListener;
import com.samplayer.model.PlayMode;
import com.samplayer.model.SongInfo;
import com.samplayer.outconfig.TimerConfig;
import com.samplayer.utils.SAMLog;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 对外提供的播放控制类
 * <p>
 * 通过{@link IServiceSession#getRemoteService()}转发给Remote Service播放
 */
public class PlayManager implements IPlayer, IReleaseAble {

    private static final String TAG = "RemotePlayManager";

    private static final int MSG_PLAYABLE_START = 1;

    private static final int MSG_PROGRESS_CHANGE = 2;

    private static final int MSG_BUFFER_PROGRESS = 3;

    private static final int MSG_IN_BUFFER = 4;

    private static final int MSG_COMPLETE = 5;

    private static final int MSG_START = 6;

    private static final int MSG_PAUSE = 7;

    private static final int MSG_STOP = 8;

    private static final int MSG_ERROR = 9;

    private static final int MSG_INTERCEPTOR_PROCESS = 10;

    //默认自动连接
    private boolean mAutoConnect = true;

    private IServiceSession mServiceSession;

    private ServiceSessionListener mServiceSessionListener;

    /**
     * 回调接口
     */
    private CopyOnWriteArrayList<IPlayerListener> mPlayerListeners = new CopyOnWriteArrayList<>();

    public PlayManager(IServiceSession serviceSession) {
        mServiceSession = serviceSession;
        //添加连接监听 当服务连接以后再监听播放器
        mServiceSession.addSessionListener(getServiceSessionListener());
    }

    /**
     * 服务连接状态监听
     *
     * @return
     */
    public ServiceSessionListener getServiceSessionListener() {
        if (mServiceSessionListener != null) {
            return mServiceSessionListener;
        }
        mServiceSessionListener = new ServiceSessionListener() {
            @Override
            public void onServiceConnect() {
                try {
                    //设置远程播放监听
                    mServiceSession.getRemoteService().setPlayCallback(getPlayCallback());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnect(boolean isError) {
                //这里想做些什么操作呢~~
            }
        };
        return mServiceSessionListener;
    }

    private boolean checkRemoteService() {
        boolean connect = mServiceSession != null && mServiceSession.isConnect() && mServiceSession.getRemoteService() != null;
        if (!connect) {
            SAMLog.w(TAG, "checkRemoteService: 貌似服务没有连接,自动连接:" + mAutoConnect);

            if (mAutoConnect && mServiceSession != null) {
                mServiceSession.connect();
                SAMLog.i(TAG, "checkRemoteService: >> 自动连接 <<");
            }
        }
        return connect;
    }

    @Override
    public void setAutoConnect(boolean auto) {
        mAutoConnect = auto;
    }

    @Override
    public void setPlayList(List<SongInfo> songInfos, boolean autoPlay) {
        if (checkRemoteService()) {
            try {
                mServiceSession.getRemoteService().setPlayList(songInfos, autoPlay);
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
    }

    @Override
    public void appendPlayList(List<SongInfo> songInfos) {
        if (checkRemoteService()) {
            try {
                mServiceSession.getRemoteService().appendPlayList(songInfos);
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
    }

    @Override
    public List<SongInfo> getPlayList() {
        if (checkRemoteService()) {
            try {
                return mServiceSession.getRemoteService().getPlayList();
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void clearPlayList() {
        if (checkRemoteService()) {
            try {
                mServiceSession.getRemoteService().clearPlayList();
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
    }

    @Override
    public boolean removeAt(int index) {
        if (checkRemoteService()) {
            try {
                return mServiceSession.getRemoteService().removeAt(index);
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
        return false;
    }

    @Override
    public boolean removeItem(SongInfo songInfo) {
        if (checkRemoteService()) {
            try {
                return mServiceSession.getRemoteService().removeItem(songInfo);
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
        return false;
    }

    @Override
    public void play() {
        if (checkRemoteService()) {
            try {
                mServiceSession.getRemoteService().play();
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
    }

    @Override
    public void play(SongInfo songInfo) {
        if (checkRemoteService()) {
            try {
                mServiceSession.getRemoteService().playItem(songInfo);
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
    }

    @Override
    public void playStartAt(SongInfo songInfo, long ms) {
        if (checkRemoteService()) {
            try {
                mServiceSession.getRemoteService().playStartAt(songInfo, ms);
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
    }

    @Override
    public void setPlayMode(int playMode) {
        if (checkRemoteService()) {
            try {
                mServiceSession.getRemoteService().setPlayMode(playMode);
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
    }

    @Override
    public int getPlayMode() {
        if (checkRemoteService()) {
            try {
                return mServiceSession.getRemoteService().getPlayMode();
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
        return PlayMode.UN_KNOW;
    }

    @Override
    public boolean isPlaying() {
        if (checkRemoteService()) {
            try {
                return mServiceSession.getRemoteService().isPlaying();
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
        return false;
    }

    @Override
    public void toggle() {
        if (checkRemoteService()) {
            try {
                mServiceSession.getRemoteService().toggle();
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
    }

    @Override
    public void next() {
        if (checkRemoteService()) {
            try {
                mServiceSession.getRemoteService().next();
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
    }

    @Override
    public void previous() {
        if (checkRemoteService()) {
            try {
                mServiceSession.getRemoteService().previous();
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
    }

    @Override
    public void skipTo(int index) {
        if (checkRemoteService()) {
            try {
                mServiceSession.getRemoteService().skipTo(index);
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
    }

    @Override
    public void stop() {
        if (checkRemoteService()) {
            try {
                mServiceSession.getRemoteService().stop();
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
    }

    @Override
    public void seekTo(long ms) {
        if (checkRemoteService()) {
            try {
                mServiceSession.getRemoteService().seekTo(ms);
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
    }

    @Override
    public long getCurrentPosition() {
        if (checkRemoteService()) {
            try {
                return mServiceSession.getRemoteService().getCurrentPosition();
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
        return 0;
    }

    @Override
    public long getDuration() {
        if (checkRemoteService()) {
            try {
                return mServiceSession.getRemoteService().getDuration();
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
        return 0;
    }

    @Nullable
    @Override
    public SongInfo getCurrentPlayable() {
        if (checkRemoteService()) {
            try {
                return mServiceSession.getRemoteService().getCurrentPlayable();
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
        return null;
    }

    @Override
    public void addPlayListener(IPlayerListener listener) {
        if (listener == null) {
            return;
        }
        if (mPlayerListeners.contains(listener)) {
            SAMLog.i(TAG, "addPlayListener: 监听已经存在");
            return;
        }
        mPlayerListeners.add(listener);
    }

    @Override
    public void removeListener(IPlayerListener listener) {
        if (listener == null) {
            return;
        }
        mPlayerListeners.remove(listener);
    }

    @Override
    public void timer(TimerConfig timerConfig) {
        if (checkRemoteService()) {
            try {
                mServiceSession.getRemoteService().timer(timerConfig);
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
    }

    @Nullable
    @Override
    public TimerConfig getTimerConfig() {
        if (checkRemoteService()) {
            try {
                return mServiceSession.getRemoteService().getTimerConfig();
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
        return null;
    }

    @Override
    public void cancelTimer() {
        if (checkRemoteService()) {
            try {
                mServiceSession.getRemoteService().cancelTimer();
            } catch (Exception e) {
                SAMLog.printCause(e);
            }
        }
    }

    /**
     * 远程播放的监听会回调到这里 然后转发给ui进程的其他监听者
     *
     * @return
     */
    private ISAMPlayerCallBack getPlayCallback() {
        return new ISAMPlayerCallBack.Stub() {
            @Override
            public void onPlayableStart(SongInfo songinfo) throws RemoteException {
                mHandler.obtainMessage(MSG_PLAYABLE_START, songinfo).sendToTarget();
            }

            @Override
            public void onProgressChange(SongInfo info, long second, long duration) throws RemoteException {
                Message message = mHandler.obtainMessage(MSG_PROGRESS_CHANGE, info);
                Bundle bundle = new Bundle();
                bundle.putLong("second", second);
                bundle.putLong("duration", duration);
                message.setData(bundle);
                message.sendToTarget();
            }

            @Override
            public void onBufferProgress(int percent) throws RemoteException {
                mHandler.obtainMessage(MSG_BUFFER_PROGRESS, percent).sendToTarget();
            }

            @Override
            public void onInBuffer(boolean inBuffer) throws RemoteException {
                mHandler.obtainMessage(MSG_IN_BUFFER, inBuffer).sendToTarget();
            }

            @Override
            public void onComplete() throws RemoteException {
                mHandler.sendEmptyMessage(MSG_COMPLETE);
            }

            @Override
            public void onStart() throws RemoteException {
                mHandler.sendEmptyMessage(MSG_START);
            }

            @Override
            public void onPause() throws RemoteException {
                mHandler.sendEmptyMessage(MSG_PAUSE);
            }

            @Override
            public void onStop() throws RemoteException {
                mHandler.sendEmptyMessage(MSG_STOP);
            }

            @Override
            public void inInterceptorProcess(SongInfo info) throws RemoteException {
                mHandler.obtainMessage(MSG_INTERCEPTOR_PROCESS, info).sendToTarget();
            }

            @Override
            public void onError(int what, int extra) throws RemoteException {
                mHandler.obtainMessage(MSG_ERROR, what, extra).sendToTarget();
            }
        };
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mServiceSession == null || !mServiceSession.isConnect()) {
                return;
            }
            switch (msg.what) {
                case MSG_PLAYABLE_START:
                    handlerPlayableStartEvent((SongInfo) msg.obj);
                    break;
                case MSG_PROGRESS_CHANGE:
                    Bundle data = msg.getData();
                    long second = data.getLong("second");
                    long duration = data.getLong("duration");
                    if (!(msg.obj instanceof SongInfo)) {
                        handlerProgressChangeEvent(null, second, duration);
                    } else {
                        handlerProgressChangeEvent((SongInfo) msg.obj, second, duration);
                    }
                    break;
                case MSG_BUFFER_PROGRESS:
                    handlerBufferProgressEvent((Integer) msg.obj);
                    break;
                case MSG_IN_BUFFER:
                    handlerInBufferEvent((Boolean) msg.obj);
                    break;
                case MSG_COMPLETE:
                    handlerCompleteEvent();
                    break;
                case MSG_START:
                    handlerStartEvent();
                    break;
                case MSG_PAUSE:
                    handlerPauseEvent();
                    break;
                case MSG_STOP:
                    handlerStopEvent();
                    break;
                case MSG_ERROR:
                    handlerErrorEvent(msg.arg1, msg.arg2);
                    break;
                case MSG_INTERCEPTOR_PROCESS:
                    handlerInterceptorProcess((SongInfo) msg.obj);
                    break;
            }
        }
    };

    private void handlerInterceptorProcess(SongInfo obj) {
        for (IPlayerListener item : mPlayerListeners) {
            item.inInterceptorProcess(obj);
        }
    }

    private void handlerErrorEvent(int what, int extra) {
        for (IPlayerListener item : mPlayerListeners) {
            item.onError(what, extra);
        }
    }

    private void handlerStopEvent() {
        for (IPlayerListener item : mPlayerListeners) {
            item.onStop();
        }
    }

    private void handlerPauseEvent() {
        for (IPlayerListener item : mPlayerListeners) {
            item.onPause();
        }
    }

    private void handlerStartEvent() {
        for (IPlayerListener item : mPlayerListeners) {
            item.onStart();
        }
    }

    private void handlerCompleteEvent() {
        for (IPlayerListener item : mPlayerListeners) {
            item.onComplete();
        }
    }

    private void handlerInBufferEvent(boolean inBuffer) {
        for (IPlayerListener item : mPlayerListeners) {
            item.onInBuffer(inBuffer);
        }
    }

    private void handlerBufferProgressEvent(int percent) {
        for (IPlayerListener item : mPlayerListeners) {
            item.onBufferProgress(percent);
        }
    }

    private void handlerProgressChangeEvent(SongInfo info, long second, long duration) {
        for (IPlayerListener item : mPlayerListeners) {
            item.onProgressChange(info, second, duration);
        }
    }

    private void handlerPlayableStartEvent(SongInfo songinfo) {
        for (IPlayerListener item : mPlayerListeners) {
            item.onPlayableStart(songinfo);
        }
    }

    @Override
    public void release() {
        mPlayerListeners.clear();
    }
}
