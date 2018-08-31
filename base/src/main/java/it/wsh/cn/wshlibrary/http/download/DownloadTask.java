package it.wsh.cn.wshlibrary.http.download;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
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
    private boolean mFinish = false; //是否下载完成
    private boolean mDownloading = false; //是否在下载中

    private String mDownloadUrl;
    private String mFileName;
    private File mSaveFile;
    private String mTotalLength = "-";
    private long mRange = 0;
    private DownloadCallBack mDownloadCallBack;
    private long mTotalSize;

    private static final int SUCCESS = 1;
    private static final int PROGRESS = 2;
    private static final int ERROR = 3;

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
                    mDownloadCallBack.onProgress(progress);
                    break;
                case ERROR:
                    String errorInfo = (String) msg.obj;
                    mDownloadCallBack.onError(HttpStateCode.ERROR_DOWNLOAD, errorInfo);
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
    public void downloadFile(final String url, final String fileName, final DownloadCallBack  downloadCallback) {

        if (mCurrentServices == null) {
            return;
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }

        mDownloadUrl = url;
        mFileName = fileName;
        mDownloadCallBack = downloadCallback;

        DownloadInfo info = DownloadInfoDaoHelper.queryTask(url);
        int progress = 0;
        if (info != null) {
            mRange = info.getDownloadPosition();
            mSaveFile = new File(info.getSavePath());
            mTotalSize = info.getTotolSize();
            mTotalLength += mTotalSize;
            if (mSaveFile.exists()) {
                if (mRange == 0) {
                    mSaveFile.delete();
                }else if (mSaveFile.length() > 0){
                    progress = (int) (mRange * 100 / mTotalSize);
                    if (mRange == mTotalSize) {
                        mDownloadCallBack.onSuccess(mFileName);
                        return;
                    }
                }
            }
        }else {
            String downloadPath = DownloadPathConfig.getDownloadPath(mContext);
            mSaveFile = new File(downloadPath + fileName);
            if (mSaveFile.exists()) {
                mSaveFile.delete();
            }
        }

        mDownloadCallBack.onProgress(progress);
        download();
    }

    private void download() {

        mDownloading = true;
        mCurrentServices.download("bytes=" + Long.toString(mRange) + mTotalLength, mDownloadUrl)
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
                        long downloadedLength = mRange;
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
                            if (mRange == 0) {
                                randomAccessFile.setLength(responseLength);
                            }
                            randomAccessFile.seek(mRange);
                            mTotalSize = randomAccessFile.length();
                            if (mRange == mTotalSize) {
                                mDownloading = false;
                                mFinish = true;
                                mExit = true;
                                Message message = new Message();
                                message.what = SUCCESS;
                                message.obj = mFileName;
                                mMainHander.sendMessage(message);
                                return;
                            }

                            int progress = 0;
                            int lastProgress = 0;

                            int updateCount = 0;
                            while (!mExit && (len = inputStream.read(buf)) != -1) {
                                randomAccessFile.write(buf, 0, len);
                                downloadedLength += len;

                                lastProgress = progress;
                                progress = (int) (downloadedLength * 100 / mTotalSize);
                                if (progress > 0 && progress != lastProgress) {
                                    Message message = new Message();
                                    message.what = PROGRESS;
                                    message.obj = progress;
                                    mMainHander.sendMessage(message);
                                }
                                if ((updateCount % 5) == 0) {
                                    DownloadInfoDaoHelper.insertInfoSync(mDownloadUrl, mFileName,
                                            mSaveFile.getPath(), downloadedLength, mTotalSize);
                                }

                                updateCount++;
                            }
                            mDownloading = false;
                            if (!mExit) {
                                mFinish = true;
                                Message message = new Message();
                                message.what = SUCCESS;
                                message.obj = mFileName;
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
                                mDownloading = false;
                                DownloadInfoDaoHelper.insertInfoSync(mDownloadUrl, mFileName,
                                        mSaveFile.getPath(), downloadedLength, mTotalSize);
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
                        mDownloading = false;
                        Message message = new Message();
                        message.what = ERROR;
                        message.obj = e.toString();
                        mMainHander.sendMessage(message);
                    }

                    @Override
                    public void onComplete() {
                        mDownloading = false;
                    }
                });
    }

    public void exit() {
        mExit = true;
        mDownloading = false;
        mDownloadCallBack.onPause();
    }

    public boolean isFinish() {
        return mFinish;
    }

    public boolean isDownloading() {return mDownloading;}

}
