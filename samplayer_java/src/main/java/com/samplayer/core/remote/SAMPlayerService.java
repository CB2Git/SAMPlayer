package com.samplayer.core.remote;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.samplayer.aidl.ISAMPlayerCallBack;
import com.samplayer.core.manager.PlayQueueManager;
import com.samplayer.core.manager.base.ICirculationMode;
import com.samplayer.core.manager.circulation.OrderCirculationMode;
import com.samplayer.core.manager.circulation.ShuffleCirculationMode;
import com.samplayer.core.manager.circulation.SingleCirculationMode;
import com.samplayer.core.remote.player.PlayerFactory;
import com.samplayer.core.remote.player.base.IPlayManager;
import com.samplayer.core.remote.player.cmd.CmdHandlerHelper;
import com.samplayer.core.remote.player.cmd.TimerHandler;
import com.samplayer.listener.IPlayerListener;
import com.samplayer.model.OutConfigInfo;
import com.samplayer.model.PlayMode;
import com.samplayer.model.SongInfo;
import com.samplayer.outconfig.NotificationConfig;
import com.samplayer.outconfig.TimerConfig;
import com.samplayer.utils.SAMLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Remote Service 播放器的主要载体
 * <p>
 * {@link com.samplayer.core.manager.base.IPlayer} 负责音乐播放以及回调
 * {@link com.samplayer.core.manager.base.IPlayQueue} 负责播放列表管理
 *
 * @see PlayerFactory
 * @see PlayQueueManager
 */
public class SAMPlayerService extends Service {

    private static final String TAG = "SAMPlayerService";

    public static final int MSG_TIME_UPDATE = 10001;

    /**
     * 播放器管理类
     */
    private IPlayManager mPlayerManager;

    /**
     * 播放队列管理
     */
    private PlayQueueManager mPlayQueueManager;

    /**
     * 播放器对client的回调接口
     */
    private ISAMPlayerCallBack mPlayerCallBack;

    /**
     * 客户端崩溃回调
     */
    private IBinder.DeathRecipient mDeathRecipient;

    /**
     * 当耳机断开连接的时候的广播 https://github.com/kesenhoo/android-training-course-in-chinese/blob/master/multimedia/audio/audio-output.md
     */
    private NoisyAudioStreamReceiver mAudioStreamReceiver = new NoisyAudioStreamReceiver();

    /**
     * 客户端发过来的请求主要在{@link ClientPlayerCmdProxy}中回调回来
     */
    private ClientPlayerCmdProxy mClientPlayerCmdProxy;

    /**
     * 外部通过反射注入的一些配置
     */
    private OutConfigInfo mOutConfigInfo = new OutConfigInfo();

    /**
     * 定时停止播放的配置
     */
    private TimerHandler mTimerHandler;

    /**
     * 当定时播放时间到了以后需要停止
     */
    private boolean isShouldStopPlay = false;

    /**
     * 用与保存开始播放的时候需要seek到指定位置
     */
    private Map<String, Long> mSongSeekToQueue = new HashMap<>();

    @Override
    public IBinder onBind(Intent intent) {
        return mClientPlayerCmdProxy;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        SAMLog.i(TAG, "SAMPlayerService  onCreate");
    }

    private void init() {
        //回调代理
        mClientPlayerCmdProxy = new ClientPlayerCmdProxy(this);
        //播放队列
        mPlayQueueManager = new PlayQueueManager();
        //播放器
        mPlayerManager = PlayerFactory.create(this);
        //播放器监听
        mPlayerManager.setPlayListener(getOnPlayListener());

        //播放命令监听
        CmdHandlerHelper.init(getCmdHandler());
        //耳机拔出监听
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(mAudioStreamReceiver, intentFilter);

        //获取配置信息
        mOutConfigInfo.inject(this);

        if (mOutConfigInfo.getInterceptors() != null) {
            //拦截器
            mPlayerManager.setInterceptor(mOutConfigInfo.getInterceptors());
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //断开远程监听
        if (mPlayerCallBack != null) {
            mPlayerCallBack.asBinder().unlinkToDeath(getDeathRecipient(), 0);
        }
        //断开耳机拔出广播
        unregisterReceiver(mAudioStreamReceiver);
        //停止刷新时间
        mUpdateProgressHandler.removeCallbacksAndMessages(null);
        //停止播放
        stop();
        //释放播放器
        mPlayerManager.release();
        //通知栏释放
        NotificationConfig notificationConfig = mOutConfigInfo.getNotificationConfig();
        if (notificationConfig != null) {
            notificationConfig.release();
        }
        stopForeground(true);
        SAMLog.i(TAG, "SAMPlayerService  onDestroy");
    }


    /**
     * 刷新播放时长的
     */
    private Handler mUpdateProgressHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            IMediaPlayer currentPlayer = mPlayerManager.getCurrentPlayer();
            SongInfo currentPlayInfo = mPlayerManager.getCurrentPlayInfo();
            long duration = currentPlayer.getDuration();
            long currentPosition = currentPlayer.getCurrentPosition();
            notifyProgressChange(currentPlayInfo, currentPosition, duration);
            mUpdateProgressHandler.removeMessages(MSG_TIME_UPDATE);
            mUpdateProgressHandler.sendEmptyMessageDelayed(MSG_TIME_UPDATE, 950);
        }
    };


    /**
     * 来自线控的监听或者其他地方发过来的命令
     */
    private Handler getCmdHandler() {
        return new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case CmdHandlerHelper.CMD_PLAY:
                        start();
                        break;
                    case CmdHandlerHelper.CMD_PAUSE:
                        pause();
                        break;
                    case CmdHandlerHelper.CMD_NEXT:
                        next();
                        break;
                    case CmdHandlerHelper.CMD_PREVIOUS:
                        previous();
                        break;
                    case CmdHandlerHelper.CMD_TOGGLE:
                        toggle();
                        break;
                    case CmdHandlerHelper.CMD_STOP:
                        stop();
                        break;
                    case CmdHandlerHelper.CMD_UP_VOLUME:
                        //TODO
                        break;
                    case CmdHandlerHelper.CMD_DOWN_VOLUME:
                        //TODO
                        break;
                }
            }
        };
    }


    /**
     * 播放器的回调
     */
    private IPlayManager.PlayListener getOnPlayListener() {
        return new IPlayManager.PlayListener() {

            /**
             * 当前是否播放出错
             *
             * 因为exo播放器当onError回调以后会继续回调onComplete(ijk的实现有问题?)
             * 所以标记一下是否出错了，然后当回调onComplete的时候去决定是否继续播放下一曲
             */
            private boolean isInErrorState = false;

            @Override
            public void onPrepareStart(SongInfo songInfo) {
                //当播放器调用prepareAsync 但是还没prepare完毕的时候就去通知ui更新(用户体验？)
                onPlayableStart(songInfo);
            }

            @Override
            public void onPrepare(IMediaPlayer iMediaPlayer) {
                isInErrorState = false;
            }

            @Override
            public void onPlayableStart(SongInfo songInfo) {
                isInErrorState = false;
                notifyPlayableStart(songInfo);
                notifyNotificationPlayableChange(songInfo);
            }

            @Override
            public void onStart() {
                notifyStart();
                notifyNotificationPlay();
                mUpdateProgressHandler.sendEmptyMessageDelayed(MSG_TIME_UPDATE, 900);

                //当播放的时候发现需要seekTo到某个位置  则操作
                long seekMap = getSeekMap(mPlayerManager.getCurrentPlayInfo(), true);
                if (seekMap > 0) {
                    IMediaPlayer currentPlayer = mPlayerManager.getCurrentPlayer();
                    if (currentPlayer != null) {
                        currentPlayer.seekTo(seekMap);
                    }
                }
            }

            @Override
            public void onBufferStart() {
                notifyBufferStart();
            }

            @Override
            public void inInterceptorProcess(SongInfo info) {
                notifyInterceptorProcess(info);
            }


            @Override
            public void onBufferEnd() {
                notifyBufferEnd();
            }

            @Override
            public void onInfo(int what, int extra) {
                //no-op
            }

            @Override
            public void onBufferingUpdate(int percent) {
                notifyBufferingUpdate(percent);
            }


            @Override
            public void onComplete() {
                notifyComplete();
                mUpdateProgressHandler.removeMessages(MSG_TIME_UPDATE);

                //定时播放时间到了
                if (isShouldStopPlay) {
                    SAMLog.i(TAG, "onComplete: 定时播放时间完成,关闭");
                    stop();
                    return;
                }

                //没有发生错误
                if (!isInErrorState) {
                    next();
                }
            }

            @Override
            public void onError(int what, int extra) {
                //-38错误  调用播放器的方法但是播放器状态不对产生的错误
                if (what == -38) {
                    return;
                }
                isInErrorState = true;
                notifyError(what, extra);
                mUpdateProgressHandler.removeMessages(MSG_TIME_UPDATE);
            }
        };
    }

    private class NoisyAudioStreamReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                SAMLog.i(TAG, "NoisyAudioStreamReceiver~~~");
                CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_PAUSE);
            }
        }
    }

    /**
     * 暂停播放
     */
    private void pause() {
        IMediaPlayer currentPlayer = mPlayerManager.getCurrentPlayer();
        if (currentPlayer.isPlaying()) {
            currentPlayer.pause();
            //TODO 这里在暂停的时候是否继续回调需要考虑
            mUpdateProgressHandler.removeMessages(MSG_TIME_UPDATE);
            notifyPause();
            notifyNotificationPause();
        }
    }

    /**
     * 继续播放
     */
    private void start() {
        IMediaPlayer currentPlayer = mPlayerManager.getCurrentPlayer();
        //当没有播放任何音乐的时候不能开始
        if (mPlayerManager.getCurrentPlayInfo() != null && !currentPlayer.isPlaying()) {
            currentPlayer.start();
            mUpdateProgressHandler.sendEmptyMessageDelayed(MSG_TIME_UPDATE, 900);
            notifyStart();
            notifyNotificationPlay();
        }
    }


    public void setPlayList(List<SongInfo> songInfos, boolean autoPlay) throws RemoteException {
        SongInfo currentPlayInfo = mPlayerManager.getCurrentPlayInfo();
        if (currentPlayInfo != null) {
            stop();
        }

        mPlayQueueManager.setPlayList(songInfos);
        if (autoPlay) {
            play();
        }
    }


    public void appendPlayList(List<SongInfo> songInfos) {
        mPlayQueueManager.appendPlayList(songInfos);
    }


    public List<SongInfo> getPlayList() {
        return mPlayQueueManager.getPlayList();
    }

    public void clearPlayList() {
        mPlayQueueManager.clearPlayList();
        stop();
    }


    public boolean removeAt(int index) {
        SongInfo songInfo = mPlayQueueManager.removeAt(index);
        SongInfo currentPlayInfo = mPlayerManager.getCurrentPlayInfo();
        //如果正在播放的被删除了 停止播放
        if (currentPlayInfo != null && currentPlayInfo.equals(songInfo)) {
            stop();
            return true;
        }
        return songInfo != null;
    }

    public boolean removeItem(SongInfo songInfo) {
        SongInfo info = mPlayQueueManager.removeItem(songInfo);
        SongInfo currentPlayInfo = mPlayerManager.getCurrentPlayInfo();
        //如果正在播放的被删除了 停止播放
        if (currentPlayInfo != null && currentPlayInfo.equals(info)) {
            stop();
            return true;
        }
        return songInfo != null;
    }

    /**
     * 当正在播放此歌曲的时候不做任何处理  没有播放则会开始播放
     */
    public void play() {
        SongInfo current = mPlayQueueManager.getCurrent();
        if (current == null) {
            SAMLog.e(TAG, "play: 播放失败  歌曲信息为null");
        } else {
            mPlayerManager.play(current, false);
        }
    }

    public void playItem(SongInfo songInfo) {
        int i = mPlayQueueManager.skipToItem(songInfo);
        SAMLog.i(TAG, "playItem: " + i);
        putSeekMap(songInfo, 0);
        play();
    }

    public void playStartAt(SongInfo songInfo, long ms) {
        putSeekMap(songInfo, ms);
        int i = mPlayQueueManager.skipToItem(songInfo);
        SAMLog.i(TAG, "playStartAt: " + i + ",seekTo：" + ms);
        play();
    }

    private void putSeekMap(SongInfo songInfo, long ms) {
        if (songInfo == null) {
            return;
        }
        String key = String.format("%s#%s", songInfo.getSongId(), songInfo.getSongUrl());
        mSongSeekToQueue.put(key, ms);
    }

    private long getSeekMap(SongInfo songInfo, boolean autoDel) {
        if (songInfo == null) {
            return -1;
        }
        String key = String.format("%s#%s", songInfo.getSongId(), songInfo.getSongUrl());
        Long seekTo;
        if (autoDel) {
            seekTo = mSongSeekToQueue.remove(key);
        } else {
            seekTo = mSongSeekToQueue.get(key);
        }
        return seekTo == null ? 0 : seekTo;
    }

    public void setPlayMode(int playMode) {
        ICirculationMode circulationMode;
        switch (playMode) {
            case PlayMode.ORDER:
                circulationMode = new OrderCirculationMode();
                break;
            case PlayMode.SINGLE:
                circulationMode = new SingleCirculationMode();
                break;
            case PlayMode.SHUFFLE:
                circulationMode = new ShuffleCirculationMode();
                break;
            default:
                circulationMode = new OrderCirculationMode();
                SAMLog.wfmt(TAG, "play mode设置为默认的顺序播放 error mode = %d", playMode);
                break;
        }
        mPlayQueueManager.setCirculationMode(circulationMode);
    }


    public int getPlayMode() {
        return mPlayQueueManager.getCirculationMode().getID();
    }


    public boolean isPlaying() {
        return mPlayerManager.getCurrentPlayer().isPlaying();
    }


    public void stop() {
        if (mPlayerManager.getCurrentPlayer().isPlaying() || mPlayerManager.getCurrentPlayInfo() != null) {
            mPlayerManager.stop();
            notifyStop();
        }

        if (mTimerHandler != null) {
            mTimerHandler.endTimer();
            mTimerHandler = null;
        }

        mUpdateProgressHandler.removeCallbacksAndMessages(null);
        stopForeground(true);
    }


    public void seekTo(long ms) {
        IMediaPlayer currentPlayer = mPlayerManager.getCurrentPlayer();
        currentPlayer.seekTo(ms);
    }


    public long getCurrentPosition() {
        IMediaPlayer currentPlayer = mPlayerManager.getCurrentPlayer();
        long currentPosition = currentPlayer.getCurrentPosition();
        return currentPosition;
    }


    public long getDuration() {
        long currentPosition = getCurrentPosition();
        //这样做的原因是getCurrentPosition有效状态多一点，
        if (currentPosition == 0) {
            return 0;
        }
        IMediaPlayer currentPlayer = mPlayerManager.getCurrentPlayer();
        long duration = currentPlayer.getDuration();
        return duration;
    }


    public SongInfo getCurrentPlayable() throws RemoteException {
        return mPlayerManager.getCurrentPlayInfo();
    }


    public void toggle() {
        IMediaPlayer currentPlayer = mPlayerManager.getCurrentPlayer();
        if (currentPlayer.isPlaying()) {
            pause();
        } else {
            start();
        }
    }


    public void next() {
        SongInfo songInfo = mPlayQueueManager.next();
        if (songInfo != null) {
            mPlayerManager.play(songInfo);
        }
    }


    public void previous() {
        SongInfo songInfo = mPlayQueueManager.previous();
        if (songInfo != null) {
            mPlayerManager.play(songInfo);
        }
    }


    public void skipTo(int index) {
        SongInfo songInfo = mPlayQueueManager.skipTo(index);
        mPlayerManager.play(songInfo);
    }


    public void setPlayCallback(ISAMPlayerCallBack callback) throws RemoteException {
        mPlayerCallBack = callback;
        mPlayerCallBack.asBinder().linkToDeath(getDeathRecipient(), 0);
        SAMLog.i(TAG, "setPlayCallback: listener+1");
    }

    public void timer(TimerConfig timerConfig) {
        if (mTimerHandler != null) {
            mTimerHandler.endTimer();
        }
        mTimerHandler = new TimerHandler(timerConfig);
        mTimerHandler.setOnTimerListener(getOnTimerListener());
        mTimerHandler.startTimer();
    }

    public TimerConfig getTimerConfig() {
        if (mTimerHandler != null) {
            return mTimerHandler.getTimerConfig();
        }
        return null;
    }

    public void cancelTimer() {
        if (mTimerHandler != null) {
            mTimerHandler.endTimer();
            mTimerHandler = null;
        }
    }

    private TimerHandler.OnTimerListener getOnTimerListener() {
        return new TimerHandler.OnTimerListener() {
            @Override
            public void onTimerStart(TimerConfig config) {
                SAMLog.i(TAG, "onTimerStart = " + config.getSecond() + ",mode = " + config.getMode());
                Intent intent = new Intent(TimerConfig.ACTION_TIMER_START);
                sendBroadcast(intent);
            }

            @Override
            public void onTimerUpdate(TimerConfig config, long residueSecond) {
                SAMLog.i(TAG, "onTimerUpdate: " + residueSecond);
                isShouldStopPlay = false;
                Intent intent = new Intent(TimerConfig.ACTION_TIMER_UPDATE);
                intent.putExtra(TimerConfig.KEY_TIMER, residueSecond);
                sendBroadcast(intent);
            }

            @Override
            public void onTimerComplete(TimerConfig config) {
                SAMLog.i(TAG, "onTimerComplete");
                if (config.getMode() == TimerConfig.MODE_ABS) {
                    stop();
                } else {
                    isShouldStopPlay = true;
                }
                Intent intent = new Intent(TimerConfig.ACTION_TIMER_COMPLETE);
                sendBroadcast(intent);
            }

            @Override
            public void onTimerStop(TimerConfig config) {
                SAMLog.i(TAG, "onTimerStop");
                isShouldStopPlay = false;
                Intent intent = new Intent(TimerConfig.ACTION_TIMER_STOP);
                sendBroadcast(intent);
            }
        };
    }

    private IBinder.DeathRecipient getDeathRecipient() {
        if (mDeathRecipient == null) {
            mDeathRecipient = new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    mPlayerCallBack = null;
                    SAMLog.w(TAG, "客户端进程退出");
                }
            };
        }
        return mDeathRecipient;
    }


    private void notifyProgressChange(SongInfo info, long position, long duration) {
        try {
            if (duration < 0) {
                duration = 0;
            }
            //转换为秒
            mPlayerCallBack.onProgressChange(info, position / 1000, duration / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyOutProgressChange(info, position, duration);
    }

    private void notifyOutProgressChange(SongInfo info, long position, long duration) {
        if (duration < 0) {
            duration = 0;
        }
        if (mOutConfigInfo.getPlayerListener() != null) {
            mOutConfigInfo.getPlayerListener().onProgressChange(info, position / 1000, duration / 1000);
        }
    }

    private void notifyError(int what, int extra) {
        if (mPlayerCallBack != null) {
            try {
                mPlayerCallBack.onError(what, extra);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifyOutError(what, extra);
    }

    private void notifyOutError(int what, int extra) {
        IPlayerListener listener = mOutConfigInfo.getPlayerListener();
        if (listener != null) {
            listener.onError(what, extra);
        }
    }

    private void notifyComplete() {
        if (mPlayerCallBack != null) {
            try {
                mPlayerCallBack.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifyOutComplete();
    }

    private void notifyOutComplete() {
        IPlayerListener listener = mOutConfigInfo.getPlayerListener();
        if (listener != null) {
            listener.onComplete();
        }
    }

    private void notifyPlayableStart(SongInfo songInfo) {
        if (mPlayerCallBack != null) {
            try {
                mPlayerCallBack.onPlayableStart(songInfo);
                mUpdateProgressHandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifyOutPlayableStart(songInfo);
    }

    private void notifyOutPlayableStart(SongInfo songInfo) {
        IPlayerListener listener = mOutConfigInfo.getPlayerListener();
        if (listener != null) {
            listener.onPlayableStart(songInfo);
        }
    }

    private void notifyBufferStart() {
        if (mPlayerCallBack != null) {
            try {
                mPlayerCallBack.onInBuffer(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifyOutBufferStart();
    }

    private void notifyOutBufferStart() {
        IPlayerListener playerListener = mOutConfigInfo.getPlayerListener();
        if (playerListener != null) {
            playerListener.onInBuffer(true);
        }
    }

    private void notifyBufferEnd() {
        if (mPlayerCallBack != null) {
            try {
                mPlayerCallBack.onInBuffer(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifyOutBufferEnd();
    }

    private void notifyOutBufferEnd() {
        IPlayerListener playerListener = mOutConfigInfo.getPlayerListener();
        if (playerListener != null) {
            playerListener.onInBuffer(false);
        }
    }

    private void notifyBufferingUpdate(int percent) {
        if (mPlayerCallBack != null) {
            try {
                mPlayerCallBack.onBufferProgress(percent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifyOutBufferingUpdate(percent);
    }

    private void notifyOutBufferingUpdate(int percent) {
        if (mOutConfigInfo.getPlayerListener() != null) {
            mOutConfigInfo.getPlayerListener().onBufferProgress(percent);
        }
    }

    private void notifyPause() {
        if (mPlayerCallBack != null) {
            try {
                mPlayerCallBack.onPause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifyOutPause();
    }

    private void notifyOutPause() {
        IPlayerListener listener = mOutConfigInfo.getPlayerListener();
        if (listener != null) {
            listener.onPause();
        }
    }

    private void notifyStart() {
        if (mPlayerCallBack != null) {
            try {
                mPlayerCallBack.onStart();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifyOutStart();
    }

    private void notifyOutStart() {
        IPlayerListener listener = mOutConfigInfo.getPlayerListener();
        if (listener != null) {
            listener.onStart();
        }
    }

    private void notifyStop() {
        if (mPlayerCallBack != null) {
            try {
                mPlayerCallBack.onStop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifyOutStop();
    }

    private void notifyOutStop() {
        IPlayerListener listener = mOutConfigInfo.getPlayerListener();
        if (listener != null) {
            listener.onStop();
        }
    }

    private void notifyInterceptorProcess(SongInfo info) {
        if (mPlayerCallBack != null) {
            try {
                mPlayerCallBack.inInterceptorProcess(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifyOutInterceptorProcess(info);
    }

    private void notifyOutInterceptorProcess(SongInfo info) {
        IPlayerListener listener = mOutConfigInfo.getPlayerListener();
        if (listener != null) {
            listener.inInterceptorProcess(info);
        }
    }

    private void notifyNotificationPlayableChange(SongInfo info) {
        NotificationConfig config = mOutConfigInfo.getNotificationConfig();
        if (config == null) {
            return;
        }
        Notification notification = config.buildNotification(this, info);
        startForeground(NotificationConfig.NOTIFICATION_ID, notification);
    }

    private void notifyNotificationPlay() {
        NotificationConfig config = mOutConfigInfo.getNotificationConfig();
        if (config == null) {
            return;
        }
        config.onPlayStateChange(true);
    }

    private void notifyNotificationPause() {
        NotificationConfig config = mOutConfigInfo.getNotificationConfig();
        if (config == null) {
            return;
        }
        config.onPlayStateChange(false);
    }
}
