package cn.robotpen.pen.handler.cmd;

//import com.codingmaster.slib.S;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.model.RemoteState;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_80;
import static cn.robotpen.pen.model.CMD.CMD_84;
import static cn.robotpen.pen.model.CMD.CMD_A2;

//import cn.robotpen.utils.log.CLog;

/**
 * Created by 王强 on 2017/2/10.
 * 简介：
 */

public class HandleCMD_80 extends RobotHandler<byte[]> {
    public HandleCMD_80(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_80) {
//            S.i(bytesHelper.bytes2Str(data));
            handleDeviceState(data);
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }

    /**
     * 处理设备发送过来的设备信息，包含硬件状态数据 电量等信息
     * 发送时机：
     * 1.初次和设备建立连接的时候
     * 2.连接状态改变的时候
     *
     * @param data
     */
    private void handleDeviceState(byte[] data) {
        servicePresenter.updateDeviceOfflineNote(data[5]);
        servicePresenter.updateDeviceBattery(data[4]);
        byte state = data[3];
        servicePresenter.updateDeviceState(state);
        if (state == 0x0A) {
            //进入同步模式
            servicePresenter.reportState(RemoteState.STATE_ENTER_SYNC_MODE_SUCCESS, String.valueOf(data[5] & 0xff));
            servicePresenter.execCommand(CMD_A2);
        } else if (servicePresenter.getConnectedDevice() != null && state == 0x04) {
            servicePresenter.reportState(RemoteState.STATE_CONNECTED, servicePresenter.getConnectedDevice().getAddress());
            servicePresenter.execCommand(CMD_84);
        }
    }
}
