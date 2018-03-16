package cn.robotpen.pen.handler.cmd;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_A2;


/**
 * Created by 王强 on 2017/2/10.
 * 简介：//返回一个笔记头信息
 */

public class HandleCMD_A2 extends RobotHandler<byte[]> {
    public HandleCMD_A2(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_A2) {
            servicePresenter.handleOfflineNoteHeadInfo(data);
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }


}
