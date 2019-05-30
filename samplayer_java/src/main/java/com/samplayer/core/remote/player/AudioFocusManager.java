package com.samplayer.core.remote.player;

import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;

import com.samplayer.core.remote.player.cmd.CmdHandlerHelper;
import com.samplayer.core.remote.player.cmd.MediaButtonReceiver;
import com.samplayer.utils.SAMLog;

/**
 * 管理播放器的音频焦点
 *
 * @url https://github.com/kesenhoo/android-training-course-in-chinese/blob/master/multimedia/audio/audio-focus.md
 */
public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "AudioFocusManager";

    private Context mContext;

    private AudioManager mAudioManager;

    public AudioFocusManager(Context context) {
        mContext = context;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 请求音频焦点
     *
     * @return true 请求到了音频焦点
     */
    public boolean requestFocus() {
        int result = mAudioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mAudioManager.registerMediaButtonEventReceiver(new ComponentName(mContext, MediaButtonReceiver.class));
            return true;
        }
        return false;
    }

    /**
     * 放弃音频焦点
     */
    public void abandonFocus() {
        mAudioManager.abandonAudioFocus(this);
        mAudioManager.unregisterMediaButtonEventReceiver(new ComponentName(mContext, MediaButtonReceiver.class));
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        SAMLog.ifmt(TAG, "onAudioFocusChange = %d", focusChange);

        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            // Pause playback
            CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_PAUSE);
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // Resume playback
            CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_PLAY);
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            // Stop playback
            CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_STOP);
            abandonFocus();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            // Lower the volume
            //TODO
        }
    }
}
