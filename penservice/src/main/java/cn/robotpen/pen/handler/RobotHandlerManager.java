package cn.robotpen.pen.handler;

/**
 * Created by 王强 on 2017/2/24.
 * 简介：
 */

public class RobotHandlerManager<D> {
    private RobotHandler<D> handler;

    private RobotHandlerManager(RobotHandler<D> h) {
        this.handler = h;
    }

    public void handle(D data) {
        handler.handle(data);
    }

    public static class HandlersBuilder<D> {
        private RobotHandler<D> header;
        private RobotHandler<D> tail;

        public HandlersBuilder() {
            header = null;
            tail = null;
        }

        public HandlersBuilder<D> addHandler(RobotHandler<D> handler) {
            if (header == null) {
                this.header = handler;
                this.tail = handler;
            } else {
                this.tail.setNextHandler(handler);
                this.tail = handler;
            }
            return this;
        }

        public RobotHandlerManager<D> build() {
            return new RobotHandlerManager<>(header);
        }
    }
}
