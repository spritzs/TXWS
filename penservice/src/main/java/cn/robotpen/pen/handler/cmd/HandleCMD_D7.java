package cn.robotpen.pen.handler.cmd;

//import com.codingmaster.slib.S;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_D7;


public class HandleCMD_D7 extends RobotHandler<byte[]> {
    public HandleCMD_D7(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_D7) {
//            S.i(bytesHelper.bytes2Str(data));
            getReceiveDataInfo(data);
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }

    public void getReceiveDataInfo(byte[] data) {
        if ((data[2] & 0xff) >= 3) {
            byte[] t = new byte[2];
            t[0] = data[4];
            t[1] = data[3];
            if (bytesHelper.bytesToInteger(data[4]) == 128) {
                servicePresenter.getConnectedDevice().setJediVer(t);
                servicePresenter.updateConnectedDeviceModuleVersion(data);
            }
        }
    }
}
