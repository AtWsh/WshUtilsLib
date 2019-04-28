package it.wsh.cn.wshutilslib.mvp;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import it.wsh.cn.wshutilslib.R;
import it.wsh.cn.wshutilslib.common.StatusBarUtil;

public abstract class BaseUiActivity extends BaseActivity implements IBaseView {
    /**
     * 标题栏的控件统一在这里初始化，并由上层自定义修改
     */

    protected ViewGroup rootView;
    protected View headerLayout;
    protected TextView headLeftTv;
    protected TextView headerCenter;
    protected TextView headRightTv;
    protected View headerLine;
    protected TextView unReadCountLeftTv;
    protected FrameLayout containerView;

    //页面发生错误时，展示的统一错误页面
    protected View errorView;
    //页面数据为空时，展示的统一为空页面
    protected View emptyView;

    protected ViewGroup baseRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //初始化布局
        initLayout();
        //初始化数据
        initData();

    }

    private void initLayout() {
        baseRootView = (ViewGroup) View.inflate(this, R.layout.shell_base_layout, null);
        View contentView = View.inflate(this, getLayoutId(), null);
        containerView = (FrameLayout) baseRootView.findViewById(R.id.container);
        containerView.addView(contentView);
        setContentView(baseRootView);
        initHeader();
        initContentView();
    }


    private void initHeader() {
        rootView = baseRootView.findViewById(R.id.root_view);
        headerLayout = baseRootView.findViewById(R.id.header_layout_rel);
        headLeftTv = baseRootView.findViewById(R.id.header_left);
        headerCenter = baseRootView.findViewById(R.id.header_center);
        headRightTv = baseRootView.findViewById(R.id.header_right);
        headerLine = baseRootView.findViewById(R.id.header_line);
        unReadCountLeftTv = baseRootView.findViewById(R.id.unread_count_left);
        headLeftTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHeadLeftClicked();
            }
        });

        headRightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHeadRightClicked();
            }
        });
    }

    protected void initContentView() {
    }


    public void setBackground(Drawable background) {
        if (baseRootView != null) {
            baseRootView.setBackground(background);
        }
    }


    /**
     * 设置状态栏颜色，如果需要自定义，重写这个方法即可
     *
     * @param colorRes
     */
    protected void setStatusBarColor(int colorRes) {
        // 设置透明状态栏
        StatusBarUtil.setColor(this, getResources().getColor(colorRes), 0);
    }

    /**
     * 设置标题栏右边内容
     *
     * @param strId
     * @param drawableId
     */
    protected void setHeadRightTv(@StringRes int strId, @DrawableRes int drawableId) {
        headRightTv.setText(strId);
        Drawable drawable = getResources().getDrawable(drawableId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            headRightTv.setCompoundDrawables(drawable, null, null, null);
        }
    }


    /**
     * 设置标题栏右边内容
     *
     * @param strId
     */
    protected void setHeadRightTv(@StringRes int strId) {
        headRightTv.setText(strId);
    }

    /**
     * 设置标题
     *
     * @param titleResID
     */
    protected void setHeaderCenter(@StringRes int titleResID) {
        headerCenter.setText(titleResID);
    }

    /**
     * 设置标题
     *
     * @param text
     */
    protected void setHeaderCenter(String text) {
        headerCenter.setText(text);
    }


    /**
     * 标题栏右边按钮点击事件处理
     */
    protected void onHeadRightClicked() {

    }

    /**
     * 标题栏左边按钮点击事件处理
     */
    protected void onHeadLeftClicked() {
        finish();
    }


    public void showLoadingDialog() {
        showLoading(true);
    }


    public void hideLoadingDialog() {
        showLoading(false);
    }

    // 加载时显示的loading
    public void showLoading(View loadingLayout, View loadingTips, boolean show) {
        if (loadingLayout == null || loadingTips == null) {
            return;
        }
        Drawable drawable = loadingTips.getBackground();
        if (show) {
            if (loadingLayout.getVisibility() != View.VISIBLE) {
                loadingLayout.setVisibility(View.VISIBLE);
                if (drawable instanceof AnimationDrawable) {
                    ((AnimationDrawable) drawable).start();
                }
            }
        } else {
            loadingLayout.setVisibility(View.GONE);
            if (drawable instanceof AnimationDrawable) {
                ((AnimationDrawable) drawable).stop();
            }
        }
    }

    public void showLoading(boolean show) {
/*        if (loadingTips == null) {
            return;
        }
        Drawable drawable = loadingTips.getBackground();
        if (show) {
            //加之前要判断有没有加过
            containerView.removeView(loadingLayout);
            containerView.addView(loadingLayout);
            if (loadingLayout.getVisibility() != View.VISIBLE) {
                loadingLayout.setVisibility(View.VISIBLE);
                if (drawable instanceof AnimationDrawable) {
                    ((AnimationDrawable) drawable).start();
                }
            }
        } else {
            containerView.removeView(loadingLayout);
            loadingLayout.setVisibility(View.GONE);
            if (drawable instanceof AnimationDrawable) {
                ((AnimationDrawable) drawable).stop();
            }
        }*/
    }

    /**
     * 显示加载失败页面
     */
    protected void showErrorView() {
        //先移除空view
        removeExceptionView();
        errorView = View.inflate(this, R.layout.error_layout, null);
        containerView.addView(errorView);
        errorView.setVisibility(View.VISIBLE);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onErroViewClicked();
            }
        });
    }

    /**
     * 显示加载失败页面
     */
    protected void showErrorView(String errorMsg) {
        //先移除空view
        removeExceptionView();
        errorView = View.inflate(this, R.layout.error_layout, null);
        if (!TextUtils.isEmpty(errorMsg)) {
            TextView msgTv = (TextView) errorView.findViewById(R.id.errorTips);
            msgTv.setText(errorMsg);
        }
        containerView.addView(errorView);
        errorView.setVisibility(View.VISIBLE);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onErroViewClicked();
            }
        });
    }


    /**
     * 移除加载异常页面
     */
    protected void removeExceptionView() {
        containerView.removeView(errorView);
        containerView.removeView(emptyView);
    }

    /**
     * 显示加载数据为空页面
     */
    protected void showEmptyView() {
        //先remove掉errorView
        removeExceptionView();
        emptyView = View.inflate(this, R.layout.empty_layout, null);
        containerView.addView(emptyView);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //暂时屏蔽，根据产品需求是否放开
                //onErroViewClicked();
            }
        });
    }

    /**
     * 显示加载数据为空页面
     */
    protected void showEmptyView(View emptyView) {
        //先remove掉errorView
        removeExceptionView();
        this.emptyView = emptyView;
        containerView.addView(emptyView);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //暂时屏蔽，根据产品需求是否放开
                //onErroViewClicked();
            }
        });
    }

    /**
     * 显示加载数据为空页面,暴露给上层，自定义文案
     */
    protected void showEmptyView(String emptyDes) {
        //先remove掉errorView
        removeExceptionView();
        emptyView = View.inflate(this, R.layout.empty_layout, null);
        TextView emptyTv = (TextView) emptyView.findViewById(R.id.empty_tv);
        if (!TextUtils.isEmpty(emptyDes)) {
            emptyTv.setText(emptyDes + "");
        }
        containerView.addView(emptyView);

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //暂时屏蔽，根据产品需求是否放开
                onErroViewClicked();
            }
        });
    }

    /**
     * 显示加载数据为空页面,暴露给上层，自定义图标和文案
     */
    protected void showEmptyView(@DrawableRes int drawableRes, String emptyDes) {
        //先remove掉errorView
        removeExceptionView();
        emptyView = View.inflate(this, R.layout.empty_layout, null);
        ImageView emptyIv = (ImageView) emptyView.findViewById(R.id.empty_iv);
        TextView emptyTv = (TextView) emptyView.findViewById(R.id.empty_tv);
        emptyIv.setImageResource(drawableRes);
        emptyTv.setText(emptyDes + "");
        containerView.addView(emptyView);

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //暂时屏蔽，根据产品需求是否放开
                //onErroViewClicked();
            }
        });
    }

    protected abstract int getLayoutId();

    /**
     * 暴露给上层，errorView点击后 应该去重新加载页面逻辑
     */
    protected void onErroViewClicked() {

    }


    /**
     * 一些初始化操作在这里执行
     */
    protected void initData() {
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /***下面两个方法是为了兼容家居的*/
    public void responseSuccess(Object... result) {

    }

    public void responseErrors(Object... error) {

    }
}
