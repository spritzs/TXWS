package cn.robotpen.pen.model;

/**
 * Created by tom on 2017/8/9.
 */

public enum AllBatteryType {
    PERCENT_CHARGING_FULL(255),
    PERCENT_CHARGING(254),

    PERCENT_100(7),
    PERCENT_90(6),

    PERCENT_75(5),
    PERCENT_50(4),

    PERCENT_20(3),
    PERCENT_10(2),

    PERCENT_0(1);

    AllBatteryType(int value) {
        this.value = value;
    }

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
