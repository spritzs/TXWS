package cn.robotpen.pen.service;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;

//import com.codingmaster.slib.S;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import cn.robotpen.pen.model.RemoteState;
import cn.robotpen.pen.model.RobotDevice;
import cn.robotpen.pen.model.RobotDeviceType;
import cn.robotpen.pen.utils.BytesHelper;

/**
 * Created by 王强 on 2017/2/9.
 * 简介：
 */

public class RobotUsbRequester extends Thread {
    UsbManager mUsbManager;
    UsbDevice usbDevice;
    RobotServiceContract.ServicePresenter presenter;
    private boolean RUNING_FLAG;
    //缓存长度
    private int BUFFER_LEN;
    //初始读取USB数据buffer
    private ByteBuffer buffer;
    private BytesHelper bytesHelper;

    /**
     * @param device     设备
     * @param usbManager usbmanager
     * @param presenter  p
     */
    public RobotUsbRequester(UsbDevice device,
                             UsbManager usbManager,
                             RobotServiceContract.ServicePresenter presenter) {
        this.usbDevice = device;
        this.mUsbManager = usbManager;
        this.presenter = presenter;
        RUNING_FLAG = true;
        bytesHelper = new BytesHelper();
    }

    @Override
    public void run() {
        RobotDevice mDevice;
        int vid = usbDevice.getProductId();
//        S.i(vid);
//        S.i(bytesHelper.bytes2Str(bytesHelper.integerTobytes(vid)));
        if (vid == 0x600F) {
            mDevice = new RobotDevice("RobotPen_T7E", "", 1);
            mDevice.setDeviceVersion(RobotDeviceType.T7E.getValue());
        } else {
            mDevice = new RobotDevice("RobotPen_P1", "", 1);
            mDevice.setDeviceVersion(4);
        }
        UsbDeviceConnection mUsbDeviceConnection = mUsbManager.openDevice(usbDevice);
        if (mUsbDeviceConnection == null) {
            presenter.reportError("无法连接到USB设备！");
            return;
        }
        presenter.reportState(RemoteState.STATE_CONNECTED, null);
        UsbInterface mUsbInterface = usbDevice.getInterface(0);
        UsbEndpoint mUsbEndpoint = mUsbInterface.getEndpoint(0);
        BUFFER_LEN = mUsbEndpoint.getMaxPacketSize();
        buffer = ByteBuffer.allocate(BUFFER_LEN);
        buffer.order(ByteOrder.nativeOrder());
        if (mUsbDeviceConnection.claimInterface(mUsbInterface, true)) {
            presenter.onDeviceChanged(mDevice);
            presenter.reportState(RemoteState.STATE_DEVICE_INFO, null);
        }
        UsbRequest usbRequest = new UsbRequest();
        if (!usbRequest.initialize(mUsbDeviceConnection, mUsbEndpoint)) {
            presenter.reportError("数据读取线程创建失败！");
            return;
        }

        while (RUNING_FLAG) {
            if (vid == 0x600F) {
                mDevice = new RobotDevice("RobotPen_T7E", "", 1);
                mDevice.setDeviceVersion(RobotDeviceType.T7E.getValue());
            } else {
                mDevice = new RobotDevice("RobotPen_P1", "", 1);
                mDevice.setDeviceVersion(4);
            }
            presenter.onDeviceChanged(mDevice);
            usbRequest.setClientData(RobotUsbRequester.this);
            if (usbRequest.queue(buffer, BUFFER_LEN)
                    && mUsbDeviceConnection.requestWait() == usbRequest) {
                byte[] data = buffer.array();
//                S.i(bytesHelper.bytes2Str(data));
                if (data[0] == 0x02
                        && data[1] == 0
                        && data[2] == (byte) 0xff
                        && data[3] == (byte) 0x21
                        && data[4] == (byte) 0xff
                        && data[5] == (byte) 0x14) {
                } else if (data[0] == (byte) 0xAA && data[1] == (byte) 0x27) {
                } else {
                    presenter.reportPenPosition(data);
                }
            }
        }
        usbRequest.close();
        mUsbDeviceConnection.releaseInterface(mUsbInterface);
        mUsbDeviceConnection.close();
        buffer.clear();
        buffer = null;
        usbDevice = null;
        presenter.reportState(RemoteState.STATE_DISCONNECTED, "");
    }

    public void quit() {
        this.RUNING_FLAG = false;
    }
}
