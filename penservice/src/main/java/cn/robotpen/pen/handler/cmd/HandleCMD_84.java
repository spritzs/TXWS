package cn.robotpen.pen.handler.cmd;

import java.util.Calendar;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.model.RemoteState;
import cn.robotpen.pen.model.RobotDevice;
import cn.robotpen.pen.service.RobotServiceContract;
import cn.robotpen.pen.utils.PairedRecoder;

import static cn.robotpen.pen.model.CMD.CMD_84;
import static cn.robotpen.pen.model.CMD.CMD_87;

/**
 * Created by 王强 on 2017/2/10.
 * 简介：Host发送获取状态命令给slave，slave返回当前设备的硬件与软件版本号。
 */

public class HandleCMD_84 extends RobotHandler<byte[]> {
    public HandleCMD_84(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_84) {
            handleDeviceInfo(data);
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }

    /**
     * 解析设备信息
     *
     * @param data 元数据
     */
    private void handleDeviceInfo(byte[] data) {
        servicePresenter.updateDeviceType(data[3] & 0xff);
        byte[] hw = new byte[2];
        hw[0] = data[4];
        hw[1] = data[3];
        servicePresenter.updateConnectedDeviceHardwareVersion(hw);
        byte[] sw = new byte[4];
        sw[0] = data[8];
        sw[1] = data[7];
        sw[2] = data[6];
        sw[3] = data[5];
        servicePresenter.updateConnectedDeviceFirmwareVersion(sw);//这里取得固件得版本号
        RobotDevice device = servicePresenter.getConnectedDevice();
        PairedRecoder.save(device.getAddress(), device.getName(), device.getDeviceVersion(), device.getBleFirmwareVerStr());
        if (device == null || !device.isSyncDateTime()) {
            device.setSyncDateTime(syncDeviceDate());
        }
        servicePresenter.reportState(RemoteState.STATE_DEVICE_INFO, servicePresenter.getConnectedDevice().getAddress());
    }

    /**
     * 同步时间
     *
     * @return
     */
    boolean syncDeviceDate() {
        //同步设备日期
        Calendar calendar = Calendar.getInstance();
        byte[] data = new byte[5];
        data[0] = (byte) (calendar.get(Calendar.YEAR) - 2000);
        data[1] = (byte) (calendar.get(Calendar.MONTH) + 1);
        data[2] = (byte) calendar.get(Calendar.DATE);
        data[3] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        data[4] = (byte) calendar.get(Calendar.MINUTE);
        return servicePresenter.execCommand(CMD_87, data);
    }
}
