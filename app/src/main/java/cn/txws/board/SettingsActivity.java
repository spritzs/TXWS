package cn.txws.board;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.robotpen.pen.callback.RobotPenActivity;
import cn.robotpen.pen.model.RemoteState;
import cn.robotpen.pen.model.RobotDevice;
import cn.txws.board.connect.BleConnectActivity;

/**
 * Created by Administrator on 2018/1/29 0029.
 */

public class SettingsActivity extends RobotPenActivity {

    @BindView(R.id.settings_device)
    View mTextDevice;
    @BindView(R.id.settings_background)
    View mTextBackground;
    @BindView(R.id.settings_teach)
    View mTextTeach;
    @BindView(R.id.settings_about)
    View mTextAbout;
    @BindView(R.id.settings_help)
    View mTextHelp;
    @BindView(R.id.settings_version)
    TextView mTextVersion;
    @BindView(R.id.conn_status)
    TextView mTextConnStatus;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ButterKnife.bind(this);
        init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.bluetooth_settings:
                connectBlueTooth();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTextVersion.setText("v"+getVerName(this));
    }

    public void connectBlueTooth(){
        Intent intent = new Intent(SettingsActivity.this, BleConnectActivity.class);
        startActivity(intent);
    }

    public void gotoAbout(){
        Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
        startActivity(intent);
    }


    @OnClick({R.id.settings_device, R.id.settings_background, R.id.settings_teach, R.id.settings_about, R.id.settings_help})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_device:
                connectBlueTooth();
                break;
            case R.id.settings_background:
                break;
            case R.id.settings_teach:
                break;
            case R.id.settings_about:
                gotoAbout();
                break;
            case R.id.settings_help:
                break;
        }
    }

    public String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    @Override
    public void onStateChanged(int state, String addr) {
        Log.e("test", "onStateChanged");
        switch (state) {
            case RemoteState.STATE_ERROR:
                Log.w("test", "STATE_ERROR");
                break;
            case RemoteState.STATE_CONNECTED:
                Log.w("test", "STATE_CONNECTED");
                break;
            case RemoteState.STATE_CONNECTING:
                break;
            case RemoteState.STATE_DISCONNECTED: //设备断开
                Log.e("test", "STATE_DISCONNECTED");
                mTextConnStatus.setText(R.string.no_connect);
                break;
            case RemoteState.STATE_DEVICE_INFO: //设备连接成功状态
                Log.e("test", "STATE_DEVICE_INFO");
                checkDevice();
                break;
            case RemoteState.STATE_ENTER_SYNC_MODE_SUCCESS://笔记同步成功

                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        Log.e("test", "onServiceConnected");
        checkDevice();//检测设备是否连接
    }

    private void checkDevice() {
        try {
            RobotDevice robotDevice = getPenServiceBinder().getConnectedDevice();
            if (null != robotDevice) {
                if (robotDevice.getDeviceVersion() > 0) {//针对固件bug进行解决 STATE_DEVICE_INFO 返回两次首次无设备信息第二次会上报设备信息
                    String name;
                    if (robotDevice.getName() != null){
                        name=robotDevice.getName();
                    }else{
                        name=getString(R.string.no_name_device);
                    }
                    mTextConnStatus.setText(name);
                }
            }else{
                mTextConnStatus.setText(R.string.no_connect);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPenServiceError(String msg) {

    }

    @Override
    public void onPageInfo(int currentPage, int totalPage) {

    }

    @Override
    public void onPageNumberAndCategory(int pageNumber, int category) {

    }

    @Override
    public void onSupportPenPressureCheck(boolean flag) {

    }

    @Override
    public void onCheckPressureing() {

    }

    @Override
    public void onCheckPressurePen() {

    }

    @Override
    public void onCheckPressureFinish(int flag) {

    }

    @Override
    public void onCheckModuleUpdate() {

    }

    @Override
    public void onCheckModuleUpdateFinish(byte[] data) {

    }
}
