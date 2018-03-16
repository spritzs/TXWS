package cn.robotpen.pen;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.support.annotation.Keep;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

//import com.codingmaster.slib.S;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import cn.robotpen.pen.model.DeviceDescriptor;
import cn.robotpen.pen.model.ServiceConfig;
import cn.robotpen.pen.utils.ConfigHelper;
import cn.robotpen.pen.utils.PairedRecoder;


/**
 * Created by 王强 on 2017/1/10.
 * 简介：罗博智慧笔服务工具类
 * 功能：
 * 1.启动服务
 * 2.绑定服务
 */
@Keep
public class RobotPenServiceImpl implements RobotPenService {
    //笔服务Action
    public static final String ACTION_PENSERVICE = "cn.robotpen.app.penservice.RobotRemotePenService";
    public static final String EXTR_NOTIFICATION = "show_notification";
    public static final String EXTR_FROM_RECEIVER = "receiver_intent";

    private final String[] requiredPermissons = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public RobotPenServiceImpl(Context ctx) {
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) ctx,requiredPermissons,0);
        }
    }

    /**
     * 绑定到笔服务
     */
    @Override
    public void bindRobotPenService(Context ctx, ServiceConnection conn) {
        Intent intent = new Intent(ACTION_PENSERVICE);
        String pkg = getEnableSharedServicePkgName(ctx);
        intent.setPackage(pkg);
        ctx.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 接触绑定
     */
    @Override
    public void unBindRobotPenService(Context ctx, ServiceConnection conn) {
        ctx.unbindService(conn);
    }

    /**
     * 启动笔服务
     *
     * @param ctx Context
     */
    @Override
    @SuppressWarnings("MissingPermission")
    public void startRobotPenService(Context ctx) {
        startRobotPenService(ctx, false);
    }

    /**
     * 启动服务并不显示通知
     *
     * @param ctx              Context
     * @param showNotification true 显示罗博智慧笔通知
     */
    @Override
    public void startRobotPenService(Context ctx, boolean showNotification) {
        Intent intent = new Intent(ACTION_PENSERVICE);
        String pkg = getEnableSharedServicePkgName(ctx);
        intent.setPackage(pkg);
        intent.putExtra(EXTR_NOTIFICATION, showNotification);
        ctx.startService(intent);
    }

    @Override
    public DeviceDescriptor getLastConnectDevice() {
        return PairedRecoder.getLastPairedDevice();
    }

    @Override
    public List<DeviceDescriptor> getPairedHistory() {
        return PairedRecoder.getPairedHistory();
    }

    private String getEnableSharedServicePkgName(Context ctx) {
        ServiceConfig config = getEnableSharedServiceConfig(ctx);
        return config == null ? ctx.getPackageName() : config.getPkgName();
    }

    /**
     * 从配置文件读取服务提供进程，如果配置文件无效返回当前进程内置服务
     *
     * @return 服务名称
     */
    private ServiceConfig getEnableSharedServiceConfig(Context ctx) {
        ServiceConfig result = null;
        List<ServiceConfig> serviceConfigs = ConfigHelper.searchSharedServices();
        int currentServiceVersion = BuildConfig.VERSION_CODE;
        for (ServiceConfig config : serviceConfigs) {
            if (config.getVersion() >= currentServiceVersion
                    && isRobotPenServiceIntalled(ctx, config.getPkgName())) {
                result = config;
//                S.i(result.toString());
            }
        }
        return result;
    }

    /**
     * 判断笔服务组件是否安装
     *
     * @return true 安装
     */
    private boolean isRobotPenServiceIntalled(Context ctx, String p) {
        try {
            ctx.getPackageManager().getPackageInfo(p, PackageManager.GET_ACTIVITIES);
            getProcessName();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
//            S.i(processName);
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
