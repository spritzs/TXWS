package cn.robotpen.pen.handler.cmd;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;
import cn.robotpen.pen.utils.BytesHelper;

import static cn.robotpen.pen.model.CMD.CMD_88;


/**
 * Created by 王强 on 2017/2/10.
 * 简介：按键事件报告 1单击,2双击,3上翻，4下翻，5新建
 */

public class HandleCMD_88 extends RobotHandler<byte[]> {
    BytesHelper bytesHelper;

    public HandleCMD_88(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
        bytesHelper = new BytesHelper();
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_88) {
            servicePresenter.reportKeyEvent(bytesHelper.bytesToInteger(data[3]));
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }
}
