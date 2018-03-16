package cn.robotpen.pen.handler.cmd;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_CA;


public class HandleCMD_CA extends RobotHandler<byte[]> {
    public HandleCMD_CA(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_CA) {
            int type = bytesHelper.bytesToInteger(data);
            servicePresenter.closeReportedDataCallback(type);
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }
}
