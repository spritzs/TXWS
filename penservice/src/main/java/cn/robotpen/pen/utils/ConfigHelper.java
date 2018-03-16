package cn.robotpen.pen.utils;

import android.os.Environment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import cn.robotpen.pen.model.ServiceConfig;

/**
 * Created by 王强 on 2017/2/7.
 * 简介：配置文件读写封装
 * 加密存储
 */

public class ConfigHelper {
    //配置文件目录
    static final String CONFIG_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/Android/data/cn.robotpen.app.core/";
    //配置文件名称
    private static final String SERVICE_PROVIDER_CONFIG = ".remote.config";
    private static final PropertyHelper helper = new PropertyHelper(CONFIG_DIR, SERVICE_PROVIDER_CONFIG);

    /**
     * 更新共享服务信息到配置文件
     *
     * @param pkgName        该共享服务的包名
     * @param serviceVersion 该共享服务的版本号
     */
    public static synchronized void registShareService(String pkgName, int serviceVersion) {
        helper.save(pkgName, String.valueOf(serviceVersion), true);
    }


    /**
     * 查找共享服务
     *
     * @return 服务配置列表
     */
    public static List<ServiceConfig> searchSharedServices() {
        Properties config = helper.getProperties();
        List<ServiceConfig> registServiceConfigs = new ArrayList<>();
        Iterator<Object> it = config.keySet().iterator();
        while (it.hasNext()) {
            String pkgName = (String) it.next();
            int serviceVersion = Integer.valueOf((String) config.get(pkgName));
            registServiceConfigs.add(new ServiceConfig(pkgName, serviceVersion));
        }
        return registServiceConfigs;
    }
}
