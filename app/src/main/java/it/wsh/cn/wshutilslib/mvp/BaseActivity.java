package it.wsh.cn.wshutilslib.mvp;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseActivity extends AppCompatActivity {
    private CompositeDisposable disposableContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposableContainer = new CompositeDisposable();
        lightStatusBar(enableLightStatusBar());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        disposableContainer.dispose();
        disposableContainer = null;
        super.onDestroy();
    }


    protected boolean enableLightStatusBar() {
        return true;
    }

    /**
     * 亮色调状态栏
     */
    private void lightStatusBar(boolean light) {
        if (light && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            decorView.setSystemUiVisibility(flags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * 沉浸式效果
     */
    protected void immerse() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(0);
        }
        View decorView = getWindow().getDecorView();
        int flags = decorView.getSystemUiVisibility();
        decorView.setSystemUiVisibility(flags | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void addDisposable(Disposable disposable) {
        disposableContainer.add(disposable);
    }
}
