package cn.robotpen.pen.service;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

import cn.robotpen.pen.BuildConfig;
import cn.robotpen.pen.IRemoteRobotServiceCallback;
import cn.robotpen.pen.RobotPenServiceImpl;
import cn.robotpen.pen.handler.RobotHandlerManager;
import cn.robotpen.pen.handler.action.NotificationActionHandler;
import cn.robotpen.pen.handler.action.USBActionHandler;
import cn.robotpen.pen.model.OffLineNoteHead;
import cn.robotpen.pen.model.RemoteState;
import cn.robotpen.pen.model.RobotDevice;
import cn.robotpen.pen.model.RobotDeviceType;
import cn.robotpen.pen.utils.BytesHelper;
import cn.robotpen.pen.utils.ConfigHelper;
import cn.robotpen.pen.utils.PairedRecoder;
import cn.robotpen.pen.utils.RobotResoureAdapter;

import static cn.robotpen.pen.RobotPenServiceImpl.EXTR_FROM_RECEIVER;
import static cn.robotpen.pen.model.CMD.CMD_A0;
import static cn.robotpen.pen.model.CMD.CMD_A3;
import static cn.robotpen.pen.model.CMD.CMD_B0;
import static cn.robotpen.pen.model.CMD.CMD_B1;
import static cn.robotpen.pen.model.CMD.CMD_B2;
import static cn.robotpen.pen.model.CMD.CMD_B3;
import static cn.robotpen.pen.model.CMD.CMD_B5;
import static cn.robotpen.pen.model.CMD.CMD_C8;
import static cn.robotpen.pen.model.CMD.CMD_D0;
import static cn.robotpen.pen.model.CMD.CMD_D1;
import static cn.robotpen.pen.model.CMD.CMD_D2;
import static cn.robotpen.pen.model.CMD.CMD_D3;
import static cn.robotpen.pen.model.CMD.CMD_D6;
import static cn.robotpen.pen.model.RemoteState.STATE_DEVICE_INFO;


/**
 * Description:远程服务
 * <p>
 * 关于交互流程：
 * 1.建立连接
 * <p>
 * 2.离线笔迹同步
 * 流程
 * 手机(host)               设备(slave)
 * "0xA0 -----------------------→       //请求 进入同步模式"
 * "←------------------------ 0x80      //响应 设备状态"
 * "0xA2 -----------------------→       //请求 一条离线笔记的头信息"
 * "←------------------------ 0xA2      //响应 一条离线笔记的头信息"
 * "0xA3 -----------------------→       //请求 开始同步离线笔记'
 * "←------------------------ 0xA4      //响应 开始同步"
 * "←------------------------           //响应 笔记数据，省略头信息"
 * "var ←-------------------- 0xA5"
 * "if(var == 0x00)                     //一个存储块传输完成"
 * "0xA5 0x01 0x03 ---------→           //请求 下一个存储块"
 * "if(var == 0x01)                     //当前笔记传输完成"
 * "0xA5 0x01 0x02 ---------→           //Host端保存完成，通知slave删除当前笔记"
 * "if(var == 0x02)                     //该条笔记设备（slave）端删除完成，可以进"下一条的传输"
 * "0xA1 -------------------→           //退出同步模式"
 * </p>
 * 3.OTA升级
 * 手机(Host)             设备(Slave)
 * 0xB0 --------------------→           //进入OTA模式
 * ←------------------------- 0xB1      //请求：固件信息
 * 0xB1 --------------------→           //响应：固件信息
 * ←------------------------- 0xB2      //请求：固件数据
 * 0xB2 --------------------→           //响应：固件数据
 * ←------------------------- 0xB3      //请求：固件指纹信息
 * 0xB3 --------------------→           //响应：固件指纹信息
 * ←------------------------- 0xB4      //返回固件指纹对比结果
 * 0xB5 --------------------→           //执行固件升级
 * 0xB6 --------------------→           //中断OTA升级
 * <p>
 *
 * @author 王强  2016/12/5 Email:249346528@qq.com 2017/2/24
 *         <p>
 *         updated by luis on 2017/04/25 增加Note noteNumber 处理
 */

public class RobotRemotePenService extends Service implements RobotServiceContract.ServicePresenter {
    public static final String ACTION_DISCONNECT_DEVICE_FROM_NOTIFICATION = "cn.robotpen.app.remoteservice.disconnect.action";
    public static final String ACTION_EXIT_SERVICE_FROM_NOTIFICATION = "cn.robotpen.app.remoteservice.exit.action";
    //强制关闭服务 Action
    public static final String ACTION_SERVICE_FORCE_CLOSE = "cn.robotpen.app.remoteservice.exit";
    //当前运行服务的包名
    private String EXTR_RUNDING_SERVICE_PACKAGE_NAME = "EXTR_RUNDING_SERVICE_PACKAGE_NAME";
    //true 显示前台通知，每次startService的时候该值会更新
    private boolean isForceGround = false;
    //服务前台通知图标
    private Bitmap notificationIcon;
    //0x04 笔模式，0x0A同步模式，0x06 OTA模式
    private byte mState = 0;
    //绑定客户端计数
    private int mBindCount = 0;
    //OTA升级的时候记录数据发送进度
    private int mOtaRawIndex = 0;
    //缓存当前发送的数据
    private SparseArray<byte[]> mOtaCache = new SparseArray<>();
    //绑定的客户端
    private RemoteCallbackList<IRemoteRobotServiceCallback> mRegistedCallbacks;
    private BluetoothGatt mBluetoothGatt;
    //当前链接设备信息
    private RobotDevice mDevice;
    //当前正在同步的离线笔记头信息
    private OffLineNoteHead mCurrentOfflineNoteHeade;
    //数据缓冲区,在ota模式下前4个字节是版本号
    private ByteArrayOutputStream mDataBuffer;
    //固件数据
    private byte[] bleFirmwareDataForUpgrade;
    private byte[] mcuFirmwareDataForUpgrade;
    private byte[] moduleDataForUpgrade;
    private byte[] currentFirmwareDataForUpgrade;

    //升级固件文件的指纹信息
    private int mFirmwareFingerPrinter = 0;
    //蓝牙连接回调
    private RobotGattCallback robotGattCallback;
    //Usb 数据读取线程
    private RobotUsbRequester mRobotUsbRequester;
    private BluetoothManager bluetoothManager;
    private BytesHelper bytesHelper;
    //监听USB设备（P1）的插拔和授权事件
    private UsbDeviceStateReceiver usbDeviceStateReceiver;
    //监听服务退出广播
    private ForceCloseReceiver forceCloseReceiver;
    private RobotServiceBinder binder;
    private RobotHandlerManager<Intent> handlerManager;
    private byte[] commandData;
    private int sendData = 0;
    private byte[] mcuCommandData;
    private byte[] blecommandData;

    @Override
    public void onCreate() {
        super.onCreate();
//        S.init(true, 1, "PP_WRITER");
        registUSBReceiver();
        mRegistedCallbacks = new RemoteCallbackList<>();
        mDataBuffer = new ByteArrayOutputStream();
        binder = new RobotServiceBinder(this);
        bytesHelper = new BytesHelper();
        forceCloseOtherPkgPenService(getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            robotGattCallback = new RobotGattCallback(this);
        }
        ConfigHelper.registShareService(getPackageName(), BuildConfig.VERSION_CODE);
        handlerManager = new RobotHandlerManager.HandlersBuilder<Intent>()
                .addHandler(new NotificationActionHandler(this))
                .addHandler(new USBActionHandler(this))
                .build();
    }


    /**
     * 强制关闭其他应用提供的服务
     *
     * @param pkg 服务包名
     */
    private void forceCloseOtherPkgPenService(String pkg) {
        Intent intent = new Intent(ACTION_SERVICE_FORCE_CLOSE);
        intent.putExtra(EXTR_RUNDING_SERVICE_PACKAGE_NAME, pkg);
        sendBroadcast(intent);
    }

    private void registExitReceiver() {
        forceCloseReceiver = new ForceCloseReceiver();
        IntentFilter exitFilter = new IntentFilter();
        exitFilter.addAction(ACTION_SERVICE_FORCE_CLOSE);
        registerReceiver(forceCloseReceiver, exitFilter);
    }

    private void registUSBReceiver() {
        usbDeviceStateReceiver = new UsbDeviceStateReceiver();
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        usbFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        usbFilter.addAction("com.android.example.USB_PERMISSION");
        registerReceiver(usbDeviceStateReceiver, usbFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null
                && !intent.getBooleanExtra(RobotPenServiceImpl.EXTR_FROM_RECEIVER, false)
                && intent.hasExtra(RobotPenServiceImpl.EXTR_NOTIFICATION)) {
            //是否显示notifaction
            isForceGround = intent.getBooleanExtra(RobotPenServiceImpl.EXTR_NOTIFICATION, false);
            updateForgroundNotification(RemoteState.STATE_DEVICE_INFO);
        }
        updateForgroundNotification(RemoteState.STATE_DISCONNECTED);
        handlerManager.handle(intent);
        return START_STICKY;
    }

    @Override
    public Service getService() {
        return this;
    }

    @Override
    public String getString(String resName, Object... arg) {
        int resId = RobotResoureAdapter.getStringResourceId(this, resName);
        return getString(resId, arg);
    }

    private int getDrawableRes(String resName) {
        return RobotResoureAdapter.getDrawableResourceId(this, resName);
    }

    @Override
    public void registClient(IRemoteRobotServiceCallback callback) {
        mRegistedCallbacks.register(callback);
    }

    @Override
    public void unregistClient(IRemoteRobotServiceCallback callback) {
        mRegistedCallbacks.unregister(callback);
        System.gc();
    }


    @Override
    public void onDeviceChanged(RobotDevice device) {
        this.mDevice = device;
    }


    @Override
    @SuppressWarnings("NewApi")
    public RobotDevice getConnectedDevice() {
        if (mDevice == null) {
            return null;
        }
        if (mDevice.getConnectType() == 1) {
            //usb 模式
            return mDevice;
        }
        //蓝牙模式

        if (isBleConnectionEnable()) {
            if (TextUtils.isEmpty(mDevice.getName())) {
                mDevice.setName(getDeviceNameFromHistory(mDevice.getAddress()));
            }
            return mDevice;
        } else {
            return null;
        }
    }

    private String getDeviceNameFromHistory(String macAddr) {
        return PairedRecoder.getPairedMap().get(macAddr);
    }

    @Override
    public void updateConnectedDeviceFirmwareVersion(byte[] firmVersion) {
        if (mDevice != null) {
            int deviceType = bytesHelper.bytesToInteger(mDevice.getHardwareVer());
            if (deviceType == RobotDeviceType.T8A.getValue()
                    || deviceType == RobotDeviceType.T9E.getValue()
                    || deviceType == RobotDeviceType.X8.getValue()
                    || deviceType == RobotDeviceType.T9A.getValue()
                    || deviceType == RobotDeviceType.J0_A4_P.getValue()
                    || deviceType == RobotDeviceType.J0_T9.getValue()
                    || deviceType == RobotDeviceType.T9_J0.getValue()) {
                byte[] bleVer = new byte[2];
                System.arraycopy(firmVersion, 2, bleVer, 0, 2);
                mDevice.setBleFirmwareVersion(bleVer);
                byte[] mcuVer = new byte[2];
                System.arraycopy(firmVersion, 0, mcuVer, 0, 2);
                mDevice.setMcuFirmwareVer(mcuVer);
            } else {
                mDevice.setBleFirmwareVersion(firmVersion);
            }
        }
        if (mDevice.getDeviceVersion() == 0) {
            disconnectBluDevice();
            reportError("获取设备信息失败");
        }
    }

    @Override
    public void updateDeviceOfflineNote(byte offlineNoteCount) {
        if (mDevice != null) {
            mDevice.setOfflineNoteNum(offlineNoteCount);
        }
    }

    @Override
    public void updateDeviceBattery(byte battery) {
        if (mDevice != null) {
            mDevice.setBattery(battery);
        }
    }

    @Override
    public void updateDeviceType(int deviceType) {
        if (mDevice != null) {
            this.mDevice.setDeviceVersion(deviceType);
        }
    }

    @Override
    public void updateConnectedDeviceHardwareVersion(byte[] hardVersion) {
        if (mDevice != null) {
            mDevice.setHardwareVer(hardVersion);
        }
    }

    @Override
    public void updateDeviceState(byte state) {
        this.mState = state;
    }

    @Override
    public byte getDeviceState() {
        return mState;
    }


    @Override
    @SuppressLint("NewApi")
    public boolean connectBlutoothDevice(String addr) {
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(addr);
        if (device == null || mDevice != null
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return false;
        }
        if (mBluetoothGatt != null) {// isBleConnectionEnable
//            S.i("mBluetoothGatt is not null");
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
            SystemClock.sleep(500);
        } else if (mBluetoothGatt == null) {
            mBluetoothGatt = device.connectGatt(RobotRemotePenService.this, false, robotGattCallback);
        }
        return true;
    }

    /**
     * 连接USB设备
     *
     * @param device usb 设备
     */
    @Override
    public boolean connectUsbDevice(UsbDevice device) {
        UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if (!mUsbManager.hasPermission(device)) {
            reportError("无USB连接权限");
            return false;
        }
        mRobotUsbRequester = new RobotUsbRequester(device, mUsbManager, this);
        mRobotUsbRequester.start();
        return true;
    }

    /**
     * 更新智能笔服务前台通知
     *
     * @param state 状态
     */
    private void updateForgroundNotification(int state) {
        if (notificationIcon == null) {
            notificationIcon = BitmapFactory.decodeResource(
                    getResources(),
                    getDrawableRes("ic_pen_notification"));
        }
        NotificationCompat.Builder foregroundNotification = new NotificationCompat.Builder(this)
                .setSmallIcon(getDrawableRes("ic_pen_notification_small"))
                .setLargeIcon(notificationIcon)
                .setTicker(getString("robot_pen_service_started"))
                .setContentTitle(getString("robot_pen_service"))
                .setContentText(getForceGroundNotificationContent(state));
        if (showDisconnectActionIfTrue(state)) {
            Intent disconnectIntent = new Intent(this, RobotRemotePenService.class);
            disconnectIntent.setAction(ACTION_DISCONNECT_DEVICE_FROM_NOTIFICATION);
            PendingIntent disconnectPendingIntent = PendingIntent.getService(
                    this,
                    1,
                    disconnectIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            foregroundNotification.addAction(
                    getDrawableRes("ic_menu_close_clear_cancel"),
                    getString("disconnect_device"),
                    disconnectPendingIntent);
        } else if (ifShowCloseAction()) {
            Intent closeIntent = new Intent(this, RobotRemotePenService.class);
            closeIntent.setAction(ACTION_EXIT_SERVICE_FROM_NOTIFICATION);
            PendingIntent closePendingIntent = PendingIntent.getService(
                    this,
                    2,
                    closeIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            foregroundNotification.addAction(
                    getDrawableRes("ic_menu_close_clear_cancel"),
                    getString("close"),
                    closePendingIntent);
        }
        if (isForceGround) {
            startForeground(0x111, foregroundNotification.build());
        } else {
            stopForeground(true);
        }
    }

    /**
     * 常驻通知描述
     *
     * @param state
     * @return 通知内容
     */
    private String getForceGroundNotificationContent(int state) {
        String result = getString("robot_pen_service_ready_content");
        if (mState == 0x06) {
            return getString("ota");
        } else if (mState == 0x0A) {
            return getString("state_sync");
        } else {
            switch (state) {
                case RemoteState.STATE_CONNECTED:
                case STATE_DEVICE_INFO:
                    if (mDevice != null) {
                        String deviceInfo = mDevice.getName() + "(" + mDevice.getAddress() + ")";
                        result = getString("notify_device_info", deviceInfo);
                    }
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    /**
     * 是否显示断开连接按钮
     *
     * @param state {@link RemoteState}
     * @return true 显示
     */
    private boolean showDisconnectActionIfTrue(int state) {
        switch (state) {
            case RemoteState.STATE_CONNECTED:
            case STATE_DEVICE_INFO:
                return mDevice != null && mState == 0x04;
            default:
                return false;
        }
    }

    /**
     * 是否显示关闭服务按钮
     * 在OTA和同步离线笔记的模式下是不允许关闭的
     *
     * @return true 允许关闭
     */
    private boolean ifShowCloseAction() {
        return mBindCount <= 0 && mState != 0x0A && mState != 0x06;
    }


    @Override
    public IBinder onBind(Intent intent) {
        startService(new Intent(this, RobotRemotePenService.class));
        mBindCount++;
        updateForgroundNotification(STATE_DEVICE_INFO);
        return binder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        mBindCount++;
        updateForgroundNotification(STATE_DEVICE_INFO);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mBindCount--;
        updateForgroundNotification(STATE_DEVICE_INFO);
        return true;
    }

    /**
     * 向硬件发送命令
     *
     * @param command 命令
     * @param data    数据
     * @return true 发送成功
     */
    @Override
    @SuppressLint("NewApi")
    public boolean execCommand(byte command, byte... data) {
        return robotGattCallback.sendMessage(command, data);
    }

    /**
     * 解析离线笔记头信息，这里需要针对不同的设备类型进行不同的处理
     *
     * @param data 数据
     */
    @Override
    public void handleOfflineNoteHeadInfo(byte[] data) {
        byte[] headData = new byte[data[2] & 0xff];
        System.arraycopy(data, 3, headData, 0, headData.length);
        int type = mDevice.getDeviceVersion();

        if (type == RobotDeviceType.ELITE_PLUS.getValue()
                || type == RobotDeviceType.ELITE_PLUS_NEW.getValue()
                || type == RobotDeviceType.J0_A5.getValue()
                || type == RobotDeviceType.J0_A4.getValue()
                || type == RobotDeviceType.J0_A4_P.getValue()
                || type == RobotDeviceType.T9_J0.getValue()
                || type == RobotDeviceType.J0_T9.getValue()
                || type == RobotDeviceType.T8A.getValue()
                || type == RobotDeviceType.T9E.getValue()
                || type == RobotDeviceType.T9A.getValue()) {
            handleElitePlusNoteHead(headData);
        } else {
            handleNoteHead(headData);
        }
    }

    @Override
    @Deprecated
    public boolean startUpdateFirmware(String firmwareVersion, byte[] firmwareData) {
        try {
            String[] bleTem = firmwareVersion.split("\\.");
            if (bleTem.length != 2 || bleTem.length != 2) {
//                S.i("版本号错误");
                return false;
            }
            commandData = new byte[4];
            byte[] bleFirmwareVersionForUpgrade = new byte[bleTem.length + 1];
            for (int i = 1; i < bleTem.length + 1; i++) {
                if (Integer.parseInt(bleTem[i - 1]) > 0) {
                    byte b = (byte) (Integer.parseInt(bleTem[i - 1]) & 0xff);
                    bleFirmwareVersionForUpgrade[0] = b;
                }
            }
            bleFirmwareVersionForUpgrade[1] = (byte) (128 & 0xff);
            System.arraycopy(bleFirmwareVersionForUpgrade, 0, commandData, 0, 3);
            this.bleFirmwareDataForUpgrade = firmwareData;
            return execCommand(CMD_B0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 固件升级
     *
     * @param bleFirmwareVersion 蓝牙固件版本名称，长度为2byte
     * @param bleFirmwareData    蓝牙固件
     * @param mcuFirmwareVersion mcu固件版本，长度为2byte
     * @param mcuFirmwareData    mcu固件
     * @return
     */
    @Override
    public boolean startUpdateFirmware(@NonNull String bleFirmwareVersion, byte[] bleFirmwareData,
                                       @NonNull String mcuFirmwareVersion, byte[] mcuFirmwareData) {
        String[] bleTem = bleFirmwareVersion.split("\\.");
        String[] mcuTem = mcuFirmwareVersion.split("\\.");
        if (bleTem.length != 2 || mcuTem.length != 2) {
//            S.i("版本号错误");
            return false;
        }

        commandData = new byte[4];
        try {
            byte[] bleFirmwareVersionForUpgrade;
            bleFirmwareVersionForUpgrade = new byte[bleTem.length];
            for (int i = 0; i < bleTem.length; i++) {
                byte b = (byte) (Integer.parseInt(bleTem[i]) & 0xff);
                bleFirmwareVersionForUpgrade[i] = b;
            }
            System.arraycopy(bleFirmwareVersionForUpgrade, 0, commandData, 0, 2);
            this.bleFirmwareDataForUpgrade = bleFirmwareData;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            byte[] mcuFirmwareVersionForUpdate;
            mcuFirmwareVersionForUpdate = new byte[mcuTem.length];
            for (int i = 0; i < mcuTem.length; i++) {
                byte b = (byte) (Integer.parseInt(mcuTem[i]) & 0xff);
                mcuFirmwareVersionForUpdate[i] = b;
            }
            this.mcuFirmwareDataForUpgrade = mcuFirmwareData;
            System.arraycopy(mcuFirmwareVersionForUpdate, 0, commandData, 2, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return execCommand(CMD_B0, commandData);
    }


    @Override
    public void enterOtaMode(int type) {
        mOtaCache.clear();
        mOtaRawIndex = 0;
        mFirmwareFingerPrinter = 0;
        byte[] commandData;
        switch (type) {
            case 0://nebula gateway file
                commandData = new byte[4];
                break;
            case 1://nebula node mcufile
                commandData = new byte[4];
                byte[] mcuDatalen = bytesHelper.integerTobytes(mcuFirmwareDataForUpgrade.length);
//                S.i("发送mcu固件");
                System.arraycopy(mcuDatalen, 0, commandData, 0, 4);
                currentFirmwareDataForUpgrade = mcuFirmwareDataForUpgrade;
                break;
            case 2:
                commandData = new byte[4];
                byte[] bleDatalen = bytesHelper.integerTobytes(bleFirmwareDataForUpgrade.length);
                System.arraycopy(bleDatalen, 0, commandData, 0, 4);
                currentFirmwareDataForUpgrade = bleFirmwareDataForUpgrade;
                break;
            case 3://jedi A4 mcu file
                commandData = new byte[4];
                break;
            case 4://jedi A5 mcu file
                commandData = new byte[4];
                break;
            case 5:
                commandData = new byte[4];
                byte[] moduleDatalen = bytesHelper.integerTobytes(moduleDataForUpgrade.length);
                System.arraycopy(moduleDatalen, 0, commandData, 0, 4);
                currentFirmwareDataForUpgrade = moduleDataForUpgrade;
                break;
            default:
                currentFirmwareDataForUpgrade = bleFirmwareDataForUpgrade;
                int count = bleFirmwareDataForUpgrade.length;
                byte[] len = bytesHelper.integerTobytes(count);
                commandData = new byte[8];
                byte[] firmVersion = this.commandData;
                commandData[0] = firmVersion[0];
                commandData[1] = firmVersion[1];
                commandData[2] = firmVersion[2];
                commandData[3] = firmVersion[3];
                commandData[4] = len[0];
                commandData[5] = len[1];
                commandData[6] = len[2];
                commandData[7] = len[3];
                mDataBuffer.reset();
                break;
        }
        if (type == 5) {
            int count = moduleDataForUpgrade.length;
            byte[] len = bytesHelper.integerTobytes(count);
            commandData = new byte[8];
            byte[] firmVersion = this.commandData;
            commandData[0] = firmVersion[0];
            commandData[1] = firmVersion[1];
            commandData[2] = firmVersion[2];
            commandData[3] = firmVersion[3];
            commandData[4] = len[0];
            commandData[5] = len[1];
            commandData[6] = len[2];
            commandData[7] = len[3];
            mDataBuffer.reset();
            execCommand(CMD_D1, commandData);
        } else {
            execCommand(CMD_B1, commandData);
        }
    }

    /**
     * 发送OTA升级数据
     *
     * @param b 数据
     */
    @Override
    public void sendFirmwareData(byte b) {
        execCommand(CMD_B2, getData(b));
        String info = currentFirmwareDataForUpgrade == bleFirmwareDataForUpgrade ? "BLE" : "MCU";
        reportFirmwareUpgradeProgress(mOtaRawIndex, currentFirmwareDataForUpgrade.length, info);
    }


    /**
     * 发送Module升级数据
     *
     * @param b 数据
     */
    @Override
    public void sendModuleData(byte b) {
        execCommand(CMD_D2, getData(b));
        reportFirmwareUpgradeProgress(mOtaRawIndex, currentFirmwareDataForUpgrade.length, "JEDI");
    }

    public byte[] getData(byte b) {
        int flowNum = b & 0xff;
        byte[] otaData;
        mDataBuffer.reset();
        int len = currentFirmwareDataForUpgrade.length;
        if (mOtaCache.get(flowNum) == null) {
            mDataBuffer.write(b);
            int i = mOtaRawIndex;
            for (; i < len && i < mOtaRawIndex + 16; i++) {
                mFirmwareFingerPrinter += (currentFirmwareDataForUpgrade[i] & 0xff);
                mDataBuffer.write(currentFirmwareDataForUpgrade[i]);
            }
            mOtaRawIndex = i;
            otaData = mDataBuffer.toByteArray();
            mOtaCache.clear();
            mOtaCache.put(flowNum, otaData);
        } else {
            otaData = mOtaCache.get(flowNum);
        }
        return otaData;
    }


    @Override
    public void executeOtaFirmwareUpgrade(byte arg) {
        if ((arg & 0xff) == 0) {
            execCommand(CMD_B5);
        } else {
            reportError("固件传输数据不一致");
        }
    }


    /**
     * 更新模组
     *
     * @param moduleVersion      固件版本号
     * @param moduleFirmwareData 固件数据
     * @return
     */
    @Override
    public boolean startUpdateModule(String moduleVersion, byte[] moduleFirmwareData) {
        try {
            String[] bleTem = moduleVersion.split("\\.");
            if (bleTem.length != 2 || bleTem.length != 2) {
//                S.i("版本号错误");
                return false;
            }
            commandData = new byte[4];
            try {
                byte[] bleFirmwareVersionForUpgrade = new byte[bleTem.length + 1];
                for (int i = 1; i < bleTem.length + 1; i++) {
                    if (Integer.parseInt(bleTem[i - 1]) > 0) {
                        byte b = (byte) (Integer.parseInt(bleTem[i - 1]) & 0xff);
                        bleFirmwareVersionForUpgrade[0] = b;
                    }
                }
                bleFirmwareVersionForUpgrade[1] = (byte) (128 & 0xff);
                System.arraycopy(bleFirmwareVersionForUpgrade, 0, commandData, 0, 3);
                this.moduleDataForUpgrade = moduleFirmwareData;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return execCommand(CMD_D0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void updateConnectedDeviceModuleVersion(byte[] moduleVersion) {
        if (mDevice != null) {
            for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
                try {
                    mRegistedCallbacks.getBroadcastItem(i).onRequestModuleVersion(moduleVersion);
                    mRegistedCallbacks.getBroadcastItem(i).onSupportPenPressureCheck(bytesHelper.bytesToInteger(moduleVersion[5]) == 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        mRegistedCallbacks.finishBroadcast();
    }


    @Override
    public void responseModuleDataFingerprinter() {
        execCommand(CMD_D3, bytesHelper.integerTobytes(mFirmwareFingerPrinter));
    }


    @Override
    public void reportModuleUpgradeFinished() {
        execCommand(CMD_D6);
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).onRemoteUpdateModuleFinished();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();
        releaseUpgradMemery();

    }

    /**
     * 校验压感开始
     */
    @Override
    public void checkPenPressing() {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).checkPenPressusering();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();
    }

    /**
     * 校验压感结束
     *
     * @param data
     */
    @Override
    public void checkPenPressFinish(byte[] data) {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).checkPenPressureFinish(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();

    }

    // C7开始 -----------------------------------

    /**
     * 设置同步时候的密码
     *
     * @param data
     */
    @Override
    public void setSyncPassWordWithOldPassWordCallback(int data) {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).onSetSyncPassWordWithOldPassWord(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();

    }

    /**
     * 开始上传数据
     *
     * @param type
     */
    @Override
    public void opneReportedDataCallback(int type) {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).onOpneReportedData(type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();

    }

    /**
     * 停止上传数据
     *
     * @param type
     */
    @Override
    public void closeReportedDataCallback(int type) {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).onCloseReportedData(type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();

    }

    /**
     * 清除数据
     *
     * @param type :1.单纯擦除 2.擦除并清除离线笔记 3.擦除并新建离线笔记
     */
    @Override
    public void cleanDeviceDataWithTypeCallback(int type) {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).onCleanDeviceDataWithType(type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();

    }

    @Override
    public void startSyncNoteWithPassWordCallback(int code) {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).onStartSyncNoteWithPassWord(code);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();
    }
    // C7开始 结束-----------------------------------

    @Override
    public void responseFirmwareDataFingerprinter() {
        execCommand(CMD_B3, bytesHelper.integerTobytes(mFirmwareFingerPrinter));
    }


    @Override
    public synchronized void reportOffLineNoteSyncFinished() {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i)
                        .onRemoteOffLineNoteSyncFinished(mCurrentOfflineNoteHeade.jsonStr(), mDataBuffer.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();
        //清空缓冲区准备接收笔记数据
        mDataBuffer.reset();
        mCurrentOfflineNoteHeade = null;
    }

    /**
     * 解析笔记头信息
     *
     * @param data 数据
     */
    private void handleNoteHead(byte[] data) {
        if (data != null && data.length >= 15) {
            int y = (data[10] & 0xff) + 2000;        // TODO 2254年后需要修改
            int m = data[11] & 0xff;
            int d = data[12] & 0xff;
            int h = data[13] & 0xff;
            int mm = data[14] & 0xff;

            Calendar calendar = Calendar.getInstance();
            calendar.set(y, m - 1, d, h, mm);
            //同步笔记长度
            int count = bytesHelper.bytesToInteger(data[9], data[8], data[7], data[6]);
            mCurrentOfflineNoteHeade = new OffLineNoteHead((int) (calendar.getTimeInMillis() / 1000),
                    mDevice.getDeviceVersion(),
                    count);
            reportNoteHeadInfo(mCurrentOfflineNoteHeade.jsonStr());
            //请求同步笔记
            execCommand(CMD_A3);
        } else {
            reportError("数据长度<15");
        }
    }

    /**
     * 解析笔记头信息
     * 0x85 0x19 0x1F 0x12 0x01 0x00 0x09 0x00 0x03 0x02 0x00 0x00 0x11 0x07 0x0E 0x0B 0x2B
     *
     * @param data 数据
     */
    private void handleElitePlusNoteHead(byte[] data) {
        //.d();
        // TODO 2254年后需要修改
        if (data != null && data.length >= 17) {
            int y = (data[12] & 0xff) + 2000;
            int m = data[13] & 0xff;
            int d = data[14] & 0xff;
            int h = data[15] & 0xff;
            int mm = data[16] & 0xff;
            Calendar calendar = Calendar.getInstance();
            calendar.set(y, m - 1, d, h, mm);
            int count = bytesHelper.bytesToInteger(data[11], data[10], data[9], data[8]);
            mCurrentOfflineNoteHeade = new OffLineNoteHead(
                    (int) (calendar.getTimeInMillis() / 1000),
                    mDevice.getDeviceVersion(),
                    count);
            mCurrentOfflineNoteHeade.setNoteNumber(bytesHelper.bytesToInteger(data[3], data[2]));
            reportNoteHeadInfo(mCurrentOfflineNoteHeade.jsonStr());
            //请求同步笔记
            execCommand(CMD_A3);
        } else {
            reportError("数据长度<17");
        }
    }


    /**
     * 通知硬件设备的链接状态
     *
     * @param state 状态吗
     * @param addr  mac地址
     */
    @Override
    public synchronized void reportState(int state, String addr) {
        updateForgroundNotification(state);
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).onRemoteStateChanged(state, addr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();
    }

    /**
     * 通知错误信息
     *
     * @param error 错误信息
     */
    @Override
    public void reportError(String error) {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).onRemotePenServiceError(error);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();
    }


    @Override
    public void reportOffLineNoteSyncProgress(byte[] data) {
        try {
            mDataBuffer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        reportSyncProgress(mDevice.getAddress(), mCurrentOfflineNoteHeade.getDataCount(), mDataBuffer.size());
    }

    /**
     * 上报笔迹数据
     *
     * @param data 数据
     */
    @Override
    public void reportPenPosition(byte[] data) {
        int len = data.length;
        if (len % 8 != 0) {
            //不合法数据
            //.w("usbtest", "不合法数据" + data.length);
            return;
        }
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                for (int index = 0; index < data.length / 8; index++) {
                    int offset = index * 8;
                    int x = bytesHelper.bytesToInteger(data[offset + 3], data[offset + 2]);
                    int y = bytesHelper.bytesToInteger(data[offset + 5], data[offset + 4]);
                    int presure = bytesHelper.bytesToInteger(data[offset + 7], data[offset + 6]);
                    byte state = data[offset + 1];
                    if (mRegistedCallbacks != null && mDevice != null) {
                        mRegistedCallbacks.getBroadcastItem(i)
                                .onRemotePenPositionChanged(mDevice.getDeviceVersion(), x, y, presure, state);
                    } else {
                        Toast.makeText(RobotRemotePenService.this, "连接已断开，请重新连接", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                //.w("usbtest", "mRegistedCallbacks.beginBroadcast()  " + e.toString());
            }
        }
        mRegistedCallbacks.finishBroadcast();

    }

    /**
     * 通知获取笔记头信息成功事件
     *
     * @param json 数据
     */
    private void reportNoteHeadInfo(String json) {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).onRemoteOffLineNoteHeadReceived(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();
    }

    /**
     * 通知同步进度事件
     *
     * @param key      key
     * @param total    总长度
     * @param progress 当前进度
     */
    private void reportSyncProgress(String key, int total, int progress) {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).onRemoteSyncProgress(key, total, progress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();
    }

    /**
     * 通知按键事件
     *
     * @param ev 事件
     */
    @Override
    public void reportKeyEvent(int ev) {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).onRemoteRobotKeyEvent(ev);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();
    }


    @Override
    public void reportPageInfo(int currentPage, int totalPage) {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).onPageInfo(currentPage, totalPage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();
    }

    @Override
    public void reportPageNumberAndOther(int currentPage, int category) {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).onPageNumberAndCategory(currentPage, category);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();
    }

    @Override
    public void reportPageOnly(short pageNumber) {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).onPageNumberOnly(pageNumber);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();
    }


    @Override
    public void reportFirmwareUpgradeFinished() {
        releaseUpgradMemery();
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).onRemoteUpdateFirmwareFinished();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();
    }


    @Override
    public void reportFirmwareUpgradeProgress(int progress, int total, String info) {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
//                S.i("the progress is :" + progress + "  ++=====>" + total + "  ---- >>" + info);
                mRegistedCallbacks.getBroadcastItem(i).onRemoteUpdateFirmwareProgress(progress, total, info);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();
    }


    /**
     * 验证当前的连接状态
     *
     * @return true 当前的连接是有效的
     */
    private boolean isBleConnectionEnable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && mBluetoothGatt != null) {
            BluetoothDevice dev = mBluetoothGatt.getDevice();
            return bluetoothManager.getConnectionState(dev, BluetoothProfile.GATT) == BluetoothGatt.STATE_CONNECTED;
        } else {
            return false;
        }
    }

    /**
     * 断开连接设备
     */
    @Override
    public void disconnectDevice() {
        disconnectUsbDevice();
        disconnectBluDevice();
    }

    /**
     * 断开USB连接
     */
    private void disconnectUsbDevice() {
        if (mRobotUsbRequester != null) {
            mRobotUsbRequester.quit();
            mRobotUsbRequester = null;
        }
        mDevice = null;
    }


    /**
     * 断开当前连接的设备
     * Build.VERSION_CODES.JELLY_BEAN_MR2 以下的设备是不支持BLE的
     */
    private synchronized void disconnectBluDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //断开蓝牙
            if (isBleConnectionEnable()) {
                mBluetoothGatt.disconnect();
            } else {
                //.d("不是有效的连接，无法断开");
                mDevice = null;
            }
            if (robotGattCallback != null) {
                robotGattCallback.sIsWriting = false;
                robotGattCallback.queue.clear();
            }
            mBluetoothGatt = null;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        S.i("服务退出");
        stopForeground(true);
        unregisterReceiver(usbDeviceStateReceiver);
        unregisterReceiver(forceCloseReceiver);
        //关闭蓝牙链接
        if (mBluetoothGatt != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                mBluetoothGatt.disconnect();
                mBluetoothGatt = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            //释放内存缓冲区
            mDataBuffer.close();
            mDataBuffer = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //撤销所有回调注册
            mRegistedCallbacks.kill();
            mRegistedCallbacks = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        releaseUpgradMemery();
    }


    //释放内存
    private void releaseUpgradMemery() {
        try {
            mOtaRawIndex = 0;
            mFirmwareFingerPrinter = 0;
            bleFirmwareDataForUpgrade = null;
            mcuFirmwareDataForUpgrade = null;
            moduleDataForUpgrade = null;
            currentFirmwareDataForUpgrade = null;
            mOtaCache.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exitSafly() {
//        S.i("mBindCount:" + mBindCount,
//                "mDevice:" + mDevice,
//                "mBluetoothGatt:" + mBluetoothGatt,
//                "mRobotUsbRequester:" + mRobotUsbRequester);
        if (mBindCount > 0
                || mDevice != null
                || (mBluetoothGatt != null)
                || mRobotUsbRequester != null) {
            Toast.makeText(this, getString("pen_service_busy"), Toast.LENGTH_SHORT).show();
        } else {
            stopSelf();
        }
    }

    // todo 这里写逻辑的实现
    @Override
    public boolean setpwd4C7(String oldPwd, String newpwd) {
        byte[] array = new byte[13];
        byte[] oldPwdByteArray = oldPwd.getBytes();
        byte[] newpwdByteArray = newpwd.getBytes();
        System.arraycopy(oldPwdByteArray, 0, array, 0, 6);
        System.arraycopy(newpwdByteArray, 0, array, 6, 6);
        array[12] = 0x00;
        return execCommand(CMD_C8, array);
    }

    /**
     * C7 同步离线笔记时先要进行校验密码操作
     *
     * @param pwd
     */
    @Override
    public boolean checkPWDandSyncCommand(String pwd) {
        byte[] array = new byte[7];
        System.arraycopy(pwd.getBytes(), 0, array, 0, 6);
        array[7] = 0x00;
        return execCommand(CMD_A0, array);
    }

    @Override
    public void checkePenPressSupport(boolean flag) {
        for (int i = mRegistedCallbacks.beginBroadcast() - 1; i >= 0; i--) {
            try {
                mRegistedCallbacks.getBroadcastItem(i).onSupportPenPressureCheck(flag);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mRegistedCallbacks.finishBroadcast();


    }

    private void forceExitService() {
//        S.i("强制退出" + getPackageName());
        android.os.Process.killProcess(android.os.Process.getUidForName("cn.robot.pen.service.remote"));
        System.exit(0);
    }


    /**
     * Description: 强制关闭服务广播,收到该广播的时候判断如果
     * 执行强制关闭服务
     *
     * @author 王强 Email: 249346538@qq.com
     * @version 1.0
     * @date 2017/2/25
     */
    class ForceCloseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String pkgName = intent.getStringExtra(EXTR_RUNDING_SERVICE_PACKAGE_NAME);
            if (!TextUtils.equals(pkgName, getPackageName())) {
                forceExitService();
            }
        }
    }

    /**
     * Description: 监听USB插／拔／授权广播
     *
     * @author 王强 Email: 249346538@qq.com
     * @version 1.0
     * @date 2017/2/27
     */
    class UsbDeviceStateReceiver extends BroadcastReceiver {
        @Override
        public IBinder peekService(Context myContext, Intent service) {
            return super.peekService(myContext, service);
        }

        @Override
        public void onReceive(Context context, Intent i) {
            UsbDevice device = i.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            boolean permission = i.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
            String usbAction = i.getAction();
            Intent intent = new Intent(RobotPenServiceImpl.ACTION_PENSERVICE);
            intent.setAction(RobotPenServiceImpl.ACTION_PENSERVICE);
            intent.setPackage(context.getPackageName());
            intent.putExtra("data", device);
            intent.putExtra("usb_action", usbAction);
            intent.putExtra(EXTR_FROM_RECEIVER, true);
            intent.putExtra(UsbManager.EXTRA_PERMISSION_GRANTED, permission);
            context.startService(intent);
        }
    }
}