package cn.robotpen.pen.handler.cmd;

//import com.codingmaster.slib.S;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.model.CMD.CMD_A1;
import static cn.robotpen.pen.model.CMD.CMD_A2;
import static cn.robotpen.pen.model.CMD.CMD_A5;

/**
 * Created by 王强 on 2017/2/10.
 * 简介：
 */

public class HandleCMD_A5 extends RobotHandler<byte[]> {
    public HandleCMD_A5(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(byte[] data) {
        if (data[1] == CMD_A5) {
            syncOfflineNoteBlockFinished(data[3]);
        } else if (nextHandler != null) {
            nextHandler.handle(data);
        }
    }

    /**
     * 完成离线笔记一个存储块的同步
     *
     * @param b
     */
    private void syncOfflineNoteBlockFinished(byte b) {
        // 0x0 块传输完成
        // 0x1 一条笔记传输完成
        // 0x2 全部传输完成
        int flag = b & 0xff;
        if (flag == 0) {
            //请求下一个数据块
            servicePresenter.execCommand(CMD_A5, (byte) 3);
        } else if (flag == 1) {
            //所有块同步完成,通知硬件删除数据
            servicePresenter.execCommand(CMD_A5, (byte) 2);
        } else {
            int mOfflineNoteNum = servicePresenter.getConnectedDevice().getOfflineNoteNum() - 1;
            servicePresenter.updateDeviceOfflineNote((byte) mOfflineNoteNum);
            servicePresenter.reportOffLineNoteSyncFinished();
//            S.i("----------第" + mOfflineNoteNum + "条完成--------------------------------");
            if (mOfflineNoteNum > 0) {
                servicePresenter.execCommand(CMD_A2);
            } else {
                //全部笔记同步完成,退出同步模式
                servicePresenter.execCommand(CMD_A1);
            }
        }
    }
}
