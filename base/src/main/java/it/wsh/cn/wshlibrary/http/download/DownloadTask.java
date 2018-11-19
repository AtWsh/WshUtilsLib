package it.wsh.cn.wshlibrary.http.download;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import it.wsh.cn.wshlibrary.database.bean.DownloadInfo;
import it.wsh.cn.wshlibrary.database.daohelper.DownloadInfoDaoHelper;
import it.wsh.cn.wshlibrary.http.HttpConfig;
import it.wsh.cn.wshlibrary.http.HttpConstants;
import it.wsh.cn.wshlibrary.http.IProcessListener;
import it.wsh.cn.wshlibrary.http.ssl.SslContextFactory;
import it.wsh.cn.wshlibrary.http.utils.IOUtil;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * author: wenshenghui
 * created on: 2018/8/25 12:51
 * description:
 */
public class DownloadTask implements IDownloadTask{

    private Context mContext;
    private OkHttpClient mClient;
    private File mSaveFile; //存储路径
    private DownloadObserver mDownloadObserver; //回调
    private DownloadInfo mDownloadInfo;
    private boolean mExit = false; //控制退出
    private boolean mDelete = false; //控制删除

    public DownloadTask(Context context, DownloadInfo info, IProcessListener callBack) {
        if (context == null || info == null) {
            return;
        }
        mContext = context.getApplicationContext();
        mDownloadInfo = info;
        init(callBack);
    }

    private void init(IProcessListener callBack) {

        if (TextUtils.isEmpty(mDownloadInfo.getUrl())) {
            return;
        }

        mDownloadObserver = new DownloadObserver(mDownloadInfo.getKey());
        mDownloadObserver.addListener(callBack);

        HttpConfig config = HttpConfig.create(true);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(config.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(config.getWriteTimeout(), TimeUnit.SECONDS);
        //测试用  跳过所有认证
        if (HttpConstants.HTTPS.startsWith(mDownloadInfo.getUrl())) {
            //SSLSocketFactory sslSocketFactory = new SslContextFactory().getSslSocket(mContext).getSocketFactory();
            //builder.sslSocketFactory(sslSocketFactory);
            builder.sslSocketFactory(new SslContextFactory().createSSLSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
        }
        mClient = builder.build();
        return;
    }

    //下载
    @Override
    public void start() {

        Observable.just(mDownloadInfo).flatMap(new Function<DownloadInfo,
                Observable<DownloadInfo>>() {
            @Override
            public Observable<DownloadInfo> apply(DownloadInfo info)
                    throws Exception {
                return Observable.just(createDownInfo(info));
            }
        }).flatMap(new Function<DownloadInfo, Observable<DownloadInfo>>() {
            @Override
            public Observable<DownloadInfo> apply(DownloadInfo downloadInfo)
                    throws Exception {
                return Observable.create(new DownloadSubscribe(downloadInfo));
            }
        }).observeOn(AndroidSchedulers.mainThread())//在主线程回调
                .subscribeOn(Schedulers.io())//在子线程执行
                .subscribe(mDownloadObserver);
    }

    /**
     * 创建DownInfo
     *
     * @param downloadInfo 请求网址
     * @return DownInfo
     */
    private DownloadInfo createDownInfo(DownloadInfo downloadInfo) {

        int key = downloadInfo.getKey();
        String url = downloadInfo.getUrl();
        DownloadInfo info = DownloadInfoDaoHelper.queryTask(key);
        if (info != null) {
            downloadInfo = info;
            mSaveFile = new File(info.getSavePath());
            long totalSize = downloadInfo.getTotalSize();
            if (mSaveFile.exists()) {
                downloadInfo.setDownloadPosition(mSaveFile.length());
            }

            if (downloadInfo.getCurrentPosition() != 0) {
                long serverContentLength = getServerContentLength(url);
                if (serverContentLength != totalSize) {
                    downloadInfo.setDownloadPosition(0);
                    downloadInfo.setTotalSize(serverContentLength);
                    if (mSaveFile.exists()) {
                        mSaveFile.delete();
                    }
                }
            }
        }else {
            initFirstDownload(downloadInfo);

        }
        return downloadInfo;
    }

    /**
     * 第一次下载的初始化
     * @param downloadInfo
     */
    private void initFirstDownload(DownloadInfo downloadInfo) {
        String downloadPath = downloadInfo.getSavePath();
        mSaveFile = new File(downloadPath);
        if (mSaveFile.exists()) {
            mSaveFile.delete();
        }
    }

    /**
     * 请求完整数据长度
     * @param url
     */
    private long getServerContentLength(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            okhttp3.Response response = mClient.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.close();
                return contentLength == 0 ? -1 : contentLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private class DownloadSubscribe implements ObservableOnSubscribe<DownloadInfo> {
        private DownloadInfo downloadInfo;

        public DownloadSubscribe(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void subscribe(ObservableEmitter<DownloadInfo> e) throws Exception {

            String url = downloadInfo.getUrl();
            long downloadLength = downloadInfo.getCurrentPosition();//已经下载好的长度
            long responseLength = downloadInfo.getTotalSize();//文件的总长度, 注意此处可能为0
            String saveFilePath = downloadInfo.getSavePath();
            File saveFile = new File(saveFilePath);
            if (!saveFile.exists()) {
                saveFile.getParentFile().mkdirs();
            }
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }
            if (responseLength == -1) {
                e.onError(new SocketTimeoutException());
                return;
            }

            Request.Builder builder = new Request.Builder().url(url);
            if (responseLength != 0) {
                builder.addHeader("RANGE", "bytes=" +
                        downloadLength + "-" + responseLength);
            }
            Request request = builder.build();
            Call call = mClient.newCall(request);
            okhttp3.Response response = call.execute();

            if (responseLength == 0) {
                responseLength = response.body().contentLength();
            }
            //初始进度信息
            e.onNext(downloadInfo);
            if (downloadLength == 0) {
                downloadInfo.setTotalSize(responseLength);
                DownloadInfoDaoHelper.insertInfo(downloadInfo);
            }
            if (downloadLength >= responseLength) {
                //初始进度信息
                e.onNext(downloadInfo);
                e.onComplete();//完成
                return;
            }

            RandomAccessFile randomAccessFile = null;
            InputStream inputStream = null;
            try {
                randomAccessFile = new RandomAccessFile(saveFile, "rwd");
                randomAccessFile.seek(downloadLength);
                inputStream = response.body().byteStream();
                byte[] buffer = new byte[1024 * 16];//缓冲数组16kB
                int len;
                while (!mDelete && !mExit && (len = inputStream.read(buffer)) != -1) {
                    randomAccessFile.write(buffer, 0, len);
                    downloadLength += len;
                    downloadInfo.setDownloadPosition(downloadLength);
                    e.onNext(downloadInfo);
                }
            } catch (Exception t) {
                e.onError(t);
                return;
            } finally {
                //关闭IO流
                IOUtil.closeAll(inputStream, randomAccessFile);
            }
            if (mDelete) { //删除操作
                downloadInfo.setDownloadPosition(0);
                e.onNext(downloadInfo);
            }else if (mExit) { //stop操作
                e.onError(new Throwable(IProcessListener.PAUSE_STATE));
            }else {
                e.onComplete();//完成
            }
        }
    }

    @Override
    public void exit() {
        mExit = true;
    }

    @Override
    public void delete() {
        this.mDelete = true;
    }


    /**
     * 添加下载监听
     * @param listener
     */
    @Override
    public boolean addListener(IProcessListener listener) {
        if (mDownloadObserver != null) {
            return mDownloadObserver.addListener(listener);
        }
        return false;
    }

    /**
     * 删除下载监听
     * @param listener
     */
    @Override
    public boolean removeProcessListener(IProcessListener listener) {
        if (mDownloadObserver != null) {
            return mDownloadObserver.removeListener(listener);
        }
        return false;
    }
}
