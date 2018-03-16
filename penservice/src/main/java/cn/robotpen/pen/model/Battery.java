package cn.robotpen.pen.model;

import android.os.Parcel;
import android.os.Parcelable;


public class Battery implements Parcelable {

    public int value;

    public Battery(int value) {
        this.value = value;
    }

    public AllBatteryType getALLBatteryType() {
        AllBatteryType allType = AllBatteryType.PERCENT_0;
        switch (this.value) {
            case 1:
                allType = AllBatteryType.PERCENT_0;
                break;
            case 2:
                allType = AllBatteryType.PERCENT_10;
                break;
            case 3:
                allType = AllBatteryType.PERCENT_20;
                break;
            case 4:
                allType = AllBatteryType.PERCENT_50;
                break;
            case 5:
                allType = AllBatteryType.PERCENT_75;
                break;
            case 6:
                allType = AllBatteryType.PERCENT_90;
                break;
            case 7:
                allType = AllBatteryType.PERCENT_100;
                break;
            case 255:
                allType = AllBatteryType.PERCENT_CHARGING_FULL;
                break;
            case 254:
                allType = AllBatteryType.PERCENT_CHARGING;
                break;
        }
        return allType;
    }

    protected Battery(Parcel in) {
        value = in.readInt();
    }

    public static final Creator<Battery> CREATOR = new Creator<Battery>() {
        @Override
        public Battery createFromParcel(Parcel in) {
            return new Battery(in);
        }

        @Override
        public Battery[] newArray(int size) {
            return new Battery[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(value);
    }


}
