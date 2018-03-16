package cn.robotpen.pen.scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Keep;
import android.support.annotation.RequiresApi;


/**
 * Created by 王强 on 2016/12/7. 简介： 0x06 0x09 0x45 0x6C 0x69 0x74 0x65 0x02 0x01 0x06 0x04 0xFF 0x61
 * 0x9F 0x06 0x11 0x07 0x9E 0xCA 0xDC 0x24 0x0E 0xE5 0xA9 0xE0 0x93 0xF3 0xA3 0xB5 0x01 0x00 0x40
 * 0x6E 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00
 * 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x06 0x09 0x45 0x6C 0x69 0x74 0x65 0x02
 * 0x01 0x06 0x04 0xFF 0x61 0x9F 0x06 0x11 0x07 0x9E 0xCA 0xDC 0x24 0x0E 0xE5 0xA9 0xE0 0x93 0xF3
 * 0xA3 0xB5 0x01 0x00 0x40 0x6E 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00
 * 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00
 */
@Keep
public abstract class RobotScanCallback {
    /**
     * RobotDevice 特征码 作为筛选依据
     */
    private final byte[] FILTER_CODE = {(byte) 0xA3, (byte) 0xB5, 0x01, 0x00, 0x40, 0x6E};
    private Object mScanCallback;
    private Handler handler;

    public RobotScanCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mScanCallback = new ScanCallback() {
                @Override
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                public void onScanResult(int callbackType, ScanResult result) {
                    scanFilter(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
                }
            };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            handler = new Handler();
            mScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            scanFilter(device, rssi, scanRecord);
                        }
                    });
                }
            };
        }
    }

    /**
     * 判断当前设备是否是RobotDevice
     *
     * @param scanRecord
     * @return true 是
     */
    private boolean isRobotDevice(byte[] scanRecord) {
        for (int i = 0; i < scanRecord.length - FILTER_CODE.length; i++) {
            if (scanRecord[i] == FILTER_CODE[0]
                    && scanRecord[i + 1] == FILTER_CODE[1]
                    && scanRecord[i + 2] == FILTER_CODE[2]
                    && scanRecord[i + 3] == FILTER_CODE[3]
                    && scanRecord[i + 4] == FILTER_CODE[4]
                    && scanRecord[i + 5] == FILTER_CODE[5]) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断该设备是否被已配对
     *
     * @param scanRecord
     * @return true 该设备已被配对
     */
    private boolean isPaired(byte[] scanRecord) {
        for (int i = 0; i < scanRecord.length - 3; i++) {
            if (scanRecord[i] == 0x61 && scanRecord[i + 2] == 0x06) {
                return true;
            }
        }
        return false;
    }

    /**
     * 扫描结果过滤
     *
     * @param scanRecord
     * @return
     */
    private void scanFilter(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (!isRobotDevice(scanRecord)) return;
        if (isPaired(scanRecord)) {
            onResult(device, rssi, true);
        } else {
            onResult(device, rssi, false);
        }
    }

    public Object getScanCallback() {
        return mScanCallback;
    }

    /**
     * 返回扫描结果
     *
     * @param device   蓝牙设备
     * @param rssi     信号强度
     * @param isPaired 是否配对
     */
    public abstract void onResult(BluetoothDevice device, int rssi, boolean isPaired);

    /**
     * 返回扫描错误信息
     * 只有在 大于api 19的情况下会收到该回调
     *
     * @param error 错误
     */
    public abstract void onFailed(int error);

}
