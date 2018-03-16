package cn.robotpen.pen.handler.cmd;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.model.RobotDeviceType;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_B5;

/**
 * Created by 王强 on 2017/2/10.
 * 简介：固件切换(固件升级完成)
 */

public class HandleCMD_B5 extends RobotHandler<byte[]> {
    public HandleCMD_B5(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_B5) {
            int deviceType = bytesHelper.bytesToInteger(servicePresenter.getConnectedDevice().getHardwareVer());
            if (deviceType == RobotDeviceType.T8A.getValue() ||
                    deviceType == RobotDeviceType.T9A.getValue() ||
                    deviceType == RobotDeviceType.T9E.getValue() ||
                    deviceType == RobotDeviceType.X8.getValue()) {
                servicePresenter.updateDeviceState((byte) 0x04);
            }
            servicePresenter.reportFirmwareUpgradeFinished();
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }
}
