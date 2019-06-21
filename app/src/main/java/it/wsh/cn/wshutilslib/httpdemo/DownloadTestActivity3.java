package it.wsh.cn.wshutilslib.httpdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import it.wsh.cn.wshutilslib.R;

/**
 * 使用系统的DownloadManager实现断点续传
 */
public class DownloadTestActivity3 extends AppCompatActivity {

    private ProgressBar mProgressBar1;
    private TextView mDownloadBtn1;
    private TextView mPauseBtn1;
    private TextView mDeleteBtn1;

    private ProgressBar mProgressBar2;
    private TextView mDownloadBtn2;
    private TextView mPauseBtn2;
    private TextView mDeleteBtn2;

    private ProgressBar mProgressBar3;
    private TextView mDownloadBtn3;
    private TextView mPauseBtn3;
    private TextView mDeleteBtn3;

    private String mDownloadUrl1 = "http://dldir1.qq.com/qqmi/aphone_p2p/TencentVideo_V6.0.0.14297_848.apk";
    private String mDownloadUrl2 = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
    private String mDownloadUrl3 = "http://downapp.baidu.com/baidusearch/AndroidPhone/10.11.0.13.1/1/757p/20180826161341/" +
            "baidusearch_AndroidPhone_10-11-0-13-1_757p.apk?responseContentDisposition=attachment%3Bfilename%3D%22baidusearch_AndroidPhone_757p." +
            "apk%22&responseContentType=application%2Fvnd.android.package-archive&request_id=1535362308_7026252018&type=static";

    private DownloadHelper.DownloadChangeObserver mObserver;

    private final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");

    private long mId1;
    private long mId2;
    private long mId3;

    public static void luanchActivity(Activity context) {
        Intent intent = new Intent(context, DownloadTestActivity3.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_test);
        initView();
        initAction();
        register();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(DownloadTestActivity3.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(DownloadTestActivity3.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                            当某条权限之前已经请求过，并且用户已经拒绝了该权限时，shouldShowRequestPermissionRationale ()方法返回的是true
                } else {
//                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        }
    }

    private void register() {
        mObserver = new DownloadHelper.DownloadChangeObserver();
        getContentResolver().registerContentObserver(CONTENT_URI, true, mObserver);
    }

    private void initView() {
        mDownloadBtn1 = findViewById(R.id.tv_download1);
        mPauseBtn1 = findViewById(R.id.tv_pause1);
        mDeleteBtn1 = findViewById(R.id.tv_delete1);
        mProgressBar1 = findViewById(R.id.pb_progress1);
        mProgressBar1.setMax(100);
        mProgressBar1.setProgress(0);

        mDownloadBtn2 = findViewById(R.id.tv_download2);
        mPauseBtn2 = findViewById(R.id.tv_pause2);
        mDeleteBtn2 = findViewById(R.id.tv_delete2);
        mProgressBar2 = findViewById(R.id.pb_progress2);
        mProgressBar2.setMax(100);
        mProgressBar2.setProgress(0);

        mDownloadBtn3 = findViewById(R.id.tv_download3);
        mPauseBtn3 = findViewById(R.id.tv_pause3);
        mDeleteBtn3 = findViewById(R.id.tv_delete3);
        mProgressBar3 = findViewById(R.id.pb_progress3);
        mProgressBar3.setMax(100);
        mProgressBar3.setProgress(0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }

    private void initAction() {
        mDownloadBtn1.setOnClickListener(mDownloadClickListener1);
        mPauseBtn1.setOnClickListener(mPauseClickListener1);
        mDeleteBtn1.setOnClickListener(mDeleteClickListener1);

        mDownloadBtn2.setOnClickListener(mDownloadClickListener2);
        mPauseBtn2.setOnClickListener(mPauseClickListener2);
        mDeleteBtn2.setOnClickListener(mDeleteClickListener2);

        mDownloadBtn3.setOnClickListener(mDownloadClickListener3);
        mPauseBtn3.setOnClickListener(mPauseClickListener3);
        mDeleteBtn3.setOnClickListener(mDeleteClickListener3);
    }

    private View.OnClickListener mDeleteClickListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DownloadHelper3.getInstance().remove(mId1);
        }
    };

    private View.OnClickListener mDeleteClickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DownloadHelper3.getInstance().remove(mId2);
        }
    };

    private View.OnClickListener mDeleteClickListener3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DownloadHelper3.getInstance().remove(mId3);
        }
    };


    private View.OnClickListener mPauseClickListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener mPauseClickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener mPauseClickListener3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener mDownloadClickListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mId1 = DownloadHelper3.getInstance().start(mDownloadUrl1, "测试1.apk");
            DownloadHelper3.getInstance().addListener(mId1, new IDownloadListener() {
                @Override
                public void progress(int progress) {
                    mProgressBar1.setProgress((int) progress);
                }
            });
        }
    };

    private View.OnClickListener mDownloadClickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mId2 = DownloadHelper3.getInstance().start(mDownloadUrl2, "测试2.apk");
            DownloadHelper3.getInstance().addListener(mId2, new IDownloadListener() {
                @Override
                public void progress(int progress) {
                    mProgressBar2.setProgress((int) progress);
                }
            });
        }
    };

    private View.OnClickListener mDownloadClickListener3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mId3 = DownloadHelper3.getInstance().start(mDownloadUrl3, "测试3.apk");
            DownloadHelper3.getInstance().addListener(mId3, new IDownloadListener() {
                @Override
                public void progress(int progress) {
                    mProgressBar3.setProgress((int) progress);
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().registerContentObserver(CONTENT_URI, true, mObserver);
        if (mObserver != null) {
            getContentResolver().unregisterContentObserver(mObserver);
        }
    }
}
