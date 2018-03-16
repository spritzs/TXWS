package cn.robotpen.pen.handler.cmd;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_D6;

public class HandleCMD_D6 extends RobotHandler<byte[]> {
    public HandleCMD_D6(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_D6) {
            servicePresenter.reportModuleUpgradeFinished();
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }
}
