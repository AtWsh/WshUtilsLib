package it.wsh.cn.wshutilslib.ossdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import it.wsh.cn.wshlibrary.http.IProcessInfo;
import it.wsh.cn.wshlibrary.http.IProcessListener;
import it.wsh.cn.wshutilslib.R;
import it.wsh.cn.wshutilslib.base.ossbase.OssHelper;
import it.wsh.cn.wshutilslib.glide.GlideApp;

public class OssDemoActivity extends AppCompatActivity {

    private static String TAG = "OssDemoActivity";
    private Button mShowNormalBtn;
    private Button mShowGlideBtn;
    private Button mOssUploadTestBtn;
    private ImageView mShowIv;
    private String mTestUrl = "product_type/0/group_2.png";

    public static void luanchActivity(Activity context) {
        Log.i(TAG, "OssDemoActivity luanchActivity");
        Intent intent = new Intent(context, OssDemoActivity.class);
        context.startActivity(intent);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "OssDemoActivity onCreate");
        setContentView(R.layout.activity_oss_test);
        initView();
        initAction();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(OssDemoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(OssDemoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                            当某条权限之前已经请求过，并且用户已经拒绝了该权限时，shouldShowRequestPermissionRationale ()方法返回的是true
                } else {
//                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        }
    }

    private void initView() {
        mShowNormalBtn = findViewById(R.id.show_oss_normal_btn);
        mShowGlideBtn = findViewById(R.id.show_oss_glide_btn);
        mShowIv = findViewById(R.id.show_oss_iv);
        mOssUploadTestBtn = findViewById(R.id.oss_upload_test_btn);
    }

    private void initAction() {
        mShowNormalBtn.setOnClickListener(mShowNormalListener);
        mShowGlideBtn.setOnClickListener(mShowGlideListener);
        mOssUploadTestBtn.setOnClickListener(mUploadTestListener);
    }

    private View.OnClickListener mUploadTestListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doOssUploadTest();
        }
    };

    private void doOssUploadTest() {
        String objectKey = "avatar/10024/9a753013faad0704027cddd446dd72e7.jpg";
        String localPath = "/storage/emulated/0/xingluo/cache/temp/avatar/10024.jpg";
        OssHelper.startUpload(objectKey, localPath, new IProcessListener() {
            @Override
            public void onStart() {
                Log.i(TAG, "doOssUploadTest onStart");
            }

            @Override
            public void onProgress(IProcessInfo info) {
                Log.i(TAG, "doOssUploadTest Progress = " + info.getProcess());
            }

            @Override
            public void onComplete(int stateCode, String info) {
                Log.i(TAG, "doOssUploadTest onComplete  stateCode = " + stateCode);
            }
        });
    }

    private View.OnClickListener mShowNormalListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doNormalShowPicTest();
        }
    };

    private View.OnClickListener mShowGlideListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "mShowGlideBtn onClick");
            doGlideShowTest();
        }
    };

    private void doGlideShowTest() {
        Log.i(TAG, "doGlideShowTest");
        String url = "http://pic.58pic.com/58pic/14/70/20/10P58PICF7b_1024.jpg";
        GlideApp.with(OssDemoActivity.this).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.i(TAG, "doGlideShowTest  onLoadFailed");
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Log.i(TAG, "doGlideShowTest  onResourceReady");
                return false;
            }
        }).dontAnimate().into(mShowIv);
    }

    private void doNormalShowPicTest() {
        Log.i(TAG, "doNormalShowPicTest");
        String fileName = "测试图片.png";
        String path = Environment.getExternalStorageDirectory().getPath();
        String savePath = path + "/222/";
        OssHelper.startDownload(mTestUrl, fileName, savePath, new IProcessListener() {
            @Override
            public void onStart() {
                Log.i(TAG, "doNormalShowPicTest  onStart");
            }

            @Override
            public void onProgress(IProcessInfo info) {
                Log.i(TAG, "doNormalShowPicTest  onProgress  progress = " + info.getProcess());
                if (info == null) {
                    Log.e(TAG, "doNormalShowPicTest  onProgress  info == null");
                    return;
                }
                if (info.getProcess() >= 100) {
                    String localPath = info.getLocalPath();
                    long threadId = Thread.currentThread().getId();
                    Log.i(TAG, "doNormalShowPicTest  onProgress  threadId = " + threadId);
                    Glide.with(OssDemoActivity.this).load(localPath).into(mShowIv);
                }
            }

            @Override
            public void onComplete(int stateCode, String info) {
                Log.i(TAG, "doNormalShowPicTest  onComplete  stateCode = " + stateCode + "     info = " + info);
            }
        });
    }
}
