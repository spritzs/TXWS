package cn.robotpen.pen.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 王强 on 2016/12/10.
 * 简介：
 */

class RPoint implements Parcelable {
    public static final Creator<RPoint> CREATOR = new Creator<RPoint>() {
        @Override
        public RPoint createFromParcel(Parcel source) {
            return new RPoint(source);
        }

        @Override
        public RPoint[] newArray(int size) {
            return new RPoint[size];
        }
    };
    int x;          //x 坐标值
    int y;          //y 坐标值
    int presure = 1;    //压感值
    /**
     * 状态
     * 0x00 -- PEN离开
     * 0x10 -- PEN悬空
     * 0x11 -- PEN压下
     */
    byte state;

    public RPoint(int x, int y, int presure, byte state) {
        this.x = x;
        this.y = y;
        this.presure = presure;
        this.state = state;
    }

    public RPoint() {
    }

    protected RPoint(Parcel in) {
        this.x = in.readInt();
        this.y = in.readInt();
        this.presure = in.readInt();
        this.state = in.readByte();
    }

    @Override
    public String toString() {
        return "RPoint{" +
                "x=" + x +
                ", y=" + y +
                ", presure=" + presure +
                ", state=" + state +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getPresure() {
        return presure;
    }

    public void setPresure(int presure) {
        this.presure = presure;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.x);
        dest.writeInt(this.y);
        dest.writeInt(this.presure);
        dest.writeByte(this.state);
    }
}
