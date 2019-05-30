package com.samplayer.exo;

import android.content.Context;

import com.samplayer.core.remote.player.base.AbstractPlayManager;

import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;

public class ExoPlayManager extends AbstractPlayManager {

    private Context mContext;

    public ExoPlayManager(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public IMediaPlayer createMediaPlayer() {
        return new IjkExoMediaPlayer(mContext);
    }
}
