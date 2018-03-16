package cn.robotpen.pen.utils;


/**
 * Created by tom on 2017/7/7.
 */

public class Utils {

    public static String reverseMac(String mac) {
        String[] macArray = mac.split(":");
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = macArray.length - 1; i >= 0; i--) {
            stringBuffer.append(macArray[i]);
            if (i > 0) {
                stringBuffer.append(":");
            }
        }
        return stringBuffer.toString();
    }
}
