package cn.robotpen.pen.handler.cmd;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_CB;


public class HandleCMD_CB extends RobotHandler<byte[]> {
    public HandleCMD_CB(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_CB) {
            int type = bytesHelper.bytesToInteger(data);
            servicePresenter.cleanDeviceDataWithTypeCallback(type);
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }
}
