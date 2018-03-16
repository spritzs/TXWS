package cn.robotpen.pen.scan;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.annotation.Keep;
import android.support.annotation.RequiresPermission;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 王强 on 2016/12/7.
 * 简介：
 */
@Keep
public class RobotScannerCompat {
    private final UUID SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private BluetoothAdapter mBluetoothAdapter;
    private WeakReference<RobotScanCallback> mCallback;

    public RobotScannerCompat() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public RobotScannerCompat(RobotScanCallback robotScanCallback) {
        this();
        this.mCallback = new WeakReference<>(robotScanCallback);
    }

    public void setScanCallback(RobotScanCallback robotScanCallback) {
        this.mCallback = new WeakReference<>(robotScanCallback);
    }

    public boolean isBluetoothEnable() {
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * 开始扫描
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public void startScan() {
        if (mCallback == null) {
            return;
        }
        stopScan();
        RobotScanCallback robotScanCallback = mCallback.get();
        if (robotScanCallback == null) {
            return;
        }
        Object callback = robotScanCallback.getScanCallback();
        if (!isBluetoothEnable() || callback == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            List<ScanFilter> filters = new ArrayList<>();
            ScanFilter filter = new ScanFilter.Builder()
                    .setServiceUuid(new ParcelUuid(SERVICE_UUID))
                    .build();
//            filters.add(filter); //屏蔽掉是因为发现
            mBluetoothAdapter.getBluetoothLeScanner()
                    .startScan(filters, settings, (ScanCallback) callback);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter.startLeScan(
                    null,//new UUID[]{SERVICE_UUID},
                    (BluetoothAdapter.LeScanCallback) callback);
        }
    }

    /**
     * 停止扫描
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public void stopScan() {
        if (mCallback == null) {
            return;
        }
        RobotScanCallback robotScanCallback = mCallback.get();
        if (robotScanCallback == null) {
            return;
        }
        Object callback = robotScanCallback.getScanCallback();
        if (!isBluetoothEnable() || callback == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothAdapter.getBluetoothLeScanner()
                    .stopScan((ScanCallback) callback);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter.stopLeScan((BluetoothAdapter.LeScanCallback) callback);
        }
    }
}
