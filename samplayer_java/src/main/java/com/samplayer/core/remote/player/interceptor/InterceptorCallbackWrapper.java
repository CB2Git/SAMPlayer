package com.samplayer.core.remote.player.interceptor;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.samplayer.model.SongInfo;

public class InterceptorCallbackWrapper implements InterceptorCallback {

    private static final int MSG_CONTINUE = 1000;

    private static final int MSG_ERROR = 10001;

    private static final int MSG_IN_PROCESS = 100002;

    private InterceptorCallback mInterceptorCallback;

    private boolean isValid = true;

    public InterceptorCallbackWrapper(InterceptorCallback interceptorCallback) {
        mInterceptorCallback = interceptorCallback;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isValid) {
                return;
            }
            switch (msg.what) {
                case MSG_CONTINUE:
                    mInterceptorCallback.onContinue((SongInfo) msg.obj);
                    break;
                case MSG_ERROR:
                    mInterceptorCallback.onError((Integer) msg.obj);
                    break;
                case MSG_IN_PROCESS:
                    mInterceptorCallback.inProcess();
                    break;
            }
        }
    };

    public void invalid() {
        isValid = false;
    }

    @Override
    public void onContinue(SongInfo info) {
        if (isValid) {
            mHandler.obtainMessage(MSG_CONTINUE, info).sendToTarget();
        }
    }

    @Override
    public void onError(int errorCode) {
        if (isValid) {
            mHandler.obtainMessage(MSG_ERROR, errorCode).sendToTarget();
        }
    }

    @Override
    public void inProcess() {
        if (isValid) {
            mHandler.obtainMessage(MSG_IN_PROCESS).sendToTarget();
        }
    }
}
