package cn.robotpen.pen.model;

/**
 * 简介：
 *
 * @author 王强（249346528@qq.com） 2017/3/14.
 *         <p>
 *         updated by luis 2017/04/25 增加J0-A5/J0-A4/T9A/X8
 */

public enum RobotDeviceType {
    TOUCH(0),
    P7(1),
    ELITE(2),
    ELITE_PLUS(3),
    P1(4),
    ELITE_PLUS_NEW(5),
    T8A(6),
    XY(7),

    J0_A5(8),
    J0_A4(11),
    J0_A4_P(19),
    T9_J0(18),
    J0_T9(21),

    T9A(12),
    T7PL(14),
    T7E(15),
    T7_TS(16),
    T7_LW(17),
    X8(13),
    C7(24),

    T9E(20);


    RobotDeviceType(int value) {
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
