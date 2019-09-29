package com.samplayer.core.remote.player.base;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.samplayer.core.remote.player.cmd.AudioFocusManager;
import com.samplayer.interceptor.Interceptor;
import com.samplayer.interceptor.InterceptorCallback;
import com.samplayer.interceptor.InterceptorCallbackWrapper;
import com.samplayer.model.SongInfo;
import com.samplayer.utils.SAMLog;

import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public abstract class AbstractPlayManager implements IPlayManager, IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener {

    private static final String TAG = "AbstractPlayManager";

    private static final int MSG_REAL_PLAY = 1000;

    /**
     * 拦截器  用与在播放之前允许用户去重设播放地址
     */
    private List<Interceptor> mInterceptors;

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
        mAudioFocusManager = AudioFocusManager.getInstance();
    }

    private void initMediaPlayer() {
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    protected abstract IMediaPlayer createMediaPlayer();

    @Override
    public void play(SongInfo songInfo) {
        if (songInfo == null) {
            SAMLog.w(TAG, "播放信息为null");
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

        if (mInterceptors != null && mInterceptors.size() > 0) {
            if (mCallbackWrapper != null) {
                mCallbackWrapper.invalid();
            }
            mCallbackWrapper = new InterceptorCallbackWrapper(songInfo, mInterceptors, new InterceptorCallback() {
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
                public void inProcess(SongInfo info) {
                    if (mPlayListener != null) {
                        mPlayListener.inInterceptorProcess(info);
                    }
                    //因为拦截器中可以进行耗时操作
                    //所以当拦截器开始处理以后停止当前播放然后将新的歌曲信息回调回去
                    getNewPlayer();
                }
            });
            mCallbackWrapper.interceptor();
        } else {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.obtainMessage(MSG_REAL_PLAY, songInfo).sendToTarget();
        }
    }

    private void realPlay(SongInfo info) {
        IMediaPlayer newPlayer = getNewPlayer();
        try {
            //如果拦截以后的url不为空 则使用被拦截器修改以后的url
            if (!TextUtils.isEmpty(info.getSongInterceptorUrl())) {
                //注意 如果使用exo播放器 这里必须在主线程中初始化
                newPlayer.setDataSource(info.getSongInterceptorUrl());
            } else {
                //注意 如果使用exo播放器 这里必须在主线程中初始化
                newPlayer.setDataSource(info.getSongUrl());
            }
            newPlayer.prepareAsync();
            mSongInfo = info;
            if (mPlayListener != null) {
                mPlayListener.onPrepareStart(info);
            }
        } catch (Exception e) {
            SAMLog.printCause(e);
        }
    }

    @Override
    public void setInterceptor(List<Interceptor> interceptors) {
        mInterceptors = interceptors;
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

    /**
     * 获取一个新的播放器 会停止当前正在播放的并清空当前播放信息
     */
    private IMediaPlayer getNewPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mSongInfo = null;
        }
        initMediaPlayer();
        return mMediaPlayer;
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
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

        //回调异步结束
        mPlayListener.onPrepare(iMediaPlayer);

        iMediaPlayer.start();
        if (mPlayListener != null && getCurrentPlayInfo() != null) {
            mPlayListener.onPlayableStart(getCurrentPlayInfo());
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
        //这里返回flase的原因是MediaPlayer以及ijk返回true均不会继续回调onComplete
        //但是exo在ijk中的实现会继续回调onComplete，所以这里将行为统一 返回false
        return false;
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        SAMLog.i(TAG, "onCompletion");
        if (mPlayListener != null) {
            mPlayListener.onComplete();
        }
    }
}
