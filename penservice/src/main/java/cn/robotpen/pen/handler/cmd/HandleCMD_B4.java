package cn.robotpen.pen.handler.cmd;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_B4;


/**
 * Created by 王强 on 2017/2/10.
 * 简介：
 */

public class HandleCMD_B4 extends RobotHandler<byte[]> {
    public HandleCMD_B4(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_B4) {
            servicePresenter.executeOtaFirmwareUpgrade(data[3]);
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }
}
