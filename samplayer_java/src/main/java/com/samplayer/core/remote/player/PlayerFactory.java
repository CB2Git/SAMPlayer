package com.samplayer.core.remote.player;

import android.content.Context;

import com.samplayer.core.remote.player.base.IPlayManager;

public class PlayerFactory {

    public static IPlayManager create(Context context) {
        //TODO 改为可配置
        return new AndroidPlayManager(context);
    }
}
