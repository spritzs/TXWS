package cn.robotpen.pen.callback;

/**
 * Created by 王强 on 2017/2/22.
 * 简介：run on UI thread
 */

public interface OnUiCallback {
    void onStateChanged(int state, String addr);

    void onOffLineNoteHeadReceived(String json);

    void onSyncProgress(String key, int total, int progress);

    void onOffLineNoteSyncFinished(String json, byte[] data);

    void onPenServiceError(String msg);

    void onPenPositionChanged(int deviceType, int x, int y, int presure, byte state);

    void onRobotKeyEvent(int e);

    void onPageInfo(int currentPage, int totalPage);

    void onPageNumberAndCategory(int pageNumber, int category);

    void onPageNumberOnly(short number);

    void onUpdateFirmwareFinished();

    void onUpdateFirmwareProgress(int progress, int total, String info);

    void onSupportPenPressureCheck(boolean flag);

    void onCheckPressureing();

    void onCheckPressurePen();

    void onCheckPressureFinish(int flag);

    void onUpdateModuleFinished();

    void onCheckModuleUpdate();

    void onCheckModuleUpdateFinish(byte[] data);

    void setSyncPassWordWithOldPassWord(String pwd, String n_pwd);

    void onSetSyncPassWordWithOldPasswordCallback(int code);

    void opneReportedData();

    void closeReportedData();

    void cleanDeviceDataWithType(int code);

    void startSyncNoteWithPassWord(String pwd);

    void opneReportedDataCallBack(int code);

    void closeReportedDataCallBack(int code);

    void cleanDeviceDataWithTypeCallBack(int code);

    void startSyncNoteWithPassWordCallBack(int code);
}
