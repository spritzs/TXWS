package cn.robotpen.pen.handler.cmd;

//import com.codingmaster.slib.S;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_D3;


public class HandleCMD_D3 extends RobotHandler<byte[]> {
    public HandleCMD_D3(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_D3) {
//            S.i(bytesHelper.bytes2Str(data));
            servicePresenter.responseModuleDataFingerprinter();
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }
}
