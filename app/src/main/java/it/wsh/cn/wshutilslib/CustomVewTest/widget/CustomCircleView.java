package it.wsh.cn.wshutilslib.CustomVewTest.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import it.wsh.cn.wshutilslib.R;

public class CustomCircleView extends View {

    private final String TAG = "CustomCircleView";

    private float mTextSize; //对应中间文本文字的大小
    private String mTextContent; //对应中间文本
    private int mCircleColor; //对应内圆的颜色
    private int mArcColor; //对应外环的颜色
    private int mTextColor; //对应文本的颜色
    private int mStartAngle; //对应外环的起始角度
    private int mSweepAngle; //对应外环扫描角度
    private float mCircleRadius; //内圆半径
    private float mArcRadius; //外环半径
    private float mArcStrokeWidth; //外环厚度

    private int mCircleX;
    private int mCircleY;
    private Paint mCirclePaint;
    private Paint mArcPaint;
    private Paint mTextPaint;
    private RectF mRectF;

    private boolean mStart;
    private int mPercent = 0;

    public CustomCircleView(Context context) {
        this(context,null);
    }

    public CustomCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public CustomCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomCircleView);
        if (typedArray != null) {
            mTextSize = typedArray.getDimension(R.styleable.CustomCircleView_textSize, 14);
            mTextContent = typedArray.getString(R.styleable.CustomCircleView_text);
            mCircleColor = typedArray.getColor(R.styleable.CustomCircleView_circleColor, 0);
            mArcColor = typedArray.getColor(R.styleable.CustomCircleView_arcColor, 0);
            mTextColor = typedArray.getColor(R.styleable.CustomCircleView_textColor, 0);
            mStartAngle = typedArray.getInt(R.styleable.CustomCircleView_startAngle, 0);
            mCircleRadius = typedArray.getDimension(R.styleable.CustomCircleView_circleRadius, 60);

            mArcRadius = typedArray.getDimension(R.styleable.CustomCircleView_arcRadius, 120);
            mArcStrokeWidth = typedArray.getDimension(R.styleable.CustomCircleView_arcStrokeWidth, 10);
            mPercent = typedArray.getInt(R.styleable.CustomCircleView_percent, 10);
            mSweepAngle = (int) (mPercent * 3.6f);
            typedArray.recycle();
        }
    }

    public void setPercent(int percent) {
        if (percent < 0 || percent >100) {
            return;
        }

        mPercent = percent;
        mTextContent = mPercent + "%";
        mSweepAngle = (int) (mPercent * 3.6f);

        invalidate(); //只会调用onDraw
        //requestLayout(); //只会调用onMeasure，onLayout
    }

    public void start() {
        mStart = true;
        mTextContent = mPercent + "%";
        mSweepAngle = (int) (mPercent * 3.6f);
        invalidate();
    }

    public void stop() {
        mStart = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init(getMeasuredWidth(), getMeasuredHeight());
    }

    private void init(int width, int height) {
        mCircleX = width / 2;
        mCircleY = height / 2;
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mCircleColor);
        mRectF = new RectF(mCircleX - mArcRadius, mCircleY - mArcRadius, mCircleX + mArcRadius,
                mCircleY + mArcRadius);

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setColor(mArcColor);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcStrokeWidth);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        if (TextUtils.isEmpty(mTextContent)) {
            mTextContent = "";
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, "onLayout");
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw");
        super.onDraw(canvas);
        drawSth(canvas);

        if (mStart && mPercent < 100) {
            doPost();
        }
    }

    private void doPost() {
        mPercent = mPercent + 2;
        mTextContent = mPercent + "%";
        mSweepAngle = (int) (mPercent * 3.6f);
        postInvalidateDelayed(500);
    }

    private void drawSth(Canvas canvas) {
        canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mCirclePaint);
        canvas.drawArc(mRectF, mStartAngle, mSweepAngle, false, mArcPaint);
        canvas.drawText(mTextContent, 0, mTextContent.length(), mCircleX, mCircleY + mTextSize
                / 4, mTextPaint);
    }
}
