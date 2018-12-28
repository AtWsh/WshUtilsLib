package it.wsh.cn.common_imageloader.target;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;

/**
 * author: wenshenghui
 * created on: 2018/11/27 15:27
 * description: Glide加载的图片需要绘制在TextView的Top位置时，用这个类
 */
public class TextViewTopTarget extends CustomViewTarget<TextView, Drawable> {

    private static final String TAG = "TextViewTopTarget";
    private int mWidth; //px
    private int mHeight; //px

    /**
     * Constructor that defaults {@code waitForLayout} to {@code false}.
     *
     * @param view
     */
    public TextViewTopTarget(@NonNull TextView view) {
        this(view, 96, 96); //默认
    }

    public TextViewTopTarget(@NonNull TextView view, int size) {
        this(view, size, size);
    }

    public TextViewTopTarget(@NonNull TextView view, int width, int height) {
        super(view);
        mWidth = width;
        mHeight = height;
    }

    //错误
    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        //BHLog.i(TAG, "onLoadFailed");
        if (errorDrawable != null) {
            errorDrawable.setBounds(0, 0, mWidth, mHeight);
            this.view.setCompoundDrawables(null, errorDrawable, null, null);
        }
    }

    @Override
    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
        //BHLog.i(TAG, "onResourceReady");
        if (resource != null) {
            resource.setBounds(0, 0, mWidth, mWidth);
            this.view.setCompoundDrawables(null, resource, null, null);
        }
    }

    //占位
    @Override
    protected void onResourceCleared(@Nullable Drawable placeholder) {
        //BHLog.i(TAG, "onResourceCleared");
        if (placeholder != null) {
            //BHLog.i(TAG, "onResourceCleared  placeholder != null");
            placeholder.setBounds(0, 0, mWidth, mHeight);
            this.view.setCompoundDrawables(null, placeholder, null, null);
        }
    }
}
