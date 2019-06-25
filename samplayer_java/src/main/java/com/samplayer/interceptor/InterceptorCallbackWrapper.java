package com.samplayer.interceptor;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.samplayer.interceptor.ex.InterceptorException;
import com.samplayer.model.SongInfo;
import com.samplayer.utils.SAMLog;

import java.util.List;

/**
 * 拦截器的包装
 */
public class InterceptorCallbackWrapper implements InterceptorCallback {

    private static final String TAG = "InterceptorCallbackWrap";

    private static final int MSG_CONTINUE = 1000;

    private static final int MSG_ERROR = 10001;

    private static final int MSG_IN_PROCESS = 100002;

    private static final int MSG_STOP = 100003;

    private InterceptorCallback mInterceptorCallback;

    private boolean isValid = true;

    private InterceptorHandler mInterceptorHandler;

    private HandlerThread mHandlerThread = new HandlerThread("InterceptorCallbackWrapper");

    public InterceptorCallbackWrapper(SongInfo songInfo, List<Interceptor> interceptors, InterceptorCallback interceptorCallback) {
        mInterceptorCallback = interceptorCallback;
        mHandlerThread.start();
        mInterceptorHandler = new InterceptorHandler(mHandlerThread.getLooper(), songInfo, interceptors, mHandler);
    }

    /**
     * 开始执行拦截器流程
     */
    public void interceptor() {
        //发送消息  开始执行拦截器流程
        mInterceptorHandler.sendEmptyMessage(2333);
    }

    /**
     * 拦截器的执行在非主线程
     */
    private class InterceptorHandler extends Handler {
        private SongInfo mSongInfo;

        private List<Interceptor> mInterceptors;

        private Handler mCallback;

        public InterceptorHandler(Looper looper, SongInfo songInfo, List<Interceptor> interceptors, Handler callback) {
            super(looper);
            mSongInfo = songInfo;
            mInterceptors = interceptors;
            mCallback = callback;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //回调 拦截器开始执行
            mCallback.obtainMessage(MSG_IN_PROCESS, mSongInfo).sendToTarget();
            try {
                RealInterceptorChain realInterceptorChain = new RealInterceptorChain(mInterceptors, 0, mSongInfo);
                //执行拦截器
                SongInfo songInfo = realInterceptorChain.proceed(mSongInfo);
                //执行完毕 并且成功了
                mCallback.obtainMessage(MSG_CONTINUE, songInfo).sendToTarget();
            } catch (Exception e) {
                if (e instanceof InterceptorException) {
                    InterceptorException exception = (InterceptorException) e;
                    mCallback.obtainMessage(MSG_ERROR, exception.getErrorCode()).sendToTarget();
                } else {
                    //拦截器内部出现错误了
                    mCallback.obtainMessage(MSG_ERROR, -123456).sendToTarget();
                }

            } finally {
                mCallback.obtainMessage(MSG_STOP).sendToTarget();
            }
        }
    }

    /**
     * 让拦截器的回调在主线程
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SAMLog.i(TAG, "handleMessage: MSG = " + msg.what);
            if (!isValid) {
                SAMLog.i(TAG, "handleMessage: " + isValid);
                mHandlerThread.quitSafely();
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
                    mInterceptorCallback.inProcess((SongInfo) msg.obj);
                    break;
                case MSG_STOP:
                    mHandlerThread.quitSafely();
                    SAMLog.i(TAG, "handleMessage: quitSafely");
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
    public void inProcess(SongInfo info) {
        if (isValid) {
            mHandler.obtainMessage(MSG_IN_PROCESS, info).sendToTarget();
        }
    }
}
