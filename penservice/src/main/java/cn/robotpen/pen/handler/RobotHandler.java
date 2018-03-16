package cn.robotpen.pen.handler;

import cn.robotpen.pen.service.RobotServiceContract;
import cn.robotpen.pen.utils.BytesHelper;

/**
 * Description:
 *
 * @author 王强 Email: 249346538@qq.com 2017/2/25.
 * @version 1.0
 */

public abstract class RobotHandler<D> {
    protected RobotServiceContract.ServicePresenter servicePresenter;
    protected RobotHandler<D> nextHandler;
    protected BytesHelper bytesHelper;

    public RobotHandler(RobotServiceContract.ServicePresenter servicePresenter) {
        this.servicePresenter = servicePresenter;
        bytesHelper = new BytesHelper();
    }

    public abstract void handle(D data);

    public void setNextHandler(RobotHandler<D> handler) {
        this.nextHandler = handler;
    }
}
