package cn.robotpen.pen.model;

/**
 * Created by 王强 on 2017/2/10.
 * 简介：
 */

public class CMD {
    public static final byte CMD_HEAD_ID = (byte) 0xAA;  //常量不变
    public static final byte CMD_A0 = (byte) 0xA0;       //进入同步模式
    public static final byte CMD_A1 = (byte) 0xA1;       //退出同步模式
    public static final byte CMD_A2 = (byte) 0xA2;       //读取一个笔记头信息
    public static final byte CMD_A3 = (byte) 0xA3;       //同步笔记
    public static final byte CMD_A4 = (byte) 0xA4;       //开始同步块
    public static final byte CMD_A5 = (byte) 0xA5;       //同步块完成
    public static final byte CMD_A6 = (byte) 0xA6;       //停止同步

    public static final byte CMD_80 = (byte) 0x80;       //硬件状态 电量等
    public static final byte CMD_81 = (byte) 0x81;       //坐标数据
    public static final byte CMD_82 = (byte) 0x82;       //修改设备名称
    public static final byte CMD_83 = (byte) 0x83;       //错误
    public static final byte CMD_84 = (byte) 0x84;       //获取版本号，硬件与软件版本号
    public static final byte CMD_87 = (byte) 0x87;       //设置时间
    public static final byte CMD_88 = (byte) 0x88;       //按键事件报告 0x01上翻页，0x02下翻页
    public static final byte CMD_89 = (byte) 0x89;       //设置页码
    public static final byte CMD_8A = (byte) 0x8a;       //页码广播
    public static final byte CMD_8B = (byte) 0x8b;       //请求页码

    public static final byte CMD_B0 = (byte) 0xB0;       //进入OTA升级模式
    public static final byte CMD_B1 = (byte) 0xB1;       //板子请求手机OTA文件信息
    public static final byte CMD_B2 = (byte) 0xB2;       //板子请求升级数据包
    public static final byte CMD_B3 = (byte) 0xB3;       //
    public static final byte CMD_B4 = (byte) 0xB4;       //升级包传输完成检查
    public static final byte CMD_B5 = (byte) 0xB5;       //固件切换
    public static final byte CMD_B6 = (byte) 0xB6;       //退出OTA模式


    public static final byte CMD_D0 = (byte) 0xD0;       //进入模组升级模式
    public static final byte CMD_D1 = (byte) 0xD1;       //板子请求手机模组文件信息
    public static final byte CMD_D2 = (byte) 0xD2;       //板子请求升级数据包
    public static final byte CMD_D3 = (byte) 0xD3;       //
    public static final byte CMD_D4 = (byte) 0xD4;       //升级包传输完成检查
    public static final byte CMD_D5 = (byte) 0xD5;
    public static final byte CMD_D6 = (byte) 0xD6;       //退出模组模式
    public static final byte CMD_D7 = (byte) 0xD7;       //主动获取模组固件版本

    public static final byte CMD_D8 = (byte) 0xD8;       //校验笔的压感
    public static final byte CMD_D9 = (byte) 0xD9;       //校验笔压感结果


    public static final byte CMD_C8 = (byte) 0xC8;       //C7 设置同步密码
    public static final byte CMD_C9 = (byte) 0xC9;       //C7 开启轨迹上报
    public static final byte CMD_CA = (byte) 0xCA;       //C7 停止轨迹上报
    public static final byte CMD_CB = (byte) 0xCB;       //C7 清除数据指令







}
