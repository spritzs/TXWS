package cn.robotpen.pen.model;

import android.support.annotation.Keep;

/**
 * Created by 王强 on 2016/12/6.
 * 简介：
 */
@Keep
public class RemoteState {
    /**
     * The profile is in disconnected state
     */
    public static final int STATE_DISCONNECTED = 0;
    /**
     * The profile is in connecting state
     */
    public static final int STATE_CONNECTING = 1;
    /**
     * The profile is in connected state
     * 收到该返回的时候是硬件建立连接，这个时候并没有设备信息
     */
    public static final int STATE_CONNECTED = 2;
    /**
     * The profile is in disconnecting state
     */
    public static final int STATE_ERROR = 3;
    /**
     * 进入同步模式成功
     */
    public static final int STATE_ENTER_SYNC_MODE_SUCCESS = 4;
    /**
     * 进入同步模式失败
     */
    public static final int STATE_ENTER_SYNC_MODE_FAIL = 5;

    /**
     * 返回设备信息
     */
    public static final int STATE_DEVICE_INFO = 6;
}
