package com.samplayer.ijk;

import android.content.Context;

import com.samplayer.core.remote.player.base.AbstractPlayManager;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IJKPlayManager extends AbstractPlayManager {

    public IJKPlayManager(Context context) {
        super(context);
    }

    @Override
    public IMediaPlayer createMediaPlayer() {
        return new IjkMediaPlayer();
    }
}
