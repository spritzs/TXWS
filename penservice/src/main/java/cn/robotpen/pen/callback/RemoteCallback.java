package cn.robotpen.pen.callback;

import android.app.Activity;
import android.os.RemoteException;

import cn.robotpen.pen.IRemoteRobotServiceCallback;

/**
 * Created by 王强 on 2016/12/9.
 * 简介：
 */
@Deprecated
public abstract class RemoteCallback extends IRemoteRobotServiceCallback.Stub {
    private Activity act;

    public RemoteCallback(Activity act) {
        this.act = act;
    }

    @Override
    public void onRemoteStateChanged(final int state, final String addr) throws RemoteException {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onStateChanged(state, addr);
            }
        });
    }

    public abstract void onStateChanged(int state, String addr);

    @Override
    public void onRemoteOffLineNoteHeadReceived(final String json) throws RemoteException {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onOffLineNoteHeadReceived(json);
            }
        });
    }

    public abstract void onOffLineNoteHeadReceived(String json);

    @Override
    public void onRemoteSyncProgress(final String key, final int total, final int progress) throws RemoteException {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onSyncProgress(key, total, progress);
            }
        });
    }

    public abstract void onSyncProgress(String key, int total, int progress);

    @Override
    public void onRemoteOffLineNoteSyncFinished(final String json, final byte[] data) throws RemoteException {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onOffLineNoteSyncFinished(json, data);
            }
        });
    }

    public abstract void onOffLineNoteSyncFinished(String json, byte[] data);

    @Override
    public void onRemotePenServiceError(final String msg) throws RemoteException {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onPenServiceError(msg);
            }
        });
    }

    public abstract void onPenServiceError(String msg);

    @Override
    public void onRemotePenPositionChanged(final int deviceType, final int x, final int y, final int presure, final byte state) throws RemoteException {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onPenPositionChanged(deviceType, x, y, presure, state);
            }
        });
    }

    public abstract void onPenPositionChanged(int deviceType, int x, int y, int presure, byte state);

    @Override
    public void onRemoteRobotKeyEvent(final int e) throws RemoteException {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onRobotKeyEvent(e);
            }
        });
    }

    /**
     * 0x01 单击
     * 0x02 双击
     * 0x03 上翻页
     * 0x04 下翻页
     * 0x05 新建页
     *
     * @param e 事件
     */
    public abstract void onRobotKeyEvent(int e);

    @Override
    public void onRemoteUpdateFirmwareFinished() throws RemoteException {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onUpdateFirmwareFinished();
            }
        });
    }

    public abstract void onUpdateFirmwareFinished();

    @Override
    public void onRemoteUpdateFirmwareProgress(final int progress, final int total,final String info) throws RemoteException {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onUpdateFirmwareProgress(progress, total,info);
            }
        });
    }

    public abstract void onUpdateFirmwareProgress(int progress, int total,String info);
}
