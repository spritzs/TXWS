package cn.robotpen.pen.handler.cmd;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;
import cn.robotpen.pen.utils.BytesHelper;

import static cn.robotpen.pen.model.CMD.CMD_8B;

/**
 * 简介：
 *
 * @author 王强（249346528@qq.com） 2017/4/11.
 */

public class HandleCMD_8B extends RobotHandler<byte[]> {
    public HandleCMD_8B(RobotServiceContract.ServicePresenter servicePresenter) {
        super(servicePresenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_8B) {
//            servicePresenter.reportPageInfo(data[3], data[4]);
            BytesHelper bytesHelper = new BytesHelper();
            servicePresenter.reportPageInfo(bytesHelper.bytesToInteger(data[3]), bytesHelper.bytesToInteger(data[4]));
        } else {
            nextHandler.handle(data);
        }
    }
}
