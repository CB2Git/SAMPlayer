package com.samplayer.core.remote.player.cmd;

import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;

import com.samplayer.SAMConfig;
import com.samplayer.utils.SAMLog;

/**
 * 管理播放器的音频焦点
 * <p>
 * https://github.com/kesenhoo/android-training-course-in-chinese/blob/master/multimedia/audio/audio-focus.md
 */
public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "AudioFocusManager";

    private Context mContext;

    private AudioManager mAudioManager;

    private static AudioFocusManager sAudioFocusManager;

    private OnAudioFocusChangeListener mOnAudioFocusChangeListener;

    public static AudioFocusManager getInstance() {
        if (sAudioFocusManager == null) {
            synchronized (AudioFocusManager.class) {
                if (sAudioFocusManager == null) {
                    sAudioFocusManager = new AudioFocusManager(SAMConfig.getAppContext());
                }
            }
        }
        return sAudioFocusManager;
    }


    public AudioFocusManager(Context context) {
        mContext = context;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public void setOnAudioFocusChangeListener(OnAudioFocusChangeListener onAudioFocusChangeListener) {
        mOnAudioFocusChangeListener = onAudioFocusChangeListener;
    }

    /**
     * 请求音频焦点
     *
     * @return true 请求到了音频焦点
     */
    public boolean requestFocus() {
        int result = mAudioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mAudioManager.registerMediaButtonEventReceiver(new ComponentName(mContext, MediaButtonReceiver.class));
            SAMLog.i(TAG, "requestFocus: true!!!");
            return true;
        }
        SAMLog.i(TAG, "requestFocus: false!!!");
        return false;
    }

    /**
     * 放弃音频焦点
     */
    public void abandonFocus() {
        mAudioManager.abandonAudioFocus(this);
        mAudioManager.unregisterMediaButtonEventReceiver(new ComponentName(mContext, MediaButtonReceiver.class));
        SAMLog.i(TAG, "abandonFocus ");
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        //-2 暂时丢失
        //-1 永久丢失
        // 1 得到了焦点
        SAMLog.ifmt(TAG, "onAudioFocusChange = %d", focusChange);

        if (mOnAudioFocusChangeListener != null) {
            mOnAudioFocusChangeListener.onAudioFocusChange(focusChange);
        }

//        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
//            // Pause playback
//            CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_PAUSE);
//        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
//            // Resume playback
//            CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_PLAY);
//        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
//            // 当丢失音频焦点，不停止播放，因为会丢失当前的播放数据 ，暂停就行
//            CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_PAUSE);
//            abandonFocus();
//        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
//            // Lower the volume
//            //TODO
//        }
    }

    /**
     * 音频焦点变化的回调
     */
    public interface OnAudioFocusChangeListener {

        /**
         * 音频焦点变化
         *
         * @param focusStatue <ul>
         *                    <li>暂时被打断丢失焦点{@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT}</li>
         *                    <li>重新获取了焦点{@link AudioManager#AUDIOFOCUS_GAIN}</li>
         *                    <li>永久丢失了焦点{@link AudioManager#AUDIOFOCUS_LOSS}</li>
         *                    <li>需要降低音量{@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK}</li>
         *                    </ul>
         */
        void onAudioFocusChange(int focusStatue);
    }
}
