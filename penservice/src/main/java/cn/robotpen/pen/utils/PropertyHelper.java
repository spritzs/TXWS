package cn.robotpen.pen.utils;

import android.support.annotation.Keep;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by 王强 on 2017/2/18.
 * 简介：
 */
@Keep
public class PropertyHelper {
    //配置文件目录
    private String dirName;
    //配置文件名称
    private String fileName;

    public PropertyHelper(String dirName, String fileName) {
        this.dirName = dirName;
        this.fileName = fileName;
    }

    @Keep
    public void save(String key, String value) {
        save(key, value, false);
    }

    /**
     * 保存
     *
     * @param key      key
     * @param value    value
     * @param override true覆盖保存
     */
    void save(String key, String value, boolean override) {
        Properties config = override ? new Properties() : getProperties();
        config.put(key, value);
        save(config);
    }

    /**
     * 保存配置文件到SD卡
     *
     * @param properties
     */
    private void save(Properties properties) {
        File configFile = loadConfigFile();
        OutputStream out = null;
        try {
            out = new FileOutputStream(configFile);
            properties.store(out, null);
        } catch (Exception e) {
            if (configFile != null && configFile.exists()) {
                configFile.delete();
            }
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Keep
    public void remove(String... keys) {
        Properties config = getProperties();
        for (String key : keys) {
            config.remove(key);
        }
        save(config);
    }

    /**
     * 清空记录
     */
    @Keep
    public void clear() {
        Properties properties = getProperties();
        properties.clear();
        save(properties);
    }

    Properties getProperties() {
        File configFile = loadConfigFile();
        InputStream in = null;
        Properties config = new Properties();
        try {
            in = new FileInputStream(configFile);
            config.load(in);
        } catch (Exception e) {
            e.printStackTrace();
            if (configFile != null && configFile.exists()) {
                configFile.delete();
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return config;
    }


    private File loadConfigFile() {
        File dir = new File(dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File configFile = new File(dir, fileName);
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return configFile;
    }

}
