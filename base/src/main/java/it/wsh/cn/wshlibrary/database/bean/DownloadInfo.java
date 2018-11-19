package it.wsh.cn.wshlibrary.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import it.wsh.cn.wshlibrary.http.IProcessInfo;

/**
 * author: wenshenghui
 * created on: 2018/8/31 9:49
 * description:
 */
@Entity
public class DownloadInfo implements IProcessInfo {

    @Unique
    private int key;

    @Property
    private String url;

    @Property
    private String savePath;

    @Property
    private long totalSize;

    @Transient
    private long downloadPosition;

    @Transient
    private String fileName;

    @Transient
    private int process;

    @Generated(hash = 1625172689) @Keep
    public DownloadInfo(int key, String url, String savePath, long totalSize) {
        this.key = key;
        this.url = url;
        this.savePath = savePath;
        this.totalSize = totalSize;
    }

    public DownloadInfo() {

    }

    @Override
    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSavePath() {
        return this.savePath;
    }

    @Override
    public String getLocalPath() {
        return this.savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    @Override
    public long getTotalSize() {
        return this.totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    public long getCurrentPosition() {
        return downloadPosition;
    }

    public void setDownloadPosition(long downloadPosition) {
        this.downloadPosition = downloadPosition;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }
}
