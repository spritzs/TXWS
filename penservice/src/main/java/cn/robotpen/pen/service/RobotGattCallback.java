package cn.robotpen.pen.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.support.annotation.RequiresApi;

//import com.codingmaster.slib.S;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.robotpen.pen.handler.RobotHandlerManager;
import cn.robotpen.pen.handler.cmd.HandleCMD_80;
import cn.robotpen.pen.handler.cmd.HandleCMD_82;
import cn.robotpen.pen.handler.cmd.HandleCMD_83;
import cn.robotpen.pen.handler.cmd.HandleCMD_84;
import cn.robotpen.pen.handler.cmd.HandleCMD_88;
import cn.robotpen.pen.handler.cmd.HandleCMD_8A;
import cn.robotpen.pen.handler.cmd.HandleCMD_8B;
import cn.robotpen.pen.handler.cmd.HandleCMD_A0;
import cn.robotpen.pen.handler.cmd.HandleCMD_A2;
import cn.robotpen.pen.handler.cmd.HandleCMD_A4;
import cn.robotpen.pen.handler.cmd.HandleCMD_A5;
import cn.robotpen.pen.handler.cmd.HandleCMD_B1;
import cn.robotpen.pen.handler.cmd.HandleCMD_B2;
import cn.robotpen.pen.handler.cmd.HandleCMD_B3;
import cn.robotpen.pen.handler.cmd.HandleCMD_B4;
import cn.robotpen.pen.handler.cmd.HandleCMD_B5;
import cn.robotpen.pen.handler.cmd.HandleCMD_C8;
import cn.robotpen.pen.handler.cmd.HandleCMD_C9;
import cn.robotpen.pen.handler.cmd.HandleCMD_CA;
import cn.robotpen.pen.handler.cmd.HandleCMD_CB;
import cn.robotpen.pen.handler.cmd.HandleCMD_D1;
import cn.robotpen.pen.handler.cmd.HandleCMD_D2;
import cn.robotpen.pen.handler.cmd.HandleCMD_D3;
import cn.robotpen.pen.handler.cmd.HandleCMD_D4;
import cn.robotpen.pen.handler.cmd.HandleCMD_D6;
import cn.robotpen.pen.handler.cmd.HandleCMD_D7;
import cn.robotpen.pen.handler.cmd.HandleCMD_D8;
import cn.robotpen.pen.handler.cmd.HandleCMD_D9;
import cn.robotpen.pen.handler.gatt.GattaErrorHandler;
import cn.robotpen.pen.handler.gatt.GattaPenPointHandler;
import cn.robotpen.pen.handler.gatt.GattaSyncHandler;
import cn.robotpen.pen.model.RemoteState;
import cn.robotpen.pen.model.RobotDevice;
import cn.robotpen.pen.utils.BytesHelper;

import static cn.robotpen.pen.model.CMD.CMD_80;
import static cn.robotpen.pen.model.CMD.CMD_84;
import static cn.robotpen.pen.model.CMD.CMD_A0;
import static cn.robotpen.pen.model.CMD.CMD_A2;
import static cn.robotpen.pen.model.CMD.CMD_A5;
import static cn.robotpen.pen.model.CMD.CMD_HEAD_ID;

/**
 * Created by 王强 on 2017/2/9.
 * 简介：
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class RobotGattCallback extends BluetoothGattCallback {
    private final UUID SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private final UUID PEN_DATA_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    private final UUID PEN_WRITE_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    private final UUID NOTIFICATION_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private BluetoothGattCharacteristic mPenDataCharacteristic;
    private BluetoothGattCharacteristic mPenWriteCharacteristic;

    private RobotServiceContract.ServicePresenter servicePresenter;
    private BluetoothGatt mBluetoothGatt;
    private BytesHelper bytesHelper;
    private RobotHandlerManager<byte[]> gattaHandlerManager;
    //这里主要是为了兼容一个硬件bug，硬件会发送两次0x80消息，会在同步的时候造成bug
    private Map<Byte, String> distinctMap;
    public volatile boolean sIsWriting = false;
    public Queue queue;

    public RobotGattCallback(RobotServiceContract.ServicePresenter presenter) {
        this.servicePresenter = presenter;
        queue = new ConcurrentLinkedQueue();
        this.gattaHandlerManager = new RobotHandlerManager.HandlersBuilder<byte[]>()
                .addHandler(new GattaErrorHandler(servicePresenter))
                .addHandler(new GattaSyncHandler(servicePresenter))
                .addHandler(new GattaPenPointHandler(servicePresenter))
                .addHandler(new HandleCMD_80(servicePresenter))
                .addHandler(new HandleCMD_82(servicePresenter))
                .addHandler(new HandleCMD_83(servicePresenter))
                .addHandler(new HandleCMD_84(servicePresenter))
                .addHandler(new HandleCMD_88(servicePresenter))
                .addHandler(new HandleCMD_8A(servicePresenter))
                .addHandler(new HandleCMD_8B(servicePresenter))
                .addHandler(new HandleCMD_A2(servicePresenter))
                .addHandler(new HandleCMD_A4(servicePresenter))
                .addHandler(new HandleCMD_A5(servicePresenter))
                .addHandler(new HandleCMD_B1(servicePresenter))
                .addHandler(new HandleCMD_B2(servicePresenter))
                .addHandler(new HandleCMD_B3(servicePresenter))
                .addHandler(new HandleCMD_B4(servicePresenter))
                .addHandler(new HandleCMD_B5(servicePresenter))
                .addHandler(new HandleCMD_D1(servicePresenter))
                .addHandler(new HandleCMD_D2(servicePresenter))
                .addHandler(new HandleCMD_D3(servicePresenter))
                .addHandler(new HandleCMD_D4(servicePresenter))
                .addHandler(new HandleCMD_D6(servicePresenter))
                .addHandler(new HandleCMD_D7(servicePresenter))
                .addHandler(new HandleCMD_D8(servicePresenter))
                .addHandler(new HandleCMD_D9(servicePresenter))
                //C7
                .addHandler(new HandleCMD_C8(servicePresenter))
                .addHandler(new HandleCMD_C9(servicePresenter))
                .addHandler(new HandleCMD_CA(servicePresenter))
                .addHandler(new HandleCMD_CB(servicePresenter))
                .addHandler(new HandleCMD_A0(servicePresenter))
                //C7
                .build();
        this.bytesHelper = new BytesHelper();
        this.distinctMap = new HashMap<>();
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatts, int status, int newState) {
        super.onConnectionStateChange(gatts, status, newState);
        mBluetoothGatt = gatts;
        String address = gatts.getDevice().getAddress();
        if (BluetoothGatt.STATE_CONNECTED == newState && status == 0) {//&& status != 133
            mBluetoothGatt.discoverServices();
            BluetoothDevice bluetoothDevice = mBluetoothGatt.getDevice();
            RobotDevice robotDevice = new RobotDevice(
                    bluetoothDevice.getName(),
                    bluetoothDevice.getAddress(), 0);
            servicePresenter.onDeviceChanged(robotDevice);
        } else {
            if (status == 133) {
                servicePresenter.reportState(RemoteState.STATE_ERROR, address);//STATE_DISCONNECTED
            } else {
                servicePresenter.reportState(status == 0 ? newState : RemoteState.STATE_DISCONNECTED, address);//STATE_DISCONNECTED
            }
            mBluetoothGatt.close();
            servicePresenter.onDeviceChanged(null);
            mBluetoothGatt.disconnect();
            queue.clear();
            sIsWriting = false;
        }
        distinctMap.clear();
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatts, int status) {
        super.onServicesDiscovered(gatts, status);
        mPenDataCharacteristic = null;
        mPenWriteCharacteristic = null;
        if (BluetoothGatt.GATT_SUCCESS == status) {
            for (BluetoothGattService service : gatts.getServices()) {
                if (service.getUuid().equals(SERVICE_UUID)) {
                    mPenDataCharacteristic = service.getCharacteristic(PEN_DATA_UUID);
                    mPenWriteCharacteristic = service.getCharacteristic(PEN_WRITE_UUID);
                    break;
                }
            }
//            S.i("Characteristic OK");
        } else {
//            S.i("Characteristic ERROR");
        }
        if (mPenDataCharacteristic != null && mPenWriteCharacteristic != null) {
            mBluetoothGatt.setCharacteristicNotification(mPenDataCharacteristic, true);
            BluetoothGattDescriptor descriptor = mPenDataCharacteristic.getDescriptor(NOTIFICATION_DESCRIPTOR_UUID);
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                write(descriptor);
            } else {
//                S.i("descriptor ERROR");
            }
        } else {
//            S.i("Characteristic OK");
            String address = gatts.getDevice().getAddress();
            servicePresenter.reportError(address + "hardware error!");
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        byte[] data = characteristic.getValue();
//        S.i("收到的数据------>"+bytesHelper.bytes2Str(data));
        if (servicePresenter.getConnectedDevice().getDeviceVersion() > 0
                || data[1] == CMD_80 || data[1] == CMD_84) {
            if (data == null || data.length < 3) {
                return;
            }
            if (CMD_80 == data[1]
                    && data[3] == (byte) 0x0A
                    && distinctMap.get(CMD_A2) != null) {   //笔记头信息
//                S.i(bytesHelper.bytes2Str(data));
            } else {
                distinctMap.put(data[1], "");
                gattaHandlerManager.handle(data);
            }
        } else {
            servicePresenter.reportState(RemoteState.STATE_ERROR, servicePresenter.getConnectedDevice().getAddress());
        }
    }


    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
//        S.i("data------>" + bytesHelper.bytes2Str(characteristic.getValue()) + "-------->>>>>>>> " + status);
        sIsWriting = false;
        nextWrite();
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
//        S.i("------>bytesHelper.bytes2Str(data):" + bytesHelper.bytes2Str(descriptor.getValue()));
        sIsWriting = false;
        nextWrite();
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
//        S.i("------>bytesHelper.bytes2Str(data):" + bytesHelper.bytes2Str(descriptor.getValue()));
    }

    /**
     * 向连接的蓝牙设备发送消息
     *
     * @param command 命令
     * @param data    数据
     */
    public synchronized boolean sendMessage(byte command, byte[] data) {
        boolean result = false;
        if (CMD_A0 == command || CMD_A5 == command) {
            distinctMap.clear();
        }
        distinctMap.put(command, "");
        byte dataSize = data != null ? (byte) data.length : 0;
        byte[] value = new byte[dataSize + 3];
        value[0] = CMD_HEAD_ID;
        value[1] = command;
        value[2] = dataSize;
        if (data != null) {
            System.arraycopy(data, 0, value, 3, dataSize);
        }
        if (mBluetoothGatt != null) {
            mPenWriteCharacteristic.setValue(value);
//            S.i("写数据：" + bytesHelper.bytes2Str(value));
            if (!write(mPenWriteCharacteristic)) {
//                S.i("写数据失败");
                return false;
            }
            return true;
        }
        return result;
    }


    private synchronized boolean write(Object o) {
        boolean flag = false;
        if (queue.isEmpty() && !sIsWriting) {
            flag = doWrite(o);
        } else {
            queue.add(o);
        }
        return flag;
    }


    private synchronized boolean nextWrite() {
        if (!queue.isEmpty() && !sIsWriting) {
            return doWrite(queue.poll());
        }
        return false;
    }


    private synchronized boolean doWrite(Object o) {
        boolean flag = false;
        if (o instanceof BluetoothGattCharacteristic) {
            sIsWriting = true;
            flag = mBluetoothGatt.writeCharacteristic((BluetoothGattCharacteristic) o);
        } else if (o instanceof BluetoothGattDescriptor) {
            sIsWriting = true;
            flag = mBluetoothGatt.writeDescriptor((BluetoothGattDescriptor) o);
        } else {
            nextWrite();
        }
        return flag;
    }
}
