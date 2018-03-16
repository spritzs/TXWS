package cn.robotpen.pen.handler.action;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;
import android.util.SparseIntArray;

//import com.codingmaster.slib.S;

import java.util.Iterator;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.model.RemoteState;
import cn.robotpen.pen.service.RobotServiceContract;

/**
 * Description: 处理USB插拔事件
 *
 * @author 王强 Email: 249346538@qq.com 2017/2/25.
 * @version 1.0
 */

public class USBActionHandler extends RobotHandler<Intent> {
    //厂家标示
    private final int[] mUsbVendorId = {0x0ed1, 0x0ed1, 0x0ed1, 0x0ed1, 0x0ed1};
    //产品标示
    private final int[] mUsbProductId = {0x7805, 0x7806, 0x7807, 0x7808, 0x781e};
    //USB授权返回
    private final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private SparseIntArray usbVPMap;
    private boolean mUsbAttached = true;
    private UsbManager mUsbManager;

    public USBActionHandler(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
        usbVPMap = new SparseIntArray();
        for (int i = 0; i < mUsbVendorId.length; i++) {
            usbVPMap.put(mUsbVendorId[i], mUsbProductId[i]);
        }
        mUsbManager = (UsbManager) presenter.getService().getSystemService(Context.USB_SERVICE);
        mUsbAttached = false;
    }

    @Override
    public void handle(Intent data) {
        if (data == null || TextUtils.isEmpty(data.getAction())) {
            detectUsbDevice();
            return;
        }
        UsbDevice device = data.getParcelableExtra("data");
        switch (getAction(data)) {
            case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                //USB 设备插入 断开蓝牙连接
                servicePresenter.disconnectDevice();
                requestUsbPermission(device);
                break;
            case UsbManager.ACTION_USB_DEVICE_DETACHED:
//                S.i("USB 断开了");
                servicePresenter.disconnectDevice();
                break;
            case ACTION_USB_PERMISSION:
                //授权
                boolean isPermissionGranted = data.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                if (isPermissionGranted) {
                    servicePresenter.connectUsbDevice(device);
                } else {
                    servicePresenter.reportError(servicePresenter.getString("usb_permission_diny"));
                }
                break;
            default:
                if (nextHandler != null) {
                    nextHandler.handle(data);
                }
        }
    }

    private String getAction(Intent data) {
        String action = data.getStringExtra("usb_action");
        return TextUtils.isEmpty(action) ? "" : action;
    }


    /**
     * 检测已连接的USB设备
     * 兼容服务被杀死的清空
     */
    private void detectUsbDevice() {
        Iterator<UsbDevice> it = mUsbManager.getDeviceList().values().iterator();
        while (it.hasNext()) {
            UsbDevice device = it.next();
            if (usbVPMap.get(device.getVendorId()) != 0) {
                requestUsbPermission(device);
            }
        }
    }

    /**
     * 请求USB连接权限
     *
     * @param device
     */
    private void requestUsbPermission(UsbDevice device) {
        if (mUsbAttached) return;
        if (device != null) {
            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(
                    servicePresenter.getService(),
                    0,
                    new Intent(ACTION_USB_PERMISSION),
                    0);
            mUsbManager.requestPermission(device, mPermissionIntent);
            servicePresenter.reportState(RemoteState.STATE_CONNECTING, "");
        } else {
            servicePresenter.reportError("无法连接USB设备");
        }
    }
}
