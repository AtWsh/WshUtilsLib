package it.wsh.cn.common_oss.task;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.HeadObjectRequest;
import com.alibaba.sdk.android.oss.model.HeadObjectResult;
import com.alibaba.sdk.android.oss.model.Range;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketTimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import it.wsh.cn.common_http.http.IProcessListener;
import it.wsh.cn.common_http.http.database.bean.OssInfo;
import it.wsh.cn.common_http.http.database.daohelper.OssInfoDaoHelper;
import it.wsh.cn.common_http.http.download.IDownloadTask;
import it.wsh.cn.common_http.http.utils.IOUtil;
import it.wsh.cn.common_oss.InputStreamCallBack;
import it.wsh.cn.common_oss.rxobserver.OssInputStreamObserver;
import it.wsh.cn.common_oss.rxobserver.OssObserver;

/**
 * author: wenshenghui
 * created on: 2018/10/11 14:42
 * description:
 */
public class OssDownloadTask implements IDownloadTask {

    private Context mContext;
    private OSS mOss;
    private OSSCredentialProvider mCredentialProvider;
    private File mSaveFile; //存储路径
    private OssObserver mOssObserver; //回调
    private OssInputStreamObserver mOssInputStreamObserver;
    private OssInfo mOssInfo;
    private boolean mExit = false; //控制退出
    private boolean mDelete = false; //控制删除

    public OssDownloadTask(Context context, OssInfo info, IProcessListener callBack) {
        if (context == null || info == null) {
            return;
        }
        mContext = context.getApplicationContext();
        mOssInfo = info;
        init(callBack);
    }

    public OssDownloadTask(Context context, OssInfo info, InputStreamCallBack callBack) {
        if (context == null || info == null) {
            return;
        }
        mContext = context.getApplicationContext();
        mOssInfo = info;
        init(callBack);
    }

    /**
     * 初始化OssClient
     * callback可以为null
     */
    private void init(InputStreamCallBack callBack) {
        if (mContext == null || mOssInfo == null) {
            if (callBack != null) {
                callBack.onError();
            }
            return;
        }
        String endpoint = mOssInfo.getEndpoint();
        mOssInputStreamObserver = new OssInputStreamObserver();
        mOssInputStreamObserver.setCallBack(callBack);
        initCredentialProvider();
        if (mCredentialProvider == null) {
            if (callBack != null) {
                callBack.onError();
            }
            return;
        }
        if (mOss == null) {
            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
            conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
            conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
            conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
            //OSSLog.enableLog(); //这个开启会支持写入手机sd卡中的一份日志文件位置在SD_path\OSSLog\logs.csv
            mOss = new OSSClient(mContext, endpoint, mCredentialProvider, conf);
        } else {
            mOss.updateCredentialProvider(mCredentialProvider);
        }
    }

    /**
     * 初始化OssClient
     * callback可以为null
     */
    private void init(IProcessListener callBack) {
        if (mContext == null || mOssInfo == null) {
            notifyCreateError(callBack);
            return;
        }
        String endpoint = mOssInfo.getEndpoint();
        mOssObserver = new OssObserver(mOssInfo.getKey());
        mOssObserver.addListener(callBack);
        initCredentialProvider();
        if (mCredentialProvider == null) {
            notifyCreateError(callBack);
            return;
        }
        if (mOss == null) {
            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
            conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
            conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
            conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
            //OSSLog.enableLog(); //这个开启会支持写入手机sd卡中的一份日志文件位置在SD_path\OSSLog\logs.csv
            mOss = new OSSClient(mContext, endpoint, mCredentialProvider, conf);
        } else {
            mOss.updateCredentialProvider(mCredentialProvider);
        }
    }

    /**
     * 初始化异常
     *
     * @param callBack
     */
    private void notifyCreateError(IProcessListener callBack) {
        if (callBack != null) {
            callBack.onResult(IProcessListener.ERROR_DOWNLOAD_OSSTASK_CREATE, null);
        }
    }

    /**
     * 初始化mCredentialProvider
     */
    private void initCredentialProvider() {
        if (mOssInfo == null) {
            return;
        }
        OSSFederationToken ossFederationToken = new OSSFederationToken(mOssInfo.getAccessKeyId(),
                mOssInfo.getAccessKeySecret(), mOssInfo.getSecurityToken(),
                mOssInfo.getExpiration());
        mCredentialProvider = new OSSStsTokenCredentialProvider(ossFederationToken.getTempAK(),
                ossFederationToken.getTempSK(), ossFederationToken.getSecurityToken());
    }

    /**
     * 从阿里服务器下载文件
     */
    @Override
    public void start() {

        Observable.just(mOssInfo).flatMap(new Function<OssInfo,
                Observable<OssInfo>>() {
            @Override
            public Observable<OssInfo> apply(OssInfo info)
                    throws Exception {
                return Observable.just(createOssInfo(info));
            }
        }).flatMap(new Function<OssInfo, Observable<OssInfo>>() {
            @Override
            public Observable<OssInfo> apply(OssInfo ossInfo)
                    throws Exception {
                return Observable.create(new DownloadSubscribe(ossInfo));
            }
        }).observeOn(AndroidSchedulers.mainThread())//在主线程回调
                .subscribeOn(Schedulers.io())//在子线程执行
                .subscribe(mOssObserver);
    }

    /**
     * 从阿里服务器下载文件
     */
    public void startDownloadPic() {

        Observable.just(mOssInfo).flatMap(new Function<OssInfo, Observable<InputStream>>() {
            @Override
            public Observable<InputStream> apply(OssInfo ossInfo)
                    throws Exception {
                return Observable.create(new DownloadPicSubscribe(ossInfo));
            }
        }).observeOn(AndroidSchedulers.mainThread())//在主线程回调
                .subscribeOn(Schedulers.io())//在子线程执行
                .subscribe(mOssInputStreamObserver);
    }

    /**
     * 创建DownInfo
     *
     * @param ossInfo 请求网址
     * @return DownInfo
     */
    private OssInfo createOssInfo(OssInfo ossInfo) {

        int key = ossInfo.getKey();
        String bucketName = ossInfo.getBucketName();
        OssInfo info = OssInfoDaoHelper.queryTask(key);
        if (info != null) {
            ossInfo = info;
            ossInfo.setBucketName(bucketName);
            mSaveFile = new File(info.getSavePath());
            long totalSize = ossInfo.getTotalSize();
            String md5 = ossInfo.getRemoteMd5();
            if (mSaveFile.exists()) {
                ossInfo.setDownloadPosition(mSaveFile.length());
            }

            if (ossInfo.getCurrentPosition() != 0) { //此种情况下开启断点续传的
                ServerInfo serverInfo = getServerInfo(ossInfo);
                if (!md5.equals(serverInfo.mServerMd5) || totalSize != serverInfo.mServerLength) {
                    ossInfo.setDownloadPosition(0);
                    ossInfo.setTotalSize(serverInfo.mServerLength);
                    if (mSaveFile.exists()) {
                        mSaveFile.delete();
                    }
                }
            }
        } else {
            initFirstDownload(ossInfo);
            ServerInfo serverInfo = getServerInfo(ossInfo);
            ossInfo.setRemoteMd5(serverInfo.mServerMd5);
            ossInfo.setTotalSize(serverInfo.mServerLength);
        }
        return ossInfo;
    }

    /**
     * 第一次下载的初始化
     *
     * @param ossInfo
     */
    private void initFirstDownload(OssInfo ossInfo) {
        String downloadPath = ossInfo.getSavePath();
        mSaveFile = new File(downloadPath);
        if (mSaveFile.exists()) {
            mSaveFile.delete();
        }
    }

    class ServerInfo {
        public String mServerMd5;
        public long mServerLength = -1;
    }

    /**
     * 获取阿里服务器的远程信息
     *
     * @param ossInfo
     * @return
     */
    private ServerInfo getServerInfo(OssInfo ossInfo) {
        String bucketName = ossInfo.getBucketName();
        String remotePath = ossInfo.getUrl();
        HeadObjectRequest headReq = new HeadObjectRequest(bucketName, remotePath);
        ServerInfo serverInfo = new ServerInfo();
        try {
            HeadObjectResult headObjectResult = mOss.headObject(headReq);
            serverInfo.mServerMd5 = headObjectResult.getMetadata().getETag();
            serverInfo.mServerLength = headObjectResult.getMetadata().getContentLength();
            return serverInfo;
        } catch (ClientException e) {
            //获取MD5值失败
            e.printStackTrace();
        } catch (ServiceException e) {
            //获取MD5值失败
            e.printStackTrace();
        }
        return serverInfo;
    }

    private class DownloadPicSubscribe implements ObservableOnSubscribe<InputStream> {
        private OssInfo ossInfo;

        public DownloadPicSubscribe(OssInfo ossInfo) {
            this.ossInfo = ossInfo;
        }

        @Override
        public void subscribe(ObservableEmitter<InputStream> e) throws Exception {
            try{
                String url = ossInfo.getUrl();
                String bucketName = ossInfo.getBucketName();
                GetObjectRequest request = new GetObjectRequest(bucketName, url);
                request.setObjectKey(url);
                GetObjectResult result = mOss.getObject(request);
                e.onNext(result.getObjectContent());
            }catch(Exception exception) {
                e.onError(exception);
            }

        }
    }

    private class DownloadSubscribe implements ObservableOnSubscribe<OssInfo> {
        private OssInfo ossInfo;

        public DownloadSubscribe(OssInfo ossInfo) {
            this.ossInfo = ossInfo;
        }

        @Override
        public void subscribe(ObservableEmitter<OssInfo> e) throws Exception {

            String url = ossInfo.getUrl();
            long downloadLength = ossInfo.getCurrentPosition();//已经下载好的长度
            long responseLength = ossInfo.getTotalSize();//文件的总长度, 注意此处可能为0
            String saveFilePath = ossInfo.getSavePath();
            String serverMd5 = ossInfo.getRemoteMd5();
            String bucketName = ossInfo.getBucketName();
            File saveFile = new File(saveFilePath);
            if (!saveFile.exists()) {
                saveFile.getParentFile().mkdirs();
            }
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }
            if (responseLength == -1 || TextUtils.isEmpty(serverMd5)) {
                e.onError(new SocketTimeoutException());
                return;
            }

            GetObjectRequest request = new GetObjectRequest(bucketName, url);
            request.setObjectKey(url);

            if (responseLength != 0) {
                request.setRange(new Range(downloadLength, Range.INFINITE));
            }
            GetObjectResult result = mOss.getObject(request);

            //初始进度信息
            e.onNext(ossInfo);
            if (downloadLength == 0) {
                OssInfoDaoHelper.insertInfo(ossInfo);
            }
            if (downloadLength >= responseLength) {
                //初始进度信息
                e.onNext(ossInfo);
                e.onComplete();//完成
                return;
            }

            RandomAccessFile randomAccessFile = null;
            InputStream inputStream = null;
            try {
                randomAccessFile = new RandomAccessFile(saveFile, "rwd");
                randomAccessFile.seek(downloadLength);
                inputStream = result.getObjectContent();
                byte[] buffer = new byte[1024 * 16];//缓冲数组16kB
                int len;
                while (!mDelete && !mExit && (len = inputStream.read(buffer)) != -1) {
                    randomAccessFile.write(buffer, 0, len);
                    downloadLength += len;
                    ossInfo.setDownloadPosition(downloadLength);
                    e.onNext(ossInfo);
                }
            } catch (Exception t) {
                e.onError(t);
                return;
            } finally {
                //关闭IO流
                IOUtil.closeAll(inputStream, randomAccessFile);
            }
            if (mDelete) { //删除操作
                ossInfo.setDownloadPosition(0);
                e.onNext(ossInfo);
            } else if (mExit) { //stop操作
                e.onError(new Throwable(IProcessListener.PAUSE_STATE));
            } else {
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
        if (mOssObserver != null) {
            return mOssObserver.addListener(listener);
        }
        return false;
    }

    /**
     * 删除下载监听
     * @param listener
     */
    @Override
    public boolean removeProcessListener(IProcessListener listener) {
        if (mOssObserver != null) {
            return mOssObserver.removeListener(listener);
        }
        return false;
    }

}
