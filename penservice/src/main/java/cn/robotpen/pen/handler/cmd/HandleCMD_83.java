package cn.robotpen.pen.handler.cmd;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;
import cn.robotpen.pen.utils.BytesHelper;

import static cn.robotpen.pen.model.CMD.CMD_83;

/**
 * Created by 王强 on 2017/2/10.
 * 简介：
 */

public class HandleCMD_83 extends RobotHandler<byte[]> {
    BytesHelper bytesHelper;

    public HandleCMD_83(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
        bytesHelper = new BytesHelper();
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_83) {
            // FIXME: 17/6/12  这里需要处理低电量升级错误
            if (1 == bytesHelper.bytesToInteger(data[2]) && bytesHelper.bytesToInteger(data[3]) == 4) {
                servicePresenter.reportError("电量低于40%，不能升级新固件");
            }else {
                servicePresenter.reportError(bytesHelper.bytes2Str(data));
            }
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }
}
