package cn.robotpen.pen.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 王强 on 2016/12/6.
 * 简介：
 * Updated by luis on 2017/04/25
 * 说明：增加noteNumber用于标识相同笔记
 */

public class OffLineNoteHead implements Parcelable {
    int createTime;
    int deviceType;
    int dataCount;
    int noteNumber = 0;
    int category;

    public OffLineNoteHead(int createTime, int deviceType, int dataCount) {
        this.createTime = createTime;
        this.deviceType = deviceType;
        this.dataCount = dataCount;
    }


    public OffLineNoteHead(int createTime, int deviceType, int dataCount, int category, int noteNumber) {
        this.createTime = createTime;
        this.deviceType = deviceType;
        this.dataCount = dataCount;
        this.category = category;
        this.noteNumber = noteNumber;
    }


    public OffLineNoteHead() {
    }

    public String jsonStr() {
        return String.format("{\"createTime\":%s,\"deviceType\":%s,\"dataCount\":%s,\"noteNumber\":%s,\"category\":%s}",
                createTime, deviceType, dataCount, noteNumber, category);
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getDataCount() {
        return dataCount;
    }

    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    public int getNoteNumber() {
        return noteNumber;
    }

    public void setNoteNumber(int noteNumber) {
        this.noteNumber = noteNumber;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.createTime);
        dest.writeInt(this.deviceType);
        dest.writeInt(this.dataCount);
        dest.writeInt(this.noteNumber);
        dest.writeInt(this.category);
    }

    protected OffLineNoteHead(Parcel in) {
        this.createTime = in.readInt();
        this.deviceType = in.readInt();
        this.dataCount = in.readInt();
        this.noteNumber = in.readInt();
        this.category = in.readInt();
    }

    public static final Creator<OffLineNoteHead> CREATOR = new Creator<OffLineNoteHead>() {
        @Override
        public OffLineNoteHead createFromParcel(Parcel source) {
            return new OffLineNoteHead(source);
        }

        @Override
        public OffLineNoteHead[] newArray(int size) {
            return new OffLineNoteHead[size];
        }
    };
}
