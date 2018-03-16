package cn.robotpen.pen.handler.cmd;

//import com.codingmaster.slib.S;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_D1;
import static cn.robotpen.pen.model.CMD.CMD_D6;


public class HandleCMD_D1 extends RobotHandler<byte[]> {

    int count = 0;

    public HandleCMD_D1(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_D1) {
//            S.i("the data :" + bytesHelper.bytes2Str(data) + "----count is :====" + count);
            if (count > 3) {
                servicePresenter.execCommand(CMD_D6);
                count = 0;
            } else {
                servicePresenter.enterOtaMode(5);
            }
            count++;
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }
}
