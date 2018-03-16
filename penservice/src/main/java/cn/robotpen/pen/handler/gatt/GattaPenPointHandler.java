package cn.robotpen.pen.handler.gatt;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract.ServicePresenter;

import static cn.robotpen.pen.model.CMD.CMD_81;

/**
 * Created by 王强 on 2017/2/10.
 * 简介：
 */

public class GattaPenPointHandler extends RobotHandler<byte[]> {
    public GattaPenPointHandler(ServicePresenter servicePresenter) {
        super(servicePresenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_81) {
            //坐标点数据
            int len = data[2] & 0xff;
            byte[] pdata = new byte[len];
            System.arraycopy(data, 3, pdata, 0, len);
            servicePresenter.reportPenPosition(pdata);
        } else {
            if (nextHandler != null)
                nextHandler.handle(data);
        }
    }
}
