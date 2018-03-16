package cn.robotpen.pen.callback;

import android.Manifest;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import cn.robotpen.pen.IRemoteRobotService;
import cn.robotpen.pen.RobotPenService;
import cn.robotpen.pen.RobotPenServiceImpl;

/**
 * s
 * Created by 王强 on 2017/2/22.
 * 简介：和罗博智能笔服务的绑定方式
 * 所有的回调接口都在UI线程中
 */
public abstract class RobotPenActivity extends AppCompatActivity implements ServiceConnection, OnUiCallback {
    private IRemoteRobotService robotServiceBinder;
    private RobotPenService robotPenService;
    private RobotPenServiceCallback penServiceCallback;
    private final String[] requiredPermissons = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        penServiceCallback = new RobotPenServiceCallback(this);
        robotPenService = new RobotPenServiceImpl(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, requiredPermissons, 0);
        } else {
            bindRobotPenService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0
                && grantResults.length > 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            bindRobotPenService();
        } else {
            Toast.makeText(this, "Require SD read/write permisson!", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressWarnings("MissingPermission")
    public void bindRobotPenService() {
        robotPenService.bindRobotPenService(this, this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        robotServiceBinder = IRemoteRobotService.Stub.asInterface(service);
        try {
            robotServiceBinder.registCallback(penServiceCallback);
            service.linkToDeath(penServiceCallback, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
            onServiceConnectError(e.getMessage());
        }
    }

    public IRemoteRobotService getPenServiceBinder() {
        return robotServiceBinder;
    }

    public RobotPenService getRobotPenService() {
        return robotPenService;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public void onServiceConnectError(String msg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        robotPenService.unBindRobotPenService(this, this);
    }


    @Override
    public void onPenPositionChanged(int deviceType, int x, int y, int presure, byte state) {
//        CLog.d(RobotPenActivity.class.getSimpleName(),String.format("------>x:%d--->y:%d-->presure:%d",x,y,presure));
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
    public void onRobotKeyEvent(int e) {

    }

    @Override
    public void onUpdateFirmwareFinished() {

    }

    @Override
    public void onUpdateFirmwareProgress(int progress, int total,String info) {

    }

    @Override
    public void onUpdateModuleFinished() {

    }

    @Override
    public void setSyncPassWordWithOldPassWord(String pwd, String n_pwd) {

    }

    @Override
    public void onSetSyncPassWordWithOldPasswordCallback(int code) {

    }

    @Override
    public void opneReportedData() {

    }

    @Override
    public void closeReportedData() {

    }

    @Override
    public void cleanDeviceDataWithType(int code) {

    }

    @Override
    public void startSyncNoteWithPassWord(String pwd) {

    }

    @Override
    public void opneReportedDataCallBack(int code) {

    }

    @Override
    public void closeReportedDataCallBack(int code) {

    }

    @Override
    public void cleanDeviceDataWithTypeCallBack(int code) {

    }

    @Override
    public void startSyncNoteWithPassWordCallBack(int code) {

    }

    @Override
    public void onPageNumberOnly(short number) {

    }
}
