package cn.robotpen.pen.handler.gatt;


import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract.ServicePresenter;

import static cn.robotpen.pen.model.CMD.CMD_HEAD_ID;

//import cn.robotpen.utils.log.CLog;

/**
 * Created by 王强 on 2017/2/10.
 * 简介：
 */

public class GattaSyncHandler extends RobotHandler<byte[]> {
    public GattaSyncHandler(ServicePresenter servicePresenter) {
        super(servicePresenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[0] != CMD_HEAD_ID) {
            servicePresenter.reportOffLineNoteSyncProgress(data);
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }
}
