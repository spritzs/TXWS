package cn.robotpen.pen.service;

import android.os.RemoteException;

import cn.robotpen.pen.IRemoteRobotService;
import cn.robotpen.pen.IRemoteRobotServiceCallback;
import cn.robotpen.pen.model.Battery;
import cn.robotpen.pen.model.RobotDevice;
import cn.robotpen.pen.model.RobotDeviceType;

import static cn.robotpen.pen.model.CMD.CMD_82;
import static cn.robotpen.pen.model.CMD.CMD_89;
import static cn.robotpen.pen.model.CMD.CMD_8B;
import static cn.robotpen.pen.model.CMD.CMD_A0;
import static cn.robotpen.pen.model.CMD.CMD_A1;
import static cn.robotpen.pen.model.CMD.CMD_A2;
import static cn.robotpen.pen.model.CMD.CMD_A6;
import static cn.robotpen.pen.model.CMD.CMD_B6;
import static cn.robotpen.pen.model.CMD.CMD_C9;
import static cn.robotpen.pen.model.CMD.CMD_CA;
import static cn.robotpen.pen.model.CMD.CMD_CB;
import static cn.robotpen.pen.model.CMD.CMD_D6;
import static cn.robotpen.pen.model.CMD.CMD_D7;
import static cn.robotpen.pen.model.CMD.CMD_D8;

/**
 * Created by 王强 on 2017/2/9.
 * 简介：
 */

public class RobotServiceBinder extends IRemoteRobotService.Stub {
    private RobotServiceContract.BinderPresenter binderPresenter;

    public RobotServiceBinder(RobotServiceContract.BinderPresenter presenter) {
        this.binderPresenter = presenter;
    }

    /**
     * 远程交互接口
     */
    @Override
    public void registCallback(IRemoteRobotServiceCallback callback) throws RemoteException {
        this.binderPresenter.registClient(callback);
    }

    @Override
    public void unRegistCallback(IRemoteRobotServiceCallback callback) throws RemoteException {
        this.binderPresenter.unregistClient(callback);
    }

    @Override
    public boolean connectDevice(String macAddr) throws RemoteException {
        return binderPresenter.connectBlutoothDevice(macAddr);
    }

    @Override
    public void setPageInfo(int cur, int total) {
        RobotDevice connectDevice = binderPresenter.getConnectedDevice();
        if (connectDevice != null) {
            binderPresenter.execCommand(CMD_89, (byte) cur, (byte) total);
        }
    }

    @Override
    public void requestPageInfo() {
        RobotDevice connectDevice = binderPresenter.getConnectedDevice();
        if (connectDevice != null) {
            binderPresenter.execCommand(CMD_8B);
        }
    }

    @Override
    public void checkPenPressure() throws RemoteException {
        binderPresenter.execCommand(CMD_D8);
    }

    @Override
    public boolean editDeviceName(String name) {
        char[] ca = name.toCharArray();
        byte[] data = new byte[ca.length];
        for (int i = 0; i < ca.length; i++) {
            data[i] = (byte) ca[i];
        }
        return binderPresenter.execCommand(CMD_82, data);
    }

    @Override
    public void disconnectDevice() throws RemoteException {
        binderPresenter.disconnectDevice();
    }


    @Override
    public boolean exitOTA() {
        if (binderPresenter != null) {
            return binderPresenter.execCommand(CMD_B6);
        }
        return false;
    }

    @Override
    public void getCurrentModuleVersion() throws RemoteException {
        if (binderPresenter != null) {
            binderPresenter.execCommand(CMD_D7);
        }
    }


    @Override
    public boolean startUpdateModule(String version, byte[] data) throws RemoteException {
        //发送请求模组升级命令
        return binderPresenter.startUpdateModule(version, data);
    }

    @Override
    public boolean exitModuleUpdate() throws RemoteException {
        return binderPresenter.execCommand(CMD_D6);
    }

    @Override
    public byte getCurrentMode() {
        return binderPresenter.getDeviceState();
    }


    /**
     * use {@link #getRemainBatteryEM()} instead
     */
    @Deprecated
    public int getRemainBattery() {
        if (binderPresenter.getConnectedDevice() != null) {
            return binderPresenter.getConnectedDevice().getBattery();
        } else {
            return 0;
        }
    }

    @Override
    public Battery getRemainBatteryEM() throws RemoteException {
        if (binderPresenter.getConnectedDevice() != null) {
            return binderPresenter.getConnectedDevice().getBatteryEm();
        } else {
            return null;
        }
    }

    @Override
    public boolean setSyncPassWordWithOldPassWord(String oldpwd, String newpwd) throws RemoteException {
        if (binderPresenter != null && RobotDeviceType.C7.getValue() == getConnectedDevice().getDeviceVersion()) {
            return binderPresenter.setpwd4C7(oldpwd, newpwd);
        } else {
            return false;
        }

    }

    @Override
    public boolean opneReportedData() throws RemoteException {
        if (binderPresenter != null && RobotDeviceType.C7.getValue() == getConnectedDevice().getDeviceVersion()) {
            return binderPresenter.execCommand(CMD_C9);
        } else {
            return false;
        }
    }

    @Override
    public boolean closeReportedData() throws RemoteException {
        if (binderPresenter != null && RobotDeviceType.C7.getValue() == getConnectedDevice().getDeviceVersion()) {
            return binderPresenter.execCommand(CMD_CA);
        } else {
            return false;
        }

    }

    @Override
    public boolean cleanDeviceDataWithType(int type) throws RemoteException {
        if (binderPresenter != null && RobotDeviceType.C7.getValue() == getConnectedDevice().getDeviceVersion()) {
            return binderPresenter.execCommand(CMD_CB, (byte) type);
        } else {
            return false;
        }

    }

    @Override
    public boolean startSyncNoteWithPassWord(String pwd) throws RemoteException {
        //进入同步模式
        if (binderPresenter.getConnectedDevice().getOfflineNoteNum() > 0) {
            return binderPresenter.checkPWDandSyncCommand(pwd);
        }
        return false;

    }


    @Override
    public boolean enterSyncMode() throws RemoteException {
        //进入同步模式
        if (binderPresenter.getConnectedDevice().getOfflineNoteNum() > 0) {
            return binderPresenter.execCommand(CMD_A0);
        }
        return false;
    }

    @Override
    public boolean exitSyncMode() throws RemoteException {
        //退出同步模式
        return binderPresenter.execCommand(CMD_A1);
    }

    @Override
    public RobotDevice getConnectedDevice() throws RemoteException {
        return binderPresenter.getConnectedDevice();
    }

    @Override
    public boolean startSyncOffLineNote() throws RemoteException {
        if (binderPresenter.getDeviceState() == 0x0A) {
            return binderPresenter.execCommand(CMD_A2);
        } else {
            if (binderPresenter.getConnectedDevice().getOfflineNoteNum() > 0) {
                return binderPresenter.execCommand(CMD_A0);
            }
            return false;
        }
    }

    @Override
    public boolean stopSyncOffLineNote() throws RemoteException {
        return binderPresenter.execCommand(CMD_A6);
    }

    @Override
    public boolean startUpdateFirmware(String ver, byte[] firmwareData) throws RemoteException {
        //更新固件
        return binderPresenter.startUpdateFirmware(ver, firmwareData);
    }

    @Override
    public boolean startUpgradeDevice(String bleVersion, byte[] bleFirmdata, String mcuVersion, byte[] mcuFirmdata) {
        return binderPresenter.startUpdateFirmware(bleVersion, bleFirmdata, mcuVersion, mcuFirmdata);
    }

}
