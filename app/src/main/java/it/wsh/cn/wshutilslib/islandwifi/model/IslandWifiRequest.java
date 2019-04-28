package it.wsh.cn.wshutilslib.islandwifi.model;

/**
 * author:wenshenghui
 * created on: 2019-3-21 15:41
 * description:
 */
public class IslandWifiRequest {

    public String deviceId;
    public String account; //账号
    public String time;
    public String courtUuid; //小区id,海花岛是当成一个固定的id看待
    public String authType;//认证类型 String(message、weixin、app)
    public String sign;
    public String appid; //保留字段


    public String terminalMac; //终端mac地址
    public String apMac; //AP的MAC地址
}
