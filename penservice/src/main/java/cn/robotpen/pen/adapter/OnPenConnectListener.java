package cn.robotpen.pen.adapter;

/**
 * Description:
 *
 * @version 1.0
 */

public interface OnPenConnectListener<T> {
    //蓝牙笔开始连接状态
    void onPenServiceStarted();

    //蓝牙笔连接成功
    void onConnected(int penType);

    //蓝牙笔连接失败
    void onConnectFailed(int reasonCode);

    //坐标接收
    void onReceiveDot(long timestamp, int x, int y, int pressure, int state);

    //断开连接
    void onDisconnected();

    //设备得电池电量上报(被动)
    void onMemoryFillLevel(int percent);

    //获取设备得电池电量(主动查询)
    void onRemainBattery(int percent);

    //离线笔记数据接收
    void onOfflineDataReceived(T event, boolean completed);

    //开始同步离线笔记
    void onOfflineSyncStart(String head);

    //同步离线笔记进度
    void onOfflienSyncProgress(String key, int total, int progress);

    //同步离线笔记结束
    void onOffLineNoteSyncFinished(String json, byte[] data);

    //设备上报页面
    void onReportPageNumberAndOther(int pageNumber, int Category);

    //C7 设置密码
    void onSetSyncPwdCallback(int code);

    //C7 开始同步数据
    void onStartUploadDataCallback(int code);

    //C7 停止同步数据
    void onCloseUploadDataCallBack(int code);

    //C7 清除数据
    void onCleanDataCallback(int code);

    //C7 检查离线笔记同步密码
    void onStartSyncNoteWithPassWord(int code);


}
