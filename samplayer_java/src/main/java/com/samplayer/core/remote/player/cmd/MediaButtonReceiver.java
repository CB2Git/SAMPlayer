package com.samplayer.core.remote.player.cmd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.samplayer.utils.SAMLog;

/**
 * 接收耳机线控
 */
public class MediaButtonReceiver extends BroadcastReceiver {

    private static final String TAG = "MediaButtonReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_MEDIA_BUTTON.equals(action)) {
            // 获得KeyEvent对象
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            // 获得按键码
            int keycode = event.getKeyCode();

            SAMLog.ifmt(TAG, "onReceive: keycode = %d,action=%d", keycode, event.getAction());

            //只响应按下时间
            if (event.getAction() == KeyEvent.ACTION_UP) {
                return;
            }

            switch (keycode) {
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    //播放下一首
                    CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_NEXT);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    //播放上一首
                    CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_PREVIOUS);
                    break;
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    //中间按钮,暂停or播放
                    CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_TOGGLE);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    //播放
                    CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_PLAY);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    //暂停
                    CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_PAUSE);
                    break;
                default:
                    break;
            }
        }
    }
}
