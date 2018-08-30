package it.wsh.cn.wshutilslib.httpdemo.bean;

/**
 * author: wenshenghui
 * created on: 2018/8/23 9:58
 * description:
 */
public class NoticeResultInfo {

    public String courtUuid;
    public long createTime;
    public String createUser;
    public int deleteFlag;
    public String noticeNumber;
    public String noticePublisher;
    public int noticeStatus;
    public String noticeSummary;
    public String noticeTitle;
    public String noticeZoomPicId;
    public long publishTime;
    public long updateTime;
    public String updateUser;
    public String uuid;
    public String validEndTime;
    public String validStartTime;

    @Override
    public String toString() {
        return "courtUuid = " + courtUuid +
                "; createTime = " + createTime +
                "; createUser = " + createUser +
                "; deleteFlag = " + deleteFlag +
                "; noticeNumber = " + noticeNumber +
                "; noticePublisher = " + noticePublisher +
                "; noticeSummary = " + noticeSummary +
                "; noticeStatus = " + noticeStatus +
                "; noticeTitle = " + noticeTitle +
                "; noticeZoomPicId = " + noticeZoomPicId +
                "; publishTime = " + publishTime +
                "; updateTime = " + updateTime +
                "; updateUser = " + updateUser +
                "; uuid = " + uuid +
                "; validEndTime = " + validEndTime +
                "; validStartTime = " + validStartTime;
    }
}
