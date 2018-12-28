package it.wsh.cn.common_http.http.download;


import it.wsh.cn.common_http.http.IProcessListener;

public interface IDownloadTask {

    /**
     * 开始下载
     */
    void start();

    /**
     * 退出
     */
    void exit();

    /**
     * 删除任务
     */
    void delete();

    /**
     * 添加进度监听
     * @return
     */
    boolean addListener(IProcessListener listener);

    /**
     * 删除进度监听
     * @return
     */
    boolean removeProcessListener(IProcessListener listener);
}
