package it.wsh.cn.wshlibrary.http.download;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.wsh.cn.wshlibrary.http.HttpConfig;
import it.wsh.cn.wshlibrary.http.HttpConstants;
import it.wsh.cn.wshlibrary.http.HttpServices;
import it.wsh.cn.wshlibrary.http.HttpStateCode;
import it.wsh.cn.wshlibrary.http.converter.ConvertFactory;
import it.wsh.cn.wshlibrary.http.https.SslContextFactory;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * author: wenshenghui
 * created on: 2018/8/25 12:51
 * description:
 */
public class DownloadTask {

    private static final String TAG = "DownloadTask";

    private Context mContext;
    private Retrofit mCurrentRetrofit;
    private HttpServices mCurrentServices;
    private final Gson mGson = new Gson();
    private boolean mExit = false; //控制退出

    private DownloadInfo mDownloadInfo = new DownloadInfo();
    private File mSaveFile;
    private String mTotalLength = "-";
    private DownloadCallBack mDownloadCallBack;

    private static final int SUCCESS = 1;
    private static final int PROGRESS = 2;
    private static final int ERROR = 3;

    private List<DownloadProcessListener> mProcessListeners = new ArrayList<>();

    public DownloadTask(Context context) {
        if (context == null) {
            return;
        }
        mContext = context.getApplicationContext();
    }

    private Handler mMainHander = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    String fileName = (String) msg.obj;
                    mDownloadCallBack.onSuccess(fileName);
                    break;
                case PROGRESS:
                    int progress = (int) msg.obj;
                    notifyProcessUpdate(progress);
                    break;
                case ERROR:
                    String errorInfo = (String) msg.obj;
                    mDownloadCallBack.onError(HttpStateCode.ERROR_DOWNLOAD_RETROFIT, errorInfo);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * @param url
     * @return
     */
    public DownloadTask init(String url) {

        if (TextUtils.isEmpty(url)) {
            return null;
        }

        HttpConfig config = HttpConfig.create(true);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(config.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(config.getWriteTimeout(), TimeUnit.SECONDS);
        //测试用  跳过所有认证
        if (url.startsWith(HttpConstants.HTTPS)) {
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
        OkHttpClient client = builder.build();

        try {
            mCurrentRetrofit = new Retrofit.Builder()
                    .client(client)
                    .addConverterFactory(new ConvertFactory(mGson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl("http://dldir1.qq.com/")
                    .build();

        } catch (Exception e) {
            mCurrentRetrofit = null;
            e.printStackTrace();
            return null;
        }

        if (mCurrentRetrofit != null) {
            mCurrentServices = mCurrentRetrofit.create(HttpServices.class);
            return this;
        }

        mCurrentServices = mCurrentRetrofit.create(HttpServices.class);
        return this;
    }

    //下载
    public void downloadFile(final String url, final String fileName, DownloadCallBack  downloadCallback) {

        if (downloadCallback == null) {
            downloadCallback = new EmptyDownloadCallback();
        }
        if (TextUtils.isEmpty(url)) {
            downloadCallback.onError(HttpStateCode.ERROR_DOWNLOAD_URL_IS_NULL, "");
            return;
        }

        mDownloadInfo.setUrl(url);
        mDownloadInfo.setFileName(fileName);
        mDownloadCallBack = downloadCallback;

        DownloadInfo info = DownloadInfoDaoHelper.queryTask(url);
        if (info != null) {
            mDownloadInfo = info;
            mSaveFile = new File(info.getSavePath());
            long totalSize = mDownloadInfo.getTotolSize();
            mTotalLength += totalSize;
            if (TextUtils.isEmpty(mDownloadInfo.getFileName())) {
                mDownloadInfo.setFileName(info.getFileName());
            }
            Log.d("wsh_log", "DownloadInfoDaoHelper.queryTask(url) != null； mDownloadInfo.getDownloadPosition() =" +  mDownloadInfo.getDownloadPosition());
            if (mSaveFile.exists()) {
                if (mDownloadInfo.getDownloadPosition() == 0) {
                    mSaveFile.delete();
                }else if (mSaveFile.length() > 0){
                    if (mDownloadInfo.getDownloadPosition() == totalSize) {
                        mDownloadCallBack.onSuccess(mDownloadInfo.getFileName());
                        return;
                    }
                }
            }else {
                mDownloadInfo.setDownloadPosition(0);
            }
        }else {
            if (TextUtils.isEmpty(mDownloadInfo.getFileName())) {
                mDownloadInfo.setFileName(url.substring(url.lastIndexOf('/') + 1));
                if (TextUtils.isEmpty(mDownloadInfo.getFileName())) {
                    mDownloadInfo.setFileName(url.hashCode() + "");
                }
            }
            String downloadPath = DownloadPathConfig.getDownloadPath(mContext);
            mSaveFile = new File(downloadPath + mDownloadInfo.getFileName());
            mDownloadInfo.setSavePath(mSaveFile.getPath());
            if (mSaveFile.exists()) {
                mSaveFile.delete();
            }
        }
        download();
    }

    private void download() {

        mCurrentServices.download("bytes=" + Long.toString(
                mDownloadInfo.getDownloadPosition()) + mTotalLength, mDownloadInfo.getUrl())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        RandomAccessFile randomAccessFile = null;
                        InputStream inputStream = null;
                        long downloadedLength = mDownloadInfo.getDownloadPosition();
                        long responseLength = 0;
                        try {
                            byte[] buf = new byte[1024 * 16];
                            int len = 0;
                            responseLength = responseBody.contentLength();
                            inputStream = responseBody.byteStream();
                            if (!mSaveFile.exists()) {
                                mSaveFile.getParentFile().mkdirs();
                            }
                            if (!mSaveFile.exists()) {
                                mSaveFile.createNewFile();
                            }
                            randomAccessFile = new RandomAccessFile(mSaveFile, "rwd");
                            if (downloadedLength == 0) {
                                randomAccessFile.setLength(responseLength);
                                mDownloadInfo.setTotolSize(responseLength);
                            }
                            randomAccessFile.seek(downloadedLength);
                            long totalSize = randomAccessFile.length();
                            if (downloadedLength == totalSize) {
                                mExit = true;
                                Message message = new Message();
                                message.what = SUCCESS;
                                message.obj = mDownloadInfo.getFileName();
                                mMainHander.sendMessage(message);
                                return;
                            }

                            int progress = 0;
                            int lastProgress = 0;

                            DownloadDataKernel.getInstance().startUpdateTimer();
                            while (!mExit && (len = inputStream.read(buf)) != -1) {
                                randomAccessFile.write(buf, 0, len);
                                downloadedLength += len;

                                lastProgress = progress;
                                progress = (int) (downloadedLength * 100 / totalSize);
                                if (progress > 0 && progress != lastProgress) {
                                    Message message = new Message();
                                    message.what = PROGRESS;
                                    message.obj = progress;
                                    mMainHander.sendMessage(message);
                                }
                                mDownloadInfo.setDownloadPosition(downloadedLength);
                            }
                            if (mExit) {
                                mDownloadCallBack.onPause();
                            }else {
                                Message message = new Message();
                                message.what = SUCCESS;
                                message.obj = mDownloadInfo.getFileName();
                                mMainHander.sendMessage(message);
                            }
                            mExit = true;
                        } catch (Exception e) {
                            Message message = new Message();
                            message.what = ERROR;
                            message.obj = e.getMessage();
                            mMainHander.sendMessage(message);
                        } finally {
                            try {
                                if (randomAccessFile != null) {
                                    randomAccessFile.close();
                                }

                                if (inputStream != null) {
                                    inputStream.close();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Message message = new Message();
                        message.what = ERROR;
                        message.obj = e.toString();
                        mMainHander.sendMessage(message);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void exit() {
        mExit = true;
    }

    /**
     * 添加下载监听
     * @param processListener
     */
    public void addProcessListener(DownloadProcessListener processListener) {
        if (processListener == null) {
            return;
        }
        if (mProcessListeners.contains(processListener)) {
            processListener.onError(HttpStateCode.ERROR_HAS_REGIST, "");
            return;
        }

        mProcessListeners.add(processListener);
    }

    /**
     * 删除下载监听
     * @param processListener
     */
    public boolean removeProcessListener(DownloadProcessListener processListener) {
        if (processListener == null || mProcessListeners == null || mProcessListeners.size() == 0) {
            return false;
        }
        return mProcessListeners.remove(processListener);
    }

    /**
     * 通知所有的DownloadProcessListener更新
     * @param progress
     */
    private void notifyProcessUpdate(int progress) {
        if (mProcessListeners == null || mProcessListeners.size() == 0) {
            return;
        }
        for (DownloadProcessListener processListener : mProcessListeners) {
            processListener.onProgress(progress);
        }
    }

    /**
     * 获取下载信息
     * @return
     */
    public DownloadInfo getDownloadInfo() {
        return mDownloadInfo;
    }
}
