package com.samplayer.core.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.samplayer.aidl.ISAMPlayerService;
import com.samplayer.core.manager.base.IReleaseAble;
import com.samplayer.core.manager.base.IServiceSession;
import com.samplayer.core.remote.SAMPlayerService;
import com.samplayer.listener.ServiceSessionListener;
import com.samplayer.utils.SAMLog;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service连接管理
 */
public class ServiceSessionManager implements IServiceSession, ServiceConnection, IReleaseAble {

    private static final String TAG = "ServiceSessionManager";

    /**
     * Remote Service提供的接口
     */
    private ISAMPlayerService mPlayerService;

    /**
     * 回调接口
     */
    private CopyOnWriteArrayList<ServiceSessionListener> mServiceSessionListeners = new CopyOnWriteArrayList<>();

    private Context mAppContext;

    public ServiceSessionManager(Context context) {
        mAppContext = context;
    }

    @Override
    public boolean isConnect() {
        return mPlayerService != null;
    }

    @Override
    public void connect() {
        if (isConnect()) {
            SAMLog.w(TAG, "connect:Service已经连接了");
            return;
        }
        Intent intent = new Intent(mAppContext, SAMPlayerService.class);
        try {
            mAppContext.startService(intent);
            mAppContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            //这里在某些情况下会出现 SecurityException  Unable to start service Intent **** process is bad
            SAMLog.e(TAG, "Service Connect失败", e);
        }
    }

    @Override
    public void disconnect() {
        if (!isConnect()) {
            SAMLog.w(TAG, "disconnect:Service未连接");
            return;
        }
        if (mPlayerService != null) {
            mAppContext.unbindService(this);
            mPlayerService = null;
            SAMLog.i(TAG, "disconnect: 断开连接");
            notifyAllListenerDisConnect(false);
        }
    }

    @Override
    public void stop() {
        disconnect();
        Intent intent = new Intent(mAppContext, SAMPlayerService.class);
        mAppContext.stopService(intent);
        SAMLog.i(TAG, "release All");
    }

    @Override
    public void addSessionListener(ServiceSessionListener listener) {
        if (listener == null) {
            return;
        }

        if (mServiceSessionListeners.contains(listener)) {
            SAMLog.w(TAG, "addSessionListener: 已经存在监听");
            return;
        }
        mServiceSessionListeners.add(listener);
    }

    @Override
    public void removeSessionListener(ServiceSessionListener listener) {
        if (listener == null) {
            return;
        }
        mServiceSessionListeners.remove(listener);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        SAMLog.i(TAG, "onServiceConnected: Success");
        mPlayerService = ISAMPlayerService.Stub.asInterface(service);
        notifyAllListenerConnect();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        SAMLog.i(TAG, "onServiceDisconnected: ");
        mPlayerService = null;
        notifyAllListenerDisConnect(true);
    }

    private void notifyAllListenerConnect() {
        for (ServiceSessionListener item : mServiceSessionListeners) {
            item.onServiceConnect();
        }
    }

    private void notifyAllListenerDisConnect(boolean isError) {
        for (ServiceSessionListener item : mServiceSessionListeners) {
            item.onServiceDisconnect(isError);
        }
    }

    @Nullable
    @Override
    public ISAMPlayerService getRemoteService() {
        return mPlayerService;
    }

    @Override
    public void release() {
        stop();
        mServiceSessionListeners.clear();
    }
}
