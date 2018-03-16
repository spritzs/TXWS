package cn.robotpen.pen.callback;

import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;


import java.lang.ref.WeakReference;

import cn.robotpen.pen.IRemoteRobotServiceCallback;
import cn.robotpen.pen.pool.RunnableMessage;
import cn.robotpen.pen.utils.BytesHelper;
import cn.robotpen.pen.utils.Config;

/**
 * Description:
 *
 * @author 王强 Email: 249346538@qq.com 2017/3/2.
 * @version 1.0
 */

public class RobotPenServiceCallback extends IRemoteRobotServiceCallback.Stub implements IBinder.DeathRecipient {
    private WeakReference<OnUiCallback> uiCallbackWeakReference;
    private WeakReference<Handler> handlerWeakReference;

    public RobotPenServiceCallback(OnUiCallback uiCallback) {
        this.uiCallbackWeakReference = new WeakReference<>(uiCallback);
        this.handlerWeakReference = new WeakReference<Handler>(new Handler());
    }

    public RobotPenServiceCallback(OnUiCallback uiCallback, Handler h) {
        this.uiCallbackWeakReference = new WeakReference<>(uiCallback);
        this.handlerWeakReference = new WeakReference<Handler>(h);
    }

    @Override
    public void onRemoteStateChanged(final int state, final String addr) throws RemoteException {
        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (uiCallbackWeakReference.get() != null) {
                            uiCallbackWeakReference.get().onStateChanged(state, addr);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (uiCallbackWeakReference.get() != null) {
                uiCallbackWeakReference.get().onStateChanged(state, addr);
            }
        }
    }

    @Override
    public void onRemoteOffLineNoteHeadReceived(final String json) throws RemoteException {
        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (uiCallbackWeakReference.get() != null) {
                            uiCallbackWeakReference.get().onOffLineNoteHeadReceived(json);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().onOffLineNoteHeadReceived(json);
        }
    }

    @Override
    public void onRemoteSyncProgress(final String key, final int total, final int progress) throws RemoteException {
        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (uiCallbackWeakReference.get() != null) {
                            uiCallbackWeakReference.get().onSyncProgress(key, total, progress);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().onSyncProgress(key, total, progress);
        }
    }

    @Override
    public void onRemoteOffLineNoteSyncFinished(final String json, final byte[] data) throws RemoteException {
        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (uiCallbackWeakReference.get() != null) {
                            uiCallbackWeakReference.get().onOffLineNoteSyncFinished(json, data);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().onOffLineNoteSyncFinished(json, data);
        }
    }

    @Override
    public void onRemotePenServiceError(final String msg) throws RemoteException {
        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (uiCallbackWeakReference.get() != null) {
                            uiCallbackWeakReference.get().onPenServiceError(msg);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().onPenServiceError(msg);
        }
    }

    @Override
    public void onRemotePenPositionChanged(int deviceType, int x, int y, int presure, byte state) throws RemoteException {
        if (uiCallbackWeakReference.get() != null) {
            RunnableMessage.obtain(deviceType, x, y, presure, state, uiCallbackWeakReference.get()).sendToTarget();
        }
    }

    @Override
    public void onRemoteRobotKeyEvent(final int e) throws RemoteException {
        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (uiCallbackWeakReference.get() != null) {
                            uiCallbackWeakReference.get().onRobotKeyEvent(e);
                        }
                    }
                });
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().onRobotKeyEvent(e);
        }
    }

    @Override
    public void onPageInfo(final int currentPage, final int totalPage) {
        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (uiCallbackWeakReference.get() != null) {
                            uiCallbackWeakReference.get().onPageInfo(currentPage, totalPage);
                        }
                    }
                });
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().onPageInfo(currentPage, totalPage);
        }
    }

    @Override
    public void onRemoteUpdateFirmwareProgress(final int progress, final int total, final String info) throws RemoteException {
        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (uiCallbackWeakReference.get() != null) {

                            uiCallbackWeakReference.get().onUpdateFirmwareProgress(progress, total, info);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().onUpdateFirmwareProgress(progress, total, info);
        }
    }

    @Override
    public void onRemoteUpdateFirmwareFinished() throws RemoteException {
        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (uiCallbackWeakReference.get() != null) {

                            uiCallbackWeakReference.get().onUpdateFirmwareFinished();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().onUpdateFirmwareFinished();
        }
    }

    @Override
    public void checkPenPressureFinish(final byte[] data) throws RemoteException {
        if (handlerWeakReference.get() != null) {
            handlerWeakReference.get().post(new Runnable() {
                @Override
                public void run() {
                    BytesHelper bytesHelper = new BytesHelper();
//                    S.i("checkPenPressureFinish--->>>192------>" + bytesHelper.bytes2Str(data));
                    uiCallbackWeakReference.get().onCheckPressureFinish(bytesHelper.bytesToInteger(data[3]));
                }
            });
        } else {
            uiCallbackWeakReference.get().onCheckPressureFinish(Config.CMD_9_FAIL);
        }
    }

    @Override
    public void checkPenPressusering() throws RemoteException {
        if (handlerWeakReference.get() != null) {
            handlerWeakReference.get().post(new Runnable() {
                @Override
                public void run() {
                    if (uiCallbackWeakReference.get() != null) {

                        uiCallbackWeakReference.get().onCheckPressureing();
                    }
                }
            });
        } else {
            uiCallbackWeakReference.get().onCheckPressureing();
        }
    }

    @Override
    public void onRequestModuleVersion(byte[] data) throws RemoteException {
        uiCallbackWeakReference.get().onCheckModuleUpdateFinish(data);
    }


    @Override
    public void onRemoteUpdateModuleProgress(int progress, int total, String info) throws RemoteException {

    }

    @Override
    public void onRemoteUpdateModuleFinished() throws RemoteException {
        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (uiCallbackWeakReference.get() != null) {
                            uiCallbackWeakReference.get().onUpdateModuleFinished();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().onUpdateModuleFinished();
        }
    }


    @Override
    public void onPageNumberAndCategory(final int currentPage, final int category) {
        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        uiCallbackWeakReference.get().onPageNumberAndCategory(currentPage, category);
                    }
                });
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().onPageNumberAndCategory(currentPage, category);
        }
    }

    @Override
    public void onPageNumberOnly(final long currentPage) throws RemoteException {
        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        uiCallbackWeakReference.get().onPageNumberOnly((short) currentPage);
                    }
                });
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().onPageNumberOnly((short) currentPage);
        }
    }


    @Override
    public void onSetSyncPassWordWithOldPassWord(final int type) throws RemoteException {

        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        uiCallbackWeakReference.get().onSetSyncPassWordWithOldPasswordCallback(type);
                    }
                });
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().onSetSyncPassWordWithOldPasswordCallback(type);
        }
    }

    @Override
    public void onOpneReportedData(final int type) throws RemoteException {

        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        uiCallbackWeakReference.get().opneReportedDataCallBack(type);
                    }
                });
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().opneReportedDataCallBack(type);
        }

    }

    @Override
    public void onCloseReportedData(final int type) throws RemoteException {
        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        uiCallbackWeakReference.get().closeReportedDataCallBack(type);
                    }
                });
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().closeReportedDataCallBack(type);
        }
    }

    @Override
    public void onCleanDeviceDataWithType(final int type) throws RemoteException {
        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        uiCallbackWeakReference.get().cleanDeviceDataWithTypeCallBack(type);
                    }
                });
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().cleanDeviceDataWithTypeCallBack(type);
        }
    }

    @Override
    public void onStartSyncNoteWithPassWord(final int type) throws RemoteException {
        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        uiCallbackWeakReference.get().startSyncNoteWithPassWordCallBack(type);
                    }
                });
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().startSyncNoteWithPassWordCallBack(type);
        }
    }


    @Override
    public void onSupportPenPressureCheck(final boolean flag) throws RemoteException {
        if (handlerWeakReference.get() != null) {
            try {
                handlerWeakReference.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (uiCallbackWeakReference.get() != null) {

                            uiCallbackWeakReference.get().onSupportPenPressureCheck(flag);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            uiCallbackWeakReference.get().onSupportPenPressureCheck(flag);
        }
    }

    @Override
    public void binderDied() {
        try {
            ((RobotPenActivity) uiCallbackWeakReference.get()).bindRobotPenService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}