package cn.robotpen.pen.adapter;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Keep;
import android.support.annotation.RequiresPermission;
import android.util.Log;

//import com.codingmaster.slib.S;

import cn.robotpen.pen.IRemoteRobotService;
import cn.robotpen.pen.RobotPenService;
import cn.robotpen.pen.RobotPenServiceImpl;
import cn.robotpen.pen.callback.OnUiCallback;
import cn.robotpen.pen.callback.RobotPenServiceCallback;
import cn.robotpen.pen.model.Battery;
import cn.robotpen.pen.model.RemoteState;
import cn.robotpen.pen.model.RobotDevice;

/**
 * Description:
 *
 * @author 王强 Email: 249346538@qq.com 2017/3/2.
 * @version 1.0
 */
@Keep
public abstract class RobotPenAdapter<T extends OnPenConnectListener<E>, E> implements
        ServiceConnection, OnUiCallback {
    private IRemoteRobotService robotServiceBinder;
    private Context ctx;
    private RobotPenService robotPenService;
    private RobotPenServiceCallback penServiceCallback;
    private T adapterCallback;
    private String macAddress;

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public RobotPenAdapter(Context ctx, T listener) {
        this.ctx = ctx;
        this.adapterCallback = listener;
    }

    @SuppressWarnings("MissingPermission")
    public boolean init(Handler handler) {
        penServiceCallback = new RobotPenServiceCallback(this, handler);
        robotPenService = new RobotPenServiceImpl(ctx);
        try {
            robotPenService.bindRobotPenService(ctx, this);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public IRemoteRobotService getRobotServiceBinder() {
        return robotServiceBinder;
    }

    public void connect(String macAddr) throws RemoteException {
        this.macAddress = macAddr;
        robotServiceBinder.connectDevice(macAddr);
    }

    public void connect(T listener, String macAddr) throws RemoteException {
        this.adapterCallback = listener;
        connect(macAddr);
    }

    //暴露一个离线笔记同步方法
    public boolean startSyncOffLineNote() {
        boolean flag = false;
        try {
            flag = robotServiceBinder.startSyncOffLineNote();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return flag;

    }

    public void reConnect() throws RemoteException {
        robotServiceBinder.connectDevice(macAddress);
    }

    public void disConnect() {
        try {
            robotServiceBinder.disconnectDevice();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRemainBattery() {
        try {
            return robotServiceBinder.getRemainBattery();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Battery getRemainBatteryEM() {
        try {
            return robotServiceBinder.getRemainBatteryEM();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void release() {
        if (robotServiceBinder != null) {
            try {
                byte model = robotServiceBinder.getCurrentMode();
                //检查当前模式
                if (model == 0x0A) {
                    //退出同步模式
                    robotServiceBinder.exitSyncMode();
                } else if (model == 0x06) {
                    //退出OTA模式
                    robotServiceBinder.exitOTA();
                }
                //取消回调
                robotServiceBinder.unRegistCallback(penServiceCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                robotServiceBinder.asBinder().unlinkToDeath(penServiceCallback, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        robotPenService.unBindRobotPenService(ctx, this);
        adapterCallback = null;
        penServiceCallback = null;
        robotPenService = null;
        macAddress = null;
        ctx = null;
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        robotServiceBinder = IRemoteRobotService.Stub.asInterface(service);
        try {
            robotServiceBinder.registCallback(penServiceCallback);
            service.linkToDeath(penServiceCallback, 0);
            adapterCallback.onPenServiceStarted();
        } catch (RemoteException e) {
            e.printStackTrace();
            adapterCallback.onConnectFailed(0);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        adapterCallback.onDisconnected();
    }

    @Override
    public void onStateChanged(int state, String addr) {
        switch (state) {
            case RemoteState.STATE_DEVICE_INFO:
                try {
                    RobotDevice robotDevice = robotServiceBinder.getConnectedDevice();
                    adapterCallback.onConnected(robotDevice.getDeviceType());
                    int batteryPercent;
                    if (robotDevice.getBattery() == 254 || robotDevice.getBattery() == 255) {
                        batteryPercent = robotDevice.getBattery();
                    } else {
                        batteryPercent = (int) (robotDevice.getBattery() * 1.0f / 7 * 100);
                    }
                    Log.i("RobotPen",""+batteryPercent);
                    adapterCallback.onRemainBattery(batteryPercent);
                } catch (Exception e) {
                    e.printStackTrace();
                    adapterCallback.onConnectFailed(-1);
                }
                break;
            case RemoteState.STATE_DISCONNECTED:
                if (adapterCallback != null) {
                    adapterCallback.onDisconnected();
                }
                break;
        }
    }

    @Override
    public void onOffLineNoteHeadReceived(String json) {
        adapterCallback.onOfflineSyncStart(json);
    }

    @Override
    public void onSyncProgress(String key, int total, int progress) {
        adapterCallback.onOfflienSyncProgress(key, total, progress);
    }


    @Override
    public void onOffLineNoteSyncFinished(String json, byte[] data) {
        adapterCallback.onOfflineDataReceived(convert(data), true);
        adapterCallback.onOffLineNoteSyncFinished(json, data);
    }

    protected abstract E convert(byte[] data);

    @Override
    public void onPenServiceError(String msg) {
        if (adapterCallback != null) {
            adapterCallback.onConnectFailed(-1);
        }
    }

    @Override
    public void onPenPositionChanged(int deviceType, int x, int y, int presure, byte state) {
        adapterCallback.onReceiveDot(System.currentTimeMillis(), x, y, presure, state);
    }

    @Override
    public void onRobotKeyEvent(int e) {

    }

    @Override
    public void onPageInfo(int currentPage, int totalPage) {
    }

    @Override
    public void onUpdateFirmwareFinished() {

    }

    @Override
    public void onUpdateFirmwareProgress(int progress, int total, String info) {

    }

    @Override
    public void onUpdateModuleFinished() {

    }

    @Override
    public void onPageNumberAndCategory(int pageNumber, int category) {
        adapterCallback.onReportPageNumberAndOther(pageNumber, category);
    }

    @Override
    public void opneReportedDataCallBack(int code) {
        adapterCallback.onStartUploadDataCallback(code);
    }


    @Override
    public void closeReportedDataCallBack(int code) {
        adapterCallback.onCloseUploadDataCallBack(code);
    }

    @Override
    public void cleanDeviceDataWithTypeCallBack(int code) {
        adapterCallback.onCleanDataCallback(code);
    }

    @Override
    public void startSyncNoteWithPassWordCallBack(int code) {
        adapterCallback.onStartSyncNoteWithPassWord(code);
    }

    @Override
    public void setSyncPassWordWithOldPassWord(String pwd, String n_pwd) {
        try {
            robotServiceBinder.setSyncPassWordWithOldPassWord(pwd, n_pwd);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSetSyncPassWordWithOldPasswordCallback(int code) {
        adapterCallback.onSetSyncPwdCallback(code);
    }

    @Override
    public void opneReportedData() {
        try {
            robotServiceBinder.opneReportedData();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void closeReportedData() {
        try {
            robotServiceBinder.closeReportedData();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanDeviceDataWithType(int code) {
        try {
            robotServiceBinder.cleanDeviceDataWithType(code);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startSyncNoteWithPassWord(String pwd) {
        try {
            robotServiceBinder.startSyncNoteWithPassWord(pwd);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
