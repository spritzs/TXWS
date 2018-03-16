package cn.robotpen.pen.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by 王强 on 2016/12/6.
 * 简介：
 */

public class RobotDevice implements Parcelable {
    /**
     * 设备类型
     *
     * @return
     */
    private int deviceVersion = 0;
    /**
     * 设备名字
     */
    private String name;
    /**
     * 设备地址
     */
    private String address;
    /**
     * 硬件版本
     */
    private byte[] hardwareVer;
    /**
     * 蓝牙固件版本
     */
    private byte[] firmwareVer;
    /**
     * mcu固件版本
     */
    private byte[] mcuVer;

    /**
     * 模组固件版本
     */
    private byte[] jediVer;

    /**
     * 电池电量
     */
    private byte battery;
    /**
     * 离线笔记数量
     */
    private byte offlineNoteNum;
    /**
     * 连接方式 0 蓝牙 ,1 USB
     */
    private int connectType;

    /**
     * 是否已经同步过事件
     */
    private boolean syncDateTime;


    public RobotDevice(String name, String address, int connType) {
        this.name = name;
        this.address = address;
        this.connectType = connType;
        hardwareVer = new byte[0];
        firmwareVer = new byte[0];
    }

    protected RobotDevice(Parcel in) {
        this.deviceVersion = in.readInt();
        this.name = in.readString();
        this.address = in.readString();
        this.hardwareVer = in.createByteArray();
        this.firmwareVer = in.createByteArray();
        this.battery = in.readByte();
        this.offlineNoteNum = in.readByte();
        this.connectType = in.readInt();
        this.mcuVer = in.createByteArray();
        this.jediVer = in.createByteArray();
    }

    public static final Creator<RobotDevice> CREATOR = new Creator<RobotDevice>() {
        @Override
        public RobotDevice createFromParcel(Parcel source) {
            return new RobotDevice(source);
        }

        @Override
        public RobotDevice[] newArray(int size) {
            return new RobotDevice[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.deviceVersion);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeByteArray(this.hardwareVer);
        dest.writeByteArray(this.firmwareVer);
        dest.writeByte(battery);
        dest.writeByte(offlineNoteNum);
        dest.writeInt(connectType);
        dest.writeByteArray(this.mcuVer);
        dest.writeByteArray(this.jediVer);
    }

    public int getConnectType() {
        return connectType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public byte[] getHardwareVer() {
        return hardwareVer;
    }

    public void setHardwareVer(byte[] hardwareVer) {
        this.hardwareVer = hardwareVer;
    }

    public byte[] getFirmwareVer() {
        return firmwareVer;
    }

    public String getHardwareVerStr() {
        return getVStr(hardwareVer);
    }


    public boolean isSyncDateTime() {
        return syncDateTime;
    }

    public void setSyncDateTime(boolean syncDateTime) {
        this.syncDateTime = syncDateTime;
    }

    /**
     * use {@link #setBleFirmwareVersion(byte[])} instead
     *
     * @param firmwareVer
     */
    @Deprecated
    public void setFirmwareVer(byte[] firmwareVer) {
        this.firmwareVer = firmwareVer;
    }

    /**
     * 设置当前蓝牙固件的版本号
     *
     * @param ver
     */
    public void setBleFirmwareVersion(byte[] ver) {
        this.firmwareVer = ver;
    }

    public void setMcuFirmwareVer(byte[] mcuVer) {
        this.mcuVer = mcuVer;
    }


    /**
     * 设置模组固件版本
     *
     * @param jediVer
     */
    public void setJediVer(byte[] jediVer) {
        this.jediVer = jediVer;
    }

    /**
     * use {@link #getBleFirmwareVerStr()} instead
     *
     * @return
     */
    @Deprecated
    public String getFirmwareVerStr() {
        return getVStr(firmwareVer);
    }


    /**
     * 获取当前的蓝牙固件版本
     *
     * @return 蓝牙固件版本
     */
    public String getBleFirmwareVerStr() {
        return getVStr(firmwareVer);
    }

    public String getMcuFirmwareVerStr() {
        return getVStr(mcuVer);
    }

    /**
     * 获取当前模组固件
     *
     * @return
     */
    public String getJediVerStr() {
        return getJedStr(jediVer);
    }


    public int getDeviceVersion() {
        return deviceVersion;
    }

    @Deprecated
    public int getDeviceType() {
        return deviceVersion;
    }

    public void setDeviceVersion(int deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    private String getVStr(byte[] data) {
        if (data == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (byte b : data) {
            sb.append(((int) b & 0xff)).append(".");
        }
        int len = sb.length();
        return len > 1 ? sb.substring(0, len - 1) : "";
    }

    public String getJedStr(byte[] data) {
        if (data == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (byte b : data) {
            if (Math.abs((int) b & 0xff) == 128) {
                sb.append(0).append(".");
            } else {
                sb.append(((int) b & 0xff)).append(".");
            }
        }
        int len = sb.length();

        return len > 1 ? sb.substring(0, len - 1) : "";
    }


    /**
     * use {@link #getBatteryEm()} instead
     */
    @Deprecated
    public int getBattery() {
        int value = battery & 0xff;
        return value;
    }


    public Battery getBatteryEm() {
        int value = battery & 0xff;
        Battery battery =  new Battery(value);
        return battery;
    }


    public void setBattery(byte battery) {
        this.battery = battery;
    }

    public int getOfflineNoteNum() {
        return offlineNoteNum & 0xff;
    }

    public void setOfflineNoteNum(byte offlineNoteNum) {
        this.offlineNoteNum = offlineNoteNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "RobotDevice{" +
                "deviceVersion=" + deviceVersion +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", hardwareVer=" + Arrays.toString(hardwareVer) +
                ", firmwareVer=" + Arrays.toString(firmwareVer) +
                ", battery=" + battery +
                ", offlineNoteNum=" + offlineNoteNum +
                ", connectType=" + connectType +
                ", jediVer = " + Arrays.toString(jediVer) +
                '}';
    }
}
