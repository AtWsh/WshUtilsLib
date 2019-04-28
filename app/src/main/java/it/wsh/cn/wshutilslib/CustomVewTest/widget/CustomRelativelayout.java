package it.wsh.cn.wshutilslib.CustomVewTest.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CustomRelativelayout extends RelativeLayout {

    private static final String TAG = "CustomRelativelayout";

    public CustomRelativelayout(Context context) {
        super(context);
    }

    public CustomRelativelayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRelativelayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d(TAG, "onLayout");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw");
    }
}
