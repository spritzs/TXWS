package cn.robotpen.pen.utils;

import android.content.Context;

/**
 * Created by 王强 on 2017/2/13.
 * 简介：
 */

public class RobotResoureAdapter {
    public static int getStringResourceId(Context c, String resName) {
        return getResourceByType(c, "string", resName);
    }

    public static int getDrawableResourceId(Context c, String resName) {
        return getResourceByType(c, "drawable", resName);
    }

    private static int getResourceByType(Context c, String type, String resName) {
        return c.getResources().getIdentifier(resName, type, c.getPackageName());
    }
}
