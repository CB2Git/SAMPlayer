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
import com.samplayer.model.OutConfigInfo;
import com.samplayer.model.PlayMode;
import com.samplayer.model.SongInfo;
import com.samplayer.outconfig.NotificationConfig;
import com.samplayer.utils.SAMLog;

import java.util.List;

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
    private IPlayManager mIPlayManager;

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
     * 客户端发过来的请求主要在{@link ClientPlayerCmdProxy}中处理
     */
    private ClientPlayerCmdProxy mClientPlayerCmdProxy;

    /**
     * 外部通过反射注入的一些配置
     */
    private OutConfigInfo mOutConfigInfo = new OutConfigInfo();
    ;

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
        mIPlayManager = PlayerFactory.create(this);
        //播放器监听
        mIPlayManager.setPlayListener(getOnPlayListener());

        //播放命令监听
        CmdHandlerHelper.init(getCmdHandler());
        //耳机拔出监听
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(mAudioStreamReceiver, intentFilter);

        //通过反射获取配置
        mOutConfigInfo.inject(this);

        if (mOutConfigInfo.getInterceptorConfig() != null) {
            //拦截器
            mIPlayManager.setInterceptor(mOutConfigInfo.getInterceptorConfig());
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
        mHandler.removeCallbacksAndMessages(null);
        //停止播放
        stop();
        //释放播放器
        mIPlayManager.release();
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
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            IMediaPlayer currentPlayer = mIPlayManager.getCurrentPlayer();
            SongInfo currentPlayInfo = mIPlayManager.getCurrentPlayInfo();
            long duration = currentPlayer.getDuration();
            long currentPosition = currentPlayer.getCurrentPosition();
            notifyProgressChange(currentPlayInfo, currentPosition, duration);
            mHandler.removeMessages(MSG_TIME_UPDATE);
            mHandler.sendEmptyMessageDelayed(MSG_TIME_UPDATE, 900);
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
            @Override
            public void onPlayableStart(SongInfo songInfo) {
                notifyPlayableStart(songInfo);
                notifyNotificationPlayableChange(songInfo);
            }

            @Override
            public void onStart() {
                notifyStart();
                notifyNotificationPlay();
                mHandler.sendEmptyMessageDelayed(MSG_TIME_UPDATE, 900);
            }

            @Override
            public void onBufferStart() {
                notifyBufferStart();
            }

            @Override
            public void inInterceptorProcess() {
                notifyInterceptorProcess();
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
                mHandler.removeMessages(MSG_TIME_UPDATE);
                next();
            }

            @Override
            public void onError(int what, int extra) {
                notifyError(what, extra);
                mHandler.removeMessages(MSG_TIME_UPDATE);
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
        IMediaPlayer currentPlayer = mIPlayManager.getCurrentPlayer();
        if (currentPlayer.isPlaying()) {
            currentPlayer.pause();
            //TODO 这里在暂停的时候是否继续回调需要考虑
            mHandler.removeMessages(MSG_TIME_UPDATE);
            notifyPause();
            notifyNotificationPause();
        }
    }

    /**
     * 继续播放
     */
    private void start() {
        IMediaPlayer currentPlayer = mIPlayManager.getCurrentPlayer();
        if (!currentPlayer.isPlaying()) {
            currentPlayer.start();
            mHandler.sendEmptyMessageDelayed(MSG_TIME_UPDATE, 900);
            notifyStart();
            notifyNotificationPlay();
        }
    }


    public void setPlayList(List<SongInfo> songInfos, boolean autoPlay) throws RemoteException {
        mPlayQueueManager.setPlayList(songInfos);
        if (autoPlay) {
            play();
        }
    }


    public void appendPlayList(List<SongInfo> songInfos) {
        mPlayQueueManager.appendPlayList(songInfos);
    }


    public void clearPlayList() throws RemoteException {
        mPlayQueueManager.clearPlayList();
        stop();
    }


    public void removeAt(int index) {
        //TODO
        mPlayQueueManager.removeAt(index);
    }


    public void removeItem(SongInfo songInfo) {
        mPlayQueueManager.removeItem(songInfo);
        //TODO
    }

    /**
     * 当正在播放此歌曲的时候不做任何处理  没有播放则会开始播放
     */
    public void play() {
        SongInfo current = mPlayQueueManager.getCurrent();
        if (current == null) {
            SAMLog.e(TAG, "play: 播放失败  歌曲信息为null");
        } else {
            mIPlayManager.play(current, false);
        }
    }


    public void playItem(SongInfo songInfo) {
        int i = mPlayQueueManager.skipToItem(songInfo);
        SAMLog.i(TAG, "playItem: " + i);
        play();
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
        return mIPlayManager.getCurrentPlayer().isPlaying();
    }


    public void stop() {
        mIPlayManager.stop();
        mHandler.removeCallbacksAndMessages(null);
        stopForeground(true);
        notifyStop();
    }


    public void seekTo(long ms) {
        IMediaPlayer currentPlayer = mIPlayManager.getCurrentPlayer();
        currentPlayer.seekTo(ms);
    }


    public long getCurrentPosition() throws RemoteException {
        IMediaPlayer currentPlayer = mIPlayManager.getCurrentPlayer();
        long currentPosition = currentPlayer.getCurrentPosition();
        return currentPosition;
    }


    public long getDuration() throws RemoteException {
        IMediaPlayer currentPlayer = mIPlayManager.getCurrentPlayer();
        long duration = currentPlayer.getDuration();
        return duration;
    }


    public SongInfo getCurrentPlayable() throws RemoteException {
        return mIPlayManager.getCurrentPlayInfo();
    }


    public void toggle() {
        IMediaPlayer currentPlayer = mIPlayManager.getCurrentPlayer();
        if (currentPlayer.isPlaying()) {
            pause();
        } else {
            start();
        }
    }


    public void next() {
        SongInfo songInfo = mPlayQueueManager.next();
        if (songInfo != null) {
            mIPlayManager.play(songInfo);
        }
    }


    public void previous() {
        SongInfo songInfo = mPlayQueueManager.previous();
        if (songInfo != null) {
            mIPlayManager.play(songInfo);
        }
    }


    public void skipTo(int index) {
        SongInfo songInfo = mPlayQueueManager.skipTo(index);
        mIPlayManager.play(songInfo);
    }


    public void setPlayCallback(ISAMPlayerCallBack callback) throws RemoteException {
        mPlayerCallBack = callback;
        mPlayerCallBack.asBinder().linkToDeath(getDeathRecipient(), 0);
        SAMLog.i(TAG, "setPlayCallback: listener+1");
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
    }

    private void notifyError(int what, int extra) {
        if (mPlayerCallBack != null) {
            try {
                mPlayerCallBack.onError(what, extra);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    }

    private void notifyPlayableStart(SongInfo songInfo) {
        if (mPlayerCallBack != null) {
            try {
                mPlayerCallBack.onPlayableStart(songInfo);
                mHandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    }

    private void notifyBufferEnd() {
        if (mPlayerCallBack != null) {
            try {
                mPlayerCallBack.onInBuffer(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    }

    private void notifyPause() {
        if (mPlayerCallBack != null) {
            try {
                mPlayerCallBack.onPause();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    }

    private void notifyStop() {
        if (mPlayerCallBack != null) {
            try {
                mPlayerCallBack.onStop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyInterceptorProcess() {
        if (mPlayerCallBack != null) {
            try {
                mPlayerCallBack.inInterceptorProcess();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
