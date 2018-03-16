package cn.robotpen.pen.handler.cmd;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_D9;

//import cn.robotpen.utils.log.CLog;

public class HandleCMD_D9 extends RobotHandler<byte[]> {

    public HandleCMD_D9(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_D9) { //0xAA 0xD9 0x01 0x02
            if (data.length == 4) {
                servicePresenter.checkPenPressFinish(data);
            }
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }
}
