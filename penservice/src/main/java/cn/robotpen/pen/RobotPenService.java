package cn.robotpen.pen;

import android.Manifest;
import android.content.Context;
import android.content.ServiceConnection;
import android.support.annotation.RequiresPermission;

import java.util.List;

import cn.robotpen.pen.model.DeviceDescriptor;

/**
 * Created by 王强 on 2017/1/10.
 * 简介：罗博智能笔服务api接口
 */
public interface RobotPenService {
    /**
     * 绑定到罗博智慧笔服务
     *
     * @param ctx  Context
     * @param conn 连接对象
     */
    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void bindRobotPenService(Context ctx, ServiceConnection conn);

    /**
     * 从罗博智慧笔服务解绑
     *
     * @param ctx  Context
     * @param conn 连接对象
     */
    void unBindRobotPenService(Context ctx, ServiceConnection conn);

    /**
     * 启动罗博智能笔服务
     *
     * @param ctx Context
     */
    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void startRobotPenService(Context ctx);

    /**
     * 启动罗博智能笔服务
     *
     * @param ctx              Context
     * @param showNotification true 显示罗博智慧笔通知
     */
    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void startRobotPenService(Context ctx, boolean showNotification);


    /**
     * 获取上一次的连接设备信息
     *
     * @return 设备描述
     */
    DeviceDescriptor getLastConnectDevice();

    /**
     * 获取连接设备历史记录
     *
     * @return 配对信息描述
     */
    List<DeviceDescriptor> getPairedHistory();
}
