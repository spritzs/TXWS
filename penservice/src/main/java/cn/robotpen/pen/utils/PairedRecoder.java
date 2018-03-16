package cn.robotpen.pen.utils;

import android.support.annotation.Keep;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cn.robotpen.pen.model.DeviceDescriptor;

/**
 * Created by 王强 on 2017/2/18.
 * 简介：记录配对设备信息
 */
@Keep
public class PairedRecoder {
    //设备配对信息保存文件
    private static final String PAIRED_CONFIG = ".paired.config";
    private static final String LAST_PAIRED = ".last_paired.config";
    private static final PropertyHelper pairedHelper = new PropertyHelper(ConfigHelper.CONFIG_DIR, PAIRED_CONFIG);
    private static final PropertyHelper lastPairedHelper = new PropertyHelper(ConfigHelper.CONFIG_DIR, LAST_PAIRED);

    public static DeviceDescriptor getLastPairedDevice() {
        Properties config = lastPairedHelper.getProperties();
        if (config.isEmpty() || config.size() < 3) {
            return null;
        }
        String name = config.getProperty("name");
        String mac = config.getProperty("mac");
        String versionName = config.getProperty("version");
        int type = Integer.parseInt(config.getProperty("type"));
        DeviceDescriptor descriptor = new DeviceDescriptor(mac, name);
        descriptor.setDeviceType(type);
        descriptor.setDeviceVersion(versionName);
        return descriptor;
    }

    /**
     * 保存配对信息
     *
     * @param mac        mac地址
     * @param deviceName 设备名称
     * @param deviceType 类型
     */
    public synchronized static void save(String mac, String deviceName, int deviceType) {
        if(mac!=null&&deviceName!=null) {
            lastPairedHelper.save("mac", mac);
            lastPairedHelper.save("name", deviceName);
            lastPairedHelper.save("type", String.valueOf(deviceType));
            pairedHelper.save(mac, deviceName);
        }
    }


    /**
     * 保存配对信息
     *
     * @param mac        mac地址
     * @param deviceName 设备名称
     * @param deviceType 类型
     * @param version    版本号
     */
    public synchronized static void save(String mac, String deviceName, int deviceType, String version) {
        lastPairedHelper.save("mac", mac);
        lastPairedHelper.save("name", deviceName);
        lastPairedHelper.save("type", String.valueOf(deviceType));
        lastPairedHelper.save("version", version);
        pairedHelper.save(mac, deviceName);

    }


    /**
     * 获取配对记录
     *
     * @return 配置列表
     */
    public static List<DeviceDescriptor> getPairedHistory() {
        List<DeviceDescriptor> pairedDevices = new ArrayList<>();
        Properties properties = pairedHelper.getProperties();
        for (String key : properties.stringPropertyNames()) {
            pairedDevices.add(new DeviceDescriptor(key, properties.getProperty(key)));
        }
        return pairedDevices;
    }

    /**
     * 获取配对记录
     *
     * @return 配置列表
     */
    public static Map<String, String> getPairedMap() {
        Map<String, String> pairedMap = new HashMap<>();
        Properties properties = pairedHelper.getProperties();
        for (String mac : properties.stringPropertyNames()) {
            pairedMap.put(mac, properties.getProperty(mac));
        }
        return pairedMap;
    }

    public synchronized static void remove(String key) {
        String lastMac = lastPairedHelper.getProperties().getProperty("mac");
        if (TextUtils.equals(key, lastMac)) {
            lastPairedHelper.clear();
        }
        pairedHelper.remove(key);
    }

    public synchronized static void clear() {
        pairedHelper.clear();
    }
}
