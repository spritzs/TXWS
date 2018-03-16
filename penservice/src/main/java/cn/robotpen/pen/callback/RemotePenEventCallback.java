package cn.robotpen.pen.callback;

import android.app.Activity;

/**
 * Created by 王强 on 2016/12/9.
 * 简介：
 */
@Deprecated
public abstract class RemotePenEventCallback extends RemoteCallback {
    public RemotePenEventCallback(Activity act) {
        super(act);
    }

    @Override
    public void onOffLineNoteHeadReceived(String json) {

    }

    @Override
    public void onSyncProgress(String key, int total, int progress) {

    }

    @Override
    public void onOffLineNoteSyncFinished(String json, byte[] data) {

    }

    @Override
    public void onPenServiceError(String msg) {

    }

    @Override
    public void onUpdateFirmwareProgress(int progress, int total, String info) {

    }

    @Override
    public void onUpdateFirmwareFinished() {

    }
}
