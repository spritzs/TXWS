package cn.robotpen.pen.model;

/**
 * Created by 王强 on 2017/2/9.
 * 简介：
 */

public class ServiceConfig{
    /**
     * 共享服务的包名
     */
    private String pkgName;
    /**
     * 共享服务的版本号
     * 一个设备可能会有多个不同版本的服务，如果当前运行的共享服务版本不是最新的，
     * 则关闭这个已经启动的共享服务，启动最新的共享服务代替。
     */
    private int version;

    public ServiceConfig() {
    }

    public ServiceConfig(String pkgName, int version) {
        this.pkgName = pkgName;
        this.version = version;
    }

    public String getPkgName() {
        return pkgName;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "ServiceConfig{" +
                "pkgName='" + pkgName + '\'' +
                ", version=" + version +
                '}';
    }
}
