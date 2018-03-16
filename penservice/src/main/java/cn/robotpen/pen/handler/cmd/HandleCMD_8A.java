package cn.robotpen.pen.handler.cmd;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.model.RobotDeviceType;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_8A;

/**
 * 简介：
 *
 * @author 王强（249346528@qq.com） 2017/4/11.
 */

public class HandleCMD_8A extends RobotHandler<byte[]> {
    public HandleCMD_8A(RobotServiceContract.ServicePresenter servicePresenter) {
        super(servicePresenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_8A) {
            reportPageAndOther(data);
        } else {
            nextHandler.handle(data);
        }
    }

    public void reportPageAndOther(byte data[]) {
        if (servicePresenter.getConnectedDevice().getDeviceVersion() == RobotDeviceType.T9A.getValue() ||
                servicePresenter.getConnectedDevice().getDeviceVersion() == RobotDeviceType.T9_J0.getValue()) {
            servicePresenter.reportPageNumberAndOther(0xff & data[3], 0xff & data[4]);
            servicePresenter.reportPageOnly(bytesHelper.byte2short(data[3],data[4]));
        } else {
            servicePresenter.reportPageInfo(0xff & data[3], 0);
        }
    }
}
