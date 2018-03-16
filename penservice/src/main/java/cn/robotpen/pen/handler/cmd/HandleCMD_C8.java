package cn.robotpen.pen.handler.cmd;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_C8;


public class HandleCMD_C8 extends RobotHandler<byte[]> {
    public HandleCMD_C8(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_C8) {
            int type = bytesHelper.bytesToInteger(data);
            servicePresenter.setSyncPassWordWithOldPassWordCallback(type);
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }
}
