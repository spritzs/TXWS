package cn.robotpen.pen.handler.cmd;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_C9;


public class HandleCMD_C9 extends RobotHandler<byte[]> {
    public HandleCMD_C9(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_C9) {
            servicePresenter.checkPenPressing();
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }
}
