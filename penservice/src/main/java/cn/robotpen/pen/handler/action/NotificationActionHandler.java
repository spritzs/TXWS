package cn.robotpen.pen.handler.action;

import android.content.Intent;
import android.text.TextUtils;

import cn.robotpen.pen.handler.RobotHandler;
import cn.robotpen.pen.service.RobotServiceContract;

import static cn.robotpen.pen.service.RobotRemotePenService.ACTION_DISCONNECT_DEVICE_FROM_NOTIFICATION;
import static cn.robotpen.pen.service.RobotRemotePenService.ACTION_EXIT_SERVICE_FROM_NOTIFICATION;

/**
 * Description:
 *
 * @author 王强 Email: 249346538@qq.com 2017/2/25.
 * @version 1.0
 */

public class NotificationActionHandler extends RobotHandler<Intent> {
    public NotificationActionHandler(RobotServiceContract.ServicePresenter presenter) {
        super(presenter);
    }

    @Override
    public void handle(Intent data) {
        switch (getAction(data)) {
            case ACTION_DISCONNECT_DEVICE_FROM_NOTIFICATION:
                //断开笔服务
                servicePresenter.disconnectDevice();
                break;
            case ACTION_EXIT_SERVICE_FROM_NOTIFICATION:
                servicePresenter.exitSafly();
                break;
            default:
                if (nextHandler != null) {
                    nextHandler.handle(data);
                }
        }
    }

    private String getAction(Intent data) {
        String action = (data == null) ? "" : data.getAction();
        return TextUtils.isEmpty(action) ? "" : action;
    }
}
