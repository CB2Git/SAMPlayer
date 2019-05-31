package com.samplayer.core.remote.player;

import android.content.Context;

import com.samplayer.core.remote.player.base.IPlayManager;

import java.lang.reflect.Constructor;

public class PlayerFactory {

    private static Class<? extends IPlayManager> sPlayerManager;

    public static void setPlayManager(Class<? extends IPlayManager> playManager) {
        sPlayerManager = playManager;
    }

    public static IPlayManager create(Context context) {
        if (sPlayerManager == null) {
            sPlayerManager = AndroidPlayManager.class;
        }
        try {
            Constructor<? extends IPlayManager> constructor = sPlayerManager.getDeclaredConstructor(Context.class);
            constructor.setAccessible(true);
            return constructor.newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AndroidPlayManager(context);
    }
}
