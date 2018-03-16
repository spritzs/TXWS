package cn.robotpen.pen.handler.gatt;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract.ServicePresenter;

/**
 * Created by 王强 on 2017/2/10.
 * 简介：
 */

public class GattaErrorHandler extends RobotHandler<byte[]> {
    public GattaErrorHandler(ServicePresenter servicePresenter) {
        super(servicePresenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data == null || data.length < 3) {
            //不合法数据丢弃
        } else {
            if (nextHandler != null) {
                nextHandler.handle(data);
            }
        }
    }
}
