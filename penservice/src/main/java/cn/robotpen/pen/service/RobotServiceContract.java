package cn.robotpen.pen.service;

import android.app.Service;
import android.hardware.usb.UsbDevice;
import android.support.annotation.NonNull;

import cn.robotpen.pen.IRemoteRobotServiceCallback;
import cn.robotpen.pen.model.RobotDevice;

/**
 * Created by 王强 on 2017/2/9.
 * 简介：
 */

public interface RobotServiceContract {
    interface BasePresenter {
        /**
         * 获取当前连接设备的信息
         *
         * @return 当前连接设备
         */
        RobotDevice getConnectedDevice();

        /**
         * 执行命令
         *
         * @param command 命令
         * @param data    数据
         * @return true成功
         */
        boolean execCommand(byte command, final byte... data);

        String getString(String resName, Object... arg);
    }


    /**
     * 当前连接设备状态改变回调
     */
    interface DeviceInfoCallback extends BasePresenter {
        /**
         * 设置当前连接的设备信息
         *
         * @param device 设备
         */
        void onDeviceChanged(RobotDevice device);

        /**
         * 更新离线笔记数量
         *
         * @param offlineNoteCount 离线笔记数量
         */
        void updateDeviceOfflineNote(byte offlineNoteCount);

        /**
         * 更新设备当前电量共7格 1~7
         * 0x00错误，0xFF充电中
         *
         * @param battery 电量值
         */
        void updateDeviceBattery(byte battery);

        /**
         * 更新设备当前状态
         * 0x04 笔模式，0x0A同步模式，0x06 OTA模式
         *
         * @param state 状态值
         */
        void updateDeviceState(byte state);

        /**
         * 更新当前连接的设备型号
         *
         * @param deviceType 型号
         */
        void updateDeviceType(int deviceType);

        /**
         * 更新当前连接设备的硬件版本号
         *
         * @param hardVersion 硬件版本号
         */
        void updateConnectedDeviceHardwareVersion(byte[] hardVersion);

        /**
         * 更新当前连接设备的固件版本好
         *
         * @param firmVersion 固件版本号
         */
        void updateConnectedDeviceFirmwareVersion(byte[] firmVersion);


        /**
         * 获取当前设备得模组版本
         *
         * @param moduleVersion
         */
        void updateConnectedDeviceModuleVersion(byte[] moduleVersion);


        void checkePenPressSupport(boolean flag);

    }

    /**
     * {@link RobotRemotePenService} 交互接口
     */
    interface BinderPresenter extends DeviceInfoCallback {
        Service getService();

        /**
         * 注册客户端
         *
         * @param callback 回调接口
         */
        void registClient(IRemoteRobotServiceCallback callback);

        /**
         * 解除注册客户端
         *
         * @param callback 回调接口
         */
        void unregistClient(IRemoteRobotServiceCallback callback);

        /**
         * 升级模组
         *
         * @param moduleVersion      固件版本号
         * @param moduleFirmwareData 固件数据
         * @return 命令直接结果，不是固件升级结果，true成功
         */
        boolean startUpdateModule(String moduleVersion, byte[] moduleFirmwareData);


        /**
         * 升级固件
         *
         * @param firmwareVersion 固件版本号
         * @param firmwareData    固件数据
         * @return 命令直接结果，不是固件升级结果，true成功
         */
        @Deprecated
        boolean startUpdateFirmware(String firmwareVersion, byte[] firmwareData);

        /**
         * 固件升级
         *
         * @param bleFirmwareVersion 蓝牙固件版本
         * @param bleFirmwareData    蓝牙固件
         * @param mcuFirmwareVersion mcu固件版本
         * @param mcuFirmwareData    mcu固件
         * @return 命令直接结果，不是固件升级结果，true成功
         */
        boolean startUpdateFirmware(@NonNull String bleFirmwareVersion,
                                    byte[] bleFirmwareData,
                                    @NonNull String mcuFirmwareVersion,
                                    byte[] mcuFirmwareData);

        /**
         * 0x04 笔模式，0x0A同步模式，0x06 OTA模式
         *
         * @return 状态
         */
        byte getDeviceState();

        /**
         * 连接蓝牙设备
         *
         * @param addr 地址
         * @return 状态true 成功
         */
        boolean connectBlutoothDevice(String addr);

        /**
         * 连接USB设备
         *
         * @param device usb设备
         * @return true 成功
         */
        boolean connectUsbDevice(UsbDevice device);

        /**
         * 断开当前连接设备
         */
        void disconnectDevice();

        /**
         * 安全退出服务
         */
        void exitSafly();

        /**
         * C7 设置密码
         *
         * @param oldPwd
         * @param newpwd
         */
        boolean setpwd4C7(String oldPwd, String newpwd);

        /**
         * C7 同步离线笔记
         */

        boolean checkPWDandSyncCommand(String pwd);

    }

    /**
     * {@link RobotRemotePenService}
     */
    interface ServicePresenter extends BinderPresenter {
        /**
         * 设备当前的连接状态
         *
         * @param state 状态
         * @param addr  地址
         */
        void reportState(int state, String addr);

        /**
         * 错误
         *
         * @param error 错误信息
         */
        void reportError(String error);

        /**
         * 坐标点原始数据
         *
         * @param data 数据
         */
        void reportPenPosition(byte[] data);

        /**
         * 离线笔记头信息
         *
         * @param headJsonInfo 头信息
         */
        void handleOfflineNoteHeadInfo(byte[] headJsonInfo);

        /**
         * 离线笔记同步进度
         *
         * @param data 进度
         */
        void reportOffLineNoteSyncProgress(byte[] data);

        /**
         * 离线笔记同步完成
         */
        void reportOffLineNoteSyncFinished();

        /**
         * 硬件设备的按键通知
         *
         * @param keyEvent 事件
         */
        void reportKeyEvent(int keyEvent);

        /**
         * 当前翻页信息
         *
         * @param currentPage 当前页码
         * @param totalPage   总页码
         */
        void reportPageInfo(int currentPage, int totalPage);


        /**
         * 上报页码信息和页码上分类信息
         *
         * @param currentPage 当前页码
         * @param category    总页码
         */
        void reportPageNumberAndOther(int currentPage, int category);

        /**
         * 只上报页码.
         * @param pageNumber
         */
        void reportPageOnly(short pageNumber);

        /**
         * 固件升级进度
         *
         * @param progress 当前进度
         * @param total    总长度
         */
        void reportFirmwareUpgradeProgress(int progress, int total, String info);

        /**
         * 固件升级完成
         */
        void reportFirmwareUpgradeFinished();

        /**
         * 进入固件升级模式
         *
         * @param type -1 - default elite 0 - nebula gateway file 1 - nebula node mcu file 2 -
         *             nebula node bt file 3 - jedi A4 mcu file 4 - jedi A5 mcu file
         */
        void enterOtaMode(int type);

        /**
         * 发送固件数据
         *
         * @param b 数据
         */
        void sendFirmwareData(byte b);

        /**
         * 发送固件校验值
         */
        void responseFirmwareDataFingerprinter();

        /**
         * 执行OTA固件升级
         *
         * @param arg 指纹信息
         */
        void executeOtaFirmwareUpgrade(byte arg);


        /**
         * 发送模组数据
         *
         * @param b 数据
         */
        void sendModuleData(byte b);

        /**
         * 发送模组校验值
         */
        void responseModuleDataFingerprinter();


        /**
         * 固件升级完成
         */
        void reportModuleUpgradeFinished();


        /**
         * 校验笔的压感结束
         */
        void checkPenPressing();

        /**
         * 校验笔的压感结束
         *
         * @param data
         */
        void checkPenPressFinish(byte[] data);

        /**
         * data:数据内容(校验位使用 CRC8或者checksum), 密码使用 ASC-II编码。
         * C7设置密码
         * Result： 00-success; 01-原密码错误;02设置的密码有误
         *
         * @param type
         */
        void setSyncPassWordWithOldPassWordCallback(int type);

        /**
         * 开启上报轨迹
         * Result： 00-success; 01-fail;
         */
        void opneReportedDataCallback(int type);

        /**
         * 停止上报轨迹
         * Result： 00-success; 01-fail;
         */
        void closeReportedDataCallback(int type);

        /**
         * 清除数据指令
         *
         * @param type :1.单纯擦除 2.擦除并清除离线笔记 3.擦除并新建离线笔记
         */
        void cleanDeviceDataWithTypeCallback(int type);

        /**
         * 00 succ
         * 01 password error
         * 02 password null
         *
         * @param code
         */
        void startSyncNoteWithPassWordCallback(int code);

    }
}
