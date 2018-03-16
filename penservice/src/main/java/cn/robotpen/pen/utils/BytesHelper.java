package cn.robotpen.pen.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by 王强 on 2017/2/9.
 * 简介：
 */

public class BytesHelper {

    public String bytes2Str(byte... data) {
        StringBuffer sb = new StringBuffer();
        for (byte b : data) {
            String tem = Integer.toHexString(b & 0xff).toUpperCase();
            sb.append(" 0x");
            sb.append(tem.length() < 2 ? "0" + tem : tem);
        }
        return sb.toString();
    }

    /**
     * byte[] 转int
     *
     * @param data 二进制数组
     * @return integer
     */
    public int bytesToInteger(byte... data) {
        int value = 0;
        for (int i = Math.max(data.length - 4, 0); i < data.length; i++) {
            int w = ((data.length - i - 1) * 8);
            value = value | ((data[i] & 0xFF) << w);
        }
        return value;
    }

    public int bytesToInteger16(byte... data) {
        int value = data[1] | ((data[0] & 0xFF) << 8);
        return value;
    }

    public byte[] integerTobytes(int arg) {
        byte[] res = new byte[4];
        res[0] = (byte) (0xff & arg);
        res[1] = (byte) ((0xff00 & arg) >> 8);
        res[2] = (byte) ((0xff0000 & arg) >> 16);
        res[3] = (byte) ((0xff000000 & arg) >> 24);
        return res;
    }

    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        //由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;//往高位游
        }
        return value;
    }


    public short byte2short(byte data, byte data2) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(data);
        bb.put(data2);
        short shortVal = bb.getShort(0);
        return shortVal;
    }

    public byte[] float2Byte(float myFloat) {
        int bits = Float.floatToIntBits(myFloat);
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (bits & 0xff);
        bytes[1] = (byte) ((bits >> 8) & 0xff);
        bytes[2] = (byte) ((bits >> 16) & 0xff);
        bytes[3] = (byte) ((bits >> 24) & 0xff);
        return bytes;
    }

    public float byte2float(byte... data) {
        float myfloatvalue = ByteBuffer.wrap(data).getFloat();
        return myfloatvalue;
    }


}
