package it.wsh.cn.common_oss.bean;


import it.wsh.cn.common_http.http.IProcessInfo;

public class UploadInfo implements IProcessInfo {

    private String mUrl;
    private long mTotalSize;
    private long mCurrentPosition;
    private String mLocalPath;

    public void setLocalPath(String localPath) {
        mLocalPath = localPath;
    }

    public void setCurrentPosition(long currentPosition) {
        mCurrentPosition = currentPosition;
    }

    public void setTotalSize(long totalSize) {
        mTotalSize = totalSize;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public long getTotalSize() {
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        return 0;
    }

    @Override
    public int getProcess() {
        return (int) (mCurrentPosition * 100 / mTotalSize);
    }

    @Override
    public String getLocalPath() {
        return mLocalPath;
    }

    @Override
    public String getFileName() {
        return ""; //需要再补
    }
}
