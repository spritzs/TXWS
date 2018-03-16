package cn.robotpen.pen.pool;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Keep;

import cn.robotpen.pen.callback.OnUiCallback;
import cn.robotpen.pen.model.Recycleable;

/**
 * Created by 王强 on 2017/1/19.
 * 简介：
 */
@Keep
public class RunnableMessage implements Runnable, Recycleable {
    private static final Handler mainThread = new Handler(Looper.getMainLooper());
    private static final Object sPoolSync = new Object();
    private static final int MAX_POOL_SIZE = 50;
    private static final int FLAG_IN_USE = 1;   //等待复用标志
    private static RunnableMessage sPool;
    private static int sPoolSize = 0;
    private RunnableMessage next;
    private OnUiCallback callback;
    private int flags;                  //0标示正在被使用
    private int deviceType;
    private int pointX, pointY, pointPresure;
    private byte pointState;

    RunnableMessage(int deviceType, int x, int y, int presure, byte state, OnUiCallback remoteCallback) {
        this.deviceType = deviceType;
        this.pointX = x;
        this.pointY = y;
        this.pointPresure = presure;
        this.pointState = state;
        this.callback = remoteCallback;
    }

    public static RunnableMessage obtain(int deviceType, int x, int y, int presure, byte state, OnUiCallback callback) {
        synchronized (sPoolSync) {
            if (sPool != null) {
                RunnableMessage m = sPool;
                sPool = m.next;
                m.next = null;
                m.flags = 0;
                m.callback = callback;
                m.deviceType = deviceType;
                m.pointX = x;
                m.pointY = y;
                m.pointPresure = presure;
                m.pointState = state;
                sPoolSize--;
                return m;
            }
        }
        return new RunnableMessage(deviceType, x, y, presure, state, callback);
    }

    boolean isInUse() {
        return ((flags & FLAG_IN_USE) == FLAG_IN_USE);
    }

    @Override
    public void recycle() {
        if (isInUse()) {
            return;
        }
        recycleUnchecked();
    }

    void recycleUnchecked() {
        flags = FLAG_IN_USE;
        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                callback = null;
                sPool = this;
                sPoolSize++;
            }
        }
    }

    public void sendToTarget() {
        mainThread.post(this);
    }

    @Override
    public void run() {
        try {
            if (callback != null) {
                callback.onPenPositionChanged(deviceType, pointX, pointY, pointPresure, pointState);
            }
            recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
