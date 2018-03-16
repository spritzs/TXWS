//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.robotpen.pen;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import cn.robotpen.pen.IRemoteRobotServiceCallback;
import cn.robotpen.pen.model.Battery;
import cn.robotpen.pen.model.RobotDevice;

public interface IRemoteRobotService extends IInterface {
    void registCallback(IRemoteRobotServiceCallback var1) throws RemoteException;

    void unRegistCallback(IRemoteRobotServiceCallback var1) throws RemoteException;

    boolean connectDevice(String var1) throws RemoteException;

    boolean editDeviceName(String var1) throws RemoteException;

    void disconnectDevice() throws RemoteException;

    byte getCurrentMode() throws RemoteException;

    boolean enterSyncMode() throws RemoteException;

    boolean exitSyncMode() throws RemoteException;

    RobotDevice getConnectedDevice() throws RemoteException;

    boolean startSyncOffLineNote() throws RemoteException;

    boolean stopSyncOffLineNote() throws RemoteException;

    boolean startUpdateFirmware(String var1, byte[] var2) throws RemoteException;

    boolean startUpgradeDevice(String var1, byte[] var2, String var3, byte[] var4) throws RemoteException;

    boolean exitOTA() throws RemoteException;

    void getCurrentModuleVersion() throws RemoteException;

    boolean startUpdateModule(String var1, byte[] var2) throws RemoteException;

    boolean exitModuleUpdate() throws RemoteException;

    void setPageInfo(int var1, int var2) throws RemoteException;

    void requestPageInfo() throws RemoteException;

    void checkPenPressure() throws RemoteException;

    int getRemainBattery() throws RemoteException;

    Battery getRemainBatteryEM() throws RemoteException;

    boolean setSyncPassWordWithOldPassWord(String var1, String var2) throws RemoteException;

    boolean opneReportedData() throws RemoteException;

    boolean closeReportedData() throws RemoteException;

    boolean cleanDeviceDataWithType(int var1) throws RemoteException;

    boolean startSyncNoteWithPassWord(String var1) throws RemoteException;

    public abstract static class Stub extends Binder implements IRemoteRobotService {
        private static final String DESCRIPTOR = "cn.robotpen.pen.IRemoteRobotService";
        static final int TRANSACTION_registCallback = 1;
        static final int TRANSACTION_unRegistCallback = 2;
        static final int TRANSACTION_connectDevice = 3;
        static final int TRANSACTION_editDeviceName = 4;
        static final int TRANSACTION_disconnectDevice = 5;
        static final int TRANSACTION_getCurrentMode = 6;
        static final int TRANSACTION_enterSyncMode = 7;
        static final int TRANSACTION_exitSyncMode = 8;
        static final int TRANSACTION_getConnectedDevice = 9;
        static final int TRANSACTION_startSyncOffLineNote = 10;
        static final int TRANSACTION_stopSyncOffLineNote = 11;
        static final int TRANSACTION_startUpdateFirmware = 12;
        static final int TRANSACTION_startUpgradeDevice = 13;
        static final int TRANSACTION_exitOTA = 14;
        static final int TRANSACTION_getCurrentModuleVersion = 15;
        static final int TRANSACTION_startUpdateModule = 16;
        static final int TRANSACTION_exitModuleUpdate = 17;
        static final int TRANSACTION_setPageInfo = 18;
        static final int TRANSACTION_requestPageInfo = 19;
        static final int TRANSACTION_checkPenPressure = 20;
        static final int TRANSACTION_getRemainBattery = 21;
        static final int TRANSACTION_getRemainBatteryEM = 22;
        static final int TRANSACTION_setSyncPassWordWithOldPassWord = 23;
        static final int TRANSACTION_opneReportedData = 24;
        static final int TRANSACTION_closeReportedData = 25;
        static final int TRANSACTION_cleanDeviceDataWithType = 26;
        static final int TRANSACTION_startSyncNoteWithPassWord = 27;

        public Stub() {
            this.attachInterface(this, "cn.robotpen.pen.IRemoteRobotService");
        }

        public static IRemoteRobotService asInterface(IBinder obj) {
            if(obj == null) {
                return null;
            } else {
                IInterface iin = obj.queryLocalInterface("cn.robotpen.pen.IRemoteRobotService");
                return (IRemoteRobotService)(iin != null && iin instanceof IRemoteRobotService?(IRemoteRobotService)iin:new IRemoteRobotService.Stub.Proxy(obj));
            }
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String _arg0;
            boolean _result;
            boolean _result1;
            int _arg01;
            boolean _arg02;
            byte[] _result5;
            IRemoteRobotServiceCallback _arg06;
            switch(code) {
                case 1:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg06 = cn.robotpen.pen.IRemoteRobotServiceCallback.Stub.asInterface(data.readStrongBinder());
                    this.registCallback(_arg06);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg06 = cn.robotpen.pen.IRemoteRobotServiceCallback.Stub.asInterface(data.readStrongBinder());
                    this.unRegistCallback(_arg06);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg0 = data.readString();
                    _result = this.connectDevice(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result?1:0);
                    return true;
                case 4:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg0 = data.readString();
                    _result = this.editDeviceName(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result?1:0);
                    return true;
                case 5:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    this.disconnectDevice();
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    byte _arg05 = this.getCurrentMode();
                    reply.writeNoException();
                    reply.writeByte(_arg05);
                    return true;
                case 7:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg02 = this.enterSyncMode();
                    reply.writeNoException();
                    reply.writeInt(_arg02?1:0);
                    return true;
                case 8:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg02 = this.exitSyncMode();
                    reply.writeNoException();
                    reply.writeInt(_arg02?1:0);
                    return true;
                case 9:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    RobotDevice _arg04 = this.getConnectedDevice();
                    reply.writeNoException();
                    if(_arg04 != null) {
                        reply.writeInt(1);
                        _arg04.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }

                    return true;
                case 10:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg02 = this.startSyncOffLineNote();
                    reply.writeNoException();
                    reply.writeInt(_arg02?1:0);
                    return true;
                case 11:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg02 = this.stopSyncOffLineNote();
                    reply.writeNoException();
                    reply.writeInt(_arg02?1:0);
                    return true;
                case 12:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg0 = data.readString();
                    _result5 = data.createByteArray();
                    _result1 = this.startUpdateFirmware(_arg0, _result5);
                    reply.writeNoException();
                    reply.writeInt(_result1?1:0);
                    return true;
                case 13:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg0 = data.readString();
                    _result5 = data.createByteArray();
                    String _result6 = data.readString();
                    byte[] _arg3 = data.createByteArray();
                    boolean _result2 = this.startUpgradeDevice(_arg0, _result5, _result6, _arg3);
                    reply.writeNoException();
                    reply.writeInt(_result2?1:0);
                    return true;
                case 14:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg02 = this.exitOTA();
                    reply.writeNoException();
                    reply.writeInt(_arg02?1:0);
                    return true;
                case 15:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    this.getCurrentModuleVersion();
                    reply.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg0 = data.readString();
                    _result5 = data.createByteArray();
                    _result1 = this.startUpdateModule(_arg0, _result5);
                    reply.writeNoException();
                    reply.writeInt(_result1?1:0);
                    return true;
                case 17:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg02 = this.exitModuleUpdate();
                    reply.writeNoException();
                    reply.writeInt(_arg02?1:0);
                    return true;
                case 18:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg01 = data.readInt();
                    int _result4 = data.readInt();
                    this.setPageInfo(_arg01, _result4);
                    reply.writeNoException();
                    return true;
                case 19:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    this.requestPageInfo();
                    reply.writeNoException();
                    return true;
                case 20:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    this.checkPenPressure();
                    reply.writeNoException();
                    return true;
                case 21:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg01 = this.getRemainBattery();
                    reply.writeNoException();
                    reply.writeInt(_arg01);
                    return true;
                case 22:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    Battery _arg03 = this.getRemainBatteryEM();
                    reply.writeNoException();
                    if(_arg03 != null) {
                        reply.writeInt(1);
                        _arg03.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }

                    return true;
                case 23:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg0 = data.readString();
                    String _result3 = data.readString();
                    _result1 = this.setSyncPassWordWithOldPassWord(_arg0, _result3);
                    reply.writeNoException();
                    reply.writeInt(_result1?1:0);
                    return true;
                case 24:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg02 = this.opneReportedData();
                    reply.writeNoException();
                    reply.writeInt(_arg02?1:0);
                    return true;
                case 25:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg02 = this.closeReportedData();
                    reply.writeNoException();
                    reply.writeInt(_arg02?1:0);
                    return true;
                case 26:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg01 = data.readInt();
                    _result = this.cleanDeviceDataWithType(_arg01);
                    reply.writeNoException();
                    reply.writeInt(_result?1:0);
                    return true;
                case 27:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotService");
                    _arg0 = data.readString();
                    _result = this.startSyncNoteWithPassWord(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result?1:0);
                    return true;
                case 1598968902:
                    reply.writeString("cn.robotpen.pen.IRemoteRobotService");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        private static class Proxy implements IRemoteRobotService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return "cn.robotpen.pen.IRemoteRobotService";
            }

            public void registCallback(IRemoteRobotServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    _data.writeStrongBinder(callback != null?callback.asBinder():null);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void unRegistCallback(IRemoteRobotServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    _data.writeStrongBinder(callback != null?callback.asBinder():null);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public boolean connectDevice(String macAddr) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    _data.writeString(macAddr);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public boolean editDeviceName(String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    _data.writeString(name);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public void disconnectDevice() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public byte getCurrentMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                byte _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readByte();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public boolean enterSyncMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public boolean exitSyncMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public RobotDevice getConnectedDevice() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                RobotDevice _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    if(0 != _reply.readInt()) {
                        _result = (RobotDevice)RobotDevice.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public boolean startSyncOffLineNote() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public boolean stopSyncOffLineNote() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public boolean startUpdateFirmware(String version, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    _data.writeString(version);
                    _data.writeByteArray(data);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public boolean startUpgradeDevice(String bleVersion, byte[] bleFirmdata, String mcuVersion, byte[] mcuFirmdata) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    _data.writeString(bleVersion);
                    _data.writeByteArray(bleFirmdata);
                    _data.writeString(mcuVersion);
                    _data.writeByteArray(mcuFirmdata);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public boolean exitOTA() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public void getCurrentModuleVersion() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public boolean startUpdateModule(String version, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    _data.writeString(version);
                    _data.writeByteArray(data);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public boolean exitModuleUpdate() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public void setPageInfo(int cur, int total) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    _data.writeInt(cur);
                    _data.writeInt(total);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void requestPageInfo() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void checkPenPressure() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public int getRemainBattery() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                int _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public Battery getRemainBatteryEM() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                Battery _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    if(0 != _reply.readInt()) {
                        _result = (Battery)Battery.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public boolean setSyncPassWordWithOldPassWord(String oldpwd, String newpwd) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    _data.writeString(oldpwd);
                    _data.writeString(newpwd);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public boolean opneReportedData() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public boolean closeReportedData() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public boolean cleanDeviceDataWithType(int type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    _data.writeInt(type);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public boolean startSyncNoteWithPassWord(String pwd) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotService");
                    _data.writeString(pwd);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }
        }
    }
}
