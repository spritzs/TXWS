package cn.robotpen.pen.callback;

import android.app.Activity;

/**
 * Created by 王强 on 2016/12/16.
 * 简介：
 */
@Deprecated
public abstract class PenPositionAndEventCallback extends RemotePenEventCallback {
    public PenPositionAndEventCallback(Activity act) {
        super(act);
    }
}
