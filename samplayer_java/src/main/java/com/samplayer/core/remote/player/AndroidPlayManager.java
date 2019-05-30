package com.samplayer.core.remote.player;

import android.content.Context;

import com.samplayer.core.remote.player.base.AbstractPlayManager;

import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;

public class AndroidPlayManager extends AbstractPlayManager {

    public AndroidPlayManager(Context context) {
        super(context);
    }

    @Override
    public IMediaPlayer createMediaPlayer() {
        return new AndroidMediaPlayer();
    }
}
