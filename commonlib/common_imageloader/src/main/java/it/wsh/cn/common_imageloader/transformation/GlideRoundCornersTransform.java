package it.wsh.cn.common_imageloader.transformation;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class GlideRoundCornersTransform extends BitmapTransformation {

    private static final String ID = "cn.evergrande.it.audiodevice.audiolib.transformers.GlideRoundCornersTransform";

    // 圆角类型
    public static final int TYPE_ALL = 0;
    public static final int TYPE_TOP = 1;
    public static final int TYPE_BOTTOM = 2;

    private float radius = 0f;
    private float diameter;
    private int mType;

    public GlideRoundCornersTransform(float radius) {
        this(radius, TYPE_ALL);
    }

    public GlideRoundCornersTransform() {
        this(5, TYPE_ALL);
    }

    public GlideRoundCornersTransform(float dp, int type) {
        super();
        this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
        this.diameter = radius * 2;
        mType = type;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap source, int outWidth, int outHeight) {
        if (source == null) {return null;}

        Bitmap result = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        }

        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        //适配centerCrop
        float scale = 0;
        float dx = 0,dy = 0;
        float scaleWidth = (float) outWidth / sourceWidth;
        float scaleHeight = (float) outHeight / sourceHeight;

        // 先保证宽与控件一致
        if (scaleWidth * sourceHeight < outHeight) {
            if (scaleHeight * sourceWidth < outWidth) {
                scale += (outHeight - scaleWidth * sourceHeight)/outHeight;
                dx = (outWidth - sourceWidth * scale) * 0.5f;
            } else {
                scale = scaleHeight;
                dx = (outWidth - sourceWidth * scale) * 0.5f;
            }
        } else  {
            //缩放后高度足够填满控件
            scale = scaleWidth;
            dy = (outHeight - sourceHeight * scale) * 0.5f;
        }

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        if (mType == TYPE_TOP) {
            // 顶部有圆角
            RectF roundRectF = new RectF(0, 0, outWidth, diameter);
            canvas.drawRoundRect(roundRectF, radius, radius, paint);

            RectF rectF = new RectF(0, radius, outWidth, outHeight);
            canvas.drawRect(rectF, paint);
        } else if (mType == TYPE_BOTTOM) {
            // 底部有圆角
            RectF roundRectF = new RectF(0, outHeight - diameter, outWidth, outHeight);
            canvas.drawRoundRect(roundRectF, radius, radius, paint);

            RectF rectF = new RectF(0, 0, outWidth, outHeight - radius);
            canvas.drawRect(rectF, paint);
        } else {
            // 默认四周都有圆角
            RectF rectF = new RectF(0f, 0f, outWidth, outHeight);
            canvas.drawRoundRect(rectF, radius, radius, paint);
        }


        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GlideRoundCornersTransform) {
            return this == obj;
        } return false;
    }
    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID.getBytes(CHARSET));
    }
}
