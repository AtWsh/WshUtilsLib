package it.wsh.cn.wshlibrary.http.download;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * author: wenshenghui
 * created on: 2018/8/31 9:49
 * description:
 */
@Entity
public class DownloadInfo {

    @Unique
    private String url;

    @Property
    private String fileName;

    @Property
    private String savePath;

    @Property
    private long downloadPosition;

    @Property
    private long totalSize;

    @Generated(hash = 327086747)
    public DownloadInfo() {
    }

    @Generated(hash = 1625172689) @Keep
    public DownloadInfo(String url, String fileName, String savePath,
            long downloadPosition, long totalSize) {
        this.url = url;
        this.fileName = fileName;
        this.savePath = savePath;
        this.downloadPosition = downloadPosition;
        this.totalSize = totalSize;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getDownloadPosition() {
        return this.downloadPosition;
    }

    public void setDownloadPosition(long downloadPosition) {
        this.downloadPosition = downloadPosition;
    }

    public String getSavePath() {
        return this.savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public long getTotalSize() {
        return this.totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

}
