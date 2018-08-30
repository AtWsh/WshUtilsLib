package it.wsh.cn.wshutilslib.httpdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import it.wsh.cn.wshlibrary.http.HttpCallBack;
import it.wsh.cn.wshlibrary.http.download.XLDownloadManager;
import it.wsh.cn.wshutilslib.R;


/**
 * author: wenshenghui
 * created on: 2018/8/24 10:52
 * description:
 */
public class DownloadTestActivity extends AppCompatActivity {

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
    private String mDownloadUrl3 = "http://downapp.baidu.com/baidusearch/AndroidPhone/10.11.0.13.1/1/757p/20180826161341/baidusearch_AndroidPhone_10-11-0-13-1_757p.apk?responseContentDisposition=attachment%3Bfilename%3D%22baidusearch_AndroidPhone_757p.apk%22&responseContentType=application%2Fvnd.android.package-archive&request_id=1535362308_7026252018&type=static";

    public static void luanchActivity(Activity context){
        Intent intent = new Intent(context, DownloadTestActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_test);

        initView();
        initAction();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(DownloadTestActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(DownloadTestActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                            当某条权限之前已经请求过，并且用户已经拒绝了该权限时，shouldShowRequestPermissionRationale ()方法返回的是true
                } else {
//                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
                // other 'case' lines to check for other
                // permissions this app might request
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

            String downloadUrl = mDownloadUrl1;
            boolean deleteDownloadTask = XLDownloadManager.getInstance(DownloadTestActivity.this).deleteDownloadTask(downloadUrl);
            if (deleteDownloadTask) {
                Toast.makeText(DownloadTestActivity.this, "成功删除", Toast.LENGTH_SHORT).show();
                mProgressBar1.setProgress(0);
            }else {
                Toast.makeText(DownloadTestActivity.this, "删除失败或者任务不存在", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener mDeleteClickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String downloadUrl = mDownloadUrl2;
            boolean deleteDownloadTask = XLDownloadManager.getInstance(DownloadTestActivity.this).deleteDownloadTask(downloadUrl);
            if (deleteDownloadTask) {
                Toast.makeText(DownloadTestActivity.this, "成功删除", Toast.LENGTH_SHORT).show();
                mProgressBar2.setProgress(0);
            }else {
                Toast.makeText(DownloadTestActivity.this, "删除失败或者任务不存在", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener mDeleteClickListener3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String downloadUrl = mDownloadUrl3;
            boolean deleteDownloadTask = XLDownloadManager.getInstance(DownloadTestActivity.this).deleteDownloadTask(downloadUrl);
            if (deleteDownloadTask) {
                Toast.makeText(DownloadTestActivity.this, "成功删除", Toast.LENGTH_SHORT).show();
                mProgressBar3.setProgress(0);
            }else {
                Toast.makeText(DownloadTestActivity.this, "删除失败或者任务不存在", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener mPauseClickListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String downloadUrl = mDownloadUrl1;
            XLDownloadManager.getInstance(DownloadTestActivity.this).stopDownload(downloadUrl);
        }
    };

    private View.OnClickListener mPauseClickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String downloadUrl = mDownloadUrl2;
            XLDownloadManager.getInstance(DownloadTestActivity.this).stopDownload(downloadUrl);
        }
    };

    private View.OnClickListener mPauseClickListener3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String downloadUrl = mDownloadUrl3;
            XLDownloadManager.getInstance(DownloadTestActivity.this).stopDownload(downloadUrl);
        }
    };

    private View.OnClickListener mDownloadClickListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String path = Environment.getExternalStorageDirectory().getPath();
            String downloadPath = path + "/111";
            XLDownloadManager.getInstance(DownloadTestActivity.this).setSaveFile(downloadPath);


            String downloadUrl = mDownloadUrl1;
            XLDownloadManager.getInstance(DownloadTestActivity.this).startDownload(downloadUrl, new HttpCallBack<String>() {
                @Override
                public void onSuccess(String fileName) {
                    Toast.makeText(DownloadTestActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    installApp(fileName);
                }

                @Override
                public void onError(int stateCode, String errorInfo) {
                    Toast.makeText(DownloadTestActivity.this, "下载失败：" + errorInfo, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(int progress) {
                    mProgressBar1.setProgress(progress);
                }
            });
        }
    };

    private View.OnClickListener mDownloadClickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String path = Environment.getExternalStorageDirectory().getPath();
            String downloadPath = path + "/111";
            XLDownloadManager.getInstance(DownloadTestActivity.this).setSaveFile(downloadPath);


            String downloadUrl = mDownloadUrl2;
            XLDownloadManager.getInstance(DownloadTestActivity.this).startDownload(downloadUrl, new HttpCallBack<String>() {
                @Override
                public void onSuccess(String fileName) {
                    Toast.makeText(DownloadTestActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    installApp(fileName);
                }

                @Override
                public void onError(int stateCode, String errorInfo) {
                    Toast.makeText(DownloadTestActivity.this, "下载失败：" + errorInfo, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(int progress) {
                    mProgressBar2.setProgress(progress);
                }
            });
        }
    };

    private View.OnClickListener mDownloadClickListener3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String path = Environment.getExternalStorageDirectory().getPath();
            String downloadPath = path + "/111";
            XLDownloadManager.getInstance(DownloadTestActivity.this).setSaveFile(downloadPath);


            String downloadUrl = mDownloadUrl3;
            XLDownloadManager.getInstance(DownloadTestActivity.this).startDownload(downloadUrl, new HttpCallBack<String>() {
                @Override
                public void onSuccess(String fileName) {
                    Toast.makeText(DownloadTestActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    installApp(fileName);
                }

                @Override
                public void onError(int stateCode, String errorInfo) {
                    Toast.makeText(DownloadTestActivity.this, "下载失败：" + errorInfo, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(int progress) {
                    mProgressBar3.setProgress(progress);
                }
            });
        }
    };

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

    private void installApp(String fileName) {

    }
}