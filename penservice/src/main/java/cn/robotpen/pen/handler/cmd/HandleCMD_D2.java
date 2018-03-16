package cn.robotpen.pen.handler.cmd;

//import com.codingmaster.slib.S;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_D2;


public class HandleCMD_D2 extends RobotHandler<byte[]> {

    public HandleCMD_D2(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_D2) {
//            S.i(bytesHelper.bytes2Str(data));
            servicePresenter.sendModuleData(data[3]);
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }
}
