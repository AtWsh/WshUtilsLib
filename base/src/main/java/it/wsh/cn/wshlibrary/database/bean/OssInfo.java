package it.wsh.cn.wshlibrary.database.bean;

import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import it.wsh.cn.wshlibrary.http.IProcessInfo;
import org.greenrobot.greendao.annotation.Generated;

/**
 * author: wenshenghui
 * created on: 2018/10/12 9:26
 * description:
 */
@Entity
public class OssInfo implements IProcessInfo {

    @Unique
    private int key;
    @Property
    private String url;
    @Property
    private String savePath;
    @Transient
    private long downloadPosition;
    @Property
    private long totalSize;
    @Property
    private String remoteMd5; //阿里服务器原文件的MD5值，用于文件校验
    @Transient
    private String fileName;
    @Transient
    private int process;
    @Transient
    private String endpoint; //由网络上取，存储在SP的值， 用于OSS初始化
    @Transient
    private String bucketName;//由网络上取，存储在SP的值，用于OSS初始化
    @Transient
    private OSSFederationToken stsToken; //Token 存储过期等信息, 用于OSS初始化

    @Generated(hash = 1482315147)
    public OssInfo(int key, String url, String savePath, long totalSize,
            String remoteMd5) {
        this.key = key;
        this.url = url;
        this.savePath = savePath;
        this.totalSize = totalSize;
        this.remoteMd5 = remoteMd5;
    }

    @Generated(hash = 1915390878)
    public OssInfo() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getRemoteMd5() {
        return remoteMd5;
    }

    public void setRemoteMd5(String remoteMd5) {
        this.remoteMd5 = remoteMd5;
    }

    public OSSFederationToken getStsToken() {
        return stsToken;
    }

    public void setStsToken(OSSFederationToken stsToken) {
        this.stsToken = stsToken;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    public long getDownloadPosition() {
        return downloadPosition;
    }

    public void setDownloadPosition(long downloadPosition) {
        this.downloadPosition = downloadPosition;
    }

    @Override
    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

}
