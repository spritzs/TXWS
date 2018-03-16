package cn.robotpen.pen.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 王强 on 2017/2/18.
 * 简介：保存设备的连接信息
 */

public class DeviceDescriptor implements Parcelable {
    //设备名称--可以修改
    private String deviceName;
    private String mac;
    private int deviceType;
    private String versionName;
    private String deviceVersionName;

    public int getDeviceType() {
        return deviceType;
    }

    public String getMac() {
        return mac;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceVersion(){
        return versionName;
    }

    public void setDeviceVersion(String deviceVersionName) {
        this.deviceVersionName = deviceVersionName;
    }

    //设备mac地址
    public DeviceDescriptor(String mac) {
        this.mac = mac;
    }

    public DeviceDescriptor(String mac,String deviceName) {
        this.mac = mac;
        this.deviceName = deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deviceName);
        dest.writeString(this.mac);
        dest.writeInt(this.deviceType);
    }

    public DeviceDescriptor() {
    }

    protected DeviceDescriptor(Parcel in) {
        this.deviceName = in.readString();
        this.mac = in.readString();
        this.deviceType = in.readInt();
    }

    @Override
    public String toString() {
        return "DeviceDescriptor{" +
                "deviceName='" + deviceName + '\'' +
                ", mac='" + mac + '\'' +
                ", deviceType=" + deviceType +
                '}';
    }

    public static final Creator<DeviceDescriptor> CREATOR = new Creator<DeviceDescriptor>() {
        @Override
        public DeviceDescriptor createFromParcel(Parcel source) {
            return new DeviceDescriptor(source);
        }

        @Override
        public DeviceDescriptor[] newArray(int size) {
            return new DeviceDescriptor[size];
        }
    };


}
