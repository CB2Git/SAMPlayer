package com.samplayer.core.remote.player.base;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.samplayer.core.remote.player.AudioFocusManager;
import com.samplayer.core.remote.player.interceptor.InterceptorCallback;
import com.samplayer.core.remote.player.interceptor.InterceptorCallbackWrapper;
import com.samplayer.model.SongInfo;
import com.samplayer.outconfig.InterceptorConfig;
import com.samplayer.utils.SAMLog;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public abstract class AbstractPlayManager implements IPlayManager, IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener {

    private static final String TAG = "AbstractPlayManager";

    private static final int MSG_REAL_PLAY = 1000;

    /**
     * 拦截器  用与在播放之前允许用户去重设播放地址
     */
    private InterceptorConfig mInterceptorConfig;

    /**
     * 播放器
     */
    private IMediaPlayer mMediaPlayer;

    /**
     * 播放器对外的回调
     */
    private PlayListener mPlayListener;

    /**
     * 音频焦点管理
     */
    private AudioFocusManager mAudioFocusManager;

    /**
     * 当前播放的歌曲  如果当前没有播放 可能不准确  比如当播放失败以后算正在播放？
     */
    private SongInfo mSongInfo;

    /**
     * 拦截器回调的包装
     */
    private InterceptorCallbackWrapper mCallbackWrapper;

    /**
     * 将播放线程切换到主线程  这么做的原因是EXO播放器的setDataSource必须在主线程调用
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_REAL_PLAY) {
                realPlay((SongInfo) msg.obj);
            }
        }
    };

    public AbstractPlayManager(Context context) {
        mAudioFocusManager = new AudioFocusManager(context);
    }

    private void initMediaPlayer() {
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    public abstract IMediaPlayer createMediaPlayer();

    @Override
    public void play(SongInfo songInfo) {
        if (songInfo == null) {
            return;
        }
        play(songInfo, true);
    }

    @Override
    public void play(SongInfo songInfo, boolean allowReplay) {
        if (songInfo == null) {
            return;
        }

        IMediaPlayer currentPlayer = getCurrentPlayer();
        if (songInfo.equals(getCurrentPlayInfo())) {
            SAMLog.w(TAG, "play: 当前正在播放这首歌,allowReplay = " + allowReplay);
            if (!allowReplay) {
                if (!currentPlayer.isPlaying()) {
                    currentPlayer.start();
                    if (mPlayListener != null) {
                        mPlayListener.onStart();
                    }
                }
                return;
            }
        }

        if (mInterceptorConfig != null) {
            if (mCallbackWrapper != null) {
                mCallbackWrapper.invalid();
            }
            mCallbackWrapper = new InterceptorCallbackWrapper(new InterceptorCallback() {
                @Override
                public void onContinue(SongInfo info) {
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.obtainMessage(MSG_REAL_PLAY, info).sendToTarget();
                }

                @Override
                public void onError(int errorCode) {
                    stop();
                    if (mPlayListener != null) {
                        mPlayListener.onError(errorCode, errorCode);
                    }
                }

                @Override
                public void inProcess() {
                    if (mPlayListener != null) {
                        mPlayListener.inInterceptorProcess();
                    }
                }
            });
            mInterceptorConfig.action(songInfo, mCallbackWrapper);
        } else {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.obtainMessage(MSG_REAL_PLAY, songInfo).sendToTarget();
        }
    }

    private void realPlay(SongInfo info) {
        IMediaPlayer newPlayer = getNewPlayer();
        try {
            //注意 如果使用exo播放器 这里必须在主线程中初始化
            newPlayer.setDataSource(info.getSongUrl());
            newPlayer.prepareAsync();
            mSongInfo = info;
            if (mPlayListener != null) {
                mPlayListener.onPlayableStart(info);
            }
        } catch (Exception e) {
            SAMLog.printCause(e);
        }
    }

    @Override
    public void setInterceptor(InterceptorConfig interceptor) {
        mInterceptorConfig = interceptor;
    }

    @Override
    public void stop() {
        IMediaPlayer currentPlayer = getCurrentPlayer();
        currentPlayer.stop();
        currentPlayer.release();
        mMediaPlayer = null;
        mSongInfo = null;
        mAudioFocusManager.abandonFocus();
    }

    @Nullable
    @Override
    public SongInfo getCurrentPlayInfo() {
        return mSongInfo;
    }

    @Override
    public IMediaPlayer getCurrentPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = createMediaPlayer();
            initMediaPlayer();
            mSongInfo = null;
        }
        return mMediaPlayer;
    }

    private IMediaPlayer getNewPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mSongInfo = null;
        }
        //initMediaPlayer();
        return mMediaPlayer;
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
        }
        mPlayListener = null;
    }

    @Override
    public void setPlayListener(PlayListener listener) {
        mPlayListener = listener;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        //播放之前请求音频焦点
        boolean requestFocus = mAudioFocusManager.requestFocus();
        SAMLog.ifmt(TAG, "onPrepared,requestFocus = %s", requestFocus);
        iMediaPlayer.start();
        if (mPlayListener != null) {
            mPlayListener.onStart();
        }
    }


    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
        SAMLog.i(TAG, "onBufferingUpdate: " + percent);
        if (mPlayListener != null) {
            if (percent > 95) {
                //ijk播放器不保证这里能到100 测试发现直到99
                percent = 100;
            }
            mPlayListener.onBufferingUpdate(percent);
        }
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
        SAMLog.ifmt(TAG, "onInfo = (%d,%d)", what, extra);
        if (mPlayListener == null) {
            return false;
        }
        if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
            mPlayListener.onBufferStart();
        } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            mPlayListener.onBufferEnd();
        } else {
            mPlayListener.onInfo(what, extra);
        }
        return false;
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
        SAMLog.efmt(TAG, "onError(%d,%d)", what, extra);
        if (mPlayListener != null) {
            mPlayListener.onError(what, extra);
        }
        mSongInfo = null;
        return true;
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        SAMLog.i(TAG, "onCompletion");
        if (mPlayListener != null) {
            mPlayListener.onComplete();
        }
    }
}
