package it.wsh.cn.wshutilslib.httpdemo;

import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class DownloadHelper3 {

    private Context mContext;

    private DownloadHelper3() {
    }

    private static volatile DownloadHelper3 sInstance;
    private DownloadManager mDownloadManager;
    private Map<Long, List<IDownloadListener>> mListeners = new HashMap<>();

    public static DownloadHelper3 getInstance() {
        if (sInstance == null) {
            synchronized (DownloadHelper3.class) {
                if (sInstance == null) {
                    sInstance = new DownloadHelper3();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        if (context == null) {
            return;
        }
        mContext = context.getApplicationContext();
        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public long start(String url, String title) {
        // uri 是你的下载地址，可以使用Uri.parse("http://")包装成Uri对象
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));

        // 通过setAllowedNetworkTypes方法可以设置允许在何种网络下下载，
        // 也可以使用setAllowedOverRoaming方法，它更加灵活
        //req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);

        // 此方法表示在下载过程中通知栏会一直显示该下载，在下载完成后仍然会显示，
        // 直到用户点击该通知或者消除该通知。还有其他参数可供选择
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // 设置下载文件存放的路径，同样你可以选择以下方法存放在你想要的位置。
        // setDestinationUri
        // setDestinationInExternalPublicDir
        req.setDestinationInExternalFilesDir(mContext, "111", title);

        // 设置一些基本显示信息
        req.setTitle(title);
        req.setDescription("下载完后请点击打开");
        req.setMimeType("application/vnd.android.package-archive");

        // Ok go!
        long downloadId = mDownloadManager.enqueue(req);

        return downloadId;
    }

    public static class DownloadChangeObserver extends ContentObserver {


        public DownloadChangeObserver() { //不设置handler
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            DownloadHelper3.getInstance().sendDownloadProcess();
        }
    }

    public void sendProcess(long id) {
        Observable.just(id).flatMap(new Function<Long,
                Observable<Long>>() {
            @Override
            public Observable<Long> apply(Long id)
                    throws Exception {
                return Observable.just(getDownloadProcess(id));
            }
        }).observeOn(AndroidSchedulers.mainThread())//在主线程回调
                .subscribeOn(Schedulers.io())//在子线程执行
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void  remove(long id) {
        mDownloadManager.remove(id);
    }

    public void  pause(long id) {
        //mDownloadManager.remove(id);
    }

    public void addListener(long id, IDownloadListener listener) {
        if (listener == null) {
            return;
        }
        List<IDownloadListener> listeners = mListeners.get(id);
        if (listeners == null) {
            listeners = new ArrayList<>();
            listeners.add(listener);
            mListeners.put(id, listeners);
            return;
        }
        if (listeners.contains(listener)) {
            return; //已经添加过
        }

        listeners.add(listener);
        mListeners.put(id, listeners);
    }

    private void sendDownloadProcess() {
        Observable.create(new ObservableOnSubscribe<Map<Integer, Integer>>() {
            @Override
            public void subscribe(ObservableEmitter<Map<Integer, Integer>> emitter) throws Exception {
                Map<Integer, Integer> processInfo = getProcessInfo();
                if (processInfo == null) {
                    emitter.onError(new Throwable("processInfo == null"));
                }else {
                    emitter.onNext(processInfo);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Map<Integer, Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map<Integer, Integer> integerIntegerMap) {
                        Iterator<Map.Entry<Integer, Integer>> it = integerIntegerMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<Integer, Integer> entry = it.next();
                            List<IDownloadListener> listeners = mListeners.get(new Long(entry.getKey()));
                            if (listeners != null) {
                                for (IDownloadListener listener : listeners) {
                                    listener.progress(entry.getValue());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private Map<Integer, Integer> getProcessInfo() {
        Map<Integer, Integer> keyAndProcess = new HashMap<>();
        DownloadManager.Query query = new DownloadManager.Query();
        Cursor c = null;
        try {
            c = mDownloadManager.query(query);
            if (!c.moveToFirst()) {
                // 无下载内容
                return null;
            }
            do {
                //已经下载的字节数
                long downloadedLength = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //总需下载的字节数
                long totalLength = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                int id = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_ID));
                String uri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                Log.d("wsh_log", "uri = " + uri + "  status = " + status);
                keyAndProcess.put(id, (int) (downloadedLength * 100 / totalLength));
            } while (c.moveToNext());
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return keyAndProcess;
    }

    private long getDownloadProcess(long id) {
        int[] bytesAndStatus = new int[]{0, 0, 0};
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(id);
        Cursor c = null;
        try {
            c = mDownloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                //已经下载的字节数
                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //总需下载的字节数
                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return (bytesAndStatus[0] * 100) / bytesAndStatus[1];
    }
}
