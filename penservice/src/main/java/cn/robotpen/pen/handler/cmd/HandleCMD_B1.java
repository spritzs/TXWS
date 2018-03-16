package cn.robotpen.pen.handler.cmd;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_B1;

/**
 * Created by 王强 on 2017/2/10.
 * 简介：进入OTA模式
 */

public class HandleCMD_B1 extends RobotHandler<byte[]> {
    public HandleCMD_B1(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_B1) {
            int type = (data[2] != 0) ? (data[3] & 0xff) : -1;
            servicePresenter.enterOtaMode(type);
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }
}
