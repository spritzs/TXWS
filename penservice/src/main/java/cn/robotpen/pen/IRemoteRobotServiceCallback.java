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

public interface IRemoteRobotServiceCallback extends IInterface {
    void onRemoteStateChanged(int var1, String var2) throws RemoteException;

    void onRemoteOffLineNoteHeadReceived(String var1) throws RemoteException;

    void onRemoteSyncProgress(String var1, int var2, int var3) throws RemoteException;

    void onRemoteOffLineNoteSyncFinished(String var1, byte[] var2) throws RemoteException;

    void onRemotePenServiceError(String var1) throws RemoteException;

    void onRemotePenPositionChanged(int var1, int var2, int var3, int var4, byte var5) throws RemoteException;

    void onRemoteRobotKeyEvent(int var1) throws RemoteException;

    void onPageInfo(int var1, int var2) throws RemoteException;

    void onRemoteUpdateFirmwareProgress(int var1, int var2, String var3) throws RemoteException;

    void onRemoteUpdateFirmwareFinished() throws RemoteException;

    void onSupportPenPressureCheck(boolean var1) throws RemoteException;

    void checkPenPressusering() throws RemoteException;

    void checkPenPressureFinish(byte[] var1) throws RemoteException;

    void onRequestModuleVersion(byte[] var1) throws RemoteException;

    void onRemoteUpdateModuleProgress(int var1, int var2, String var3) throws RemoteException;

    void onRemoteUpdateModuleFinished() throws RemoteException;

    void onPageNumberAndCategory(int var1, int var2) throws RemoteException;

    void onPageNumberOnly(long var1) throws RemoteException;

    void onSetSyncPassWordWithOldPassWord(int var1) throws RemoteException;

    void onOpneReportedData(int var1) throws RemoteException;

    void onCloseReportedData(int var1) throws RemoteException;

    void onCleanDeviceDataWithType(int var1) throws RemoteException;

    void onStartSyncNoteWithPassWord(int var1) throws RemoteException;

    public abstract static class Stub extends Binder implements IRemoteRobotServiceCallback {
        private static final String DESCRIPTOR = "cn.robotpen.pen.IRemoteRobotServiceCallback";
        static final int TRANSACTION_onRemoteStateChanged = 1;
        static final int TRANSACTION_onRemoteOffLineNoteHeadReceived = 2;
        static final int TRANSACTION_onRemoteSyncProgress = 3;
        static final int TRANSACTION_onRemoteOffLineNoteSyncFinished = 4;
        static final int TRANSACTION_onRemotePenServiceError = 5;
        static final int TRANSACTION_onRemotePenPositionChanged = 6;
        static final int TRANSACTION_onRemoteRobotKeyEvent = 7;
        static final int TRANSACTION_onPageInfo = 8;
        static final int TRANSACTION_onRemoteUpdateFirmwareProgress = 9;
        static final int TRANSACTION_onRemoteUpdateFirmwareFinished = 10;
        static final int TRANSACTION_onSupportPenPressureCheck = 11;
        static final int TRANSACTION_checkPenPressusering = 12;
        static final int TRANSACTION_checkPenPressureFinish = 13;
        static final int TRANSACTION_onRequestModuleVersion = 14;
        static final int TRANSACTION_onRemoteUpdateModuleProgress = 15;
        static final int TRANSACTION_onRemoteUpdateModuleFinished = 16;
        static final int TRANSACTION_onPageNumberAndCategory = 17;
        static final int TRANSACTION_onPageNumberOnly = 18;
        static final int TRANSACTION_onSetSyncPassWordWithOldPassWord = 19;
        static final int TRANSACTION_onOpneReportedData = 20;
        static final int TRANSACTION_onCloseReportedData = 21;
        static final int TRANSACTION_onCleanDeviceDataWithType = 22;
        static final int TRANSACTION_onStartSyncNoteWithPassWord = 23;

        public Stub() {
            this.attachInterface(this, "cn.robotpen.pen.IRemoteRobotServiceCallback");
        }

        public static IRemoteRobotServiceCallback asInterface(IBinder obj) {
            if(obj == null) {
                return null;
            } else {
                IInterface iin = obj.queryLocalInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                return (IRemoteRobotServiceCallback)(iin != null && iin instanceof IRemoteRobotServiceCallback?(IRemoteRobotServiceCallback)iin:new IRemoteRobotServiceCallback.Stub.Proxy(obj));
            }
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int _arg0;
            int _arg1;
            String _arg2;
            int _arg21;
            byte[] _arg02;
            String _arg04;
            switch(code) {
                case 1:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg0 = data.readInt();
                    String _arg12 = data.readString();
                    this.onRemoteStateChanged(_arg0, _arg12);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg04 = data.readString();
                    this.onRemoteOffLineNoteHeadReceived(_arg04);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg04 = data.readString();
                    _arg1 = data.readInt();
                    _arg21 = data.readInt();
                    this.onRemoteSyncProgress(_arg04, _arg1, _arg21);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg04 = data.readString();
                    byte[] _arg11 = data.createByteArray();
                    this.onRemoteOffLineNoteSyncFinished(_arg04, _arg11);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg04 = data.readString();
                    this.onRemotePenServiceError(_arg04);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg0 = data.readInt();
                    _arg1 = data.readInt();
                    _arg21 = data.readInt();
                    int _arg3 = data.readInt();
                    byte _arg4 = data.readByte();
                    this.onRemotePenPositionChanged(_arg0, _arg1, _arg21, _arg3, _arg4);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg0 = data.readInt();
                    this.onRemoteRobotKeyEvent(_arg0);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg0 = data.readInt();
                    _arg1 = data.readInt();
                    this.onPageInfo(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg0 = data.readInt();
                    _arg1 = data.readInt();
                    _arg2 = data.readString();
                    this.onRemoteUpdateFirmwareProgress(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    this.onRemoteUpdateFirmwareFinished();
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    boolean _arg03 = 0 != data.readInt();
                    this.onSupportPenPressureCheck(_arg03);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    this.checkPenPressusering();
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg02 = data.createByteArray();
                    this.checkPenPressureFinish(_arg02);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg02 = data.createByteArray();
                    this.onRequestModuleVersion(_arg02);
                    reply.writeNoException();
                    return true;
                case 15:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg0 = data.readInt();
                    _arg1 = data.readInt();
                    _arg2 = data.readString();
                    this.onRemoteUpdateModuleProgress(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    this.onRemoteUpdateModuleFinished();
                    reply.writeNoException();
                    return true;
                case 17:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg0 = data.readInt();
                    _arg1 = data.readInt();
                    this.onPageNumberAndCategory(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                case 18:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    long _arg01 = data.readLong();
                    this.onPageNumberOnly(_arg01);
                    reply.writeNoException();
                    return true;
                case 19:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg0 = data.readInt();
                    this.onSetSyncPassWordWithOldPassWord(_arg0);
                    reply.writeNoException();
                    return true;
                case 20:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg0 = data.readInt();
                    this.onOpneReportedData(_arg0);
                    reply.writeNoException();
                    return true;
                case 21:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg0 = data.readInt();
                    this.onCloseReportedData(_arg0);
                    reply.writeNoException();
                    return true;
                case 22:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg0 = data.readInt();
                    this.onCleanDeviceDataWithType(_arg0);
                    reply.writeNoException();
                    return true;
                case 23:
                    data.enforceInterface("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _arg0 = data.readInt();
                    this.onStartSyncNoteWithPassWord(_arg0);
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        private static class Proxy implements IRemoteRobotServiceCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return "cn.robotpen.pen.IRemoteRobotServiceCallback";
            }

            public void onRemoteStateChanged(int state, String addr) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeInt(state);
                    _data.writeString(addr);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onRemoteOffLineNoteHeadReceived(String json) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeString(json);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onRemoteSyncProgress(String key, int total, int progress) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeString(key);
                    _data.writeInt(total);
                    _data.writeInt(progress);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onRemoteOffLineNoteSyncFinished(String json, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeString(json);
                    _data.writeByteArray(data);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onRemotePenServiceError(String msg) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeString(msg);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onRemotePenPositionChanged(int deviceVersion, int x, int y, int presure, byte state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeInt(deviceVersion);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    _data.writeInt(presure);
                    _data.writeByte(state);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onRemoteRobotKeyEvent(int e) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeInt(e);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onPageInfo(int currentPage, int totalPage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeInt(currentPage);
                    _data.writeInt(totalPage);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onRemoteUpdateFirmwareProgress(int progress, int total, String info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeInt(progress);
                    _data.writeInt(total);
                    _data.writeString(info);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onRemoteUpdateFirmwareFinished() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onSupportPenPressureCheck(boolean flag) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeInt(flag?1:0);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void checkPenPressusering() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void checkPenPressureFinish(byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeByteArray(data);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onRequestModuleVersion(byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeByteArray(data);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onRemoteUpdateModuleProgress(int progress, int total, String info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeInt(progress);
                    _data.writeInt(total);
                    _data.writeString(info);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onRemoteUpdateModuleFinished() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onPageNumberAndCategory(int currentPage, int category) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeInt(currentPage);
                    _data.writeInt(category);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onPageNumberOnly(long currentPage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeLong(currentPage);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onSetSyncPassWordWithOldPassWord(int type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeInt(type);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onOpneReportedData(int type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeInt(type);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onCloseReportedData(int type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeInt(type);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onCleanDeviceDataWithType(int type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeInt(type);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void onStartSyncNoteWithPassWord(int type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("cn.robotpen.pen.IRemoteRobotServiceCallback");
                    _data.writeInt(type);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }
        }
    }
}
